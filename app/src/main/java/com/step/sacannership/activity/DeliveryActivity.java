package com.step.sacannership.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.databinding.DeliveryViewBinding;
import com.step.sacannership.listener.LogisticsListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.LogisticsListBean;
import com.step.sacannership.model.bean.Request;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class DeliveryActivity extends BaseTActivity<DeliveryViewBinding> implements TrayInfoListener, TPresenter<DeliveryBean>, LogisticsListener {
//
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.tray_no)
//    EditText trayNo;
//    @BindView(R.id.recycler)
//    RecyclerView recycler;
//    @BindView(R.id.empty)
//    QMUIEmptyView empty;
//    @BindView(R.id.tv_name)
//    TextView tnName;
//
//    @BindView(R.id.ln_logistic_no)
//    LinearLayout lnLogisticNo;
//    @BindView(R.id.ln_logisticType)
//    LinearLayout lnLogisticType;
//
//    @BindView(R.id.logistics_no)
//    EditText logisticsNoEdit;
//    @BindView(R.id.logistics_type)
//    EditText logisticsTypeEdit;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.delivery_view;
    }

    private boolean hasPallet;
    @Override
    protected void initView() {
        hasPallet = getIntent().getBooleanExtra("pallet", true);
        binding.topBar.setTitle(hasPallet ? "托盘" : "发运单号");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
        binding.trayNo.setHint(hasPallet ? "扫描托盘条码" :  "扫描发运单条码");

        binding.lnLogisticNo.setVisibility(hasPallet ? View.GONE :View.VISIBLE);
        binding.lnLogisticType.setVisibility(hasPallet ? View.GONE :View.VISIBLE);

//        initToolBar(toolbar);
        setOnclick(binding.trayNo);

        binding.trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
//                /**
//                 * 获取托盘数据
//                 * */
//                getTrayData();
//                trayNo.setText("");
//                requestFocus(logisticsTypeEdit);
//            }
            if (!isTwice()){
                /**
                 * 获取托盘数据
                 * */
                getTrayData();
                binding.trayNo.setText("");
                requestFocus(binding.logisticsType);
            }
            QMUIKeyboardHelper.hideKeyboard(binding.trayNo);
            return true;
        });
        binding.logisticsType.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
//                requestFocus(logisticsNoEdit);
//            }
            if (!isTwice()){
                requestFocus(binding.logisticsNo);
            }
            QMUIKeyboardHelper.hideKeyboard(binding.trayNo);
            return true;
        });
        binding.logisticsNo.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
//                String logistics = logisticsNoEdit.getText().toString()+",";
//                logisticsNoEdit.setText(logistics);
//                logisticsNoEdit.setSelection(logistics.length());
//            }
            if (!isTwice()){
                String logistics = binding.logisticsNo.getText().toString()+",";
                binding.logisticsNo.setText(logistics);
                binding.logisticsNo.setSelection(logistics.length());
            }
            QMUIKeyboardHelper.hideKeyboard(binding.trayNo);
            return true;
        });

        binding.empty.setLoadingShowing(false);
        binding.empty.show( "暂无数据", "");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        binding.recycler.addItemDecoration(decoration);

        datas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, 4);
        binding.recycler.setAdapter(adapter);

        binding.btnDelivery.setOnClickListener(v -> {
            if (tModel == null) tModel = new TModel();
            String logisticsNo = binding.logisticsNo.getText().toString().trim();
            String logisticsName = binding.logisticsType.getText().toString().trim();
            if (!hasPallet){
                if (TextUtils.isEmpty(logisticsName)){
                    ToastUtils.showToast(this, "选择物流公司");
                    return;
                }
                if (logisticsNo.endsWith(",")){
                    logisticsNo = logisticsNo.substring(0, logisticsNo.length()-1);
                }
            }

            if (hasPallet){
                if (infoBean == null) return;
                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("请核对托盘编号："+infoBean.getPalletNO())
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            tModel.deliveryTray(infoBean.getPalletNO(), DeliveryActivity.this);
                            dialog.dismiss();
                        }).create().show();
            }else {
                if (delivery == null) return;
                Request request = new Request();
                request.put("logisticsName", logisticsName);
                request.put("logisticsOrderNO", logisticsNo);
                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("请核对发运单编号："+delivery.getBillNo())
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            request.put("billNo", delivery.getBillNo());
                            tModel.deliveryTrayUnPallet(request, DeliveryActivity.this);
                            dialog.dismiss();
                        }).create().show();
            }
        });
        binding.logisticsImg.setOnClickListener(v -> {
            if (logisticsListBeans == null){
                if (tModel == null){
                    tModel = new TModel();
                }
                tModel.getLogisticsList(this);
            }else {
                getLogisticsSuccess(logisticsListBeans);
            }
        });
    }

    public void getTrayData(){
        String billNO = binding.trayNo.getText().toString().trim();
        if (!TextUtils.isEmpty(billNO)){
            if (tModel == null) tModel = new TModel();
            if (hasPallet){
                tModel.getTrayInfos(billNO, this);
            }else {
                tModel.getDeliveryListUnPallet(billNO, this);
            }
        }
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
            }
        }
        if (datas.size() == 0){
            binding.empty.setLoadingShowing(false);
            binding.empty.show("暂无数据", "");
            binding.empty.setVisibility(View.VISIBLE);
        }else {
            binding.empty.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getTrayFailed(String message) {
        binding.empty.show(false, "加载失败", "", "重新加载", view -> getTrayData());
    }

    @Override
    public void showDialog(String message) {
        binding.empty.setLoadingShowing(true);
        binding.empty.show("正在加载", "");
    }

    @Override
    public void dismissDialog() {
        binding.empty.setLoadingShowing(false);
        binding.empty.setVisibility(View.GONE);
    }
    DeliveryBean delivery;
    @Override
    public void getSuccess(DeliveryBean deliveryBean) {
        delivery = deliveryBean;
        datas.clear();
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
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getFailed(String message) {
        binding.empty.show("加载失败", message);
    }
    List<LogisticsListBean> logisticsListBeans;
    @Override
    public void getLogisticsSuccess(List<LogisticsListBean> datas) {
        if (logisticsListBeans == null){
            logisticsListBeans = datas;
        }
        if (datas == null || datas.isEmpty()){
            getLogisticsFailed("空");
        }else {
            List<String> items = new ArrayList<>();
            for (LogisticsListBean itemBean : logisticsListBeans){
                items.add(itemBean.getName());
            }

            QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(this);
            builder.addItems(items.toArray(new String[items.size()]), (dialog, which) -> {})
                .setCheckedIndex(0)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    int checkedIndex = builder.getCheckedIndex();
                    binding.logisticsType.setText(items.get(checkedIndex));
                    dialog.dismiss();
                })
                .create().show();
        }
    }

    @Override
    public void getLogisticsFailed(String errorMessage) {
        new QMUIDialog.MessageDialogBuilder(this)
            .setMessage("无法获取物流列表，无法为您列出物流信息表；您可以直接输入所使用的物流")
            .addAction("确定", (dialog, index) -> dialog.dismiss())
            .create().show();
    }
}