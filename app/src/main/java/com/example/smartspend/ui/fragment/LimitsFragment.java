package com.example.smartspend.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.dao.BudgetDao;
import com.example.smartspend.data.dao.CategoryDao;
import com.example.smartspend.data.entities.Budgets;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.ui.BudgetViewModel;
import com.example.smartspend.ui.adapters.LimitsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LimitsFragment extends Fragment {

    private LimitsAdapter adapter;
    private BudgetViewModel viewModel;

    private AppDatabase db;
    private CategoryDao categoryDao;
    private BudgetDao budgetDao;

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

        // Ініціалізація БД та DAO
        db = AppDatabase.getInstance(requireContext());
        categoryDao = db.categoryDao();
        budgetDao = db.budgetDao();

        // Ініціалізація UI
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory); // Перевір, чи в XML лімітів ID саме такий
        View layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Шукаємо кнопку (Git merge міг залишити або fab_add_limit, або btnAddLimit)
        View btnAddLimit = view.findViewById(R.id.btnAddLimit);
        if (btnAddLimit == null) {
            btnAddLimit = view.findViewById(R.id.btnAddLimit);
        }

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

        if (btnAddLimit != null) {
            btnAddLimit.setOnClickListener(v -> showAddLimitDialog());
        }
    }

    private void showAddLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_limit, null);
        builder.setView(dialogView);

        Spinner spinnerCategories = dialogView.findViewById(R.id.spinner_categories);
        EditText etAmount = dialogView.findViewById(R.id.et_limit_amount);

        // Завантаження категорій у фоновому потоці для Spinner
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = categoryDao.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : categories) {
                // Зверни увагу: якщо у вашому класі Category поле називається name (як ми робили раніше),
                // то тут має бути c.name. Якщо ви додали геттери - залишай c.getCategoryName().
                // Я залишив c.name для сумісності з попереднім кодом.
                categoryNames.add(c.name);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategories.setAdapter(adapter);
                });
            }
        });

        builder.setPositiveButton("Зберегти", (dialog, which) -> {
            String amountStr = etAmount.getText().toString();
            Object selectedItem = spinnerCategories.getSelectedItem();

            if (selectedItem == null) {
                Toast.makeText(getContext(), "Спочатку додайте категорії в налаштуваннях", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!amountStr.isEmpty()) {
                saveLimit(selectedItem.toString(), Double.parseDouble(amountStr));
            } else {
                Toast.makeText(getContext(), "Будь ласка, введіть суму", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void saveLimit(String categoryName, double amount) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Отримуємо ID категорії за її назвою
            int categoryId = categoryDao.getCategoryIdByName(categoryName);

            Budgets newBudget = new Budgets();

            newBudget.category_id = categoryId;
            newBudget.limit_sum = amount;

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("LLLL yyyy", new java.util.Locale("uk"));
            String currentMonthYear = sdf.format(new java.util.Date());
            newBudget.period = currentMonthYear.substring(0, 1).toUpperCase() + currentMonthYear.substring(1);

            budgetDao.insert(newBudget);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ліміт встановлено для: " + categoryName, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}