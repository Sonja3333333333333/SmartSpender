package com.example.smartspend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartspend.R;
import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.LegendViewHolder> {

    private final List<LegendItem> items;

    public LegendAdapter(List<LegendItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public LegendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_legend, parent, false);
        return new LegendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LegendViewHolder holder, int position) {
        LegendItem item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvAmount.setText(String.format("%.2f ₴", item.amount));
        holder.viewColor.setBackgroundColor(item.color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LegendViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvName, tvAmount;

        public LegendViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.view_category_color);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_category_amount);
        }
    }

    // Проста модель даних для рядка
    public static class LegendItem {
        String name;
        double amount;
        int color;

        public LegendItem(String name, double amount, int color) {
            this.name = name;
            this.amount = amount;
            this.color = color;
        }
    }
}