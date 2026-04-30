package com.example.smartspend.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Budgets;

public class EditLimitActivity extends AppCompatActivity {

    private TextView tvCategoryName;
    private EditText etLimitAmount;
    private AppDatabase db;
    private Budgets currentBudget;
    private int categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_limit);

        tvCategoryName = findViewById(R.id.tvEditCategoryName);
        etLimitAmount = findViewById(R.id.etEditLimitAmount);
        Button btnSave = findViewById(R.id.btnSaveEdit);
        Button btnCancel = findViewById(R.id.btnCancelEdit);

        db = AppDatabase.getInstance(this);

        // Отримуємо дані, які передав адаптер (LimitsAdapter)
        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryId != -1 && categoryName != null) {
            tvCategoryName.setText(categoryName);
            loadBudgetData();
        } else {
            Toast.makeText(this, "Помилка завантаження ліміту", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Обробка натискань кнопок
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveLimitChanges());
        }
    }

    private void loadBudgetData() {
        new Thread(() -> {
            // Шукаємо ліміт у базі за ID категорії
            currentBudget = db.budgetDao().getBudgetByCategory(categoryId);

            if (currentBudget != null) {
                runOnUiThread(() -> {
                    // Показуємо поточну суму ліміту в полі для вводу
                    etLimitAmount.setText(String.valueOf(currentBudget.limit_sum));
                });
            }
        }).start();
    }

    private void saveLimitChanges() {
        String amountStr = etLimitAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etLimitAmount.setError("Введіть суму");
            return;
        }

        double newAmount = Double.parseDouble(amountStr);

        if (newAmount <= 0) {
            etLimitAmount.setError("Сума має бути більшою за 0");
            return;
        }

        new Thread(() -> {
            if (currentBudget != null) {
                // Оновлюємо суму в об'єкті (використовуємо поле limit_sum, як у тебе в БД)
                currentBudget.limit_sum = newAmount;
                // Зберігаємо зміни в базу даних
                db.budgetDao().update(currentBudget);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Ліміт успішно оновлено!", Toast.LENGTH_SHORT).show();
                    // Закриваємо вікно після збереження
                    finish();
                });
            }
        }).start();
    }
}