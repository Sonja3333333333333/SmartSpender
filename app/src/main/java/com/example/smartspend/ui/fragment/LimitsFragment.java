package com.example.smartspend.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.ui.BudgetViewModel;
import com.example.smartspend.ui.activities.AddLimitActivity;
import com.example.smartspend.ui.adapters.LimitsAdapter;

public class LimitsFragment extends Fragment {

    private LimitsAdapter adapter;
    private BudgetViewModel viewModel;

    public LimitsFragment() {
        // Необхідний порожній конструктор
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_limits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ініціалізація UI
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        View layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        View btnAddLimit = view.findViewById(R.id.btnAddLimit);

        // Налаштування списку (RecyclerView)
        adapter = new LimitsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Підключення ViewModel (Автоматичне оновлення списку)
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        viewModel.getBudgetsWithProgress().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null && !budgets.isEmpty()) {
                layoutEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setBudgets(budgets);
            } else {
                layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Запуск нашої нової активності замість діалогу
        if (btnAddLimit != null) {
            btnAddLimit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddLimitActivity.class);
                startActivity(intent);
            });
        }
    }
}