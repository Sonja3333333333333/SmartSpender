package com.example.smartspend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tvType, tvDate, tvCategory, tvComment, tvAmount;
    private AppDatabase db;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Ініціалізація View з вашого XML
        tvType = findViewById(R.id.tvDetailType);
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

        findViewById(R.id.btnDeleteTransaction).setOnClickListener(v -> deleteTransaction());
    }

    private void loadTransactionData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Transaction_Log transaction = db.transactionDao().getTransactionById(transactionId);
            if (transaction != null) {
                runOnUiThread(() -> {
                    tvType.setText("Тип: " + transaction.type);
                    tvDate.setText("Дата: " + transaction.date);
                    // ВИПРАВЛЕНО: .sum замість .amount
                    tvAmount.setText("Сума: " + transaction.sum + " UAH");
                    tvComment.setText("Коментар: " + (transaction.comment != null ? transaction.comment : ""));
                });
            }
        });
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