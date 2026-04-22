package com.example.smartspend;

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
}