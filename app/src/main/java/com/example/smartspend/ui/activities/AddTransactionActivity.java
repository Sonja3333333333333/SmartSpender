package com.example.smartspend.ui.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartspend.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private int currentType = 0; // 0 - витрати, 1 - доходи, 2 - скарбничка
    private Button btnTypeExpense, btnTypeIncome, btnTypeSavings;
    private View categoryContainer;
    private List<Button> categoryButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

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
        categoryContainer = findViewById(R.id.categoryContainer);
        EditText inputDate = findViewById(R.id.inputDate);

        inputDate.setOnClickListener(v -> showDatePickerDialog());

        btnTypeExpense.setOnClickListener(v -> updateTypeSelection(0));
        btnTypeIncome.setOnClickListener(v -> updateTypeSelection(1));
        btnTypeSavings.setOnClickListener(v -> updateTypeSelection(2));

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

        //  ОЧИЩЕННЯ ПРИ ПЕРЕМИКАННІ
        if (inputAmount != null) inputAmount.setText("");
        if (inputComment != null) inputComment.setText("");

        // Скидаємо дату до сьогоднішньої для кожної нової вкладки
        if (inputDate != null) {
            inputDate.setText(getTodayDate());
        }

        //  ВІЗУАЛ КНОПОК
        int activeColor = Color.parseColor("#4A86F7");
        int inactiveColor = Color.parseColor("#F0F0F0");

        btnTypeExpense.setBackgroundTintList(ColorStateList.valueOf(type == 0 ? activeColor : inactiveColor));
        btnTypeExpense.setTextColor(type == 0 ? Color.WHITE : Color.parseColor("#666666"));

        btnTypeIncome.setBackgroundTintList(ColorStateList.valueOf(type == 1 ? activeColor : inactiveColor));
        btnTypeIncome.setTextColor(type == 1 ? Color.WHITE : Color.parseColor("#666666"));

        btnTypeSavings.setBackgroundTintList(ColorStateList.valueOf(type == 2 ? activeColor : inactiveColor));
        btnTypeSavings.setTextColor(type == 2 ? Color.WHITE : Color.parseColor("#666666"));

        //  ПРИХОВУВАННЯ КАТЕГОРІЙ
        if (categoryContainer != null) {
            categoryContainer.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        }

        // СКИНУТИ КАТЕГОРІЇ (сірий колір)
        resetCategorySelection();

        currentType = type;
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
                R.id.cat_beauty, R.id.cat_gift, R.id.cat_clothes,
                R.id.cat_tech, R.id.cat_other
        };

        for (int id : catIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                categoryButtons.add(btn);
                btn.setOnClickListener(v -> {
                    resetCategorySelection(); // скидаємо інші кнопки
                    v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A1C4FD")));
                });
            }
        }
    }

    private String getTodayDate() {
        final Calendar c = Calendar.getInstance();
        return String.format("%02d/%02d/%04d",
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    EditText inputDate = findViewById(R.id.inputDate);
                    inputDate.setText(selectedDate);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}