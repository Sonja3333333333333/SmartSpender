package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Budgets budget);
}