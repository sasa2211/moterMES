package com.step.sacannership.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.step.sacannership.R;
import com.step.sacannership.model.bean.DeliveryBillPalletDetailsBean;
import com.step.sacannership.model.bean.TrayDeliveryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryAdapter extends BaseExpandableRecyclerViewAdapter<TrayDeliveryBean, DeliveryBillPalletDetailsBean, DeliveryAdapter.GroupHolder, DeliveryAdapter.DeliveryHolder> {

    private List<TrayDeliveryBean> datas;
    private Context context;
    private int type;
    private EditNumListener listener;

    public void setListener(EditNumListener listener) {
        this.listener = listener;
    }

    //1-绑定（物料绑定）   2-查看（删除绑定） 3 - 检验  4-发运
    public DeliveryAdapter(List<TrayDeliveryBean> datas, Context context, int type) {
        this.datas = datas;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    public void clear(){
        datas.clear();
        notifyDataSetChanged();
    }

    @Override
    public TrayDeliveryBean getGroupItem(int groupIndex) {
        return datas.get(groupIndex);
    }

    @Override
    public GroupHolder onCreateGroupViewHolder(ViewGroup parent, int groupViewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.header_view, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(GroupHolder holder, TrayDeliveryBean groupBean, boolean isExpand) {
        holder.title.setText("发运单号："+groupBean.getDeliveryBillNO());
        StringBuilder sbTime = new StringBuilder();
        if (type == 1){//绑定
            sbTime.append("出库时间："+groupBean.getOutStockTime());
        }else if (type == 2){//查看
            sbTime.append("提交时间："+groupBean.getCreateTime());
            sbTime.append("\n出库时间："+groupBean.getOutStockTime());
            sbTime.append("\n绑定时间："+groupBean.getPalletBindCompletedTime());
        }else if (type == 3){//检验
            sbTime.append("绑定时间："+groupBean.getPalletBindCompletedTime());

        }else if (type == 4){
            sbTime.append("检验时间："+groupBean.getInspectCompletedTime());
        }
        holder.tvData.setText(sbTime.toString());
        if (type == 4 || type == 1 || type == 3 || type == -1){
            holder.tvBindMaterial.setVisibility(View.GONE);
        }else {
            holder.tvBindMaterial.setText(type == 2  ? "删除绑定" : "");
            holder.tvBindMaterial.setOnClickListener(v -> {
                String no = groupBean.getDeliveryBillNO();
                listener.bindMaterialListener(getGroupIndex(groupBean), no);
            });
        }
    }

    @Override
    public DeliveryHolder onCreateChildViewHolder(ViewGroup parent, int childViewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_item, parent, false);
        return new DeliveryHolder(view);
    }

    @Override
    public void onBindChildViewHolder(DeliveryHolder holder, TrayDeliveryBean groupBean, DeliveryBillPalletDetailsBean data) {
        holder.tvMaterialNo.setText("物料编号："+data.getMaterialNo());
        holder.tvMaterialDesc.setText("物料描述："+data.getMaterialName());
        holder.editNum.setText(String.valueOf(data.getQuantity()));
        if (groupBean.isNewBind()){
            holder.tvNumMsg.setVisibility(View.GONE);
        }else {
            holder.tvNumMsg.setVisibility(View.VISIBLE);
        }
        StringBuilder sb = new StringBuilder("发货单数量：");
        sb.append(data.getBillQuantity());
        sb.append(",已出库：");
        sb.append(data.getOutQuantity());
        sb.append(",已发货：");
        sb.append(data.getBindQuantity());
        sb.append(",已检验：");
        sb.append(data.getQualifiedQuantity());

        holder.tvNumMsg.setText(sb.toString());

        holder.tvPalletTime.setVisibility(View.GONE);
        if (type == 1){
            holder.tvBindSN.setVisibility(View.VISIBLE);
        }else {
            holder.tvBindSN.setVisibility(View.GONE);
        }

        holder.tvBindSN.setOnClickListener(v -> {
            String no = groupBean.getDeliveryBillNO();
            listener.bindMaterialSn(data.getMaterialNo(), data.getRowIndex(), no, data.getDeliveryBillID());
        });
        holder.editNum.setEnabled(data.getSnQuantity() == 0);
        holder.editNum.setOnClickListener(view -> {
            double snCount = data.getSnQuantity();//数量大于0  禁止编辑
            if (snCount > 0){
                return;
            }
            listener.changeNum(groupBean.getDeliveryBillNO(), data.getMaterialNo(), data.getRowIndex());
        });
    }

    public class GroupHolder extends BaseExpandableRecyclerViewAdapter.BaseGroupViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_data)
        TextView tvData;
        @BindView(R.id.tv_bind_material)
        TextView tvBindMaterial;
        @BindView(R.id.flag)
        ImageView flag;

        public GroupHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void onExpandStatusChanged(RecyclerView.Adapter relatedAdapter, boolean isExpanding) {
            flag.setImageResource(isExpanding ? R.mipmap.down : R.mipmap.up);
        }
    }

    public class DeliveryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMaterialNo)
        TextView tvMaterialNo;
        @BindView(R.id.edit_num)
        TextView editNum;
        @BindView(R.id.tv_pallet_time)
        TextView tvPalletTime;
        @BindView(R.id.tvMaterialDesc)
        TextView tvMaterialDesc;
        @BindView(R.id.tv_bind_material)
        TextView tvBindSN;
        @BindView(R.id.tv_num_msg)
        TextView tvNumMsg;
        public DeliveryHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface EditNumListener {
        void changeNum(String deliveryNo, String materialNo, String rowIndex);
        void bindMaterialListener(int groupIndex, String billNo);
        void bindMaterialSn(String materialNo, String rowIndex, String billNo, int billID);
    }

    /**
     * 需要获得分组的序号
     * 数据在分组中的序号
     * */
    public int[] getClickPosition(String deliveryNo, String materialNo, String rowIndex){
        int [] arr = {-1, -1};
        for (int i = 0; i < datas.size(); i ++){
            TrayDeliveryBean deliveryBean = datas.get(i);
            if (deliveryBean.getDeliveryBillNO().equals(deliveryNo)){
                arr[0] = i;
                List<DeliveryBillPalletDetailsBean> palletLists = deliveryBean.getDeliveryBillPalletDetails();
                for (int j = 0; j < palletLists.size(); j ++){
                    DeliveryBillPalletDetailsBean palletDetailsBean = palletLists.get(j);
                    if (palletDetailsBean.getMaterialNo().equals(materialNo) && palletDetailsBean.getRowIndex().equals(rowIndex)){
                        arr[1] = j;
                        break;
                    }
                }
                break;
            }
        }
        return arr;
    }
}
