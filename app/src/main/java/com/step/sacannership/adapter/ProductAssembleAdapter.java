package com.step.sacannership.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.step.sacannership.R;
import com.step.sacannership.model.bean.ProductItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAssembleAdapter extends RecyclerView.Adapter<ProductAssembleAdapter.ProductHolder> {
    private final List<ProductItem> dates;

    public ProductAssembleAdapter() {
        this.dates = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_assemble_item, viewGroup, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        ProductItem item = dates.get(position);
        holder.tvNo.setText(String.valueOf(item.getScanIndex()));
        holder.serialNo.setText("产品sn:"+item.getSerialNumber());
        holder.partSerial.setText("部件sn:"+item.getPartsSerialNumber());
        holder.creator.setText("采集人员"+item.getCreatorName());
        holder.createTime.setText("采集时间"+item.getCreateTime());
        holder.imgDelete.setOnClickListener(v->{

        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNo)
        TextView tvNo;
        @BindView(R.id.serialNo)
        TextView serialNo;
        @BindView(R.id.partSerial)
        TextView partSerial;
        @BindView(R.id.creator)
        TextView creator;
        @BindView(R.id.createTime)
        TextView createTime;
        @BindView(R.id.imgDelete)
        ImageView imgDelete;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addItems(List<ProductItem> dates){
        int start = getItemCount();
        this.dates.addAll(dates);
        notifyItemRangeChanged(start, dates.size());
    }
    public void removeItem(int position){
        this.dates.remove(position);
        notifyItemRemoved(position);
    }
    public void clear(){
        int count = dates.size();
        this.dates.clear();
        notifyItemRangeRemoved(0, count);
    }
}
