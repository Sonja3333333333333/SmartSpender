package com.example.smartspend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View pieChart = view.findViewById(R.id.pieChart);
        View balanceGroup = view.findViewById(R.id.balance_group);
        View emptyDataParent = view.findViewById(R.id.empty_data_parent);

        // умова для відображення плейсхолдера якщо нема даних для pie chart
        boolean hasData = false;

        if (hasData) {
            pieChart.setVisibility(View.VISIBLE);
            balanceGroup.setVisibility(View.VISIBLE);
            emptyDataParent.setVisibility(View.GONE);
        } else {
            // Ховаємо діаграму та баланс
            pieChart.setVisibility(View.GONE);
            balanceGroup.setVisibility(View.GONE);

            // Показуємо відразу весь блок з плейсхолдером, текстом і стрілкою
            emptyDataParent.setVisibility(View.VISIBLE);
        }

        //кнопка додати операцію
        ImageView btnAdd = view.findViewById(R.id.btn_add_transaction);

        if (btnAdd != null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                    startActivity(intent);

                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            });
        }
    }
}