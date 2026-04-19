package com.example.smartspend.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smartspend.data.entities.Balance;
import com.example.smartspend.data.dao.BalanceDao;
import com.example.smartspend.data.dao.BudgetDao;
import com.example.smartspend.data.entities.Budgets;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.dao.CategoryDao;
import com.example.smartspend.data.dao.TransactionDao;
import com.example.smartspend.data.entities.Transaction_Log;

// Вказуємо всі 4 наші таблиці [cite: 3, 5, 8, 10]
@Database(entities = {Category.class, Transaction_Log.class, Budgets.class, Balance.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Підключаємо наш DAO
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
    public abstract BalanceDao balanceDao();

    private static volatile AppDatabase INSTANCE;

    // Метод для безпечного підключення до бази (щоб не створювати її двічі)
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smart_spend_db")
                            .allowMainThreadQueries() // Дозволяємо запити для тестування
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}