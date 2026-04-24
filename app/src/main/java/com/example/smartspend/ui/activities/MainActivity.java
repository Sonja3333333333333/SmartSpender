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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. ЗАСТОСУВАННЯ ТЕМИ
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

        // 3. НАВІГАЦІЯ
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
}