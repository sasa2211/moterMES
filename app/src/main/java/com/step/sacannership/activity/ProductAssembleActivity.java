package com.step.sacannership.activity;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.step.sacannership.R;
import com.step.sacannership.adapter.ProductAssembleAdapter;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.AssembleDeleteBean;
import com.step.sacannership.model.bean.AssembleSubmitBean;
import com.step.sacannership.model.bean.ProductHeader;
import com.step.sacannership.model.bean.ProductItem;
import com.step.sacannership.model.bean.Request;
import com.step.sacannership.tools.ToastUtils;
import java.util.List;
import butterknife.BindView;
import okhttp3.ResponseBody;

public class ProductAssembleActivity extends BaseActivity implements TPresenter<List<ProductItem>> {
    @BindView(R.id.topBar)
    QMUITopBarLayout topBar;
    @BindView(R.id.tvBatch)
    TextView tvBatch;
    @BindView(R.id.tvOrder)
    TextView tvOrder;
    @BindView(R.id.materialNo)
    TextView tvMaterialNo;
    @BindView(R.id.tvLine)
    TextView tvLine;
    @BindView(R.id.tvStation)
    TextView tvStation;
    @BindView(R.id.materialDesc)
    TextView materialDesc;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.binded)
    TextView binded;
    @BindView(R.id.unBind)
    TextView unBind;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty_view)
    QMUIEmptyView emptyView;
    @BindView(R.id.tvSet)
    TextView tvSet;
    @BindView(R.id.editSnScan)
    EditText editSnScan;

    private final ProductHeader header = new ProductHeader();

    private final ProductAssembleAdapter adapter = new ProductAssembleAdapter();
    @Override
    protected int contentView() {
        return R.layout.product_assemble_view;
    }

    @Override
    protected void initView() {
        topBar.setTitle("产品组装扫描");
        topBar.addLeftBackImageButton().setOnClickListener(v-> onBackPressed());

        refresh.setRefreshFooter(new ClassicsFooter(this));
        refresh.setRefreshHeader(new ClassicsHeader(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(adapter);

        refresh.setOnRefreshListener(ref -> getData(true));
        refresh.setOnLoadmoreListener(ref -> getData(false));

        adapter.setItemClick(position -> {
            ProductItem item = adapter.getItem(position);
            String sn = item.getSerialNumber();
            new QMUIDialog.MessageDialogBuilder(this)
                .setMessage("是否删除产品序列号：" + sn)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    deleteSN(position);
                    dialog.dismiss();
                })
                .create().show();
        });

        tvSet.setOnClickListener(v->{
            Intent intent = new Intent(this, ProductAssembleSetActivity.class);
            String orderNo = header.getProductOrder();
            if (!TextUtils.isEmpty(orderNo)){
                intent.putExtra("orderNo", orderNo);
            }
            startActivityForResult(intent, 10);
        });
        tModel = new TModel();
        setHeader();

        editSnScan.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP){
//                submit();
//            }
            if (!isTwice()){
                submit();
            }
            QMUIKeyboardHelper.hideKeyboard(v);
            return true;
        });
    }

    private void setHeader(){
        tvBatch.setText("生产批次:" + header.getBatchStr());
        tvOrder.setText("生产订单:" + header.getProductOrder());
        tvMaterialNo.setText("物料编号:" + header.getMaterialNo());
        tvLine.setText("线别名称:" + header.getLineNo());
        tvStation.setText("站点名称:" + header.getStationPort());
        materialDesc.setText("物料描述:" + header.getMaterialDesc());
        count.setText("批次总量:" + header.getCount());
        binded.setText("已扫数量:" + header.getScanNo());
        unBind.setText("未扫数量:" + header.getUnScanNo());
    }

    private final Request request = new Request();
    private void getData(boolean isRefresh){
        if (tModel == null){
            tModel = new TModel();
        }
        if (isRefresh){
            request.put("pageNum", 1);
        }
        request.put("pageSize", 10);
        tModel.getProductManifest(request, this);
    }

    @Override
    public void showDialog(String message) {

    }

    @Override
    public void dismissDialog() {
        refresh.finishRefresh();
        refresh.finishLoadmore();
    }

    @Override
    public void getSuccess(List<ProductItem> productItems) {
        int pageNum = (int) request.get("pageNum");
        if (pageNum == 1){
            adapter.clear();
        }
        request.put("pageNum", pageNum + 1);
        adapter.addItems(productItems);
        refresh.setEnableLoadmore(productItems.size() == 10);
        if (adapter.getItemCount() == 0){
            emptyView.show("", "暂无数据");
        }else {
            emptyView.hide();
        }
        createMediaPlayer(R.raw.success);
    }

    @Override
    public void getFailed(String message) {
        if (adapter.getItemCount() == 0){
            emptyView.show("", "加载失败："+message);
        }else {
            ToastUtils.showToast(this, message);
        }
        createMediaPlayer(R.raw.failed);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 10 && data != null){
            ProductHeader bean = data.getParcelableExtra("header");
            Log.e("TAGGG", "bean === " + new Gson().toJson(bean));
            if (bean != null){
                header.copyDate(bean);
                setHeader();
                request.put("orderId", bean.getHeadrId());
                request.put("stationId", bean.getStatueId());
                refresh.autoRefresh();
                getStationCount();
                getScannedNum();
            }
        }
    }

    private void getStationCount(){
        int orderNo = header.getHeadrId();
        int stationId = header.getStatueId();
        if (orderNo == 0 || stationId == 0){
            return;
        }
        tModel.getStationCount(orderNo, stationId, new TPresenter<Request>() {
            @Override
            public void getSuccess(Request request) {
                try {
                    Object oNum = request.get("num");
                    if (oNum != null){
                        String statuePort = header.getStationPort();
                        statuePort = statuePort + "/" + oNum;
                        header.setStationPort(statuePort);
                    }
                    setHeader();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailed(String message) {

            }

            @Override
            public void showDialog(String message) {

            }

            @Override
            public void dismissDialog() {

            }
        });
    }

    private void getScannedNum(){
        int orderNo = header.getHeadrId();
        int stationId = header.getStatueId();
        if (orderNo == 0 || stationId == 0){
            return;
        }
        tModel.getScannedNum(header.getHeadrId(), stationId, new TPresenter<Integer>() {
            @Override
            public void getSuccess(Integer num) {
                try {
                    if (num != null){
                        header.setScanNo(num);
                        header.setUnScanNo(header.getCount() - num);
                        setHeader();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailed(String message) {

            }

            @Override
            public void showDialog(String message) {

            }

            @Override
            public void dismissDialog() {

            }
        });
    }

    private void submit(){
        String sn = editSnScan.getText().toString().trim();
        if (TextUtils.isEmpty(sn)){
            ToastUtils.showToast(this, "序列号不能为空");
            return;
        }
        AssembleSubmitBean bean = new AssembleSubmitBean();
        bean.setSn(sn);
        bean.setProductionOrderId(header.getHeadrId());
        bean.setProductionOrderNo(header.getProductOrder());
        bean.setProductionLineId(header.getLineId());
        bean.setProductionStationId(header.getStatueId());
        bean.setMaterialNo(header.getMaterialNo());
        bean.setWithstandTestCheck("A00017".equals(header.getStationNo()));
        tModel.submitAssembleInfo(bean, new TPresenter<ResponseBody>() {
            @Override
            public void getSuccess(ResponseBody responseBody) {
                getData(true);
                int scanNo = header.getScanNo() + 1;
                header.setScanNo(scanNo);
                int count = header.getCount();
                header.setUnScanNo(count - scanNo);

                setHeader();
                requestFocus(editSnScan);
                createMediaPlayer(R.raw.success);
            }

            @Override
            public void getFailed(String message) {
                new QMUIDialog.MessageDialogBuilder(ProductAssembleActivity.this)
                        .setMessage("提交失败：" + message)
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
                requestFocus(editSnScan);
                createMediaPlayer(R.raw.failed);
            }

            @Override
            public void showDialog(String message) {
                showTipDialog(message);
            }

            @Override
            public void dismissDialog() {
                dismissTipDialog();
            }
        });
    }

    private void deleteSN(int position){
        ProductItem item = adapter.getItem(position);
        if (item == null){
            return;
        }
        AssembleDeleteBean bean = new AssembleDeleteBean();
        bean.setSn(item.getSerialNumber());
        bean.setProductionOrderId(header.getHeadrId());
        bean.setProductionStationId(header.getStatueId());
        tModel.deleteAssembleSn(bean, new TPresenter<ResponseBody>() {
            @Override
            public void getSuccess(ResponseBody responseBody) {
                int scanNo = header.getScanNo() - 1;
                header.setScanNo(scanNo);
                int count = header.getCount();
                header.setUnScanNo(count - scanNo);
                requestFocus(editSnScan);
                setHeader();
                adapter.removeItem(position);
                createMediaPlayer(R.raw.success);
            }

            @Override
            public void getFailed(String message) {
                new QMUIDialog.MessageDialogBuilder(ProductAssembleActivity.this)
                        .setMessage(message)
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
                createMediaPlayer(R.raw.failed);
            }

            @Override
            public void showDialog(String message) {
                showTipDialog(message);
            }

            @Override
            public void dismissDialog() {
                dismissTipDialog();
            }
        });
    }
    private QMUITipDialog dialog;
    private void showTipDialog(String message){
        dialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(message)
                .create();
        dialog.show();
    }

    private void dismissTipDialog(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}