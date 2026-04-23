package com.example.smartspend.ui.activities; // ВИПРАВЛЕНО: тепер вказано правильну папку

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// ДОДАНО: імпорти, щоб бачити файли з інших папок
import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Transaction_Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private int currentType = 0; // 0 - витрати, 1 - доходи, 2 - скарбничка
    private Button btnTypeExpense, btnTypeIncome, btnTypeSavings, btnSave;
    private View categoryContainer;
    private List<Button> categoryButtons = new ArrayList<>();

    private AppDatabase db;
    private String selectedCategoryName = "Інше";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        db = AppDatabase.getInstance(this);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        btnTypeExpense = findViewById(R.id.btnTypeExpense);
        btnTypeIncome = findViewById(R.id.btnTypeIncome);
        btnTypeSavings = findViewById(R.id.btnTypeSavings);
        btnSave = findViewById(R.id.btnAdd);
        categoryContainer = findViewById(R.id.categoryContainer);
        EditText inputDate = findViewById(R.id.inputDate);

        inputDate.setOnClickListener(v -> showDatePickerDialog());

        btnTypeExpense.setOnClickListener(v -> updateTypeSelection(0));
        btnTypeIncome.setOnClickListener(v -> updateTypeSelection(1));
        btnTypeSavings.setOnClickListener(v -> updateTypeSelection(2));

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveTransaction());
        }

        setupCategoryButtons();
        updateTypeSelection(0);

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
    }

    private void updateTypeSelection(int type) {
        EditText inputAmount = findViewById(R.id.inputAmount);
        EditText inputComment = findViewById(R.id.inputComment);
        EditText inputDate = findViewById(R.id.inputDate);

        if (inputAmount != null) inputAmount.setText("");
        if (inputComment != null) inputComment.setText("");
        if (inputDate != null) inputDate.setText(getTodayDate());

        int activeColor = Color.parseColor("#4A86F7");
        int inactiveColor = Color.parseColor("#F0F0F0");

        btnTypeExpense.setBackgroundTintList(ColorStateList.valueOf(type == 0 ? activeColor : inactiveColor));
        btnTypeExpense.setTextColor(type == 0 ? Color.WHITE : Color.parseColor("#666666"));

        btnTypeIncome.setBackgroundTintList(ColorStateList.valueOf(type == 1 ? activeColor : inactiveColor));
        btnTypeIncome.setTextColor(type == 1 ? Color.WHITE : Color.parseColor("#666666"));

        btnTypeSavings.setBackgroundTintList(ColorStateList.valueOf(type == 2 ? activeColor : inactiveColor));
        btnTypeSavings.setTextColor(type == 2 ? Color.WHITE : Color.parseColor("#666666"));

        if (categoryContainer != null) {
            categoryContainer.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        }

        resetCategorySelection();
        currentType = type;

        selectedCategoryName = (type == 0) ? "Інше" : "Дохід/Скарбничка";
    }

    private void resetCategorySelection() {
        for (Button b : categoryButtons) {
            if (b != null) {
                b.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            }
        }
    }

    private void setupCategoryButtons() {
        int[] catIds = {
                R.id.cat_products, R.id.cat_cafe, R.id.cat_transport,
                R.id.cat_utility, R.id.cat_entertainment, R.id.cat_health,
                R.id.cat_beauty, R.id.cat_gift, R.id.cat_clothes
        };

        for (int id : catIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                categoryButtons.add(btn);
                btn.setOnClickListener(v -> {
                    resetCategorySelection();
                    v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A1C4FD")));

                    int clickedId = v.getId();
                    if (clickedId == R.id.cat_products) selectedCategoryName = "Продукти";
                    else if (clickedId == R.id.cat_cafe) selectedCategoryName = "Кафе і ресторани";
                    else if (clickedId == R.id.cat_transport) selectedCategoryName = "Транспорт";
                    else if (clickedId == R.id.cat_utility) selectedCategoryName = "Комунальні послуги";
                    else if (clickedId == R.id.cat_entertainment) selectedCategoryName = "Розваги";
                    else if (clickedId == R.id.cat_health) selectedCategoryName = "Здоров'я";
                    else if (clickedId == R.id.cat_beauty) selectedCategoryName = "Краса та догляд";
                    else if (clickedId == R.id.cat_gift) selectedCategoryName = "Подарунки";
                    else if (clickedId == R.id.cat_clothes) selectedCategoryName = "Одяг та взуття";
                    else if (clickedId == R.id.cat_tech) selectedCategoryName = "Техніка";

                    else selectedCategoryName = "Інше";
                });
            }
        }
    }

    private void saveTransaction() {
        EditText inputAmount = findViewById(R.id.inputAmount);
        EditText inputComment = findViewById(R.id.inputComment);
        TextView errorAmountText = findViewById(R.id.errorAmountText);

        String amountStr = inputAmount.getText().toString().trim();
        String comment = inputComment.getText().toString().trim();

        if (amountStr.isEmpty()) {
            inputAmount.setError("Введіть суму!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            inputAmount.setError("Некоректний формат суми!");
            return;
        }

        if (amount <= 0) {
            errorAmountText.setVisibility(View.VISIBLE);
            inputAmount.setError("Сума має бути більшою за 0");
            return;
        } else {
            errorAmountText.setVisibility(View.GONE);
        }

        if (currentType == 0 && (selectedCategoryName == null || selectedCategoryName.isEmpty())) {
            Toast.makeText(this, "Будь ласка, оберіть категорію!", Toast.LENGTH_SHORT).show();
            return;
        }

        String typeStr;
        int isFromSavings = 0;

        if (currentType == 1) {
            typeStr = "income";
        } else if (currentType == 2) {
            typeStr = "savings";
            isFromSavings = 1;
        } else {
            typeStr = "expense";
        }

        final String finalType = typeStr;
        final int finalIsFromSavings = isFromSavings;
        final double finalAmount = amount;

        new Thread(() -> {
            Transaction_Log transaction = new Transaction_Log();
            transaction.sum = finalAmount;
            transaction.type = finalType;
            transaction.comment = comment;

            // ВИПРАВЛЕНО: Прибрав "/ 1000", бо інакше дата буде зберігатися з помилкою в 1970 рік
            transaction.date = System.currentTimeMillis();
            transaction.is_from_savings = finalIsFromSavings;

            if (finalType.equals("expense")) {
                int catId = db.categoryDao().getCategoryIdByName(selectedCategoryName);
                if (catId > 0) {
                    transaction.category_id = catId;
                }
            }

            db.transactionDao().insert(transaction);

            runOnUiThread(() -> {
                Toast.makeText(this, "Операцію збережено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private String getTodayDate() {
        final Calendar c = Calendar.getInstance();
        return String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d",
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    EditText inputDate = findViewById(R.id.inputDate);
                    inputDate.setText(selectedDate);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}