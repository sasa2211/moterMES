package com.step.sacannership.activity;

import android.content.Intent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.listener.BindListener;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.model.bean.UnBind;
import com.step.sacannership.tools.SPTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanTrayActivity extends BaseActivity implements TrayInfoListener, BindListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.delivery_no)
    EditText editDelivery;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty)
    QMUIEmptyView empty;
    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;
    private List<TrayDeliveryBean> originalDatas;

    @Override
    protected int contentView() {
        return R.layout.scan_tray_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);
        setOnclick(trayNo);
        Intent intent = getIntent();
        String palletNo = intent.getStringExtra("palletID");
        trayNo.setText(palletNo);
        if (palletNo != null && !TextUtils.isEmpty(palletNo)){
            getTrayData();
        }
        trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
//                /**
//                 * 获取托盘数据
//                 * */
//                getTrayData();
//            }
            if (!isTwice()){
                /**
                 * 获取托盘数据
                 * */
                getTrayData();
            }
            QMUIKeyboardHelper.hideKeyboard(trayNo);
            return true;
        });
        editDelivery.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP){
//                /**
//                 * 检索托盘中的数据
//                 * */
//                List<TrayDeliveryBean> searchDatas = getDataBySearch(editDelivery.getText().toString().trim());
//                this.datas.clear();
//                this.datas.addAll(searchDatas);
//                adapter.notifyDataSetChanged();
//                if (datas.isEmpty()){
//                    empty.show("暂无数据", "");
//                }else {
//                    empty.hide();
//                }
//            }
            if (!isTwice()){
                /**
                 * 检索托盘中的数据
                 * */
                List<TrayDeliveryBean> searchDatas = getDataBySearch(editDelivery.getText().toString().trim());
                this.datas.clear();
                this.datas.addAll(searchDatas);
                adapter.notifyDataSetChanged();
                if (datas.isEmpty()){
                    empty.show("暂无数据", "");
                }else {
                    empty.hide();
                }
            }
            editDelivery.setText("");
            QMUIKeyboardHelper.hideKeyboard(editDelivery);
            return true;
        });
        empty.setLoadingShowing(false);
        empty.show( "暂无数据", "");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);

        datas = new ArrayList<>();
        originalDatas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, 2);
        recycler.setAdapter(adapter);
        adapter.setListener(new DeliveryAdapter.EditNumListener()   {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {

            }

            @Override
            public void bindMaterialListener(int groupIndex, String billNo) {
                TrayDeliveryBean deliveryBean = datas.get(groupIndex);
                int id = deliveryBean.getDeliveryBillId();
                String [] nos = {deliveryBean.getDeliveryBillNO()};
                deleteAlert(nos, id);
            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int id) {
                Log.e("TAGGG", "查看物料条码");
            }
        });
    }

    public void getTrayData(){
        String trayno = trayNo.getText().toString().trim();
        if (!TextUtils.isEmpty(trayno)){
            if (tModel == null) tModel = new TModel();
            String billNO = trayNo.getText().toString().trim();
            tModel.getTrayInfos(billNO, this);
        }
    }

    public void deleteAlert(String[] deleteNos, Integer ...ids){
        StringBuilder sb = new StringBuilder("确定删除发运单：");
        for (String deliveryNo : deleteNos){
            sb.append("\n"+deliveryNo);
        }
        new QMUIDialog.MessageDialogBuilder(this)
                .setMessage(sb.toString())
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    UnBind unBind = new UnBind();
                    unBind.setPalletId(infoBean.getPalletId());
                    unBind.setDeliveryBillIds(Arrays.asList(ids));
                    tModel.deleteBindDelivery(unBind, ScanTrayActivity.this);
                    dialog.dismiss();
                }).create().show();
    }

    @OnClick(R.id.btn_delete)
    public void onViewClicked() {
        if (infoBean == null){
            return;
        }
        List<TrayDeliveryBean> deliveryBeanList = infoBean.getDeliveryBills();
        String[] arr = new String[deliveryBeanList.size()];
        Integer[] ids = new Integer[deliveryBeanList.size()];
        for (int i = 0; i < deliveryBeanList.size(); i ++){
            TrayDeliveryBean deliveryBean = deliveryBeanList.get(i);
            arr[i]= deliveryBean.getDeliveryBillNO();
            ids[i]= deliveryBean.getDeliveryBillId();
        }
        /**解除绑定*/
        deleteAlert(arr, ids);

    }
    private TrayInfoBean infoBean;

    @Override
    public void getTraySuccess(TrayInfoBean infoBean) {
        this.infoBean = infoBean;
        datas.clear();
        if (infoBean != null){
            List<TrayDeliveryBean> deliverys = infoBean.getDeliveryBills();
            if (deliverys != null){
                datas.addAll(deliverys);
                originalDatas.addAll(deliverys);
            }
        }
        if (datas.size() == 0){
            empty.setLoadingShowing(false);
            empty.show("暂无数据", "");
            empty.setVisibility(View.VISIBLE);
        }else {
            empty.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getTrayFailed(String message) {
        empty.show(false, "加载失败", message, "重新加载", view -> getTrayData());
    }

    @Override
    public void showDialog(String message) {
        empty.setLoadingShowing(true);
        empty.show("正在加载", "");
    }

    @Override
    public void dismissDialog() {
        empty.setLoadingShowing(false);
        empty.setVisibility(View.GONE);
    }

    @Override
    public void bindMaterialSuccess(BindResultBean result) {

    }

    @Override
    public void bindMaterialFail(String message) {

    }

    @Override
    public void bindSuccess(String material, boolean isExit) {

    }

    @Override
    public void bindFailed(String message) {

    }

    @Override
    public void deleteSuccess() {
        /**
         * 获取托盘数据
         * */
        getTrayData();
    }

    @Override
    public void deleteFailed(String message) {
        SPTool.showToast(this, message);
    }

    private List<TrayDeliveryBean> getDataBySearch(String no){
        if (TextUtils.isEmpty(no)){
            return originalDatas;
        }
        List<TrayDeliveryBean> datas = new ArrayList<>();
        for (TrayDeliveryBean deliveryBean : originalDatas){
            if (deliveryBean.getDeliveryBillNO().startsWith(no)){
                datas.add(deliveryBean);
            }
        }
        return datas;
    }
}
