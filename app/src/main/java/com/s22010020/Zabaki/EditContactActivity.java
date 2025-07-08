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

public class EditContactActivity extends AppCompatActivity {

    private EditText editTextName, editTextNumber;
    private Button buttonSaveContact;
    private DatabaseHelper databaseHelper;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextNumber = findViewById(R.id.editTextNumber);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);
        ImageView backBtn = findViewById(R.id.backBtn);

        // Get contact data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contactId = extras.getInt("contact_id");
            String name = extras.getString("contact_name");
            String number = extras.getString("contact_number");

            editTextName.setText(name);
            editTextNumber.setText(number);
        } else {
            // Handle case where no contact data is passed (e.g., show an error or finish activity)
            Toast.makeText(this, "No contact data provided for editing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backBtn.setOnClickListener(v -> finish());

        buttonSaveContact.setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            String newNumber = editTextNumber.getText().toString().trim();

            if (newName.isEmpty() || newNumber.isEmpty()) {
                Toast.makeText(EditContactActivity.this, "Name and Number cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUpdated = databaseHelper.updateData(contactId, newName, newNumber);
                if (isUpdated) {
                    Toast.makeText(EditContactActivity.this, "Contact Updated Successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Indicate that an update occurred
                    finish(); // Go back to ManageContactsActivity
                } else {
                    Toast.makeText(EditContactActivity.this, "Failed to update contact", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}