package com.s22010020.Zabaki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CardView selfDefenceCard = findViewById(R.id.selfDefenceCardView);
        CardView editSosCard = findViewById(R.id.editSosMessageCardView);
        CardView manageContactsCard = findViewById(R.id.manageContactsCardView);
        CardView addContactCard = findViewById(R.id.addContactCardView);
        CardView instructionsCard = findViewById(R.id.instructionsCardView);
        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        });

        selfDefenceCard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SelfDefenceActivity.class);
            startActivity(intent);
        });
        editSosCard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, EditSOSMassageActivity.class);
            startActivity(intent);
        });
        manageContactsCard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ManageContactsActivity.class);
            startActivity(intent);
        });
        addContactCard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
        instructionsCard.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, InstructionActivity.class);
            startActivity(intent);
        });

    }

}