package com.example.smartspend;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Підключаємося до нашої локальної бази даних
        AppDatabase db = AppDatabase.getInstance(this);

// Створюємо тестову категорію
        Category testCategory = new Category("Продукти");

// Записуємо її в базу (щоб не було помилок при повторному запуску, використовуємо try-catch для унікального поля)
        try {
            db.categoryDao().insert(testCategory);
            System.out.println("Категорію успішно додано в базу SQLite!");
        } catch (Exception e) {
            System.out.println("Категорія вже існує.");
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}