package com.s22010020.Zabaki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Contacts.db";
    private static final String TABLE_NAME = "contact_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "NUMBER";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, NUMBER TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check current number of contacts
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count >= 5) {
            // Limit reached, do not insert more
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, number);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                contacts.add(new Contact(id, name, number));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }

    public boolean updateData(int id, String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id); // Ensure ID is included for update
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, number);
        // Update the row where ID matches
        int result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0; // Returns true if at least one row was updated
    }

    public boolean deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
