package com.example.smartspend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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




}