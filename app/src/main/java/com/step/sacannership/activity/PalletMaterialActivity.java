package com.step.sacannership.activity;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.R;
import com.step.sacannership.adapter.DeliveryAdapter;
import com.step.sacannership.fragment.ExportSnDialog;
import com.step.sacannership.listener.BindListener;
import com.step.sacannership.listener.OnTextChangeListener;
import com.step.sacannership.listener.SyncCodeListener;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.TrayInfoListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.BindResultBean;
import com.step.sacannership.model.bean.DeliveryBean;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.MaterialNum;
import com.step.sacannership.model.bean.TrayDeliveryBean;
import com.step.sacannership.model.bean.TrayInfoBean;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;

public class PalletMaterialActivity extends BaseActivity implements TrayInfoListener,
        TPresenter<DeliveryBean>, SyncCodeListener, BindListener {

    @BindView(R.id.topBar)
    QMUITopBarLayout topBarLayout;
    @BindView(R.id.pallet_no)
    EditText editPallet;
    @BindView(R.id.delivery_no)
    EditText editDelivery;
    @BindView(R.id.material_no)
    EditText editMaterial;
    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ln_pallet)
    LinearLayout lnPallet;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_delivery_number)
    TextView tvDeliveryNumber;
    private boolean hasPallet;

    private boolean hasChange = false;

    private DeliveryAdapter adapter;
    private List<TrayDeliveryBean> datas;

    @Override
    protected int contentView() {
        return R.layout.pallet_material_view;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        topBarLayout.addLeftBackImageButton().setOnClickListener(v -> onBackPressed());
        topBarLayout.setTitle(intent.getStringExtra("title"));
        hasPallet = intent.getBooleanExtra("hasPallet", false);

        lnPallet.setVisibility(hasPallet ? View.VISIBLE : View.GONE);

        setEdit();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recycler.addItemDecoration(decoration);

        datas = new ArrayList<>();
        adapter = new DeliveryAdapter(datas, this, 1);
        recycler.setAdapter(adapter);

        adapter.setListener(new DeliveryAdapter.EditNumListener() {
            @Override
            public void changeNum(String deliveryNo, String materialNo, String rowIndex) {
                QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(PalletMaterialActivity.this);
                builder.setPlaceholder("编辑");
                builder.setTitle("编辑物料数量");
                builder.addAction("取消", (dialog, index) -> dialog.dismiss());
                builder.addAction("确定", (dialog, index) -> {
                    int [] nos = adapter.getClickPosition(deliveryNo, materialNo, rowIndex);
                    if (nos[0]*nos[1] == -1){
                        ToastUtils.showToast(PalletMaterialActivity.this, "修改位置不明确");
                        return;
                    }

                    TrayDeliveryBean beanGroup = datas.get(nos[0]);
                    DeliveryBillPalletDetailsBean childBean = beanGroup.getChildAt(nos[1]);
                    double quality = childBean.getQuantity();

                    try {
                        quality = Double.parseDouble(builder.getEditText().getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast(PalletMaterialActivity.this, "格式化数据出现异常");
                    }

                    childBean.setQuantity(quality);
                    adapter.notifyDataSetChanged();
                    QMUIKeyboardHelper.hideKeyboard(builder.getEditText());
                    dialog.dismiss();

                });
                builder.create().show();
            }

            @Override
            public void bindMaterialListener(int index, String billNo) {

            }

            @Override
            public void bindMaterialSn(String materialNo, String rowIndex, String billNo, int billID) {

                Intent intent = new Intent(PalletMaterialActivity.this, BindMaterial.class);
                intent.putExtra("billNo", billNo);
                intent.putExtra("billID", billID);
//                intent.putExtra("index", index);
                intent.putExtra("palletID", infoBean.getPalletId());
                intent.putExtra("materialNO", materialNo);
                intent.putExtra("rowIndex", rowIndex);
                startActivityForResult(intent, 10);
            }
        });
    }

    private void setEdit(){
        editPallet.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP){
//                getPalletInfo(false);
//                QMUIKeyboardHelper.hideKeyboard(editPallet);
//            }
            getPalletInfo(false);
            QMUIKeyboardHelper.hideKeyboard(editPallet);
            return true;
        });

        editDelivery.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP){
//                if (hasPallet){
//                    searchDelivery();
//                    QMUIKeyboardHelper.hideKeyboard(editPallet);
//                }
//            }
            if (hasPallet){
                searchDelivery();
                QMUIKeyboardHelper.hideKeyboard(editPallet);
            }
            return true;
        });

        editMaterial.addTextChangedListener(new OnTextChangeListener(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(count == 1){
                    editMaterial.setText("");
                }
            }
        });

        editMaterial.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP){
//                binding();
//                hasChange = true;
//                QMUIKeyboardHelper.hideKeyboard(editPallet);
//            }
            binding();
            hasChange = true;
            QMUIKeyboardHelper.hideKeyboard(editPallet);
            return true;
        });

        if (hasPallet){
            requestFocus(editPallet);
        }else {
            requestFocus(editDelivery);
        }
    }
    /**
     * 从列表中查发货单
     * 如果发货单存在就扫物料条码
     * 如果发运单不存在从服务器上拉取发货单并绑定在托盘上
     * */
    private void searchDelivery(){
        if (isTwice()){
            return;
        }
        if (infoBean == null){
            createMediaPlayer(R.raw.failed);
            ToastUtils.showToast(this, "请先扫描托盘号");
            return;
        }
        boolean hasDelivery = false;
        String editDeliveryText = editDelivery.getText().toString().trim();
        for (TrayDeliveryBean deliveryBean : datas){
            if (editDeliveryText.equals(deliveryBean.getDeliveryBillNO())){
                hasDelivery = true;
                break;
            }
        }
        if (hasDelivery){
            createMediaPlayer(R.raw.success);
            requestFocus(editMaterial);
        }else {
            //获取发货单详情，绑定到托盘
            if (tModel == null) tModel = new TModel();
            tModel.getDeliveryListAndBindPallet(editDeliveryText, infoBean, this);
        }
    }

    /**
     * 获取托盘信息
     * */
    private void getPalletInfo(boolean simpleSyncNum){
        if (isTwice()){
            return;
        }
        if (tModel == null) tModel = new TModel();
        String billNO = editPallet.getText().toString().trim();
        if (TextUtils.isEmpty(billNO)){
            ToastUtils.showToast(this, "请扫描托盘条码");
            createMediaPlayer(R.raw.failed);
            return;
        }
        tModel.getPalletInfo(billNO, simpleSyncNum,this,  this);
    }
    /**
     * 绑定物料
     * */
    private void binding(){
        if (isTwice()){
            return;
        }
        if (tModel == null) tModel = new TModel();
        if (infoBean == null){
            ToastUtils.showToast(this, "请先扫描托盘号获取托盘信息");
            return;
        }
        Map<String, Object> map= new HashMap<String, Object>();
        map.put("palletID", infoBean.getPalletId());
        String deliveryNo = editDelivery.getText().toString().trim();
        if (TextUtils.isEmpty(deliveryNo)){
            ToastUtils.showToast(this, "请扫描发货单条码");
            return;
        }

        for (TrayDeliveryBean deliveryBean : datas){
            if (deliveryNo.equals(deliveryBean.getDeliveryBillNO())){
                map.put("deliveryBillID", deliveryBean.getDeliveryBillId());
                break;
            }
        }

        String materialNo = editMaterial.getText().toString().trim();
        if (TextUtils.isEmpty(materialNo)){
            ToastUtils.showToast(this, "请扫描物料条码");
            return;
        }

        map.put("materialBarcode", materialNo);
        tModel.bindMaterialNo(map, this);
    }

    @OnClick({R.id.tv_save})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.tv_save:
                saveBind(false);
                break;
        }
    }

    private void saveBind(boolean exit){
        hasChange = false;
        if (infoBean == null) return;
        if (tModel == null){
            tModel = new TModel();
        }
        infoBean.setDeliveryBills(datas);
        tModel.bindTray(infoBean, exit, this);
    }

    TrayInfoBean infoBean;
    @Override
    public void getTraySuccess(TrayInfoBean infoBean) {
        this.infoBean = infoBean;
        datas.clear();
        adapter.notifyDataSetChanged();
        if (infoBean != null) {
            List<TrayDeliveryBean> deliverys = infoBean.getDeliveryBills();
            if (deliverys != null) {
                datas.addAll(deliverys);

            }
        }
        adapter.notifyDataSetChanged();
        if (datas.size() == 0){
            tvLoading.setText("暂无发货单数据");
        }else {
            tvLoading.setText("");
        }
        if (TextUtils.isEmpty(editDelivery.getText().toString().trim())){
            requestFocus(editDelivery);
        }else {
            requestFocus(editMaterial);
        }
        tvDeliveryNumber.setText("发货单数量："+datas.size());
        createMediaPlayer(R.raw.success);
    }

    @Override
    public void getTrayFailed(String message) {
        tvLoading.setText("托盘信息加载失败:"+message);
        String deliveryNo = editDelivery.getText().toString().trim();
        if (TextUtils.isEmpty(deliveryNo)){
            requestFocus(editPallet);
        }else {
            requestFocus(editDelivery);
        }
        createMediaPlayer(R.raw.failed);
    }

    @Override
    public void showDialog(String message) {
        runOnUiThread(() -> tvLoading.setText(message));
    }

    @Override
    public void dismissDialog() {
        runOnUiThread(() -> tvLoading.setText(""));
    }

    @Override
    public void getSuccess(DeliveryBean map) {

    }

    @Override
    public void getFailed(String message) {

    }

    @Override
    public void syncSuccess() {
        if (messageDialog != null && messageDialog.isShowing()){
            messageDialog.dismiss();
        }
        binding();
    }

    @Override
    public void syncFail(String message) {
        editDelivery.setText("");
        editMaterial.setText("");
        requestFocus(editDelivery);
        SPTool.showToast(this, message);
    }

    @Override
    public void getSnNumSuccess(Integer snNum) {
        runOnUiThread(() -> tvNumber.setText(String.valueOf(snNum)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 10){
            if (data != null){
                MaterialNum materialNum = (MaterialNum) data.getSerializableExtra("materialInfos");
                try {
                    boolean findExist = false;
                    for (TrayDeliveryBean deliveryBean : datas){
                        if (deliveryBean.getDeliveryBillNO().equals(materialNum.getBillNo())){
                            for (DeliveryBillPalletDetailsBean detailsBean : deliveryBean.getDeliveryBillPalletDetails()){
                                if (detailsBean.getMaterialNo().equals(materialNum.getMaterialNo()) && detailsBean.getRowIndex().equals(materialNum.getRowIndex())){
                                    detailsBean.setQuantity(materialNum.getMaterialNum());
                                    detailsBean.setSnQuantity(materialNum.getMaterialNum());
                                    findExist = true;
                                    break;
                                }
                            }
                        }

                        if (findExist){
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    getPalletInfo(true);
                }
            }
        }
    }

    @Override
    public void bindMaterialSuccess(BindResultBean map) {
        int resultCode = map.getResultCode();
        if (resultCode == 0){
            tvLoading.setText("绑定成功");
            editMaterial.setText("");
            requestFocus(editDelivery);

            String materialNo = map.getMaterialNo();
            int deliveryBillID = map.getDeliveryBillID();
            String rowIndex = map.getRowIndex();
            for (TrayDeliveryBean deliveryBean : datas){
                if (deliveryBean.getDeliveryBillId() == deliveryBillID){
                    List<DeliveryBillPalletDetailsBean> palletDetails = deliveryBean.getDeliveryBillPalletDetails();
                    for (DeliveryBillPalletDetailsBean detailsBean : palletDetails){
                        if (materialNo.equals(detailsBean.getMaterialNo()) && rowIndex.equals(detailsBean.getRowIndex())){
                            detailsBean.setQuantity(map.getNum());
                            detailsBean.setSnQuantity(detailsBean.getQuantity() + 1);
                            break;
                        }
                    }
                    break;
                }
            }
            int snNum = 0;
            try {
                snNum = Integer.parseInt(tvNumber.getText().toString().trim());
            }catch (Exception e){
                e.printStackTrace();
            }
            snNum += 1;
            tvNumber.setText(String.valueOf(snNum));
            adapter.notifyDataSetChanged();
            createMediaPlayer(R.raw.success);
        }else {
            createMediaPlayer(R.raw.failed);
            String errorMsg = map.getErrorMsg();
            if (messageDialog != null){
                if (messageDialog.isShowing()){
                    messageDialog.dismiss();
                }
                messageDialog = null;
            }
            QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this)
                    .setMessage(errorMsg)
                    .addAction("关闭", (dialog, index) -> dialog.dismiss());
            if (resultCode == 2){
                builder.addAction("导入", (dialog, index) -> {
                    String materialCode = editMaterial.getText().toString().trim();
                    if (TextUtils.isEmpty(materialCode)){
                        return;
                    }
                    ExportSnDialog exportSnDialog = new ExportSnDialog(PalletMaterialActivity.this, materialCode);
                    exportSnDialog.create().show();
                    exportSnDialog.setListener(new ExportSnDialog.ImportListener() {
                        @Override
                        public void importSuccess() {
                            binding();
                            exportSnDialog.dismiss();
                            if (messageDialog != null && messageDialog.isShowing()){
                                messageDialog.dismiss();
                            }
                        }
                        @Override
                        public void importFail() {
                            editDelivery.setText("");
                            editMaterial.setText("");
                            requestFocus(editDelivery);
                            exportSnDialog.dismiss();
                            if (messageDialog != null && messageDialog.isShowing()){
                                messageDialog.dismiss();
                            }
                            createMediaPlayer(R.raw.failed);
                        }
                    });
                })
                .addAction("更新", (dialog, index) -> {
                    if (tModel == null) tModel = new TModel();
                    String materialCode = editMaterial.getText().toString().trim();
                    tModel.syncBarcode(materialCode, PalletMaterialActivity.this);
                });
            }
            messageDialog = builder.create();
            
            messageDialog.setOnDismissListener(dialog -> {
                requestFocus(editMaterial);
                editMaterial.setText("");
            });
            messageDialog.show();
        }
    }

    private QMUIDialog messageDialog = null;

    @Override
    public void bindMaterialFail(String message) {
        tvLoading.setText(message);
        editMaterial.setText("");
        requestFocus(editDelivery);
        new QMUIDialog.MessageDialogBuilder(this)
            .setTitle("绑定失败")
            .setMessage("提示："+message)
            .addAction("确定", (dialog, index) -> dialog.dismiss())
            .create().show();
        createMediaPlayer(R.raw.failed);
    }

    @Override
    public void bindSuccess(String materialNo, boolean isExit) {
        createMediaPlayer(R.raw.success);
        SPTool.showToast(this, "绑定成功");
        getPalletInfo(false);

        if (isExit){
            onBackPressed();
        }
    }

    @Override
    public void bindFailed(String message) {
        createMediaPlayer(R.raw.failed);
        SPTool.showToast(this, message);
        getPalletInfo(false);
    }

    @Override
    public void deleteSuccess() {

    }

    @Override
    public void deleteFailed(String message) {

    }

    @Override
    public void onBackPressed() {
        if (hasChange){
            new QMUIDialog.MessageDialogBuilder(this)
                .setMessage("退出前将对绑定数据保存，是否继续退出？")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    saveBind(true);
                    dialog.dismiss();
                }).create().show();
        }else {
            super.onBackPressed();
        }
    }
}