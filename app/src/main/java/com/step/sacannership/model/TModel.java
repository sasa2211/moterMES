package com.step.sacannership.model;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.step.sacannership.BuildConfig;
import com.step.sacannership.activity.App;
import com.step.sacannership.activity.ArriveActivity;
import com.step.sacannership.api.ApiManager;
import com.step.sacannership.api.ApiService;
import com.step.sacannership.listener.BindListener;
import com.step.sacannership.listener.BindMaterialView;
import com.step.sacannership.listener.CancelListener;
import com.step.sacannership.listener.LogisticsListener;
import com.step.sacannership.listener.MaterialBindListener;
import com.step.sacannership.listener.SyncCodeListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.listener.UnBindDeliveryListener;
import com.step.sacannership.model.bean.AssembleDeleteBean;
import com.step.sacannership.model.bean.AssembleSubmitBean;
import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.DeliveryBaseBean;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.NoPalletDetails;
import com.step.sacannership.model.bean.NpBindSaveBean;
import com.step.sacannership.model.bean.ProductItem;
import com.step.sacannership.model.bean.ProductLine;
import com.step.sacannership.model.bean.ProductOrderInfo;
import com.step.sacannership.model.bean.ProductSnInfo;
import com.step.sacannership.model.bean.ProductStatue;
import com.step.sacannership.model.bean.Request;
import com.step.sacannership.model.bean.Result;
import com.step.sacannership.model.bean.ScanDeliveryMaterialBean;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.model.bean.UnBind;
import com.step.sacannership.model.bean.UserBean;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;
import com.step.sacannership.update.UpdateBean;

import org.reactivestreams.Subscription;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TModel extends BaseModuel{

    ApiService apiService;

    public TModel() {
        apiService = ApiManager.getService(ApiService.class);
    }

    /**登录*/
    public void login(String account, String password, TPresenter<UserBean> presenter){
        Request request = new Request();
        request.put("userName", account);
        request.put("password", password);
        Disposable disposable = apiService.login(request)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在登录"))
                .subscribe(userBean -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(userBean);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void getDeliveryListUnPallet(String billNO, TPresenter<DeliveryBean> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getMaterialListsNoPallet(billNO)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在加载"))
                .map(deliveryBean -> {
                    List<DeliveryDetailBean> sBillDetails = deliveryBean.getSapDeliveryBillDetails();
                    List<NoPalletDetails> sNPDetails = deliveryBean.getSdDeliveryBillNoPalletDetails();
                    if (sNPDetails != null && sNPDetails.size() > 0){
                        for (NoPalletDetails noPalletDetail : sNPDetails){
                            for (DeliveryDetailBean deliveryDetailBean : sBillDetails){
                                if (noPalletDetail.getMaterialNO().equals(deliveryDetailBean.getMaterialNo()) && noPalletDetail.getRowIndex().equals(deliveryDetailBean.getRowIndex())){
                                    deliveryDetailBean.setQuantity(noPalletDetail.getQuantity());
                                    break;
                                }
                            }
                        }
                    }
                    deliveryBean.setSapDeliveryBillDetails(sBillDetails);
                    return deliveryBean;
                })
                .subscribe(deliveryBean -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(deliveryBean);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 获取发货单信息并保存到托盘
     * */
    public void getDeliveryListAndBindPallet(String billNO, TrayInfoBean trayInfoBean, TrayInfoListener listener){
        apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getDeliveryList(billNO)
            .compose(toMain())
            .doOnSubscribe(subscription -> listener.showDialog("正在获取发运单"))
            .doOnNext(deliveryBean -> listener.dismissDialog())
            .flatMap((Function<DeliveryBean, Flowable<ResponseBody>>) deliveryBean -> {
                TrayDeliveryBean trayDeliveryBean = new TrayDeliveryBean();
                trayDeliveryBean.setNewBind(true);
                trayDeliveryBean.setDeliveryBillId(deliveryBean.getPkId());
                trayDeliveryBean.setDeliveryBillNO(deliveryBean.getBillNo());
                trayDeliveryBean.setCreateTime(deliveryBean.getCreateTime());
                trayDeliveryBean.setOutStockTime(deliveryBean.getOutStockTime());
                List<DeliveryBillPalletDetailsBean> billPalletDetailsBeans = new ArrayList<>();
                for (DeliveryDetailBean deliveryDetailBean : deliveryBean.getSapDeliveryBillDetails()){
                    DeliveryBillPalletDetailsBean palletDetailsBean= new DeliveryBillPalletDetailsBean();
                    palletDetailsBean.setDeliveryBillID(deliveryBean.getPkId());//
                    palletDetailsBean.setMasterID(deliveryDetailBean.getMasterId());
                    palletDetailsBean.setMaterialName(deliveryDetailBean.getMaterialName());
                    palletDetailsBean.setMaterialNo(deliveryDetailBean.getMaterialNo());
                    palletDetailsBean.setPkid(deliveryDetailBean.getPkId());
                    palletDetailsBean.setQuantity(deliveryDetailBean.getQuantity() - deliveryDetailBean.getBindQuantity());
                    palletDetailsBean.setRowIndex(deliveryDetailBean.getRowIndex());
                    palletDetailsBean.setOutQuantity(deliveryDetailBean.getOutQuantity());
                    palletDetailsBean.setBindQuantity(deliveryDetailBean.getBindQuantity());
                    palletDetailsBean.setQualifiedQuantity(deliveryDetailBean.getQualifiedQuantity());
                    billPalletDetailsBeans.add(palletDetailsBean);
                }

                trayDeliveryBean.setDeliveryBillPalletDetails(billPalletDetailsBeans);
                List<TrayDeliveryBean> datas = trayInfoBean.getDeliveryBills();
                datas.add(trayDeliveryBean);
                trayInfoBean.setDeliveryBills(datas);
                return apiService.bindTray(trayInfoBean);
            })
            .compose(toMain())
            .doOnSubscribe(subscription -> listener.showDialog("正在将发运单绑定到托盘"))
            .doOnNext(responseBody -> listener.dismissDialog())
            .flatMap((Function<ResponseBody, Flowable<TrayInfoBean>>) responseBody ->
                    apiService.trayInfo(trayInfoBean.getPalletNO()))
            .compose(toMain())
            .doOnSubscribe(subscription -> listener.showDialog("正在重新获取托盘信息"))
            .subscribe(infoBean -> {
                listener.dismissDialog();
                listener.getTraySuccess(infoBean);
            }, throwable -> {
                listener.dismissDialog();
                listener.getTrayFailed(getErrorMessage(throwable));
            });
        compositeDisposable.add(disposable);
    }
    public void getDeliveryList(String billNO, TPresenter<DeliveryBean> presenter){
        Disposable disposable = apiService.getDeliveryList(billNO)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在加载"))
                .subscribe(deliveryBean -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(deliveryBean);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    public void getPalletInfo(String billNo, boolean simpleSyncNum, TrayInfoListener listener, SyncCodeListener syncCodeListener){
        if (TextUtils.isEmpty(billNo)){
            ToastUtils.showToast(App.getInstance().getApplicationContext(), "发货单号不能为空");
            return;
        }
        Disposable disposable = apiService.getSNNum(billNo)
                .compose(toMain())
                .doOnNext(syncCodeListener::getSnNumSuccess)
                .flatMap((Function<Integer, Flowable<TrayInfoBean>>) integer -> {
                    if (simpleSyncNum){
                        return null;
                    }else {
                        return apiService.trayInfo(billNo);
                    }
                })
                .compose(toMain())
                .doOnSubscribe(subscription -> listener.showDialog("加载中"))
                .map(infoBean -> {
                    List<TrayDeliveryBean> beans = infoBean.getDeliveryBills();
                    List<TrayDeliveryBean> newBeans = new ArrayList<>();
                    if (beans != null && !beans.isEmpty()){
                        for (TrayDeliveryBean deliveryBean : beans){
                            if (deliveryBean.getDeliveryBillNO() == null){
                                continue;
                            }
                            newBeans.add(deliveryBean);
                        }
                    }
                    infoBean.setDeliveryBills(newBeans);
                    return infoBean;
                })
                .subscribe(infoBean -> {
                    listener.dismissDialog();
                    listener.getTraySuccess(infoBean);
                }, throwable -> {
                    listener.dismissDialog();
                    if (!simpleSyncNum){
                        listener.getTrayFailed(getErrorMessage(throwable));
                    }
                });
        compositeDisposable.add(disposable);
    }
    public void getTrayInfos(String billNo, TrayInfoListener listener){
        if (TextUtils.isEmpty(billNo)){
            ToastUtils.showToast(App.getInstance().getApplicationContext(), "发货单号不能为空");
            return;
        }
        Disposable disposable = apiService.trayInfo(billNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> listener.showDialog("加载中"))
                .map(infoBean -> {
                    List<TrayDeliveryBean> beans = infoBean.getDeliveryBills();
                    List<TrayDeliveryBean> newBeans = new ArrayList<>();
                    if (beans != null && !beans.isEmpty()){
                        for (TrayDeliveryBean deliveryBean : beans){
                            if (deliveryBean.getDeliveryBillNO() == null){
                                continue;
                            }
                            newBeans.add(deliveryBean);
                        }
                    }
                    infoBean.setDeliveryBills(newBeans);
                    return infoBean;
                })
                .subscribe(infoBean -> {
                    listener.dismissDialog();
                    listener.getTraySuccess(infoBean);
                }, throwable -> {
                    listener.dismissDialog();
                    listener.getTrayFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    private void showLog(String message){
        if (message.length() > 500){
            Log.e("TAGGG", "message="+message.substring(0, 500));
            message = message.substring(500);
            showLog(message);
        }else {
            Log.e("TAGGG", "messageLast="+message);
        }
    }
    //getPalletArrive
    public void getPalletArrive(String billNo, TrayInfoListener listener){
        Disposable disposable = apiService.getPalletArrive(billNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> listener.showDialog("加载中"))
                .map(infoBean -> {
                    List<TrayDeliveryBean> beans = infoBean.getDeliveryBills();
                    List<TrayDeliveryBean> newBeans = new ArrayList<>();
                    if (beans != null && !beans.isEmpty()){
                        for (TrayDeliveryBean deliveryBean : beans){
                            if (deliveryBean.getDeliveryBillNO() == null){
                                continue;
                            }
                            newBeans.add(deliveryBean);
                        }
                    }
                    infoBean.setDeliveryBills(newBeans);
                    return infoBean;
                })
                .subscribe(infoBean -> {
                    listener.dismissDialog();
                    listener.getTraySuccess(infoBean);
                }, throwable -> {
                    listener.dismissDialog();
                    listener.getTrayFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 保存到货信息
     * */
    public void savePalletArrive(String billNo, ArriveActivity activity){
        Disposable disposable = apiService.savePalletArrive(billNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> activity.showDialog("加载中"))
                .subscribe(infoBean -> {
                    activity.dismissDialog();
                    activity.saveSuccess();
                }, throwable -> {
                    activity.dismissDialog();
                    activity.saveFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 绑定
     * */
    public void bindTray(TrayInfoBean infoBean, boolean isExit, BindListener listener){
        Disposable disposable = apiService.bindTray(infoBean)
                .compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> listener.bindSuccess("", isExit),
                throwable -> listener.bindFailed("绑定失败："+getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }
    /**
     * 撤销出库
     * */
    public void cancelSapOutStock(String billNo, CancelListener listener){
        Disposable disposable = apiService.deliverySubmitCancel(billNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> {listener.showDialog("正在撤销出库");})
                .subscribe(responseBody -> {
                    listener.dismissDialog();
                    listener.cancelSuccess("撤销成功");
                }, throwable -> {
                    listener.dismissDialog();
                    listener.cancelFailed("撤销失败："+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 撤销发运
     * */
    public void cancelDelivery(String palletNo, boolean hasPallet, CancelListener listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Flowable<ResponseBody> flowable;
        if (hasPallet){
            flowable = apiService.deliveryPalletCancel(palletNo);
        }else {
            flowable = apiService.deliveryPalletCancelUnPallet(palletNo);
        }
        Disposable disposable = flowable.compose(toMain())
                .doOnSubscribe(subscription -> {listener.showDialog("正在撤销发运");})
                .subscribe(responseBody -> {
                    listener.dismissDialog();
                    listener.cancelSuccess("撤销成功");
                }, throwable -> {
                    listener.dismissDialog();
                    listener.cancelFailed("撤销失败："+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    //删除绑定
    public void deleteBindDelivery(UnBind unBind, BindListener deleteListener){
        Disposable disposable = apiService.unBind(unBind)
                .compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(responseBody -> {
                    deleteListener.deleteSuccess();
                }, throwable -> {
                    deleteListener.deleteFailed("删除绑定失败，请重试："+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    //发运
    public void deliveryTray(String palletNo, Activity activity){
        if (apiService == null){
            apiService = ApiManager.getService(ApiService.class);
        }
        Disposable disposable = apiService.delivery(palletNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> {
                    SPTool.showToast(activity, "发运成功");
                }, throwable -> {
                    SPTool.showToast(activity, "发运失败，请重试："+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 无托盘发运单编号发运
     * */
    public void deliveryTrayUnPallet(Request logisticsBean, Activity activity){
        if (apiService == null){
            apiService = ApiManager.getService(ApiService.class);
        }
        Disposable disposable = apiService.deliveryUnPalletNew(logisticsBean)
                .compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> {
                    SPTool.showToast(activity, "发运成功");
                }, throwable -> {
                    SPTool.showToast(activity, "发运失败，请重试："+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    public void testUnPallet(String no, Activity activity){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.testDeliveryUnPallet(no)
                .compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> SPTool.showToast(activity, "检验成功"),
                        throwable -> SPTool.showToast(activity, "检验失败，请重试："+getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }
    /**托盘检验合格*/
    public void testpallet(String no, int type, Activity activity){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Flowable<ResponseBody> flowable;
        if (type == 1){
            flowable = apiService.testPallet(no);
        }else {
            flowable = apiService.testDelivery(no);
        }
        Disposable disposable = flowable.compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> SPTool.showToast(activity, "检验成功"),
                        throwable -> SPTool.showToast(activity, "检验失败，请重试："+getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }
    /**
     * 托盘撤销检验
     * */
    public void cancelPalletTest(String palletNo, Activity activity){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.cancelTestPallet(palletNo).compose(toMain())
                .doOnSubscribe(subscription -> {})
                .subscribe(deliveryBean -> SPTool.showToast(activity, "撤销成功"),
                        throwable -> SPTool.showToast(activity, "撤销失败，请重试："+getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
        //
        //托盘检验撤销
    }
    /**
     * 获取发运物流列表
     * getLogisticsList
     * */
    public void getLogisticsList(LogisticsListener listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getLogisticsList().compose(toMain())
                .subscribe(listener::getLogisticsSuccess,
                throwable -> listener.getLogisticsFailed(getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }
    /**
     * 扫描发货单获取物料列表
     * */
    public void getMaterialList(boolean hasPallet, int palletId, String materialNO, String billNO, String rowIndex, TPresenter<List<ScanDeliveryMaterialBean>> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Flowable<List<ScanDeliveryMaterialBean>> flowable;
        if (hasPallet){
            flowable = apiService.scanMaterial(palletId, billNO, materialNO, rowIndex);
        }else {
            flowable = apiService.scanMaterialNopallet(billNO, materialNO, rowIndex);
        }
        Disposable disposable = flowable.compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("1"))
                .subscribe(deliveryBean -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(deliveryBean);
                },throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 发货单绑定物料
     * */
    public void bindMaterial(boolean hasPallet, ScanDeliveryMaterialBean materialBean, BindMaterialView listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Flowable<Map<String, Object>> flowable;
        if (hasPallet){
            flowable = apiService.bindMaterial(materialBean);
        }else {
            flowable = apiService.bindMaterialUnPallet(materialBean);
        }
        //                    String materialNo = jsonObject.optString("materialNo");
        //                    listener.bindSuccess(materialNo);
        Disposable disposable = flowable.compose(toMain())
                .subscribe(map -> {
                            Log.e("TAGGG", "map1212="+new Gson().toJson(map));
                            listener.bindSuccess(map);
                        },
                    throwable -> {
                        Log.e("TAGGG", "error="+throwable.getMessage());
                        Log.e("TAGGG", "error="+throwable.getCause());
                        listener.bindFailed("物料绑定失败:"+getErrorMessage(throwable));
                    });
        compositeDisposable.add(disposable);
    }
    /**
     * 发货单解除绑定物料
     * */
    public void unBindMaterial(ScanDeliveryMaterialBean materialBean, BindMaterialView listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.unBindMaterial(materialBean)
                .compose(toMain())
                .subscribe(responseBody  -> listener.deleteSuccess(),
                        throwable -> listener.deleteFailed(getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }

    /**
     * 同步一个物料
     * */
    public void syncBarcode(String barCode, SyncCodeListener listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.syncBarcode(barCode)
                .compose(toMain())
                .subscribe(responseBody  -> listener.syncSuccess(),
                throwable -> listener.syncFail("物料同步失败："+getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }

    /**
     * 发货单出货物料表
     * */
    public void getMaterialLists(String billNo, boolean hasPallet, TPresenter<DeliveryBean> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Flowable<DeliveryBean> flowable = null;
        if (hasPallet){
            flowable = apiService.getMaterialLists(billNo);
        }else {
            flowable = apiService.getMaterialListsNoPallet(billNo);
        }
        Disposable disposable = flowable.compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在加载"))
                .subscribe(deliveryBean  -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(deliveryBean);
                },throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    /**
     * 无托盘发货扫描
     * 保存编辑的数量
     * */
    private boolean isSaveBind = false;
    public void saveBindNum(String deliveryNo, List<NoPalletDetails> datas, TPresenter<DeliveryBean> presenter){
        if (isSaveBind){
            return;
        }
        isSaveBind = true;
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);

        Disposable disposable = apiService.saveNpNum(new NpBindSaveBean(datas))
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在保存"))
                .doOnNext(responseBody -> {
//                    presenter.dismissDialog();
                })
                .flatMap((Function<ResponseBody, Flowable<DeliveryBean>>) responseBody ->
                        apiService.getMaterialListsNoPallet(deliveryNo))
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("重新读取订单"))
                .subscribe(deliveryBean  -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(deliveryBean);
                    ToastUtils.showToast(App.getInstance().getApplicationContext(), "成功");
                    isSaveBind = false;
                },throwable -> {
                    String message = getErrorMessage(throwable);
                    Log.e("TAGGG", "error="+message);
                    presenter.dismissDialog();
                    presenter.getFailed("失败："+message);
                    isSaveBind = false;
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 修改发货单数据提交
     * */
    public void submitMaterial(DeliveryBaseBean deliveryBean, CancelListener listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.sapDeliverySubmit(deliveryBean)
                .compose(toMain())
                .doOnSubscribe(subscription -> listener.showDialog("正在请求"))//
                .subscribe(responseBody -> {
                    listener.dismissDialog();
                    listener.cancelSuccess("出库完成");
                }, throwable -> {
                    listener.dismissDialog();
                    listener.cancelSuccess("出库失败:"+getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 修改发货单数据提交
     * */
    public void unBindDelivery(int billID, UnBindDeliveryListener listener){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.unBindDelivery(billID)
                .compose(toMain())
                .doOnSubscribe(subscription -> listener.showDialog("正在解绑"))
                .subscribe(responseBody -> {
                    listener.dismissDialog();
                    listener.unBindSuccess();
                },throwable -> {
                    Log.e("TAGGG", "fail thread="+Thread.currentThread());
                    listener.dismissDialog();
                    listener.unBindFail(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 获取一个发货单的托盘号
     * */
    public void getTrayPallets(String billNo, TPresenter<List<String>> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getPallets(billNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在请求"))//
                .subscribe(responseBody -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(responseBody);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    /**
     * 根据托盘id
     * 发货单号
     * 物料号绑定一个物料条码
     *TPresenter<BindResultBean>
     * */
    public void bindMaterialNo(Map<String, Object> map,  TPresenter<BindResultBean> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.bindingMaterial(map)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在请求"))
                .subscribe(responseBody -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(responseBody);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void bindMaterialNoPallet(Map<String, Object> map, MaterialBindListener presenter){

        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.bindingMaterialNoPallet(map)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在请求"))//
                .subscribe(responseBody -> {
                    presenter.dismissDialog();
                    presenter.bindMaterialNoPalletSuccess(responseBody);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.bindMaterialNoPalletFail(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void exportMaterialSn(String materialCode, String materialSn, TPresenter<ResponseBody> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.saveSerialNo(materialCode, materialSn)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(response -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(response);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void backUnBind(String serialNo, TPresenter<ResponseBody> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.backUnbind(serialNo)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(response -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(response);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void getApkVersion(boolean proActive, TPresenter<UpdateBean> presenter){
        String appID = BuildConfig.APPLICATION_ID;
        ApiService api = getRetrofit("http://app.elevatorstar.com/").create(ApiService.class);
        Disposable disposable = api.getVersionInfo(appID)
            .compose(toMain())
            .doOnSubscribe(subscription -> {

            })
            .subscribe(updateBean -> {
                presenter.dismissDialog();
                updateBean.setProActive(proActive);
                presenter.getSuccess(updateBean);
            }, throwable -> {
                presenter.dismissDialog();
                presenter.getFailed(getErrorMessage(throwable));
            });
        compositeDisposable.add(disposable);
    }

    public void getProductManifest(Request request, TPresenter<List<ProductItem>> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getProductList(request)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .map(Result::getRecords)
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void getAllLines(String filter, TPresenter<List<ProductLine>> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getProductLines(filter)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void getProductInfoBySn(String snText, TPresenter<ProductSnInfo> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getProductInfoBySn(snText)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    public void getProductInfoByOrder(String snText, TPresenter<ProductOrderInfo> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getProductInfoByOrder(snText)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(response -> {
                    presenter.dismissDialog();
                    String result = response.string();
                    ProductOrderInfo info;
                    try {
                        info = new Gson().fromJson(result, ProductOrderInfo.class);
                        presenter.getSuccess(info);
                    }catch (Exception e){
                        e.printStackTrace();
                        presenter.getFailed("请求失败");
                    }
                }, throwable -> {
                    String error = getErrorMessage(throwable);
                    presenter.dismissDialog();
                    presenter.getFailed(error);
                });
        compositeDisposable.add(disposable);
    }

    public void getProductStatue(String orderNo, TPresenter<List<ProductStatue>> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getStationNo(orderNo, false)
                .compose(toMain())
                .subscribe(presenter::getSuccess,
                throwable -> presenter.getFailed(getErrorMessage(throwable)));
        compositeDisposable.add(disposable);
    }
    public void getStationCount(int orderNo, int stationId, TPresenter<Request> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getStationCount(orderNo, stationId)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
    public void getScannedNum(int orderNo, int stationId, TPresenter<Integer> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.getScanNum(orderNo, stationId)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog(""))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void submitAssembleInfo(AssembleSubmitBean bean, TPresenter<ResponseBody> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.postSn(bean)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在提交"))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }

    public void deleteAssembleSn(AssembleDeleteBean bean, TPresenter<ResponseBody> presenter){
        if (apiService == null) apiService = ApiManager.getService(ApiService.class);
        Disposable disposable = apiService.deleteSn(bean)
                .compose(toMain())
                .doOnSubscribe(subscription -> presenter.showDialog("正在删除sn信息"))
                .subscribe(records -> {
                    presenter.dismissDialog();
                    presenter.getSuccess(records);
                }, throwable -> {
                    presenter.dismissDialog();
                    presenter.getFailed(getErrorMessage(throwable));
                });
        compositeDisposable.add(disposable);
    }
}