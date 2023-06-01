package com.step.sacannership.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class ArriveActivity extends BaseActivity implements TrayInfoListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty)
    QMUIEmptyView empty;
    @BindView(R.id.tv_name)
    TextView tnName;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.arrive_view;
    }

    @Override
    protected void initView() {
//        hasPallet = getIntent().getBooleanExtra("pallet", true);
        tnName.setText("托盘条码");
        trayNo.setHint("扫描托盘条码");

        initToolBar(toolbar);
        setOnclick(trayNo);

        trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                /**
                 * 获取托盘数据
                 * */
                getTrayData();
            }
            QMUIKeyboardHelper.hideKeyboard(trayNo);
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
        adapter = new DeliveryAdapter(datas, this, 4);
        recycler.setAdapter(adapter);

    }

    public void getTrayData(){
        String billNO = trayNo.getText().toString().trim();
        if (!TextUtils.isEmpty(billNO)){
            if (tModel == null) tModel = new TModel();
            tModel.getPalletArrive(billNO, this);
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
        empty.show(false, "加载失败", "", "重新加载", view -> getTrayData());
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

    public void saveSuccess(){
        datas.clear();
        adapter.notifyDataSetChanged();
        infoBean = null;

        empty.show("暂无数据", "");

        ToastUtils.showToast(this, "提交成功");
        requestFocus(trayNo);
    }
    public void saveFailed(String message){
        new QMUIDialog.MessageDialogBuilder(this)
                .setMessage("提交失败："+message+"。是否重新提交？")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> submit()).create().show();
    }
    @OnClick({R.id.btn_delivery})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.btn_delivery:
                submit();
                break;
        }
    }

    public void submit(){
        if (tModel == null){
            tModel = new TModel();
        }
        if (infoBean == null){
            return;
        }
        String palletNo = infoBean.getPalletNO();
        tModel.savePalletArrive(palletNo, this);
    }
}