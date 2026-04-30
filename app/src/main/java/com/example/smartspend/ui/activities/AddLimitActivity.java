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
import com.example.smartspend.data.entities.Budgets;
import com.example.smartspend.data.entities.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddLimitActivity extends AppCompatActivity {

    private Spinner spinnerCategories;
    private EditText etLimitAmount;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_limit);

        spinnerCategories = findViewById(R.id.spinner_categories);
        etLimitAmount = findViewById(R.id.et_limit_amount);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        db = AppDatabase.getInstance(this);

        loadCategories();

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNewLimit());
    }

    private void loadCategories() {
        new Thread(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();

            for (Category c : categories) {
                categoryNames.add(c.name);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(adapter);
            });
        }).start();
    }

    private void saveNewLimit() {
        String amountStr = etLimitAmount.getText().toString().trim();
        Object selectedItem = spinnerCategories.getSelectedItem();

        if (selectedItem == null) {
            Toast.makeText(this, "Спочатку додайте категорії в налаштуваннях", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            etLimitAmount.setError("Введіть суму");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String categoryName = selectedItem.toString();

        new Thread(() -> {
            int categoryId = db.categoryDao().getCategoryIdByName(categoryName);

            Budgets newBudget = new Budgets();
            newBudget.category_id = categoryId;
            newBudget.limit_sum = amount;

            SimpleDateFormat sdf = new SimpleDateFormat("LLLL yyyy", new Locale("uk"));
            String currentMonthYear = sdf.format(new Date());
            newBudget.period = currentMonthYear.substring(0, 1).toUpperCase() + currentMonthYear.substring(1);

            db.budgetDao().insert(newBudget);

            runOnUiThread(() -> {
                Toast.makeText(this, "Ліміт встановлено!", Toast.LENGTH_SHORT).show();
                finish(); // Закриваємо активність після збереження
            });
        }).start();
    }
}