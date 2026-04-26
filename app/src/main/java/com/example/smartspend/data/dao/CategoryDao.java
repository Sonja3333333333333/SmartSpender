package com.example.smartspend.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartspend.data.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    // Метод для додавання нової категорії в базу
    @Insert
    void insert(Category category);

    // Метод для отримання списку всіх категорій
    @Query("SELECT * FROM Category")
    List<Category> getAllCategories();

    // Метод для отримання категорії за її ID (з гілки main)
    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategoryById(int id);

    @Query("SELECT id FROM Category WHERE name = :name LIMIT 1")
    int getCategoryIdByName(String name);

    @Query("SELECT name FROM Category WHERE id = :id LIMIT 1")
    String getCategoryNameById(int id);
}