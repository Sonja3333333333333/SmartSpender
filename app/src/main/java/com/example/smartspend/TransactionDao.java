package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    // Додавання нової транзакції
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Transaction_Log transaction);

    // Отримання конкретної транзакції за її ID (для вікна деталей)
    @Query("SELECT * FROM Transaction_Log WHERE id = :id")
    Transaction_Log getTransactionById(int id);

    // Отримання всіх транзакцій, відсортованих від нових до старих (для списку історії)
    @Query("SELECT * FROM Transaction_Log ORDER BY date DESC")
    List<Transaction_Log> getAllTransactions();

    // Видалення конкретної транзакції
    @Delete
    void delete(Transaction_Log transaction);

    // Видалення всіх транзакцій (наприклад, для очищення даних)
    @Query("DELETE FROM Transaction_Log")
    void deleteAllTransactions();
}