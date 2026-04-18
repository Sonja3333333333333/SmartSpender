package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Balance balance);

    @Query("SELECT * FROM Balance WHERE balance_type = :type LIMIT 1")
    Balance getBalanceByType(int type);
}