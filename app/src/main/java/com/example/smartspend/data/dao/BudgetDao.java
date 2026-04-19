package com.example.smartspend.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.smartspend.data.entities.Budgets;

@Dao
public interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Budgets budget);
}