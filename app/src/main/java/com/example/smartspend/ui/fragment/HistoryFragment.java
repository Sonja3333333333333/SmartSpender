package com.example.smartspend.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;
import com.example.smartspend.ui.adapters.HistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout layoutEmptyState;
    private HistoryAdapter adapter;
    private AppDatabase db;
    private TextView tvMonthName;
    private int currentMonthOffset = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Ініціалізуємо перемикачі
        tvMonthName = view.findViewById(R.id.tvMonthName);
        ImageView btnPrev = view.findViewById(R.id.btnPrevMonth);
        ImageView btnNext = view.findViewById(R.id.btnNextMonth);

        db = AppDatabase.getInstance(requireContext());

        setupRecyclerView();

        // Обробка натискань на стрілки
        btnPrev.setOnClickListener(v -> {
            currentMonthOffset--;
            loadDataFromDatabase();
        });

        btnNext.setOnClickListener(v -> {
            currentMonthOffset++;
            loadDataFromDatabase();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataFromDatabase();
    }

    private void setupRecyclerView() {
        // Тепер ми передаємо третім параметром логіку кліку
        adapter = new HistoryAdapter(new ArrayList<>(), new ArrayList<>(), transaction -> {

            // Створюємо "квиток" на перехід до іншої сторінки
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.smartspend.TransactionDetailActivity.class);

            // Передаємо ID транзакції, щоб на тій сторінці знати, що показувати
            intent.putExtra("TRANSACTION_ID", transaction.id);

            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, currentMonthOffset); // Додаємо зсув місяця

        // Оновлюємо назву місяця в UI
        SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL yyyy", new Locale("uk"));
        String monthTitle = monthFormat.format(calendar.getTime());
        // Робимо першу літеру великою
        String capitalizedMonth = monthTitle.substring(0, 1).toUpperCase() + monthTitle.substring(1);
        tvMonthName.setText(capitalizedMonth);

        // Рахуємо межі вибраного місяця
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long start = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long end = calendar.getTimeInMillis();

        new Thread(() -> {
            List<Category> allCategories = db.categoryDao().getAllCategories();
            List<Transaction_Log> transactions = db.transactionDao().getTransactionsByMonth(start, end);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    if (transactions.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);
                        adapter.updateData(transactions, allCategories);
                    }
                });
            }
        }).start();
    }
}