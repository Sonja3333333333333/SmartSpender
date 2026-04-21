package com.example.smartspend;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppDatabase db;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = AppDatabase.getInstance(getContext());
        loadHistory();

        return view;
    }

    private void loadHistory() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Тепер цей метод точно є в DAO
            List<Transaction_Log> transactions = db.transactionDao().getAllTransactions();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    TransactionAdapter adapter = new TransactionAdapter(transactions, transaction -> {
                        Intent intent = new Intent(getContext(), TransactionDetailActivity.class);
                        intent.putExtra("TRANSACTION_ID", transaction.id);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }
}