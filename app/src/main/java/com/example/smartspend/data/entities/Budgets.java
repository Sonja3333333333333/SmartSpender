package com.example.smartspend.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

// Унікальне обмеження: один ліміт на одну категорію в один місяць
@Entity(tableName = "Budgets",
        indices = {@Index(value = {"category_id", "period"}, unique = true)},
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE))
public class Budgets {
    @PrimaryKey(autoGenerate = true)
    public int id; // ID ліміту [cite: 7]

    public double limit_sum; // максимальна сума ліміту [cite: 7]

    public String period; // період (наприклад, "Березень 2026") [cite: 8]

    public int category_id; // ID категорії, для якої встановлено ліміт [cite: 8]
}