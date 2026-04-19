package com.example.smartspend.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartspend.data.entities.Transaction_Log;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Transaction_Log transaction);

    @Query("DELETE FROM Transaction_Log")
    void deleteAllTransactions();

    @Query("SELECT * FROM Transaction_Log WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    List<Transaction_Log> getTransactionsByMonth(long startDate, long endDate);
}