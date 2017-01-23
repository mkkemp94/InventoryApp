package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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

public class MainActivity extends AppCompatActivity
                            implements LoaderManager.LoaderCallbacks<Cursor> {

    // Item list adapter
    ItemCursorAdapter mCursorAdapter;

    // Loader key
    private static final int ITEM_LOADER = 333;

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

        // Adapter for item details
        mCursorAdapter = new ItemCursorAdapter(this, null);
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

        getLoaderManager().initLoader(333, null, this);
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
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {

        // Specify columns I want
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.ITEM_NAME,
                ItemEntry.ITEM_SUPPLIER,
                ItemEntry.ITEM_QUANTITY,
                ItemEntry.ITEM_PRICE,
                ItemEntry.ITEM_IMAGE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                ItemEntry.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {

        // Swap the new cursor into the adapter
        mCursorAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

        // Reset cursor adapter
        mCursorAdapter.swapCursor(null);
    }
}
