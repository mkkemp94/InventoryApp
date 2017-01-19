package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

/**
 * Created by kempm on 1/19/2017.
 */

public class InventoryDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    // Constructor
    public InventoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_PETS_TABLE =
                "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                        ItemEntry._ID + " INTEGER PRIMARY KEY," +
                        ItemEntry.ITEM_NAME + " TEXT NOT NULL," +
                        ItemEntry.ITEM_SUPPLIER + " TEXT," +
                        ItemEntry.ITEM_QUANTITY + " INTEGER NOT NULL," +
                        ItemEntry.ITEM_PRICE + " INTEGER NOT NULL," +
                        ItemEntry.ITEM_IMAGE + " INTEGER NOT NULL DEFAULT 0);";
        sqLiteDatabase.execSQL(CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
