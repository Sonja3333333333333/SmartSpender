package com.example.smartspend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartspend.data.AppDatabase; // Перевір, щоб імпорт бази був правильним
import com.example.smartspend.data.entities.Transaction_Log;
import com.example.smartspend.ui.activities.SpendSavingsActivity;
import com.example.smartspend.ui.activities.UpdateTransactionActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tvDate, tvCategory, tvComment, tvAmount;
    private AppDatabase db;
    private int transactionId;
    private Transaction_Log currentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        tvDate = findViewById(R.id.tvDetailDate);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvComment = findViewById(R.id.tvDetailComment);
        tvAmount = findViewById(R.id.tvDetailAmount);

        db = AppDatabase.getInstance(this);
        transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);

        if (transactionId != -1) {
            loadTransactionData();
        }

        findViewById(R.id.btnBackToHistory).setOnClickListener(v -> finish());
        findViewById(R.id.btnDeleteTransaction).setOnClickListener(v -> showDeleteConfirmationDialog());
        Button btnEdit = findViewById(R.id.btnEditTransaction);

        btnEdit.setOnClickListener(v -> {
            Intent intent;

            if (currentTransaction.is_from_savings == 1) {
                intent = new Intent(TransactionDetailActivity.this, SpendSavingsActivity.class);
            } else {
                intent = new Intent(TransactionDetailActivity.this, UpdateTransactionActivity.class);
            }

            intent.putExtra("EDIT_TRANSACTION_ID", currentTransaction.id);

            startActivity(intent);
        });
    }

    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Видалення операції")
                .setMessage("Ви впевнені, що хочете видалити цю операцію? Цю дію неможливо скасувати.")
                .setPositiveButton("Видалити", (dialog, which) -> {
                    // Якщо користувач натиснув "Видалити", викликаємо наш старий метод
                    deleteTransaction();
                })
                .setNegativeButton("Скасувати", (dialog, which) -> {
                    // Якщо натиснув "Скасувати", просто закриваємо віконце
                    dialog.dismiss();
                })
                .show();
    }

    private void loadTransactionData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Transaction_Log transaction = db.transactionDao().getTransactionById(transactionId);
            currentTransaction = transaction;
            if (transaction != null) {
                runOnUiThread(() -> {

                    String typeText = "";
                    if ("income".equals(transaction.type)) {
                        typeText = String.format(Locale.getDefault(), "Дохід: +%.2f UAH", transaction.sum);
                        tvAmount.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Зелений
                    } else if ("savings".equals(transaction.type)) {
                        typeText = String.format(Locale.getDefault(), "Скарбничка: -%.2f UAH", transaction.sum);
                        tvAmount.setTextColor(android.graphics.Color.parseColor("#2196F3")); // Синій
                    } else {
                        typeText = String.format(Locale.getDefault(), "Витрата: -%.2f UAH", transaction.sum);
                        tvAmount.setTextColor(android.graphics.Color.parseColor("#E53935")); // Червоний
                    }
                    tvAmount.setText(typeText);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("uk"));
                    tvDate.setText(sdf.format(new Date(transaction.date)));

                    tvComment.setText(transaction.comment != null && !transaction.comment.isEmpty() ? transaction.comment : "Немає коментаря");
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (transactionId != -1) {
            loadTransactionData();
        }
    }

    private void deleteTransaction() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Transaction_Log transaction = db.transactionDao().getTransactionById(transactionId);
            if (transaction != null) {
                db.transactionDao().delete(transaction);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Видалено", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}