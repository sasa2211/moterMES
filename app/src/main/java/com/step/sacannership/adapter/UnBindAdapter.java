package com.step.sacannership.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.step.sacannership.R;
import com.step.sacannership.model.bean.DeliveryDetailBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UnBindAdapter extends RecyclerView.Adapter<UnBindAdapter.UnBindHolder> {

    private Context context;
    private List<DeliveryDetailBean> datas;
    private String times;

    public UnBindAdapter(Context context, List<DeliveryDetailBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @NonNull
    @Override
    public UnBindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_item, viewGroup, false);
        return new UnBindHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnBindHolder holder, int i) {
        DeliveryDetailBean deliveryBean = datas.get(i);
        holder.deliveryNo.setVisibility(View.VISIBLE);
        holder.deliveryNo.setText("发运单号："+deliveryBean.getDeliveryBillNo());
        holder.tvMaterialNo.setText("物料编号："+deliveryBean.getMaterialNo());
        holder.tvPalletTime.setText(times);
        holder.tvMaterialDesc.setText("物料名称："+deliveryBean.getMaterialName());
        holder.editNum.setText(""+deliveryBean.getQuantity());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class UnBindHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvMaterialNo)
        TextView tvMaterialNo;
        @BindView(R.id.edit_num)
        TextView editNum;
        @BindView(R.id.tv_pallet_time)
        TextView tvPalletTime;
        @BindView(R.id.tvMaterialDesc)
        TextView tvMaterialDesc;
        @BindView(R.id.tvDeliveryNo)
        TextView deliveryNo;

        public UnBindHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
