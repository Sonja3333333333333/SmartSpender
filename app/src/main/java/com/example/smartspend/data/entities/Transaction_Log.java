package com.example.smartspend.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Встановлюємо зв'язок: одна категорія — багато операцій
@Entity(tableName = "Transaction_Log",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL))
public class Transaction_Log {
    @PrimaryKey(autoGenerate = true)
    public int id; // ідентифікатор операції [cite: 5]

    public double sum; // сума операції [cite: 5]

    public String type; // тип: 'income', 'expense', 'savings' [cite: 5]

    public Integer category_id; // ID категорії (лише для витрат, може бути null) [cite: 5]

    public long date; // дата здійснення операції [cite: 5]

    public String comment; // коментар до операції [cite: 5]

    public int is_from_savings; // чи була здійснена витрата зі скарбнички (0 - ні, 1 - так) [cite: 5]
}