package com.example.smartspend.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.smartspend.data.AppDatabase;
import com.example.smartspend.data.BudgetProgressModel;
import java.util.List;

public class BudgetViewModel extends AndroidViewModel {
    private LiveData<List<BudgetProgressModel>> budgetsWithProgress;

    public BudgetViewModel(Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        budgetsWithProgress = db.budgetDao().getBudgetsWithProgress();
    }

    public LiveData<List<BudgetProgressModel>> getBudgetsWithProgress() {
        return budgetsWithProgress;
    }
}