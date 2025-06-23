package com.s22010020.Zabaki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final Context context;
    private final List<Contact> contactList;
    private final DatabaseHelper databaseHelper;

    public ContactAdapter(Context context, List<Contact> contactList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.contactList = contactList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.nameTextView.setText(contact.getName());
        holder.numberTextView.setText(contact.getNumber());

        // Edit Button
        holder.editButton.setOnClickListener(v -> Toast.makeText(context, "Edit feature is under development", Toast.LENGTH_SHORT).show());

        // Delete Button
        holder.deleteButton.setOnClickListener(v -> {
            boolean isDeleted = databaseHelper.deleteData(contact.getId());
            if (isDeleted) {
                contactList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete contact", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, numberTextView;
        Button editButton, deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
