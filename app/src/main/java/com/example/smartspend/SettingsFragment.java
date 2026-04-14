package com.example.smartspend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {


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
            db.transactionDao().deleteAllTransactions();

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Дані успішно видалено", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            });
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Твоя наявна кнопка скидання
        Button btnReset = view.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> showResetConfirmation());

        // НОВЕ: Знаходимо текст "< Назад" і вішаємо слухача
        TextView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            // Ця команда еквівалентна натисканню системної кнопки "Назад" на телефоні
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return view;
    }
}