// ManageContactsActivity.java
package com.s22010020.Zabaki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList; // Import ArrayList
import java.util.List;

public class ManageContactsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList = new ArrayList<>(); // Initialize contactList here to prevent NullPointerException

    private static final int EDIT_CONTACT_REQUEST_CODE = 1;

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

        contactAdapter = new ContactAdapter(this, contactList, databaseHelper);
        recyclerView.setAdapter(contactAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        // Back button functionality
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContactsActivity.this, MenuActivity.class);
            startActivity(intent);
            finish(); // Finish this activity to go back to MenuActivity
        });

        LinearLayout addContentBtn = findViewById(R.id.addContactBtn);
        addContentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContactsActivity.this, AddContactActivity.class);
            startActivity(intent);
        });

        // Removed initial load of contacts from onCreate.
        // It will now be handled by onResume(), which is guaranteed to run after onCreate.
    }

    private List<Contact> getAllContacts() {
        return databaseHelper.getAllContacts(); // Fetch all contacts from the database
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity or when it's first displayed
        refreshContactList();
    }

    private void refreshContactList() {
        // contactList is guaranteed to be non-null due to its initialization in declaration
        contactList.clear(); // Clear existing data
        contactList.addAll(databaseHelper.getAllContacts()); // Fetch updated data
        // contactAdapter is guaranteed to be non-null if onCreate completed successfully
        contactAdapter.notifyDataSetChanged(); // Notify adapter of data changes
    }

    // Handle result from EditContactActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {
            // If EditContactActivity returned RESULT_OK, refresh the list
            refreshContactList();
        }
    }
}
