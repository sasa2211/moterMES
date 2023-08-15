package com.step.sacannership.activity;

import android.app.Dialog;
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
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliverySapCancelAdapter;
import com.step.sacannership.databinding.ModifyCancelViewBinding;
import com.step.sacannership.listener.CancelListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.tools.LoadingDialog;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;

public class ModifyNumCancelActivity extends BaseTActivity<ModifyCancelViewBinding> implements TPresenter<DeliveryBean>, CancelListener {

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.delivery_no)
//    EditText deliveryNo;
//    @BindView(R.id.empty)
//    QMUIEmptyView empty;
//    @BindView(R.id.listView)
//    RecyclerView listView;

    private List<DeliveryDetailBean> datas;
    private DeliverySapCancelAdapter adapter;
    @Override
    protected int contentView() {
        return R.layout.modify_cancel_view;
    }

    @Override
    protected void initView() {
        binding.topBar.setTitle("撤销出库");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v -> onBackPressed());

        binding.empty.show("暂无数据", "");
        binding.deliveryNo.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                getDeliveryMaterialList();
//                deliveryNo.setText("");
//            }
            if (!isTwice()){
                getDeliveryMaterialList();
                binding.deliveryNo.setText("");
            }

            QMUIKeyboardHelper.hideKeyboard(v);
            return true;
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.listView.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        binding.listView.addItemDecoration(decoration);

        datas = new ArrayList<>();
        adapter = new DeliverySapCancelAdapter(datas, this);
        binding.listView.setAdapter(adapter);

        binding.btnSubmit.setOnClickListener(v -> cancelDelivery());
    }

    private boolean isCancel = false;
//    @OnClick(R.id.btn_submit)
//    public void onViewClicked() {
//
//        cancelDelivery();
//    }

    private void getDeliveryMaterialList() {
        String billNo = binding.deliveryNo.getText().toString().trim();
        if (TextUtils.isEmpty(billNo)){
            SPTool.showToast(this, "发货单号不能为空");
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        tModel.getMaterialLists(billNo, true, this);
    }

    private void cancelDelivery(){
        if (deliveryBean == null){
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        isCancel = true;
        tModel.cancelSapOutStock(deliveryBean.getBillNo(), this);
    }

    private DeliveryBean deliveryBean;
    @Override
    public void getSuccess(DeliveryBean deliveryBean) {
        binding.empty.hide();
        try {
            this.deliveryBean = null;
            datas.clear();
            datas.addAll(deliveryBean.getSapDeliveryBillDetails());
            adapter.notifyDataSetChanged();
            adapter.setOutStockData(deliveryBean.getOutStockTime());
            this.deliveryBean = deliveryBean;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void getFailed(String message) {
        binding.empty.show("加载失败，请重新扫描", "原因："+message);
    }

    private Dialog dialog;
    @Override
    public void showDialog(String message) {
        if (isCancel){
            dialog = LoadingDialog.createLoadingDialog(this, "正在请求");
            LoadingDialog.showLoadingDialog(dialog);
        }else {
            binding.empty.show("加载中", "");
            binding.empty.setLoadingShowing(true);
        }
    }

    @Override
    public void dismissDialog() {
        if (isCancel){
            if (dialog != null){
                LoadingDialog.hideLoadingDialog(dialog);
                dialog = null;
            }
        }else {
            binding.empty.hide();
        }
        isCancel = false;
    }

    @Override
    public void cancelSuccess(String message) {
        ToastUtils.showToast(this, "撤销成功");
    }

    @Override
    public void cancelFailed(String message) {
        ToastUtils.showToast(this, "撤销失败："+message);
    }
}