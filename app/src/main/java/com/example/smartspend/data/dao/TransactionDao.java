package com.example.smartspend.data.dao; // ВИПРАВЛЕНО: правильна папка

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

// ДОДАНО: імпорт таблиці транзакцій, бо вона тепер в іншій папці
import com.example.smartspend.data.entities.Transaction_Log;

import java.util.List;

@Dao
public interface TransactionDao {

    // Додавання нової транзакції
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Transaction_Log transaction);

    // Видалення всіх транзакцій (очищення бази)
    @Query("DELETE FROM Transaction_Log")
    void deleteAllTransactions();

    // МЕТОД ДЛЯ ДАШБОРДУ: Отримуємо всі операції за вибраний період
    @Query("SELECT * FROM Transaction_Log WHERE date >= :startOfMonth AND date <= :endOfMonth ORDER BY date DESC")
    List<Transaction_Log> getTransactionsForPeriod(long startOfMonth, long endOfMonth);

    // МЕТОД ДЛЯ ІСТОРІЇ: Працює так само, але називається інакше (щоб історія не зламалася)
    @Query("SELECT * FROM Transaction_Log WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    List<Transaction_Log> getTransactionsByMonth(long startDate, long endDate);

    // --- МЕТОДИ ДЛЯ ЕКРАНУ ДЕТАЛЕЙ (щоб кнопки "Редагувати" і "Видалити" працювали) ---

    // Отримання конкретної транзакції за її ID
    @Query("SELECT * FROM Transaction_Log WHERE id = :id")
    Transaction_Log getTransactionById(int id);

    // Видалення конкретної транзакції
    @Delete
    void delete(Transaction_Log transaction);

    // Отримання взагалі всіх транзакцій (про всяк випадок)
    @Query("SELECT * FROM Transaction_Log ORDER BY date DESC")
    List<Transaction_Log> getAllTransactions();

    @Query("SELECT " +
            "TOTAL(CASE WHEN type = 'savings' THEN sum ELSE 0 END) - " +
            "TOTAL(CASE WHEN is_from_savings = 1 THEN sum ELSE 0 END) " +
            "FROM Transaction_Log")
    double getSavingsBalance();
}