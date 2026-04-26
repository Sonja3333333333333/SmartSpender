package com.example.smartspend.ui.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Transaction_Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateTransactionActivity extends AppCompatActivity {

    private View categoryContainer;
    private List<Button> categoryButtons = new ArrayList<>();
    private AppDatabase db;
    private String selectedCategoryName = "Інше";

    private int transactionId = -1;
    private Transaction_Log currentTransaction;
    private EditText inputAmount, inputComment, inputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        db = AppDatabase.getInstance(this);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Button btnUpdate = findViewById(R.id.btnUpdate);
        categoryContainer = findViewById(R.id.categoryContainer);
        inputAmount = findViewById(R.id.inputAmount);
        inputComment = findViewById(R.id.inputComment);
        inputDate = findViewById(R.id.inputDate);

        setupCategoryButtons();

        inputDate.setOnClickListener(v -> showDatePickerDialog());

        transactionId = getIntent().getIntExtra("EDIT_TRANSACTION_ID", -1);
        if (transactionId != -1) {
            loadTransactionData();
        } else {
            Toast.makeText(this, "Помилка завантаження даних", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> updateTransaction());
        }

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
    }

    private void loadTransactionData() {
        new Thread(() -> {
            currentTransaction = db.transactionDao().getTransactionById(transactionId);

            if (currentTransaction != null) {
                runOnUiThread(() -> {
                    if (inputAmount != null) inputAmount.setText(String.valueOf(currentTransaction.sum));
                    if (inputComment != null && currentTransaction.comment != null) {
                        inputComment.setText(currentTransaction.comment);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    if (inputDate != null) inputDate.setText(sdf.format(new Date(currentTransaction.date)));

                    // Якщо це витрата — показуємо категорії, інакше ховаємо
                    if ("expense".equals(currentTransaction.type)) {
                        if (categoryContainer != null) categoryContainer.setVisibility(View.VISIBLE);
                        if (currentTransaction.category_id != null) {
                            loadCategoryForEdit(currentTransaction.category_id);
                        }
                    } else {
                        if (categoryContainer != null) categoryContainer.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void loadCategoryForEdit(int categoryId) {
        new Thread(() -> {
            String catName = db.categoryDao().getCategoryNameById(categoryId);
            if (catName != null) {
                runOnUiThread(() -> {
                    selectedCategoryName = catName;
                    // Підсвічуємо потрібну кнопку
                    for (Button btn : categoryButtons) {
                        if (btn.getText().toString().equals(catName)) {
                            resetCategorySelection();
                            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A1C4FD")));
                            break;
                        }
                    }
                });
            }
        }).start();
    }

    private void setupCategoryButtons() {
        int[] catIds = {
                R.id.cat_products, R.id.cat_cafe, R.id.cat_transport,
                R.id.cat_utility, R.id.cat_entertainment, R.id.cat_health,
                R.id.cat_beauty, R.id.cat_gift, R.id.cat_clothes, R.id.cat_tech, R.id.cat_other
        };

        for (int id : catIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                categoryButtons.add(btn);
                btn.setOnClickListener(v -> {
                    resetCategorySelection();
                    v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A1C4FD")));
                    selectedCategoryName = ((Button) v).getText().toString();
                });
            }
        }
    }

    private void resetCategorySelection() {
        for (Button b : categoryButtons) {
            if (b != null) {
                b.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            }
        }
    }

    private void updateTransaction() {
        String amountStr = inputAmount.getText().toString().trim();
        String comment = inputComment.getText().toString().trim();
        String dateStr = inputDate.getText().toString().trim();

        if (amountStr.isEmpty()) {
            inputAmount.setError("Введіть суму!");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        long finalTimestamp = System.currentTimeMillis();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date newDate = sdf.parse(dateStr);

            if (newDate != null && currentTransaction != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(newDate);

                Calendar oldTime = Calendar.getInstance();
                oldTime.setTimeInMillis(currentTransaction.date);

                calendar.set(Calendar.HOUR_OF_DAY, oldTime.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, oldTime.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, oldTime.get(Calendar.SECOND));

                finalTimestamp = calendar.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long timestampToSave = finalTimestamp;

        new Thread(() -> {
            currentTransaction.sum = amount;
            currentTransaction.comment = comment;
            currentTransaction.date = timestampToSave;

            if ("expense".equals(currentTransaction.type)) {
                int catId = db.categoryDao().getCategoryIdByName(selectedCategoryName);
                if (catId > 0) currentTransaction.category_id = catId;
            }

            db.transactionDao().update(currentTransaction);

            runOnUiThread(() -> {
                Toast.makeText(this, "Зміни збережено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    inputDate.setText(selectedDate);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}