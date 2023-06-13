package com.step.sacannership.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.UnBindDeliveryListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import com.step.sacannership.model.bean.NoPalletDetails;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class UnBindDeliveryActivity extends BaseActivity implements TPresenter<DeliveryBean>, UnBindDeliveryListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tray_no)
    EditText trayNo;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.unbind_delivery_view;
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

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);

        datas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, -1);
        recycler.setAdapter(adapter);
        adapter.setListener(new DeliveryAdapter.EditNumListener() {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {
            }

            @Override
            public void bindMaterialListener(int index, String billNo) {
            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int billID) {
            }
        });
    }

    @OnClick(R.id.tv_save_bind)
    public void onViewClick(){
        unbindDelivery();
    }

    private void unbindDelivery(){

        if (deliveryBean == null){
            ToastUtils.showToast(this, "请先扫描发货单号获取发货单");
            return;
        }

        List<NoPalletDetails>  deliverys = deliveryBean.getSdDeliveryBillNoPalletDetails();
        Log.e("TAGGG", "delivery="+deliverys.size());

        if (deliverys != null && !deliverys.isEmpty()){
            if (tModel == null){
                tModel = new TModel();
            }
            tModel.unBindDelivery(deliverys.get(0).getDeliveryBillID(), this);
        }else {
            unBindFail("无法获取发货单信息");
        }
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

        adapter.notifyDataSetChanged();
        requestFocus(trayNo);
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

    @Override
    public void unBindSuccess() {
        deliveryBean = null;
        datas.clear();
        adapter.notifyDataSetChanged();
        requestFocus(trayNo);
        tvMessage.setText("已撤销绑定");
    }

    @Override
    public void unBindFail(String message) {
        tvMessage.setText("失败："+message);
    }
}