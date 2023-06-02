package com.step.sacannership.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.step.sacannership.R;
import com.step.sacannership.adapter.ProductAssembleAdapter;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.ProductHeader;
import com.step.sacannership.model.bean.ProductItem;
import com.step.sacannership.model.bean.Request;
import com.step.sacannership.tools.ToastUtils;
import java.util.List;
import butterknife.BindView;

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

        tvSet.setOnClickListener(v->{

        });

        setHeader();
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
        request.put("orderId", 90736);
        request.put("stationId", 11);
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
    }

    @Override
    public void getFailed(String message) {
        if (adapter.getItemCount() == 0){
            emptyView.show("", "加载失败："+message);
        }else {
            ToastUtils.showToast(this, message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}