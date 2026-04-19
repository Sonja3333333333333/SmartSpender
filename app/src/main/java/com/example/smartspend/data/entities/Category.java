package com.example.smartspend.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;
import androidx.room.Ignore;

// Створюємо таблицю з унікальною назвою категорії
@Entity(tableName = "Category", indices = {@Index(value = {"name"}, unique = true)})
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id; // ідентифікатор категорії

    @ColumnInfo(name = "name")
    public String name; // назва категорії

    public Category() {
    }

    @Ignore
    public Category(String name) {
        this.name = name;
    }
}