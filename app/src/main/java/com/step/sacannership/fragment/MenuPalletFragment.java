package com.step.sacannership.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.step.sacannership.BuildConfig;
import com.step.sacannership.R;
import com.step.sacannership.activity.ArriveActivity;
import com.step.sacannership.activity.BackUnBindActivity;
import com.step.sacannership.activity.BindMaterialActivity;
import com.step.sacannership.activity.BindTray;
import com.step.sacannership.activity.DeliveryActivity;
import com.step.sacannership.activity.DeliverySapCancel;
import com.step.sacannership.activity.DeliveryTestActivity;
import com.step.sacannership.activity.ModifyNumActivity;
import com.step.sacannership.activity.ModifyNumCancelActivity;
import com.step.sacannership.activity.PalletMaterialActivity;
import com.step.sacannership.activity.ScanTrayActivity;
import com.step.sacannership.activity.SearchPalletActivity;
import com.step.sacannership.activity.TrayTestActivity;
import com.step.sacannership.activity.UnBindDeliveryActivity;
import com.step.sacannership.adapter.MenuAdapter;
import com.step.sacannership.model.bean.MenuItem;
import com.step.sacannership.tools.SPTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuPalletFragment extends Fragment {

    Unbinder unbinder;
    private boolean hasPallet;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private MenuAdapter menuAdapter;
    private List<MenuItem> datas;

    public static MenuPalletFragment newInstance(boolean hasPallet) {
        MenuPalletFragment fragment = new MenuPalletFragment();
        fragment.hasPallet = hasPallet;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_pallet_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }
                                                        
    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), manager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        datas = new ArrayList<>();
        menuAdapter = new MenuAdapter(datas, getContext());
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setItemClickListener(position -> {
            MenuItem menuItem = datas.get(position);
            String tag = menuItem.getTag();
            Intent intent = new Intent();
            switch (tag) {
                case "submit_pallet"://出库
                    intent.setClass(getContext(), ModifyNumActivity.class);
                    intent.putExtra("pallet", hasPallet);
                    startActivity(intent);
                    break;
                case "submit_cancel"://撤销出库
                    intent.setClass(getContext(), ModifyNumCancelActivity.class);
                    startActivity(intent);
                    break;
                case "search_palletNo"://查发货单托盘号
                    intent.setClass(getContext(), SearchPalletActivity.class);
                    startActivity(intent);
                    break;
//                case "tray_bind"://托盘绑定
//                    startActivity(new Intent(getContext(), BindTray.class));
//                    break;
                case "tray_material_bind"://托盘物料绑定
                    intent.setClass(getContext(), PalletMaterialActivity.class);
                    intent.putExtra("title", "托盘物料绑定");
                    intent.putExtra("hasPallet", true);
                    startActivity(intent);
                    break;
                case "tray_scan"://托盘查看
                    intent.setClass(getContext(), ScanTrayActivity.class);
                    startActivity(intent);
                    break;
                case "bind_material"://发运单物料绑定
                    intent.setClass(getContext(), BindMaterialActivity.class);
                    startActivity(intent);
                    break;
                case "tray_check"://托盘检验
                    intent.setClass(getContext(), TrayTestActivity.class);
                    intent.putExtra("type", "check");
                    startActivity(intent);
                    break;
                case "tray_check_cancel"://托盘检验撤销
                    intent.setClass(getContext(), TrayTestActivity.class);
                    intent.putExtra("type", "cancel_check");
                    startActivity(intent);
                    break;
                case "unbind_delivery":
                    intent.setClass(getContext(), UnBindDeliveryActivity.class);
                    startActivity(intent);
                    break;
                case "delivery_check"://发运单检验
                    intent.setClass(getContext(), DeliveryTestActivity.class);
                    intent.putExtra("pallet", hasPallet);
                    startActivity(intent);
                    break;
                case "delivery_check_cancel"://发运单检验撤销

                    break;
                case "delivery"://发运
                    intent.setClass(getContext(), DeliveryActivity.class);
                    intent.putExtra("pallet", hasPallet);
                    startActivity(intent);
                    break;
                case "delivery_cancel"://撤销发运
                    intent.setClass(getContext(), DeliverySapCancel.class);
                    intent.putExtra("pallet", hasPallet);
                    startActivity(intent);
                    break;
                case "pallet_arrive":
                    intent.setClass(getContext(), ArriveActivity.class);
                    intent.putExtra("pallet", hasPallet);
                    startActivity(intent);
                    break;

                case "back_unBind":
                    intent.setClass(getContext(), BackUnBindActivity.class);
                    startActivity(intent);
                    break;
            }
        });
        initItem();
    }

    public void initItem() {
        try {
            String authority = SPTool.getAuthority();
            MenuItem menuItem = null;
            if ("1".equals(authority.substring(392, 393))) {//SAP提交（出库）
                menuItem = new MenuItem("出库", "submit_pallet");
                datas.add(menuItem);

                menuItem = new MenuItem("出库撤销", "submit_cancel");
                datas.add(menuItem);
            }

            if (hasPallet){
                if ("1".equals(authority.substring(393, 394))) {//托盘管理编辑

//                    menuItem = new MenuItem("托盘绑定", "tray_bind");
//                    datas.add(menuItem);
                    menuItem = new MenuItem("托盘物料绑定", "tray_material_bind");
                    datas.add(menuItem);

                    menuItem = new MenuItem("托盘查看", "tray_scan");
                    datas.add(menuItem);
                }

                if ("1".equals(authority.substring(394, 395))) {//SAP更新(检验)
                    menuItem = new MenuItem("托盘检验", "tray_check");
                    datas.add(menuItem);
                    menuItem = new MenuItem("托盘检验撤销", "tray_check_cancel");
                    datas.add(menuItem);
                }

                if ("1".equals(authority.substring(393, 394))) {//托盘管理编辑
                    menuItem = new MenuItem("查发货单托盘号", "search_palletNo");
                    datas.add(menuItem);
                }

                if ("1".equals(authority.substring(389, 390))){//到货操作
                    menuItem = new MenuItem("托盘到货", "pallet_arrive");
                    datas.add(menuItem);
                }
            }else {//
                if ("1".equals(authority.substring(393, 394))) {//物料绑定
                    menuItem = new MenuItem("发运单物料绑定", "bind_material");
                    datas.add(menuItem);

                    menuItem = new MenuItem("无托盘解绑发货单", "unbind_delivery");
                    datas.add(menuItem);
                }
            }

            if ("1".equals(authority.substring(394, 395))) {//SAP更新(检验)
                menuItem = new MenuItem("发货单检验", "delivery_check");
                datas.add(menuItem);

//                menuItem = new MenuItem("发货单检验撤销", "delivery_check_cancel");
//                datas.add(menuItem);
            }

            if ("1".equals(authority.substring(395, 396))) {//SAP发货单管理--确认（发运）
                menuItem = new MenuItem("发运", "delivery");
                datas.add(menuItem);
                menuItem = new MenuItem("发运撤销", "delivery_cancel");
                datas.add(menuItem);
            }

            if ("1".equals(authority.substring(386, 387)) || BuildConfig.DEBUG) {//SAP更新(检验)
                menuItem = new MenuItem("退货机器解绑", "back_unBind");
                datas.add(menuItem);
            }
            menuAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}