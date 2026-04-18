package com.example.smartspend;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SpendSavingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_savings);

        // Кнопка Скасувати просто закриває цей екран і повертає назад
        Button btnCancel = findViewById(R.id.btn_cancel_spend);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
    }
}