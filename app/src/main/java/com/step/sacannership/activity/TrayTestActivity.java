package com.step.sacannership.activity;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class TrayTestActivity extends BaseActivity implements TrayInfoListener /**TPresenter<TrayInfoBean> */ {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty_view)
    QMUIEmptyView emptyView;
    @BindView(R.id.tv_number)
    TextView tvNumber;
//    private List<UnBindBean> datas;
//    private UnBindAdapter adapter;
    private List<TrayDeliveryBean> datas;
    private DeliveryAdapter adapter;

//    private int type = 0;
    private boolean isCancelTest = false;
    @Override
    protected int contentView() {
        return R.layout.activity_tray_test;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if ("check".equals(type)){
            tvTitle.setText("检验");
            btnTest.setText("合格");
            isCancelTest = false;
        }else if ("cancel_check".equals(type)){
            tvTitle.setText("撤销检验");
            btnTest.setText("撤销检验");
            isCancelTest = true;
        }

        initToolBar(toolbar);

        setOnclick(trayNo);
        trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                getData();
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
        adapter = new DeliveryAdapter( datas, this,3);
        recycler.setAdapter(adapter);

        adapter.setListener(new DeliveryAdapter.EditNumListener() {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {

            }

            @Override
            public void bindMaterialListener(int groupIndex, String billNo) {
                QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(TrayTestActivity.this);
                builder.setMessage("检验发运单号："+billNo+",确定检验合格？");
                builder.addAction("取消", (dialog, index) -> dialog.dismiss());
                builder.addAction("确定", (dialog, index) -> {
                    //检验合格
                    tModel.testpallet(billNo, 2, TrayTestActivity.this);
                    dialog.dismiss();
                });
                builder.create().show();
            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int id) {

            }
        });
    }

    public void getData() {
        String no = trayNo.getText().toString().trim();
        if (tModel == null) tModel = new TModel();
        tModel.getTrayInfos(no, this);
    }

    @Override
    public void showDialog(String message) {
        emptyView.setLoadingShowing(true);
        emptyView.show("加载中", "");
    }

    @Override
    public void dismissDialog() {
        emptyView.hide();
    }

    @OnClick(R.id.btn_test)
    public void onViewClicked() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setMessage(isCancelTest ? "是否撤销该单的检验？" : "确定检验合格？");
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.addAction("确定", (dialog, index) -> {
            if (infoBean != null) {
                if (isCancelTest){
                    tModel.cancelPalletTest(infoBean.getPalletNO(), TrayTestActivity.this);
                }else {
                    tModel.testpallet(infoBean.getPalletNO(), 1, TrayTestActivity.this);
                }
            }
            dialog.dismiss();
        });
        builder.create().show();
    }
    private TrayInfoBean infoBean;
    @Override
    public void getTraySuccess(TrayInfoBean infoBean) {
        this.infoBean = infoBean;
        datas.clear();
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
        if (datas.size() == 0) {
            emptyView.setLoadingShowing(false);
            emptyView.show("暂无数据", "");
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getTrayFailed(String message) {
        emptyView.show(false, "加载失败", message, "重新加载", view -> getData());
    }
}
