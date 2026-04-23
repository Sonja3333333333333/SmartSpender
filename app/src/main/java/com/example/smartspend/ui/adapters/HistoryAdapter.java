package com.example.smartspend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Transaction_Log> transactionList;
    private List<Category> categories;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction_Log transaction);
    }

    // Оновлений конструктор
    public HistoryAdapter(List<Transaction_Log> transactions, List<Category> categories, OnItemClickListener listener) {
        this.transactionList = transactions; // Присвоюємо саме в transactionList
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction_Log transaction = transactionList.get(position);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(transaction));

        // --- ЛОГІКА РОЗДІЛЮВАЧА ДАТИ ---
        SimpleDateFormat headerFormat = new SimpleDateFormat("d MMMM", new Locale("uk"));
        String currentDateStr = headerFormat.format(new Date(transaction.date));

        boolean isNewDay = false;
        if (position == 0) {
            isNewDay = true; // Для першого елемента завжди показуємо дату
        } else {
            Transaction_Log previousTransaction = transactionList.get(position - 1);
            String previousDateStr = headerFormat.format(new Date(previousTransaction.date));
            if (!currentDateStr.equals(previousDateStr)) {
                isNewDay = true; // Якщо дата змінилася — це новий день
            }
        }

        if (isNewDay) {
            holder.tvHeaderDate.setVisibility(View.VISIBLE);
            holder.tvHeaderDate.setText(currentDateStr);
        } else {
            holder.tvHeaderDate.setVisibility(View.GONE);
        }



        String displayName = "";

        if ("income".equals(transaction.type)) {
            displayName = "Дохід";
        } else if ("savings".equals(transaction.type)) {
            displayName = "У скарбничку 🐷";
        } else if ("expense".equals(transaction.type)) {
            // Шукаємо назву категорії
            displayName = "Витрата";
            if (categories != null) {
                for (Category cat : categories) {
                    if (cat.id == transaction.category_id) {
                        displayName = cat.name;
                        break;
                    }
                }
            }
            if (transaction.is_from_savings == 1) {
                displayName += " (зі скарбнички 🐷)";
            }
        }
        holder.tvCategory.setText(displayName);

        //дата
        //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        //holder.tvDate.setText(sdf.format(new Date(transaction.date)));

        // --- ЧАС (у картці показуємо тільки години та хвилини) ---
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(new Date(transaction.date));

        if ("savings".equals(transaction.type)) {
            holder.tvDate.setText(timeText + " • На майбутнє");
        } else {
            holder.tvDate.setText(timeText);
        }

        //сума
        double amount = transaction.sum;
        if ("expense".equals(transaction.type)) {
            holder.tvAmount.setText(String.format("-%.2f UA", amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_expense));
        } else if ("income".equals(transaction.type)){
            holder.tvAmount.setText(String.format("+%.2f UA", amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_income));
        } else{
            holder.tvAmount.setText(String.format("-%.2f UA", amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_savings));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    public void updateData(List<Transaction_Log> newList, List<Category> newCategories) {
        this.transactionList = newList;
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount, tvHeaderDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvHeaderDate = itemView.findViewById(R.id.tvHeaderDate);
        }
    }
}