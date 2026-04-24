package com.example.smartspend.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Balance;
import com.example.smartspend.ui.activities.SpendSavingsActivity;

import java.util.Locale;

public class SavingsFragment extends Fragment {

    private TextView tvSavingsAmount;

    public SavingsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_savings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        tvSavingsAmount = view.findViewById(R.id.tv_savings_amount);

        Button btnSpend = view.findViewById(R.id.btn_spend_savings);

        btnSpend.setOnClickListener(v ->{
            android.content.Intent intent = new android.content.Intent(getActivity(), SpendSavingsActivity.class);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        loadSavingsBalance();
    }

    private void loadSavingsBalance(){
        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() ->{
            try{
                Balance savings = db.balanceDao().getBalanceByType(2);

                double amount = (savings != null) ? savings.balance : 0.0;

                String formattedBalance = String.format("%.2f UAH", amount);

                requireActivity().runOnUiThread(() -> {
                    tvSavingsAmount.setText(formattedBalance);
                });
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    // Усередині SavingsFragment.java

    private void updateSavingsBalance() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> {
            // Отримуємо баланс із бази даних
            double balance = db.transactionDao().getSavingsBalance();

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // tvSavingsAmount — це ID твого TextView для суми в XML
                    TextView tvAmount = getView().findViewById(R.id.tv_savings_amount);
                    if (tvAmount != null) {
                        tvAmount.setText(String.format(Locale.getDefault(), "%.2f UAH", balance));
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSavingsBalance();
    }
}