package com.example.smartspend.ui.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpendSavingsActivity extends AppCompatActivity {

    private EditText etAmount;
    private Spinner spinnerCategory;
    private AppDatabase db;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_savings);

        db = AppDatabase.getInstance(this);

        etAmount = findViewById(R.id.et_spend_amount);
        spinnerCategory = findViewById(R.id.spinner_spend_category);
        Button btnConfirm = findViewById(R.id.btn_confirm_spend);
        Button btnCancel = findViewById(R.id.btn_cancel_spend);

        loadCategories();

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> saveSavingsExpense());
        }
    }

    private void loadCategories() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            categoryList = db.categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();

            for (Category cat : categoryList) {
                categoryNames.add(cat.name);
            }

            if (categoryNames.isEmpty()) {
                categoryNames.add("Інше");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            });
        });
    }

    private void saveSavingsExpense() {
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError("Введіть суму!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Некоректний формат!");
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Сума має бути більшою за 0");
            return;
        }

        int selectedIndex = spinnerCategory.getSelectedItemPosition();
        int categoryId = 0;
        if (!categoryList.isEmpty() && selectedIndex >= 0) {
            categoryId = categoryList.get(selectedIndex).id;
        }

        final int finalCatId = categoryId;
        final double finalAmount = amount;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Transaction_Log transaction = new Transaction_Log();
            transaction.sum = finalAmount;

            transaction.type = "expense";
            transaction.is_from_savings = 1;

            transaction.date = System.currentTimeMillis();
            transaction.category_id = finalCatId;
            transaction.comment = "Витрата зі скарбнички";

            db.transactionDao().insert(transaction);

            runOnUiThread(() -> {
                Toast.makeText(this, "Витрату зі скарбнички успішно збережено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}