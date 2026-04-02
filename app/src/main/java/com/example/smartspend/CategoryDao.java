package com.example.smartspend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CategoryDao {
    // Метод для додавання нової категорії в базу
    @Insert
    void insert(Category category);

    // Метод для отримання списку всіх категорій
    @Query("SELECT * FROM Category")
    List<Category> getAllCategories();
}