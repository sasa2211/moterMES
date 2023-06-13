package com.step.sacannership.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.step.sacannership.R;
import com.step.sacannership.adapter.PalletAdapter;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class SearchPalletActivity extends BaseActivity implements TPresenter<List<String>> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.delivery_no)
    EditText deliveryNoTV;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty)
    QMUIEmptyView empty;

    private PalletAdapter adapter;
    List<String> datas;
    @Override
    protected int contentView() {
        return R.layout.search_pallet_view;
    }

    @Override
    protected void initView() {
        initToolBar(toolbar);
        requestFocus(deliveryNoTV);
        deliveryNoTV.setOnEditorActionListener((v, actionId, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP){
                getData();
            }
            QMUIKeyboardHelper.hideKeyboard(deliveryNoTV);
            return true;
        });
        datas = new ArrayList<>();
        adapter = new PalletAdapter(this, datas);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        recyclerView.setAdapter(adapter);
    }
    private void getData(){
        String deliveryNo = deliveryNoTV.getText().toString().trim();
        if (TextUtils.isEmpty(deliveryNo)){
            ToastUtils.showToast(this, "请扫描发货单号");
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        requestFocus(deliveryNoTV);
        tModel.getTrayPallets(deliveryNo, this);
    }

    @Override
    public void getSuccess(List<String> response) {
        datas.clear();
        if (response.isEmpty()){
            empty.show("正在加载", "");
            empty.setLoadingShowing(false);
        }

        datas.addAll(response);
        adapter.notifyDataSetChanged();
        if (datas.isEmpty()){
            empty.show("暂无数据", "");
        }
    }

    @Override
    public void getFailed(String message) {
        empty.show("加载失败", message);
        empty.setLoadingShowing(false);
    }

    @Override
    public void showDialog(String message) {
        empty.show("正在加载", "");
        empty.setLoadingShowing(true);
    }

    @Override
    public void dismissDialog() {
        empty.hide();
    }
}