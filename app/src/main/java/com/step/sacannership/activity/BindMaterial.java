package com.step.sacannership.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.step.sacannership.R;
import com.step.sacannership.listener.BindMaterialView;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.MaterialNum;
import com.step.sacannership.model.bean.ScanDeliveryMaterialBean;
import com.step.sacannership.tools.SPTool;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class BindMaterial extends BaseActivity implements TPresenter<List<ScanDeliveryMaterialBean>>, BindMaterialView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.delivery_no)
    EditText deliveryNo;
//    @BindView(R.id.material_no)
//    EditText materialNo;
    @BindView(R.id.empty)
    QMUIEmptyView emptyView;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tv_material)
    TextView tvMaterial;

    private TModel tModel;
    private List<ScanDeliveryMaterialBean> datas;
    private MaterialAdapter adapter;

    private String billNo;

    private int palletID;
    private int billID;
    private String materialNO;
    private String rowIndex;
    private boolean hasPallet = true;

    @Override
    protected int contentView() {
        return R.layout.bind_material_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);
        setEdit();

        emptyView.show("暂无数据", "");
        datas = new ArrayList<>();
        adapter = new MaterialAdapter();
        listView.setAdapter(adapter);

        billNo = getIntent().getStringExtra("billNo");
        billID = getIntent().getIntExtra("billID", -1);
//        index = getIntent().getIntExtra("index", 0);
        palletID = getIntent().getIntExtra("palletID", -1);
        materialNO = getIntent().getStringExtra("materialNO");
        rowIndex = getIntent().getStringExtra("rowIndex");
        hasPallet = getIntent().getBooleanExtra("hasPallet", true);
        if (!TextUtils.isEmpty(billNo)){
            deliveryNo.setText(materialNO);
            deliveryNo.setFocusable(false);
//            requestFocus(materialNo);
            getDeliveryMaterialList();
        }else {
            deliveryNo.setFocusable(true);
            requestFocus(deliveryNo);
        }
    }

    private void setEdit() {
        requestFocus(deliveryNo);
        setOnclick(deliveryNo);
//        setOnclick(materialNo);
        deliveryNo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                getDeliveryMaterialList();
            }
            QMUIKeyboardHelper.hideKeyboard(deliveryNo);
            return true;
        });
//        materialNo.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
//                bindMaterial();
//            }
//            QMUIKeyboardHelper.hideKeyboard(deliveryNo);
//            return true;
//        });
    }


    @OnClick(R.id.refresh)
    public void onViewClicked() {
        getDeliveryMaterialList();
    }

    public void getDeliveryMaterialList() {

        datas.clear();
        adapter.notifyDataSetChanged();

        String deliveryText = deliveryNo.getText().toString().trim();
        if (TextUtils.isEmpty(deliveryText)) {
            SPTool.showToast(this, "发运单号不能为空");
            return;
        }

        if (tModel == null) {
            tModel = new TModel();
        }
        tModel.getMaterialList(hasPallet, palletID, materialNO, billNo, rowIndex, this);
    }

//    DeliveryMaterial deliveryBean;
    @Override
    public void getSuccess(List<ScanDeliveryMaterialBean> list) {
        if (list.isEmpty()) {
            emptyView.show("暂无数据", "");
            return;
        }
//        requestFocus(materialNo);

        datas.addAll(list);
        adapter.notifyDataSetChanged();
        if (datas.isEmpty()) {
            emptyView.setLoadingShowing(false);
            emptyView.show("暂无数据", "");
        }
        tvMaterial.setText("物料列表（数量"+datas.size()+"）");
        createMediaPlayer(R.raw.success);
    }

    @Override
    public void getFailed(String message) {
        emptyView.show(false, "加载失败", message, "重新加载", view -> getDeliveryMaterialList());
        createMediaPlayer(R.raw.success);
    }

    @Override
    public void showDialog(String message) {
        if ("1".equals(message)) {
            emptyView.setLoadingShowing(true);
            emptyView.show("加载中", "");
        }
    }

    @Override
    public void dismissDialog() {
        if (emptyView.isShowing()) {
            emptyView.hide();
        }
    }

    @Override
    public void bindSuccess(Map<String, Object> map) {
    }

    @Override
    public void bindFailed(String message) {

    }

    @Override
    public void syncBarcodeSuccess() {

    }

    @Override
    public void deleteSuccess() {
        createMediaPlayer(R.raw.success);
        SPTool.showToast(this, "解除绑定成功");
        if (deletePosition >= 0){
            datas.remove(deletePosition);
            adapter.notifyDataSetChanged();
        }
        if (datas.isEmpty()){
            emptyView.show("暂无数据", "");
        }
        deletePosition = -1;
        tvMaterial.setText("物料列表（数量"+datas.size()+"）");
    }

    @Override
    public void deleteFailed(String message) {
        createMediaPlayer(R.raw.failed);
        SPTool.showToast(this, "解除失败:"+message);
        deletePosition = -1;
    }
    private int deletePosition = -1;
    public class MaterialAdapter extends BaseAdapter {
        private String materialAdd = "";

        public void setMaterialAdd(String materialAdd) {
            this.materialAdd = materialAdd;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public ScanDeliveryMaterialBean getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            MaterialHolder holder;
            if (view == null) {
                holder = new MaterialHolder();
                view = getLayoutInflater().inflate(R.layout.material_item, viewGroup, false);
                holder.materialNo = view.findViewById(R.id.material_no);
                holder.materialCode = view.findViewById(R.id.material_barcode);
                holder.deleteImg = view.findViewById(R.id.img_delete);
                view.setTag(holder);
            } else {
                holder = (MaterialHolder) view.getTag();
            }
            ScanDeliveryMaterialBean materialBean = datas.get(position);
            String materialNo = materialBean.getMaterialNo();
            String MaterialCode = materialBean.getMaterialBarcode();
            holder.materialNo.setText("物料编号："+materialNo);
            holder.materialCode.setText("物料条码："+MaterialCode);
            if (materialAdd.equals(MaterialCode) && position == 0) {
                holder.materialNo.setTextColor(getResources().getColor(R.color.red));
            } else {
                holder.materialNo.setTextColor(getResources().getColor(R.color.black));
            }
            holder.deleteImg.setOnClickListener(view1 -> {
                if (tModel == null) tModel = new TModel();
                deletePosition = position;

                tModel.unBindMaterial(materialBean, BindMaterial.this);
            });
            return view;
        }

        public class MaterialHolder {
            public TextView materialNo;
            public TextView materialCode;
            public ImageView deleteImg;
        }
    }

    @Override
    public void onBackPressed() {

        /**
         * 按照物料编号分类
         * 计算出每种物料编号的数量
         * String rowIndex, String billNo, String materialNo, int materialNum
         * */
        MaterialNum materialNum = new MaterialNum(rowIndex, billNo, materialNO, datas.size());
        Intent intent = new Intent();
        intent.putExtra("materialInfos", (Serializable) materialNum);
        setResult(10, intent);
        super.onBackPressed();
    }
}
