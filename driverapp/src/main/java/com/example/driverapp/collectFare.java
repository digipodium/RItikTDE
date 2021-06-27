package com.example.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class collectFare extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_fare);
        TextView textPrice = findViewById(R.id.price);
        double price = getIntent().getDoubleExtra("price", 0.0);
        textPrice.setText(" " + String.valueOf(price));
        findViewById(R.id.taskDone).setOnClickListener(view -> {
            startActivity(new Intent(this, MainScreen.class));
            finish();
        });
    }
}