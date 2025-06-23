package com.s22010020.Zabaki;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddContactActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Setup UI components
        ImageView backBtn = findViewById(R.id.backBtn);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText phoneEditText = findViewById(R.id.editTextPhone);
        Button saveButton = findViewById(R.id.button);

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // Verify inputs
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert data into SQLite
            boolean isInserted = databaseHelper.insertData(name, phone);
            if (isInserted) {
                Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                nameEditText.setText("");
                phoneEditText.setText("");
            } else {
                Toast.makeText(this, "Only 5 Contacts Allowed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}