package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Balance balance);
}