
package com.example.smartspend;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

// Створюємо таблицю з унікальною назвою категорії [cite: 3]
@Entity(tableName = "Category", indices = {@Index(value = {"name"}, unique = true)})
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id; // ідентифікатор категорії [cite: 3]

    @ColumnInfo(name = "name")
    public String name; // назва категорії [cite: 3]

    // Конструктор для створення об'єкта
    public Category(String name) {
        this.name = name;
    }
}