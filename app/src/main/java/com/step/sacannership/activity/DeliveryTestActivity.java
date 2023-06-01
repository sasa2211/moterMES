package com.step.sacannership.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.UnBindAdapter;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.UnBindBean;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class DeliveryTestActivity extends BaseActivity implements TPresenter<DeliveryBean> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_delivery)
    EditText editDelivery;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty_view)
    QMUIEmptyView emptyView;

    private boolean hasPallet;

    private List<DeliveryDetailBean> datas;
    private UnBindAdapter adapter;

    @Override
    protected int contentView() {
        return R.layout.delivery_test_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);
        hasPallet = getIntent().getBooleanExtra("pallet", true);
        setEdit();


        datas = new ArrayList<>();
        adapter = new UnBindAdapter(this, datas);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);
        recycler.setAdapter(adapter);
    }

    private void setEdit() {
        editDelivery.setOnEditorActionListener((v, actionId, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                //获取发运单详情
                getData();
                editDelivery.setText("");
            }
            QMUIKeyboardHelper.hideKeyboard(editDelivery);
            return true;
        });
    }

    public void getData() {
        if (tModel == null) {
            tModel = new TModel();
        }
        String deliveryNo = editDelivery.getText().toString();
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
            emptyView.show("发运单中没有物料", "");
        }else {
            List<DeliveryDetailBean> billDetails = deliveryBean.getSapDeliveryBillDetails();
            datas.addAll(billDetails);
            adapter.setTimes("绑定时间："+deliveryBean.getPalletBindCompletedTime());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getFailed(String message) {
        emptyView.show("加载失败", message);
    }

    @Override
    public void showDialog(String message) {
        emptyView.show("正在加载", "");
        emptyView.setLoadingShowing(true);
    }

    @Override
    public void dismissDialog() {
        emptyView.hide();
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
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
    }
}
