package com.step.sacannership.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.step.sacannership.R;
import com.step.sacannership.model.bean.MenuItem;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    private List<MenuItem> menusData;
    private Context context;

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuAdapter(List<MenuItem> menusData, Context context) {
        this.menusData = menusData;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, viewGroup, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder menuHolder, int i) {
        menuHolder.menuItem.setText(menusData.get(i).getContent());
        menuHolder.menuItem.setOnClickListener(v -> {
            if (itemClickListener != null){
                itemClickListener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menusData.size();
    }

    public class MenuHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.menu_item)
        TextView menuItem;
        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
