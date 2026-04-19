package com.example.smartspend.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartspend.data.entities.Balance;

@Dao
public interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Balance balance);

    @Query("SELECT * FROM Balance WHERE balance_type = :type LIMIT 1")
    Balance getBalanceByType(int type);
}