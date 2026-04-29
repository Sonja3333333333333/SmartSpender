package com.example.smartspend.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartspend.data.BudgetProgressModel;
import com.example.smartspend.data.entities.Budgets;

import java.util.List;

@Dao
public interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Budgets budget);

    @Update
    void update(Budgets budget);

    @Delete
    void delete(Budgets budget);

    // Отримати всі ліміти для відображення у списку
    @Query("SELECT * FROM Budgets ORDER BY id DESC")
    LiveData<List<Budgets>> getAllBudgets();

    // Отримати конкретний бюджет за категорією
    @Query("SELECT * FROM Budgets WHERE category_id = :categoryId LIMIT 1")
    Budgets getBudgetByCategory(int categoryId);

    /* ВАЖЛИВО: Щоб бачити прогрес (скільки витрачено),
       нам потрібно зробити запит до таблиці транзакцій.
       Припускаємо, що у вас є таблиця transactions з колонками category_id та amount.
    */
    @Query("SELECT SUM(sum) FROM Transaction_Log WHERE category_id = :categoryId")
    LiveData<Double> getSpentAmountByCategory(int categoryId);

    @Query("SELECT " +
            "c.name AS categoryName, " +
            "b.limit_sum AS limitSum, " +
            "b.category_id AS categoryId, " +
            "(SELECT SUM(sum) FROM Transaction_Log WHERE category_id = b.category_id AND type = 'expense') AS spentSum " +
            "FROM Budgets b " +
            "JOIN Category c ON b.category_id = c.id")
    LiveData<List<BudgetProgressModel>> getBudgetsWithProgress();
}