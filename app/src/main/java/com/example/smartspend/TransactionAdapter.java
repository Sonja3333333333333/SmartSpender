package com.example.smartspend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction_Log> transactions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction_Log transaction);
    }

    public TransactionAdapter(List<Transaction_Log> transactions, OnItemClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction_Log transaction = transactions.get(position);
        // Використовуємо .sum
        holder.text1.setText(transaction.type + ": " + transaction.sum + " UAH");
        holder.text2.setText(transaction.date + " " + (transaction.comment != null ? transaction.comment : ""));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}