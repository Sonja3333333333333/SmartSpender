package com.example.smartspend; // Має бути ідентичним до AppDatabase

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Query("SELECT * FROM Category")
    List<Category> getAllCategories();

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategoryById(int id);
}