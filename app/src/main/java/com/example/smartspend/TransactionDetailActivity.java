package com.example.smartspend;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartspend.data.AppDatabase; // Перевір, щоб імпорт бази був правильним
import com.example.smartspend.data.entities.Transaction_Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionDetailActivity extends AppCompatActivity {

    // ВИДАЛИЛИ tvType звідси
    private TextView tvDate, tvCategory, tvComment, tvAmount;
    private AppDatabase db;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Ініціалізація View з вашого нового XML (без tvDetailType)
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

                    // 1. Форматуємо тип і суму в один рядок (як на макеті)
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

                    // 2. Форматуємо дату (щоб були не мілісекунди, а нормальна дата)
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("uk"));
                    tvDate.setText(sdf.format(new Date(transaction.date)));

                    // 3. Коментар
                    tvComment.setText(transaction.comment != null && !transaction.comment.isEmpty() ? transaction.comment : "Немає коментаря");

                    // Поки що ховаємо категорію, якщо її складно витягнути (або можеш дописати логіку)
                    // tvCategory.setText("...");
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