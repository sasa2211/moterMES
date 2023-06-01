package com.step.sacannership.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.listener.BindListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.MaterialNum;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class BindTray extends BaseActivity implements TPresenter<DeliveryBean>, TrayInfoListener, BindListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.delivery_no)
    EditText deliveryNo;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty)
    QMUIEmptyView empty;
    @BindView(R.id.tv_number)
    TextView tvNumber;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.bind_tray_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);
        setEdit();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);

        empty.show("", "无数据");

        datas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, 1);
        recycler.setAdapter(adapter);
        adapter.setListener(new DeliveryAdapter.EditNumListener() {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {
                QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(BindTray.this);
                builder.setPlaceholder("编辑");
                builder.setTitle("编辑物料数量");
                builder.addAction("取消", (dialog, index) -> dialog.dismiss());
                builder.addAction("确定", (dialog, index) -> {
                    Log.e("TAGGG", "修改数量:"+deliveryNo);
                    Log.e("TAGGG", "修改数量:"+materialNo);
                    int [] nos = adapter.getClickPosition(deliveryNo, materialNo, rowIndex);
                    if (nos[0]*nos[1] == -1){
                        ToastUtils.showToast(BindTray.this, "修改位置不明确");
                        return;
                    }
                    Log.e("TAGGG", "修改数量:"+new Gson().toJson(nos));
                    TrayDeliveryBean beanGroup = datas.get(nos[0]);
                    DeliveryBillPalletDetailsBean childBean = beanGroup.getChildAt(nos[1]);
                    double quality = childBean.getQuantity();
                    try {
                        quality = Double.parseDouble(builder.getEditText().getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast(BindTray.this, "格式化数据出现异常");
                    }
                    childBean.setQuantity(quality);
                    adapter.notifyDataSetChanged();

                    QMUIKeyboardHelper.hideKeyboard(builder.getEditText());
                    dialog.dismiss();
                });
                builder.create().show();
            }

            @Override
            public void bindMaterialListener(int index, String billNo) {

            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int billID) {
                Intent intent = new Intent(BindTray.this, BindMaterial.class);
                intent.putExtra("billNo", billNo);
                intent.putExtra("billID", billID);
//                intent.putExtra("index", index);
                intent.putExtra("palletID", infoBean.getPalletId());
                intent.putExtra("materialNO", materialNo);
                intent.putExtra("rowIndex", rowIndex);
                startActivityForResult(intent, 10);
            }
        });
    }

    private void setEdit() {
        setOnclick(trayNo);
        setOnclick(deliveryNo);

        trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                getTrayInfo();
            }
            QMUIKeyboardHelper.hideKeyboard(deliveryNo);
            return true;
        });

        deliveryNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                if (infoBean == null){
                    requestFocus(trayNo);
                    new QMUIDialog.MessageDialogBuilder(this)
                            .setMessage("无法获取托盘信息，请重新扫描托盘号！如果托盘号为键盘输入，请不要忘记按下回车键ENT")
                            .addAction("确定", (dialog, index) -> dialog.dismiss())
                            .create().show();
                    return true;
                }
                getData();
            }
            QMUIKeyboardHelper.hideKeyboard(deliveryNo);
            return true;
        });
    }
    /**
     * 获取托盘信息
     * */

    public void getTrayInfo(){
        if (tModel == null) tModel = new TModel();
        String billNO = trayNo.getText().toString().trim();
        tModel.getTrayInfos(billNO, this);
    }
    /**
     * 获取扫描发货单内容
     * */
    public void getData() {
        if (tModel == null) tModel = new TModel();
        String billNO = deliveryNo.getText().toString().trim();
        tModel.getDeliveryList(billNO, this);
    }

    DeliveryBean deliveryBean;
    @Override
    public void getSuccess(DeliveryBean delivery) {
        this.deliveryBean = delivery;
        if (deliveryBean == null) return;

        boolean isBind = false;
        for (TrayDeliveryBean trayDeliveryBean : datas){
            if (trayDeliveryBean.getDeliveryBillNO().equals(deliveryBean.getBillNo())){
                isBind = true;
                break;
            }
        }
        if (!isBind){
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
                palletDetailsBean.setQuantity(deliveryDetailBean.getQuantity());
                palletDetailsBean.setRowIndex(deliveryDetailBean.getRowIndex());
                palletDetailsBean.setOutQuantity(deliveryDetailBean.getOutQuantity());
                palletDetailsBean.setBindQuantity(deliveryDetailBean.getBindQuantity());
                palletDetailsBean.setQualifiedQuantity(deliveryDetailBean.getQualifiedQuantity());
                billPalletDetailsBeans.add(palletDetailsBean);
            }
            trayDeliveryBean.setDeliveryBillPalletDetails(billPalletDetailsBeans);
            datas.add(trayDeliveryBean);
        }
        adapter.notifyDataSetChanged();
        if (datas.size() == 0){
            empty.setLoadingShowing(false);
            empty.show("暂无数据", "");
        }else {
            empty.hide();
        }

        adapter.notifyDataSetChanged();
        requestFocus(deliveryNo);
    }

    TrayInfoBean infoBean;
    @Override
    public void getTraySuccess(TrayInfoBean infoBean) {
        this.infoBean = infoBean;
        datas.clear();
        adapter.notifyDataSetChanged();
        if (infoBean != null) {
            List<TrayDeliveryBean> deliverys = infoBean.getDeliveryBills();
            if (deliverys != null) {
                datas.addAll(deliverys);
                double materialCount = 0;
                for (TrayDeliveryBean deliveryBean : deliverys){
                    for (DeliveryBillPalletDetailsBean detailsBean : deliveryBean.getDeliveryBillPalletDetails()){
                        materialCount += detailsBean.getSnQuantity();
                    }
                }
                tvNumber.setText("物料总数："+materialCount);
            }
        }
//        List<TrayDeliveryBean> trayDatas = infoBean.getDeliveryBills();
//        if (trayDatas != null){
//            this.datas.addAll(trayDatas);
//        }

        adapter.notifyDataSetChanged();
        if (datas.size() == 0){
            empty.setLoadingShowing(false);
            empty.show("暂无发货单数据", "");
        }else {
            empty.hide();
        }

        requestFocus(deliveryNo);
    }

    @Override
    public void getTrayFailed(String message) {
        empty.show(false, "", "托盘信息加载失败:"+message, "点击重新加载", view -> getTrayInfo());
    }

    @Override
    public void getFailed(String message) {
        empty.show(false, "", "发货单加载失败："+message, "点击重新加载", view -> getData());
    }

    @Override
    public void showDialog(String message) {
        empty.setLoadingShowing(true);
        empty.show(message, null);
    }

    @Override
    public void dismissDialog() {
        empty.hide();
    }


    @OnClick({R.id.btn_save, R.id.tray, R.id.delivery})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                if (infoBean == null) return;
                infoBean.setDeliveryBills(datas);
                tModel.bindTray(infoBean, false, this);
                break;
            case R.id.tray:
                scane(trayNo);
                break;
            case R.id.delivery:
                scane(deliveryNo);
                break;
        }
    }

    @Override
    public void bindMaterialSuccess(BindResultBean result) {

    }

    @Override
    public void bindMaterialFail(String message) {

    }

    @Override
    public void bindSuccess(String material, boolean isExit) {
        SPTool.showToast(this, "绑定成功");
        getTrayInfo();
    }

    @Override
    public void bindFailed(String message) {
        SPTool.showToast(this, message);
        getTrayInfo();
    }

    @Override
    public void deleteSuccess() {

    }

    @Override
    public void deleteFailed(String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 10){
            if (data != null){
                MaterialNum materialNum = (MaterialNum) data.getSerializableExtra("materialInfos");
                Log.e("TAGGG", "json="+new Gson().toJson(materialNum));
                try {
                    boolean findExist = false;
                    for (TrayDeliveryBean deliveryBean : datas){
                        Log.e("TAGGG", "billNo="+deliveryBean.getDeliveryBillNO());
                        if (deliveryBean.getDeliveryBillNO().equals(materialNum.getBillNo())){
                            for (DeliveryBillPalletDetailsBean detailsBean : deliveryBean.getDeliveryBillPalletDetails()){
                                Log.e("TAGGG", "materialNo="+detailsBean.getMaterialNo());
                                Log.e("TAGGG", "rowIndex="+detailsBean.getRowIndex());
                                if (detailsBean.getMaterialNo().equals(materialNum.getMaterialNo()) && detailsBean.getRowIndex().equals(materialNum.getRowIndex())){
                                    detailsBean.setQuantity(materialNum.getMaterialNum());
                                    findExist = true;
                                    break;
                                }
                            }
                        }

                        if (findExist){
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}