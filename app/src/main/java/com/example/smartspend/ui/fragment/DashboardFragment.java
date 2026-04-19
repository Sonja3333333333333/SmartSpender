package com.example.smartspend.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.smartspend.R;
import com.example.smartspend.ui.activities.AddTransactionActivity;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Тут ми ТІЛЬКИ "надуваємо" макет
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Знаходимо всі елементи на екрані ---
        View pieChart = view.findViewById(R.id.pieChart);
        View balanceGroup = view.findViewById(R.id.balance_group);
        View emptyDataParent = view.findViewById(R.id.empty_data_parent);
        ImageView btnAdd = view.findViewById(R.id.btn_add_transaction);
        ImageView btnSettings = view.findViewById(R.id.btn_settings);

        // --- 2. Логіка порожнього екрану (від Наталі) ---
        boolean hasData = false; // Поки що ставимо false, бо бази даних ще не підключені до графіка

        if (hasData) {
            pieChart.setVisibility(View.VISIBLE);
            balanceGroup.setVisibility(View.VISIBLE);
            emptyDataParent.setVisibility(View.GONE);
        } else {
            // Ховаємо діаграму та баланс, показуємо плейсхолдер зі стрілкою
            pieChart.setVisibility(View.GONE);
            balanceGroup.setVisibility(View.GONE);
            emptyDataParent.setVisibility(View.VISIBLE);
        }

        // --- 3. Кнопка Додати операцію (+) ---
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                startActivity(intent);

                if (getActivity() != null) {
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        // --- 4. Наша Шестірня (Налаштування) ---
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null) // Щоб працювала кнопка "Назад"
                        .commit();
            });
        }
    }
}