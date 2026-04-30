package com.example.smartspend.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.data.BudgetProgressModel;
import com.example.smartspend.ui.activities.EditLimitActivity;

import java.util.ArrayList;
import java.util.List;

public class LimitsAdapter extends RecyclerView.Adapter<LimitsAdapter.LimitViewHolder> {

    private List<BudgetProgressModel> budgetList = new ArrayList<>();

    public void setBudgets(List<BudgetProgressModel> budgets) {
        this.budgetList = budgets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LimitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_limit, parent, false);
        return new LimitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LimitViewHolder holder, int position) {
        BudgetProgressModel item = budgetList.get(position);

        holder.tvCategory.setText(item.categoryName);
        holder.tvLimitValue.setText(String.format("Ліміт: %.0f UAH", item.limitSum));
        holder.tvSpentValue.setText(String.format("Витрачено: %.0f UAH", item.spentSum));

        // Рахуємо прогрес у відсотках
        int progressPercent = 0;
        if (item.limitSum > 0) {
            progressPercent = (int) ((item.spentSum / item.limitSum) * 100);
        }

        holder.progressBar.setProgress(Math.min(progressPercent, 100));

        // Встановлюємо колір залежно від прогресу
        Context context = holder.progressBar.getContext();

        int colorRed = ContextCompat.getColor(context, R.color.red_expense);
        int colorGreen = ContextCompat.getColor(context, R.color.green_income);
        int colorYellow = ContextCompat.getColor(context, R.color.yellow);

        ColorStateList tint;
        if (progressPercent >= 80) {
            tint = ColorStateList.valueOf(colorRed);
        } else if (progressPercent <= 40) {
            tint = ColorStateList.valueOf(colorGreen);
        } else {
            tint = ColorStateList.valueOf(colorYellow);
        }

        holder.progressBar.setProgressTintList(tint);

        // Обробка кліку на іконку "Олівчика"
        holder.btnEditLimit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditLimitActivity.class);
            intent.putExtra("CATEGORY_ID", item.categoryId);
            intent.putExtra("CATEGORY_NAME", item.categoryName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    static class LimitViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvLimitValue, tvSpentValue;
        ProgressBar progressBar;
        ImageView btnEditLimit; // Змінено назву на ту, що у твоєму XML

        public LimitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvLimitCategory);
            tvLimitValue = itemView.findViewById(R.id.tvLimitValue);
            tvSpentValue = itemView.findViewById(R.id.tvSpentValue);
            progressBar = itemView.findViewById(R.id.pbLimitProgress);
            // Знаходимо нашу іконку за правильним ID
            btnEditLimit = itemView.findViewById(R.id.btnEditLimit);
        }
    }
}