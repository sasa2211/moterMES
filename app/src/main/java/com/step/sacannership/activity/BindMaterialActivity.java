package com.step.sacannership.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.fragment.ExportSnDialog;
import com.step.sacannership.listener.MaterialBindListener;
import com.step.sacannership.listener.SyncCodeListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.MaterialNum;
import com.step.sacannership.model.bean.NoPalletDetails;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class BindMaterialActivity extends BaseActivity implements TPresenter<DeliveryBean>, MaterialBindListener, SyncCodeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.material_no)
    EditText materialNo;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.unpallet_bind_material_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);

        trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                getTrayInfo();
            }
            QMUIKeyboardHelper.hideKeyboard(trayNo);
            return true;
        });
        materialNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                bindMaterial();
            }
            QMUIKeyboardHelper.hideKeyboard(trayNo);
            return true;
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);

        datas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, 1);
        recycler.setAdapter(adapter);
        adapter.setListener(new DeliveryAdapter.EditNumListener() {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {
                QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(BindMaterialActivity.this);
                builder.setPlaceholder("编辑");
                builder.setTitle("编辑物料数量");
                builder.addAction("取消", (dialog, index) -> dialog.dismiss());
                builder.addAction("确定", (dialog, index) -> {
                    int [] nos = adapter.getClickPosition(deliveryNo, materialNo, rowIndex);
                    if (nos[0]*nos[1] == -1){
                        ToastUtils.showToast(BindMaterialActivity.this, "修改位置不明确");
                        return;
                    }
                    TrayDeliveryBean beanGroup = datas.get(nos[0]);
                    DeliveryBillPalletDetailsBean childBean = beanGroup.getChildAt(nos[1]);
                    double quality = childBean.getQuantity();
                    try {
                        quality = Double.parseDouble(builder.getEditText().getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast(BindMaterialActivity.this, "格式化数据出现异常");
                    }
                    double diff = childBean.getOutQuantity() - childBean.getBindQuantity();
//                    if (quality < childBean.getSnQuantity()){
//                        ToastUtils.showToast(BindMaterialActivity.this, "修改的数量不能比实际sn数量小");
//                    }else {
//                        childBean.setQuantity(quality);
//                        adapter.notifyDataSetChanged();
//                    }
                    childBean.setQuantity(quality);
                    adapter.notifyDataSetChanged();
                    QMUIKeyboardHelper.hideKeyboard(builder.getEditText());
                    dialog.dismiss();
                    changeNum = true;
                });
                builder.create().show();
            }

            @Override
            public void bindMaterialListener(int index, String billNo) {

            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int billID) {
                Intent intent = new Intent(BindMaterialActivity.this, BindMaterial.class);
                intent.putExtra("billNo", billNo);
                intent.putExtra("billID", billID);
                intent.putExtra("hasPallet", false);
                intent.putExtra("materialNO", materialNo);
                intent.putExtra("rowIndex", rowIndex);
                startActivityForResult(intent, 10);
                changeNum = true;
            }
        });
    }

    private long clickTime = System.currentTimeMillis();
    @OnClick(R.id.tv_save_bind)
    public void onViewClick(){
        if (System.currentTimeMillis() - clickTime < 1000){
            ToastUtils.showToast(this, "请不要连续点击");
            return;
        }
        clickTime = System.currentTimeMillis();
        saveInfo();
    }

    private void saveInfo(){

        if (deliveryBean == null){
            return;
        }
        List<NoPalletDetails> sNPDetails = deliveryBean.getSdDeliveryBillNoPalletDetails();

        for (TrayDeliveryBean detailBean : datas){
            for (DeliveryBillPalletDetailsBean detailsBean : detailBean.getDeliveryBillPalletDetails()){
                boolean findDelivery = false;
                for (NoPalletDetails npDetail : sNPDetails){
                    if (detailsBean.getMaterialNo().equals(npDetail.getMaterialNO()) && detailsBean.getRowIndex().equals(npDetail.getRowIndex())){
                        findDelivery = true;
                        npDetail.setQuantity(detailsBean.getQuantity());
                    }
                }
                if (!findDelivery){
                    NoPalletDetails npDetail = new NoPalletDetails(detailsBean.getMasterID(), detailsBean.getMaterialNo(), detailsBean.getQuantity(), detailsBean.getRowIndex());
                    sNPDetails.add(npDetail);
                }
            }
        }
        if (tModel == null){
            tModel = new TModel();
        }

        tModel.saveBindNum(deliveryBean.getBillNo(), sNPDetails, this);
        changeNum = false;
    }

    private void bindMaterial(){
        if (tModel == null) tModel = new TModel();
        if (deliveryBean == null){
            ToastUtils.showToast(this, "请先扫描发货单编号获取发货单信息");
            return;
        }
        Map<String, Object> map= new HashMap<String, Object>();

        map.put("palletID", null);
        map.put("deliveryBillID", deliveryBean.getPkId());

        String materialNoText = materialNo.getText().toString().trim();
        if (TextUtils.isEmpty(materialNoText)){
            ToastUtils.showToast(this, "请扫描物料条码");
            return;
        }

        map.put("materialBarcode", materialNoText);
        tModel.bindMaterialNoPallet(map, this);
    }
    public void getTrayInfo(){
        if (tModel == null) tModel = new TModel();
        String billNO = trayNo.getText().toString().trim();
        tModel.getDeliveryListUnPallet(billNO, this);
    }
    DeliveryBean deliveryBean;
    @Override
    public void getSuccess(DeliveryBean delivery) {
        this.deliveryBean = delivery;
        if (deliveryBean == null) return;

        datas.clear();

//        boolean isBind = false;
//        for (TrayDeliveryBean trayDeliveryBean : datas){
//            if (trayDeliveryBean.getDeliveryBillNO().equals(deliveryBean.getBillNo())){
//                isBind = true;
//                break;
//            }
//        }
//        if (!isBind){
            TrayDeliveryBean trayDeliveryBean = new TrayDeliveryBean();
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
                billPalletDetailsBeans.add(palletDetailsBean);
            }
            trayDeliveryBean.setDeliveryBillPalletDetails(billPalletDetailsBeans);
            datas.add(trayDeliveryBean);
//        }

        adapter.notifyDataSetChanged();

        requestFocus(materialNo);
    }

    @Override
    public void getFailed(String message) {
        tvMessage.setText("失败："+message);
    }

    @Override
    public void showDialog(String message) {
        tvMessage.setText(message);
    }

    @Override
    public void dismissDialog() {
        tvMessage.setText("");
    }

    private boolean changeNum = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == 10){
            if (data != null){
                MaterialNum materialNum = (MaterialNum) data.getSerializableExtra("materialInfos");
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
//                                    detailsBean.setSnQuantity(materialNum.getMaterialNum());
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

    @Override
    public void onBackPressed() {
        if (changeNum){
            new QMUIDialog.MessageDialogBuilder(this)
                    .setMessage("系统检测到你修改了物料数量，请在退出前保存，否则数量将不会保存！")
                    .addAction("退出", (dialog, index) -> {
                        dialog.dismiss();
                        finish();
                    }).addAction("保存", (dialog, index) -> {
                        saveInfo();
                        dialog.dismiss();
                    }).create().show();
            return;
        }
        super.onBackPressed();
    }
    private QMUIDialog messageDialog = null;
    @Override
    public void bindMaterialNoPalletSuccess(BindResultBean map) {
        int resultCode = map.getResultCode();
        if (resultCode == 0){
            tvMessage.setText("绑定成功");
            materialNo.setText("");
            requestFocus(materialNo);

            String materialNo = map.getMaterialNo();
            int deliveryBillID = map.getDeliveryBillID();
            String rowIndex = map.getRowIndex();
            for (TrayDeliveryBean deliveryBean : datas){
                if (deliveryBean.getDeliveryBillId() == deliveryBillID){
                    List<DeliveryBillPalletDetailsBean> palletDetails = deliveryBean.getDeliveryBillPalletDetails();
                    for (DeliveryBillPalletDetailsBean detailsBean : palletDetails){
                        if (materialNo.equals(detailsBean.getMaterialNo()) && rowIndex.equals(detailsBean.getRowIndex())){
                            detailsBean.setQuantity(map.getNum());
                            break;
                        }
                    }
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }else {
            if (messageDialog != null){
                if (messageDialog.isShowing()){
                    messageDialog.dismiss();
                }
                messageDialog = null;
            }
            String errorMsg = map.getErrorMsg();
            messageDialog = new QMUIDialog.MessageDialogBuilder(this)
                    .setMessage(errorMsg)
                    .addAction("关闭", (dialog, index) -> {
                        materialNo.setText("");
                        requestFocus(materialNo);
                        dialog.dismiss();
                    })
                    .addAction("导入", (dialog, index) -> {
                        String materialCode = materialNo.getText().toString().trim();
                        if (TextUtils.isEmpty(materialCode)){
                            return;
                        }
                        ExportSnDialog exportSnDialog = new ExportSnDialog(BindMaterialActivity.this, materialCode);
                        exportSnDialog.create().show();
                        exportSnDialog.setListener(new ExportSnDialog.ImportListener() {
                            @Override
                            public void importSuccess() {
                                bindMaterial();
                                exportSnDialog.dismiss();
                                if (messageDialog != null && messageDialog.isShowing()){
                                    messageDialog.dismiss();
                                }
                            }

                            @Override
                            public void importFail() {
                                materialNo.setText("");
                                requestFocus(materialNo);
                                exportSnDialog.dismiss();
                                if (messageDialog != null && messageDialog.isShowing()){
                                    messageDialog.dismiss();
                                }
                            }
                        });
                    })
                    .addAction("更新", (dialog, index) -> {
                        if (tModel == null) tModel = new TModel();
                        String materialCode = materialNo.getText().toString().trim();
                        tModel.syncBarcode(materialCode, BindMaterialActivity.this);
                    }).create();
            messageDialog.show();
        }
    }

    @Override
    public void bindMaterialNoPalletFail(String errorMessage) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("绑定失败")
                .setMessage("提示："+errorMessage)
                .addAction("确定", (dialog, index) -> dialog.dismiss()).create().show();
        tvMessage.setText(errorMessage);
        requestFocus(materialNo);
    }

    @Override
    public void syncSuccess() {
        if (messageDialog != null && messageDialog.isShowing()){
            messageDialog.dismiss();
        }
        if (messageDialog != null && messageDialog.isShowing()){
            messageDialog.dismiss();
        }
        bindMaterial();
    }

    @Override
    public void syncFail(String message) {
        materialNo.setText("");
        requestFocus(materialNo);
        SPTool.showToast(this, message);
    }

    @Override
    public void getSnNumSuccess(Integer snNum) {

    }
}