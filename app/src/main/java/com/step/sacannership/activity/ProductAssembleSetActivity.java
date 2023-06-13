package com.step.sacannership.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.ProductHeader;
import com.step.sacannership.model.bean.ProductLine;
import com.step.sacannership.model.bean.ProductOrderInfo;
import com.step.sacannership.model.bean.ProductSnInfo;
import com.step.sacannership.model.bean.ProductStatue;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class ProductAssembleSetActivity extends BaseActivity {

    @BindView(R.id.topBar)
    QMUITopBarLayout topBar;
    @BindView(R.id.loading)
    QMUILoadingView loadingView;
    @BindView(R.id.snValue)
    EditText editSnValue;
    @BindView(R.id.noValue)
    EditText editNoValue;
    @BindView(R.id.lineValue)
    TextView lineValue;
    @BindView(R.id.portValue)
    TextView portValue;
    @BindView(R.id.ckSMT)
    CheckBox ckSMT;
    @BindView(R.id.factoryInfo)
    TextView factoryInfo;
    @BindView(R.id.partInfo)
    TextView partInfo;
    @BindView(R.id.orderNo)
    TextView orderNoTV;
    @BindView(R.id.materialNo)
    TextView materialNo;
    @BindView(R.id.orderType)
    TextView orderType;
    @BindView(R.id.numValue)
    TextView numValue;
    @BindView(R.id.empty_view)
    QMUIEmptyView emptyView;

    @BindView(R.id.imgChooseLine)
    ImageView imgChooseLine;
    @BindView(R.id.imgChooseport)
    ImageView imgChoosePort;
    @BindView(R.id.tv_sure)
    TextView tvSure;

    @Override
    protected int contentView() {
        return R.layout.assemble_set_view;
    }

    @Override
    protected void initView() {

        topBar.setTitle("产品组装扫描设置");
        topBar.addLeftBackImageButton().setOnClickListener(v -> onBackPressed());

        tModel = new TModel();
        getProductLine();

        String orderStr = getIntent().getStringExtra("orderNo");
        if (!TextUtils.isEmpty(orderStr)){
            editNoValue.setText(orderStr);
            getProductInfoByOrder(orderStr);
            getProductStatue(orderStr);
        }

        editSnValue.setOnEditorActionListener((v, actionId, event) -> {
            if (!isTwice()){
                String snStr = editSnValue.getText().toString().trim();
                if (TextUtils.isEmpty(snStr)){
                    requestFocus(editNoValue);
                }else {
                    getProductInfoBySn();
                }
            }
            QMUIKeyboardHelper.hideKeyboard(v);
            return true;
        });
        editNoValue.setOnEditorActionListener((v, actionId, event) -> {
            if (!isTwice()){
                String orderNo = editNoValue.getText().toString().trim();
                getProductInfoByOrder(orderNo);
                getProductStatue(orderNo);
            }
            QMUIKeyboardHelper.hideKeyboard(v);
            return true;
        });

        imgChooseLine.setOnClickListener(v -> {
            if (lines.isEmpty()){
                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("没有相关信息")
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
                return;
            }
            String[] items = new String[lines.size()];
            for (int i = 0; i < lines.size(); i ++){
                ProductLine line = lines.get(i);

                items[i] = line.getProductionLineName() + "-" + line.getProductionLineNo();
            }
            QMUIDialog.CheckableDialogBuilder builder =
            new QMUIDialog.CheckableDialogBuilder(this);
            builder.setCheckedIndex(0)
                    .setTitle("选择生产线别")
                    .addItems(items, (dialog, which) -> {})
                    .addAction("确定", (dialog, index) -> {
                        int checkIndex = builder.getCheckedIndex();
                        ProductLine line = lines.get(checkIndex);
                        lineValue.setText(line.getProductionLineName() + "-" + line.getProductionLineNo());
                        lineValue.setTag(line.getPkid());
                        dialog.dismiss();
                    })
                    .create().show();
        });
        imgChoosePort.setOnClickListener(v -> {
            if (stations.isEmpty()){
                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("没有相关信息")
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
                return;
            }
            String[] items = new String[stations.size()];
            for (int i = 0; i < stations.size(); i ++){
                ProductStatue statue = stations.get(i);
                items[i] = statue.getStationName() + "-" + statue.getStationNo();
            }
            QMUIDialog.CheckableDialogBuilder builder =
                    new QMUIDialog.CheckableDialogBuilder(this);
            builder.setCheckedIndex(0)
                    .setTitle("选择站点编号")
                    .addItems(items, (dialog, which) -> {})
                    .addAction("确定", (dialog, index) -> {
                        int checkIndex = builder.getCheckedIndex();
                        ProductStatue line = stations.get(checkIndex);
                        portValue.setText(line.getStationName() + "-" + line.getStationNo());
                        portValue.setTag(line.getPkid());
                        dialog.dismiss();
                    })
                    .create().show();
        });

        tvSure.setOnClickListener(v -> {
            String lineStr = lineValue.getText().toString();
            if (TextUtils.isEmpty(lineStr) || lineValue.getTag() == null){
                imgChooseLine.callOnClick();
                return;
            }
            String portStr = portValue.getText().toString();
            if (TextUtils.isEmpty(portStr) || portValue.getTag() == null){
                imgChoosePort.callOnClick();
                return;
            }
            if (orderInfo != null){
                ProductHeader header = new ProductHeader();
                header.setHeadrId(orderInfo.getPkId());
                header.setBatchStr(orderInfo.getBatchNo());
                header.setProductOrder(orderInfo.getOrders());
                header.setMaterialNo(orderInfo.getMaterialNo());
                header.setMaterialDesc(orderInfo.getMaterialDescription());
                header.setCount(orderInfo.getOrderNum());
                header.setStatueId((Integer) portValue.getTag());
                header.setLineId((Integer) lineValue.getTag());

                int index = lineStr.indexOf("-");
                header.setLineNo(lineStr.substring(0, index));
                index = portStr.indexOf("-");
                header.setStationPort(portStr.substring(0, index));
                header.setStationNo(portStr.substring(index + 1));
                Intent intent = new Intent();
                Log.e("TAGGG", "header === " + new Gson().toJson(header));
                intent.putExtra("header", header);
                setResult(10, intent);
                finish();
            }
        });
    }

    private ProductOrderInfo orderInfo;
    /**根据订单号获取订单信息*/
    private void getProductInfoByOrder(String orderNo){

        if (TextUtils.isEmpty(orderNo)){
            ToastUtils.showToast(this, "sn不能为空");
            requestFocus(editNoValue);
            return;
        }
        tModel.getProductInfoByOrder(orderNo, new TPresenter<ProductOrderInfo>() {
            @Override
            public void getSuccess(ProductOrderInfo info) {
                editNoValue.setText(info.getOrders());
                factoryInfo.setText(info.getFactoryName());
                orderNoTV.setText("生产订单:"+info.getOrders());
                materialNo.setText("产品料号:"+info.getMaterialNo());
                orderType.setText(info.getOrderTypeName());
                numValue.setText(""+info.getOrderNum());
                orderInfo = info;
                createMediaPlayer(R.raw.success);
            }

            @Override
            public void getFailed(String message) {
                requestFocus(editNoValue);
                createMediaPlayer(R.raw.failed);
                Log.e("TAGGG", "无法获取数据 === " + message);
            }

            @Override
            public void showDialog(String message) {
                loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void dismissDialog() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }
    /**根据sn获取订单信息*/
    private void getProductInfoBySn(){
        String snStr = editSnValue.getText().toString().trim();
        Log.e("TAGGG", snStr);
        if (TextUtils.isEmpty(snStr)){
            ToastUtils.showToast(this, "sn不能为空");
            requestFocus(editSnValue);
            return;
        }
        tModel.getProductInfoBySn(snStr, new TPresenter<ProductSnInfo>() {
            @Override
            public void getSuccess(ProductSnInfo info) {
                String orderNo = info.getOrders();
                new Handler(Looper.getMainLooper()).postDelayed(() -> getProductInfoByOrder(orderNo), 1000);
                getProductStatue(orderNo);

                createMediaPlayer(R.raw.success);
            }

            @Override
            public void getFailed(String message) {
                new QMUIDialog.MessageDialogBuilder(ProductAssembleSetActivity.this)
                        .setMessage("无法获取订单信息:" + message)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
                requestFocus(editSnValue);
                createMediaPlayer(R.raw.failed);
            }

            @Override
            public void showDialog(String message) {
                loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void dismissDialog() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }
    /**获取产线信息选项表*/
    private final List<ProductLine> lines = new ArrayList<>();
    private void getProductLine(){
        tModel.getAllLines("BPQ", new TPresenter<List<ProductLine>>() {
            @Override
            public void getSuccess(List<ProductLine> productLines) {
                lines.clear();
                lines.addAll(productLines);
            }

            @Override
            public void getFailed(String message) {
                new QMUIDialog.MessageDialogBuilder(ProductAssembleSetActivity.this)
                        .setMessage("无法获取产线信息:" + message+"!重新获取?")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addAction("退出", (dialog, index) -> {
                            finish();
                            dialog.dismiss();
                        })
                        .addAction("确定", (dialog, index) -> {
                            getProductLine();
                            dialog.dismiss();
                        })
                        .create().show();
                requestFocus(editNoValue);
                createMediaPlayer(R.raw.failed);
            }

            @Override
            public void showDialog(String message) {
                loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void dismissDialog() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }
    /**获取站点编号*/
    private final List<ProductStatue> stations = new ArrayList<>();
    private void getProductStatue(String orderNo){
        tModel.getProductStatue(orderNo, new TPresenter<List<ProductStatue>>() {
            @Override
            public void getSuccess(List<ProductStatue> productStatues) {
                stations.clear();
                stations.addAll(productStatues);
            }

            @Override
            public void getFailed(String message) {
                new QMUIDialog.MessageDialogBuilder(ProductAssembleSetActivity.this)
                        .setMessage("无法获取站点信息:" + message+"!重新获取?")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addAction("退出", (dialog, index) -> {
                            finish();
                            dialog.dismiss();
                        })
                        .addAction("确定", (dialog, index) -> {
                            getProductStatue(orderNo);
                            dialog.dismiss();
                        })
                        .create().show();
                createMediaPlayer(R.raw.failed);
            }

            @Override
            public void showDialog(String message) {

            }

            @Override
            public void dismissDialog() {

            }
        });
    }
}