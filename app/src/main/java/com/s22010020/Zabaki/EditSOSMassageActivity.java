package com.s22010020.Zabaki;

import android.content.SharedPreferences;
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

public class EditSOSMassageActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ZabakiPrefs";
    private static final String KEY_SOS_MESSAGE = "sos_message";

    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_sos_massage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        nameEditText = findViewById(R.id.nameEditText);
        Button saveButton = findViewById(R.id.button);

        // Load saved SOS message or default text
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedMessage = prefs.getString(KEY_SOS_MESSAGE, "Help me!! I’m in Critical Situation");
        nameEditText.setText(savedMessage);

        backBtn.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            String newMessage = nameEditText.getText().toString().trim();
            if (newMessage.isEmpty()) {
                Toast.makeText(this, "SOS message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newMessage.length() >= 50) {  // You can change limit if needed
                Toast.makeText(this, "SOS message too long (max 250 characters)", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save message to SharedPreferences
            prefs.edit().putString(KEY_SOS_MESSAGE, newMessage).apply();
            Toast.makeText(this, "SOS message saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

