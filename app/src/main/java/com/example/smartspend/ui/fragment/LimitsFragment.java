package com.example.smartspend.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.dao.BudgetDao;
import com.example.smartspend.data.dao.CategoryDao;
import com.example.smartspend.data.entities.Budgets;
import com.example.smartspend.data.entities.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LimitsFragment extends Fragment {

    private FloatingActionButton fabAddLimit;
    private AppDatabase db;
    private CategoryDao categoryDao;
    private BudgetDao budgetDao;

    public LimitsFragment() {
        // Необхідний порожній конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Використовуємо ваш XML макет
        View view = inflater.inflate(R.layout.fragment_limits, container, false);

        // Ініціалізація БД та DAO
        db = AppDatabase.getInstance(requireContext());
        categoryDao = db.categoryDao();
        budgetDao = db.budgetDao();

        // Кнопка додавання ліміту
        fabAddLimit = view.findViewById(R.id.fab_add_limit);
        fabAddLimit.setOnClickListener(v -> showAddLimitDialog());

        return view;
    }

    private void showAddLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_limit, null);
        builder.setView(dialogView);

        Spinner spinnerCategories = dialogView.findViewById(R.id.spinner_categories);
        EditText etAmount = dialogView.findViewById(R.id.et_limit_amount);

        // Завантаження категорій у фоновому потоці
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = categoryDao.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : categories) {
                categoryNames.add(c.getCategoryName());
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
            newBudget.setCategoryId(categoryId);
            newBudget.setAmount(amount);

            budgetDao.insertBudget(newBudget);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ліміт встановлено для: " + categoryName, Toast.LENGTH_SHORT).show();
                    // Тут ви можете викликати метод для оновлення списку лімітів, якщо він є
                });
            }
        });
    }
}