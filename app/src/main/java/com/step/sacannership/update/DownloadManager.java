package com.step.sacannership.update;

import android.os.Environment;

import com.step.sacannership.api.MyIntercepter;
import com.step.sacannership.tools.ToastUtils;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {
    private final String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/scanApk/";
    private static final AtomicReference<DownloadManager> INSTANCE = new AtomicReference<>();
    private HashMap<String, Call> downCalls;//用来存放各个下载的请求
    private OkHttpClient mClient;//OKHttpClient;

    //获得一个单例类
    public static DownloadManager getInstance() {
        for (; ; ) {
            DownloadManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new DownloadManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private DownloadManager() {
        downCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().addInterceptor(new MyIntercepter()).build();
    }

    /**
     * 开始下载
     * @param url 载请求的网址
     * @param downLoadObserver 用来回调的接口
     */
    public void download(String url, String fileName,Observer<DownLoadInfo> downLoadObserver) {
        Observable.just(url)
            .filter(s -> !downCalls.containsKey(s))//call的map已经有了,就证明正在下载,则这次不下载
            .flatMap(s -> Observable.just(createDownInfo(s, fileName)))
            .map(downLoadInfo -> getRealFileName(downLoadInfo, apkPath))//检测本地文件夹,生成新的文件名
            .flatMap(downloadInfo -> Observable.create(new DownloadSubscribe(downloadInfo, apkPath)))//下载
            .observeOn(AndroidSchedulers.mainThread())//在主线程回调
            .subscribeOn(Schedulers.io())//在子线程执行
            .subscribe(downLoadObserver);//添加观察者
    }

    public void clear(){
        Set<Map.Entry<String, Call>> entries = downCalls.entrySet();
        for (Map.Entry<String, Call> entry : entries){
            Call call = entry.getValue();
            if (call != null){
                call.cancel();
            }
        }
        downCalls.clear();
    }

    public void cancel(String url) {
        Call call = downCalls.get(url);
        if (call != null) {
            call.cancel();//取消
        }
        downCalls.remove(url);
    }
    /**
     * 创建DownLoadInfo
     *
     * @param url 请求网址
     * @return DownLoadInfo
     */
    private DownLoadInfo createDownInfo(String url, String fileName){
        DownLoadInfo loadInfo = new DownLoadInfo(url);
        long contentLength = getContentLength(url);//获得文件大小
        loadInfo.setTotal(contentLength);
        loadInfo.setFileName(fileName);
        return loadInfo;
    }

    private DownLoadInfo getRealFileName(DownLoadInfo downInfo, String path) {
        String fileName = downInfo.getFileName();
        long downloadLength = 0, contentLength = downInfo.getTotal();
        File file = new File(path, fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()){
            boolean isCreate = parentFile.mkdirs();
        }
        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
        }
        //之前下载过,需要重新来一个文件
        int i = 1;
        while (downloadLength >= contentLength) {
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            if (dotIndex == -1) {
                fileNameOther = fileName + "(" + i + ")";
            } else {
                fileNameOther = fileName.substring(0, dotIndex)
                        + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(path, fileNameOther);
            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        //设置改变过的文件名/大小
        downInfo.setProgress(downloadLength);
        downInfo.setFileName(file.getName());
        return downInfo;
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<DownLoadInfo> {

        private DownLoadInfo downloadInfo;
        private String path;

        public DownloadSubscribe(DownLoadInfo downloadInfo, String path) {
            this.downloadInfo = downloadInfo;
            this.path = path;
        }

        @Override
        public void subscribe(ObservableEmitter<DownLoadInfo> e) throws Exception {
            String url = (String) downloadInfo.getUrl();
            long downloadLength = (long) downloadInfo.getProgress();//已经下载好的长度
            long contentLength = (long) downloadInfo.getTotal();//文件的总长度
            //初始进度信息
            e.onNext(downloadInfo);

            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .addHeader("Content-Type", "application/json")
                    .url(url)
                    .build();
            Call call = mClient.newCall(request);
            downCalls.put(url, call);//把这个添加到call里,方便取消
            Response response = call.execute();

            File file = new File(path, downloadInfo.getFileName());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
                is = response.body().byteStream();
                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setProgress(downloadLength);
                    e.onNext(downloadInfo);
                }
                fileOutputStream.flush();
                downCalls.remove(url);
            } finally {
                //关闭IO流
                try {
                    if (is != null){
                        is.close();
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                }
                try {
                    if (fileOutputStream != null){
                        fileOutputStream.close();
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            e.onComplete();//完成
            //安装apk
            ToastUtils.installApp(file.getAbsolutePath());
        }
    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "bearer "+ SPTool.getToken())
                .url(downloadUrl)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0? -1 : contentLength;
            }else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}