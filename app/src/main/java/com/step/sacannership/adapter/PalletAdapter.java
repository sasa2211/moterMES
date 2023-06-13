package com.step.sacannership.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.step.sacannership.R;
import com.step.sacannership.activity.ScanTrayActivity;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PalletAdapter extends RecyclerView.Adapter<PalletAdapter.PalletHolder> {

    private Context context;
    private List<String> datas;

    public PalletAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public PalletHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.pallet_item_view, viewGroup, false);
        return new PalletHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PalletHolder holder, int position) {
        String text = datas.get(position);
        holder.tvPalletName.setText(text);
        holder.tvPalletName.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScanTrayActivity.class);
            intent.putExtra("palletID", text);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class PalletHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_pallet_name)
        TextView tvPalletName;
        public PalletHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
