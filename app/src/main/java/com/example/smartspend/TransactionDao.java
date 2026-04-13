package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Transaction_Log transaction);
}
@Query("DELETE FROM Transaction_Log")
void deleteAllTransactions();