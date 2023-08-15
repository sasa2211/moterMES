package com.step.sacannership.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.databinding.ArriveViewBinding;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;

public class ArriveActivity extends BaseTActivity<ArriveViewBinding> implements TrayInfoListener {

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.arrive_view;
    }

    @Override
    protected void initView() {
        binding.tvName.setText("托盘条码");
        binding.trayNo.setHint("扫描托盘条码");

        initToolBar(binding.toolbar);
        setOnclick(binding.trayNo);

        binding.trayNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (!isTwice()){
                /**
                 * 获取托盘数据
                 * */
                getTrayData();
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

        binding.btnDelivery.setOnClickListener(v -> submit());
    }

    public void getTrayData(){
        String billNO = binding.trayNo.getText().toString().trim();
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
            binding.empty.setLoadingShowing(false);
            binding.empty.show("暂无数据", "");
            binding. empty.setVisibility(View.VISIBLE);
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

    public void saveSuccess(){
        datas.clear();
        adapter.notifyDataSetChanged();
        infoBean = null;

        binding.empty.show("暂无数据", "");

        ToastUtils.showToast(this, "提交成功");
        requestFocus(binding.trayNo);
    }
    public void saveFailed(String message){
        new QMUIDialog.MessageDialogBuilder(this)
                .setMessage("提交失败："+message+"。是否重新提交？")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> submit()).create().show();
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