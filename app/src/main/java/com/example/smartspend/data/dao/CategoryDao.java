package com.example.smartspend.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartspend.data.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    // Отримання всіх категорій для відображення у списку/спінері
    @Query("SELECT * FROM Category")
    List<Category> getAllCategories();

    // Отримання ID категорії за її назвою (важливо для збереження ліміту)
    @Query("SELECT id FROM Category WHERE name = :name LIMIT 1")
    int getCategoryIdByName(String name);

    // Отримання назви категорії за ID (для відображення в історії)
    @Query("SELECT name FROM Category WHERE id = :id LIMIT 1")
    String getCategoryNameById(int id);
}