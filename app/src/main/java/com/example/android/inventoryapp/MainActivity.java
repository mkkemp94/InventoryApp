package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;
import com.example.android.inventoryapp.data.InventoryDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    // For testing
    InventoryDatabaseHelper mDbHelper;

    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open DetailsActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        // List of items in inventory
        ListView listView = (ListView) findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Get info from database
        Cursor cursor = displayDatabase();

        // Adapter for item details
        mCursorAdapter = new ItemCursorAdapter(this, cursor);
        listView.setAdapter(mCursorAdapter);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // New intent to go to DetailsActivity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                // Form the content URI for the clicked pet
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                // Set the uri on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the DetailsActivity to display the current item's details
                startActivity(intent);
            }
        });

        // Get helper for testing
        mDbHelper = new InventoryDatabaseHelper(this);


    }

    private Cursor displayDatabase() {

        // Get actual database for reading
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Specify columns I want
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.ITEM_NAME,
                ItemEntry.ITEM_SUPPLIER,
                ItemEntry.ITEM_QUANTITY,
                ItemEntry.ITEM_PRICE,
                ItemEntry.ITEM_IMAGE,
        };

        // Cursor to read from - instead of a raw query, query the db object
        // using the table name I want and custom projection
        Cursor cursor = getContentResolver().query
                (ItemEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

        return cursor;
//        TextView displayView = (TextView) findViewById(R.id.test);
//
//        // Cursors are fickle and must be closed
//        try {
//
//            displayView.setText("Number of rows: " + cursor.getCount() + "\n\n");
//
//            // Show table for testing - header (1 line)
//            displayView.append(ItemEntry._ID + " - " +
//                ItemEntry.ITEM_NAME + " - " +
//                ItemEntry.ITEM_SUPPLIER + " - " +
//                ItemEntry.ITEM_QUANTITY + " - " +
//                ItemEntry.ITEM_PRICE + " - " +
//                ItemEntry.ITEM_IMAGE  + "\n"
//            );

//            // Get column indexes
//            int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_NAME);
//            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_SUPPLIER);
//            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_QUANTITY);
//            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_PRICE);
//            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_IMAGE);
//
//            // Iterate through all rows in the cursor
//            while (cursor.moveToNext()) {
//
//                // Extract actual data form the columns with above ids
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                String currentSupplier = cursor.getString(supplierColumnIndex);
//                int currentQuantity = cursor.getInt(quantityColumnIndex);
//                int currentPrice = cursor.getInt(priceColumnIndex);
//                int currentImage = cursor.getInt(imageColumnIndex);
//
//                // Actually append the data now
//                displayView.append("\n" + currentID + " - " +
//                    currentName + " - " +
//                    currentSupplier + " - " +
//                    currentQuantity + " - " +
//                    currentPrice + " - " +
//                    currentImage
//                );
//            }
//
//        } finally {
//
//            cursor.close();
//        }
    }

    // Insert dummy item data
    private void insertItem() {

        // Map new values to their columns
        ContentValues values = new ContentValues();
        values.put(ItemEntry.ITEM_NAME, "Item X");
        values.put(ItemEntry.ITEM_SUPPLIER, "Supplier Y");
        values.put(ItemEntry.ITEM_QUANTITY, 88);
        values.put(ItemEntry.ITEM_PRICE, 562);
        values.put(ItemEntry.ITEM_IMAGE, 2);

        // Open the database for testing
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert new item into row using content provider
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                //displayDatabase();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //displayDatabase();
    }
}
