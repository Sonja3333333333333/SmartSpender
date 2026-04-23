package com.example.smartspend.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.smartspend.ui.fragment.DashboardFragment;
import com.example.smartspend.ui.fragment.HistoryFragment;
import com.example.smartspend.ui.fragment.LimitsFragment;
import com.example.smartspend.R;
import com.example.smartspend.ui.fragment.SavingsFragment;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Balance;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. ЗАСТОСУВАННЯ ТЕМИ (Має бути до super.onCreate)
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int mode = prefs.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(mode);

        super.onCreate(savedInstanceState);

        // 2. НАЛАШТУВАННЯ ІНТЕРФЕЙСУ
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Обробка системних відступів (Status Bar / Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3. ІНІЦІАЛІЗАЦІЯ БАЗИ ТА НАВІГАЦІЇ
        initializeDatabase();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Встановлення початкового фрагмента (Dashboard)
        if (savedInstanceState == null) {
            replaceFragment(new DashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }

        // Слухач натискань на нижню панель
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                replaceFragment(new DashboardFragment());
                return true;
            } else if (id == R.id.nav_history) {
                replaceFragment(new HistoryFragment());
                return true;
            } else if (id == R.id.nav_limits) {
                replaceFragment(new LimitsFragment());
                return true;
            } else if (id == R.id.nav_savings) {
                replaceFragment(new SavingsFragment());
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void initializeDatabase() {
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            try {
                // Додаємо базові категорії
                Category c1 = new Category(); c1.id = 1; c1.name = "Продукти"; db.categoryDao().insert(c1);
                Category c2 = new Category(); c2.id = 2; c2.name = "Кафе/Кава"; db.categoryDao().insert(c2);
                Category c3 = new Category(); c3.id = 3; c3.name = "Транспорт/Таксі"; db.categoryDao().insert(c3);

                // Початковий баланс
                Balance b1 = new Balance(); b1.id = 1; b1.balance = 5500.50; b1.balance_type = 1; db.balanceDao().insert(b1);

                // Тестова транзакція (використовуємо поле .sum)
                Transaction_Log t1 = new Transaction_Log();
                t1.id = 1;
                t1.sum = 450.0;
                t1.type = "expense";
                t1.category_id = 1;
                t1.date = System.currentTimeMillis();
                t1.comment = "Тестовий запис";
                db.transactionDao().insert(t1);

                System.out.println("База даних успішно ініціалізована!");
            } catch (Exception e) {
                System.out.println("Тестові дані вже присутні або сталася помилка.");
            }
        }).start();
    }
}