package com.step.sacannership.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.UnBindAdapter;
import com.step.sacannership.databinding.DeliveryTestViewBinding;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import java.util.ArrayList;
import java.util.List;

public class DeliveryTestActivity extends BaseTActivity<DeliveryTestViewBinding> implements TPresenter<DeliveryBean> {

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.edit_delivery)
//    EditText editDelivery;
//    @BindView(R.id.recycler)
//    RecyclerView recycler;
//    @BindView(R.id.empty_view)
//    QMUIEmptyView emptyView;

    private boolean hasPallet;

    private List<DeliveryDetailBean> datas;
    private UnBindAdapter adapter;

    @Override
    protected int contentView() {
        return R.layout.delivery_test_view;
    }

    @Override
    protected void initView() {

        hasPallet = getIntent().getBooleanExtra("pallet", true);
        binding.topBar.setTitle("发运单检验");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
        setEdit();


        datas = new ArrayList<>();
        adapter = new UnBindAdapter(this, datas);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        binding.recycler.addItemDecoration(decoration);
        binding.recycler.setAdapter(adapter);
    }

    private void setEdit() {
        binding.editDelivery.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                //获取发运单详情
//                getData();
//                editDelivery.setText("");
//            }
            if (!isTwice()){
                //获取发运单详情
                getData();
                binding.editDelivery.setText("");
            }
            QMUIKeyboardHelper.hideKeyboard(binding.editDelivery);
            return true;
        });
        binding.tvSubmit.setOnClickListener(v -> {
            if (deliveryBean == null) return;
            new QMUIDialog.MessageDialogBuilder(this)
                    .setMessage("发运单号："+deliveryBean.getBillNo()+"确认合格？")
                    .addAction("取消", (qmuiDialog, i) -> qmuiDialog.dismiss())
                    .addAction("确定", (qmuiDialog, i) -> {
                        if (hasPallet){
                            tModel.testpallet(deliveryBean.getBillNo(), 2, DeliveryTestActivity.this);
                        }else {
                            //无托盘发运单检验
                            tModel.testUnPallet(deliveryBean.getBillNo(), DeliveryTestActivity.this);
                        }
                        qmuiDialog.dismiss();
                    }).create().show();
        });
    }

    public void getData() {
        if (tModel == null) {
            tModel = new TModel();
        }
        String deliveryNo = binding.editDelivery.getText().toString();
        if (TextUtils.isEmpty(deliveryNo)) {
            return;
        }
        if (hasPallet){
            tModel.getDeliveryList(deliveryNo, this);
        }else {
            tModel.getDeliveryListUnPallet(deliveryNo, this);
        }
    }

    private DeliveryBean deliveryBean;
    @Override
    public void getSuccess(DeliveryBean deliveryBean) {
        this.deliveryBean = deliveryBean;
        datas.clear();
        if (deliveryBean == null || deliveryBean.getSapDeliveryBillDetails().isEmpty()){
            binding.emptyView.show("发运单中没有物料", "");
        }else {
            List<DeliveryDetailBean> billDetails = deliveryBean.getSapDeliveryBillDetails();
            datas.addAll(billDetails);
            adapter.setTimes("绑定时间："+deliveryBean.getPalletBindCompletedTime());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getFailed(String message) {
        binding.emptyView.show("加载失败", message);
    }

    @Override
    public void showDialog(String message) {
        binding.emptyView.show("正在加载", "");
        binding.emptyView.setLoadingShowing(true);
    }

    @Override
    public void dismissDialog() {
        binding.emptyView.hide();
    }

}
