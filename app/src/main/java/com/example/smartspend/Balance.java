package com.example.smartspend;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Balance")
public class Balance {
    @PrimaryKey(autoGenerate = true)
    public int id; // унікальний ідентифікатор накопичення [cite: 10]

    public double balance; // числове значення балансу [cite: 10]

    public int balance_type; // тип балансу (1 - загальний, 2 - скарбничка) [cite: 10]
}