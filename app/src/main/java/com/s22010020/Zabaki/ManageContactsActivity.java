package com.s22010020.Zabaki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageContactsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_contacts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        List<Contact> contactList = getAllContacts();

        ContactAdapter contactAdapter = new ContactAdapter(this, contactList, databaseHelper);
        recyclerView.setAdapter(contactAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        // Back button functionality
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContactsActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        LinearLayout addContentBtn = findViewById(R.id.addContactBtn);
        addContentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContactsActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    private List<Contact> getAllContacts() {
        return databaseHelper.getAllContacts(); // Fetch all contacts from the database
    }

}
