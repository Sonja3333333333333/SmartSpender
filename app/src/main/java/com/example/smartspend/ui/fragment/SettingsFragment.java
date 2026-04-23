package com.example.smartspend.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;

public class SettingsFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // 1. Логіка вибору теми
        themeRadioGroup = view.findViewById(R.id.themeRadioGroup);
        sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        int savedTheme = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateRadioButtons(savedTheme);

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.radioLight) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioDark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            sharedPreferences.edit().putInt("ThemeMode", mode).apply();
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        // 2. Кнопка "Назад"
        TextView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        // 3. Кнопка "Скинути застосунок"
        Button btnReset = view.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> showResetConfirmation());

        return view;
    }

    private void updateRadioButtons(int mode) {
        if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
            themeRadioGroup.check(R.id.radioLight);
        } else if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            themeRadioGroup.check(R.id.radioDark);
        } else {
            themeRadioGroup.check(R.id.radioSystem);
        }
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Підтвердження")
                .setMessage("Ви впевнені, що хочете видалити всі дані? Цю дію неможливо скасувати.")
                .setPositiveButton("Так", (dialog, which) -> resetAppData())
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void resetAppData() {
        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            // Очищення таблиць
            db.transactionDao().deleteAllTransactions();
            // Тут можна додати очищення інших таблиць за потреби, наприклад:
            // db.budgetDao().deleteAllBudgets();

            if (isAdded()) { // Перевірка, чи фрагмент ще активний
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Дані успішно видалено", Toast.LENGTH_SHORT).show();
                    // Повернення на головний екран після очищення
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new DashboardFragment())
                            .commit();
                });
            }
        }).start();
    }
}