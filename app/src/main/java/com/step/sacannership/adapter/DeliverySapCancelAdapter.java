package com.step.sacannership.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.step.sacannership.R;
import com.step.sacannership.model.bean.DeliveryDetailBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliverySapCancelAdapter extends RecyclerView.Adapter<DeliverySapCancelAdapter.DeliveryHolder> {

    private List<DeliveryDetailBean> datas;
    private Context context;
    private String outStockData = "";

    public DeliverySapCancelAdapter(List<DeliveryDetailBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void setOutStockData(String outStockData) {
        this.outStockData = outStockData;
    }

    @NonNull
    @Override
    public DeliveryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.modifynum_item, viewGroup, false);
        return new DeliveryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryHolder holder, int i) {
        DeliveryDetailBean detailBean = datas.get(i);
        holder.materialNo.setText("物料编号："+detailBean.getMaterialNo());
        holder.materialName.setText("物料名称："+detailBean.getMaterialName());
        holder.tvData.setText("出库时间："+outStockData);
        holder.editNum.setText(String.valueOf(detailBean.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class DeliveryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.material_no)
        TextView materialNo;
        @BindView(R.id.material_name)
        TextView materialName;
        @BindView(R.id.tv_data)
        TextView tvData;
        @BindView(R.id.edit_num)
        TextView editNum;
        public DeliveryHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
