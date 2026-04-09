package com.example.smartspend;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

// Додаємо імпорт для нашого меню
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- БЛОК 1: ТВОЇ ТЕСТОВІ ДАНІ (БАЗА ДАНИХ) ---
        AppDatabase db = AppDatabase.getInstance(this);
        try {
            Category c1 = new Category(); c1.id = 1; c1.name = "Продукти"; db.categoryDao().insert(c1);
            Category c2 = new Category(); c2.id = 2; c2.name = "Кафе/Кава"; db.categoryDao().insert(c2);
            Category c3 = new Category(); c3.id = 3; c3.name = "Транспорт/Таксі"; db.categoryDao().insert(c3);
            Category c4 = new Category(); c4.id = 4; c4.name = "Житло/Комуналка"; db.categoryDao().insert(c4);
            Category c5 = new Category(); c5.id = 5; c5.name = "Розваги"; db.categoryDao().insert(c5);
            Category c6 = new Category(); c6.id = 6; c6.name = "Здоров'я"; db.categoryDao().insert(c6);
            Category c7 = new Category(); c7.id = 7; c7.name = "Краса та догляд"; db.categoryDao().insert(c7);
            Category c8 = new Category(); c8.id = 8; c8.name = "Одяг"; db.categoryDao().insert(c8);
            Category c9 = new Category(); c9.id = 9; c9.name = "Подарунок"; db.categoryDao().insert(c9);
        } catch (Exception e) {
            // Ігноруємо
        }

        try {
            Balance b1 = new Balance(); b1.id = 1; b1.balance = 5500.50; b1.balance_type = 1; db.balanceDao().insert(b1);
            Balance b2 = new Balance(); b2.id = 2; b2.balance = 1200.00; b2.balance_type = 2; db.balanceDao().insert(b2);

            Budgets budget1 = new Budgets(); budget1.id = 1; budget1.limit_sum = 4000.0; budget1.period = "04-2026"; budget1.category_id = 1; db.budgetDao().insert(budget1);

            long currentTime = System.currentTimeMillis();

            Transaction_Log t1 = new Transaction_Log(); t1.id = 1; t1.sum = 450.0; t1.type = "expense"; t1.category_id = 1; t1.date = currentTime; t1.comment = "Сільпо"; t1.is_from_savings = 0;
            db.transactionDao().insert(t1);

            Transaction_Log t2 = new Transaction_Log(); t2.id = 2; t2.sum = 85.0; t2.type = "expense"; t2.category_id = 2; t2.date = currentTime; t2.comment = "Кава зранку"; t2.is_from_savings = 0;
            db.transactionDao().insert(t2);

            System.out.println("Всі тестові дані успішно додано через DAO!");
        } catch (Exception e) {
            System.out.println("Помилка запису: " + e.getMessage());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            replaceFragment(new DashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}