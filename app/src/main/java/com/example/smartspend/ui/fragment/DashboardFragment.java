package com.example.smartspend.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;
import com.example.smartspend.ui.activities.AddTransactionActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private AppDatabase db;
    private PieChart pieChart;
    private View balanceGroup;
    private View emptyDataParent;
    private TextView tvBalanceAmount;

    private TextView tvMonthName;
    private int currentMonthOffset = 0;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        pieChart = view.findViewById(R.id.pieChart);
        balanceGroup = view.findViewById(R.id.balance_group);
        emptyDataParent = view.findViewById(R.id.empty_data_parent);
        tvBalanceAmount = view.findViewById(R.id.tv_balance_amount);

        tvMonthName = view.findViewById(R.id.tv_month_name);
        ImageView btnPrev = view.findViewById(R.id.btn_prev_month);
        ImageView btnNext = view.findViewById(R.id.btn_next_month);

        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> {
                currentMonthOffset--;
                loadDashboardData();
            });
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                currentMonthOffset++;
                loadDashboardData();
            });
        }

        ImageView btnAdd = view.findViewById(R.id.btn_add_transaction);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        ImageView btnSettings = view.findViewById(R.id.btn_settings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, currentMonthOffset);

        if (tvMonthName != null && isAdded() && getActivity() != null) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", new Locale("uk"));
            String monthTitle = monthFormat.format(calendar.getTime());
            String capitalizedMonth = monthTitle.substring(0, 1).toUpperCase() + monthTitle.substring(1);
            getActivity().runOnUiThread(() -> tvMonthName.setText(capitalizedMonth));
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endOfMonth = calendar.getTimeInMillis();

        new Thread(() -> {
            List<Transaction_Log> currentMonthTransactions = db.transactionDao().getTransactionsForPeriod(startOfMonth, endOfMonth);
            List<Category> allCategories = db.categoryDao().getAllCategories();

            Map<Integer, String> categoryNames = new HashMap<>();
            if (allCategories != null) {
                for (Category cat : allCategories) {
                    categoryNames.put(cat.id, cat.name);
                }
            }

            double totalIncome = 0;
            double totalExpense = 0;
            Map<String, Double> expenseByCategory = new HashMap<>();

            if (currentMonthTransactions != null) {
                for (Transaction_Log transaction : currentMonthTransactions) {
                    if ("income".equals(transaction.type)) {
                        totalIncome += transaction.sum;
                    } else if ("expense".equals(transaction.type)) {
                        totalExpense += transaction.sum;

                        String catName = categoryNames.get(transaction.category_id);
                        if (catName == null) {
                            catName = "Інше";
                        }

                        double currentCatSum = 0.0;
                        if (expenseByCategory.containsKey(catName)) {
                            currentCatSum = expenseByCategory.get(catName);
                        }
                        expenseByCategory.put(catName, currentCatSum + transaction.sum);
                    }
                    else if ("savings".equals(transaction.type)) {
                        totalExpense += transaction.sum;
                    }
                }
            }

            final double finalBalance = totalIncome - totalExpense;

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvBalanceAmount.setText(String.format("%.2f ₴", finalBalance));

                    if (expenseByCategory.isEmpty()) {
                        pieChart.setVisibility(View.GONE);
                        balanceGroup.setVisibility(View.GONE);
                        emptyDataParent.setVisibility(View.VISIBLE);
                    } else {
                        pieChart.setVisibility(View.VISIBLE);
                        balanceGroup.setVisibility(View.VISIBLE);
                        emptyDataParent.setVisibility(View.GONE);
                        setupPieChart(expenseByCategory);
                    }
                });
            }
        }).start();
    }

    private void setupPieChart(Map<String, Double> expenseByCategory) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Витрати");

        int[] colors = {
                Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"),
                Color.parseColor("#FFC107"), Color.parseColor("#F44336"),
                Color.parseColor("#9C27B0"), Color.parseColor("#FF9800")
        };

        ArrayList<Integer> colorList = new ArrayList<>();
        for (int color : colors) {
            colorList.add(color);
        }
        dataSet.setColors(colorList);

        dataSet.setValueTextSize(14f);

        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);

        pieChart.getLegend().setEnabled(false);

        pieChart.animateY(1000);

        pieChart.invalidate();
    }
}