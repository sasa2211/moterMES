package com.step.sacannership.activity;

import android.app.Dialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.step.sacannership.R;
import com.step.sacannership.adapter.ModifyNumAdapter;
import com.step.sacannership.databinding.ModifyNumViewBinding;
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

public class ModifyNumActivity extends BaseTActivity<ModifyNumViewBinding> implements TPresenter<DeliveryBean>, CancelListener {

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.delivery_no)
//    EditText deliveryNo;
//    @BindView(R.id.empty)
//    QMUIEmptyView empty;
//    @BindView(R.id.listView)
//    ListView listView;

    private TModel tModel;
    private List<DeliveryDetailBean> deliveryDatas;
    private ModifyNumAdapter adapter;
    private boolean haspallet = true;
    @Override
    protected int contentView() {
        return R.layout.modify_num_view;
    }

    @Override
    protected void initView() {
//        initToolBar(toolbar);
        binding.topBar.setTitle("出库");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
        requestFocus(binding.deliveryNo);
        haspallet = getIntent().getBooleanExtra("pallet", true);

        binding.deliveryNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (!isTwice()){
                getDeliveryMaterialList();
                binding.deliveryNo.setText("");
            }
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
//                getDeliveryMaterialList();
//                deliveryNo.setText("");
//            }
            QMUIKeyboardHelper.hideKeyboard(textView);
            return true;
        });
        setOnclick(binding.deliveryNo);

        deliveryDatas = new ArrayList<>();
        adapter = new ModifyNumAdapter(deliveryDatas, this);
        adapter.setModifyView((position, numText) -> {
            DeliveryDetailBean detailBean = deliveryDatas.get(position);
            try {
                float number = Float.parseFloat(numText);
                detailBean.setQuantity(number);
            }catch (Exception e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            QMUIKeyboardHelper.hideKeyboard(binding.listView);
        });
        binding.listView.setAdapter(adapter);

        binding.refresh.setOnClickListener(v -> getDeliveryMaterialList());
        binding.btnSubmit.setOnClickListener(v -> {
            if (deliveryBean == null){SPTool.showToast(this, "无内容可提交");
                return;
            }
            deliveryBean.setSapDeliveryBillDetails(deliveryDatas);
            isSubmit = true;
            tModel.submitMaterial(deliveryBean, this);
        });
    }

    private void getDeliveryMaterialList() {
        String billNo = binding.deliveryNo.getText().toString().trim();
        if (TextUtils.isEmpty(billNo)){
            SPTool.showToast(this, "发货单号不能为空");
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        tModel.getMaterialLists(billNo, haspallet, this);
    }

    private boolean isSubmit = false;

    DeliveryBean deliveryBean;
    @Override
    public void getSuccess(DeliveryBean deliveryBean) {
        this.deliveryBean = deliveryBean;

        if (deliveryBean != null && deliveryBean.getSapDeliveryBillDetails() != null){
            deliveryDatas.addAll(deliveryBean.getSapDeliveryBillDetails());
            adapter.setData(deliveryBean.getCreateTime());
            adapter.notifyDataSetChanged();
        }
        if (deliveryDatas.isEmpty()){
            binding.empty.show("暂无数据", "");
        }
    }

    @Override
    public void getFailed(String message) {
        binding.empty.show(false, "加载失败", message, "重新加载", view -> getDeliveryMaterialList());
    }
    private Dialog dialog;
    @Override
    public void showDialog(String message) {
        if (isSubmit){
            dialog = LoadingDialog.createLoadingDialog(this, "出库请求中");
            LoadingDialog.showLoadingDialog(dialog);
        }else {
            binding.empty.show(message, "");
            binding.empty.setLoadingShowing(true);
            deliveryDatas.clear();
            deliveryBean = null;
        }
    }

    @Override
    public void dismissDialog() {
        if (isSubmit){
            LoadingDialog.hideLoadingDialog(dialog);
            dialog = null;
        }else {
            binding.empty.hide();
        }
        isSubmit = false;
    }

    @Override
    public void cancelSuccess(String message) {
        ToastUtils.showToast(this, message);
    }

    @Override
    public void cancelFailed(String message) {
        ToastUtils.showToast(this, message);
    }
}
