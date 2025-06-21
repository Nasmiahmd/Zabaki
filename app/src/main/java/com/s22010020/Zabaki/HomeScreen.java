package com.s22010020.Zabaki;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity {
    private Button move;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView logoButton = findViewById(R.id.logoButton);
        ImageView instructionsImageView = findViewById(R.id.instructionsImageView);

        instructionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, InstructionActivity.class);
                startActivity(intent);
            }
        });


        // Set click listener for the circular logo
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreen.this, "Logo Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        move=findViewById(R.id.liveLocationBtn);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, LiveLocationActivity.class);
                startActivity(intent);
            }
        });
        move=findViewById(R.id.menuBtn);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }
}