package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;
import com.example.android.inventoryapp.data.InventoryDatabaseHelper;

public class DatailsActivity extends AppCompatActivity {

    /** EditText field to enter the item name */
    private EditText mNameEditText;

    /** EditText field to enter the item supplier */
    private EditText mSupplierEditText;

    /** EditText field to enter the item's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the item's price */
    private EditText mPriceEditText;

    /** Spinner to choose an image */
    private Spinner mImageSpinner;

    /** Image for the item. Possible values are 0 for unknown, 1, 2, and 3 */
    private int mImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mSupplierEditText = (EditText) findViewById(R.id.edit_item_supplier);
        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mImageSpinner = (Spinner) findViewById(R.id.spinner_image);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the quantity of the item.
     */
    private void setupSpinner() {

        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter imageSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_image_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        imageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mImageSpinner.setAdapter(imageSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mImageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_1))) {
                        mImage = ItemEntry.IMAGE_CATEGORY_1;
                    } else if (selection.equals(getString(R.string.category_2))) {
                        mImage = ItemEntry.IMAGE_CATEGORY_2;
                    } else if (selection.equals(getString(R.string.category_3))) {
                        mImage = ItemEntry.IMAGE_CATEGORY_3;
                    } else {
                        mImage = ItemEntry.IMAGE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mImage = ItemEntry.IMAGE_UNKNOWN; // Unknown
            }
        });
    }

    // Gets values from edit text fields to insert pet into database
    private void insertItem() {

        // Retrieve data from edit text fields
        String nameString = mNameEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        int priceInt = Integer.parseInt(mPriceEditText.getText().toString().trim());
        int imageInt = mImage;

        // Open helper for testing
        InventoryDatabaseHelper helper = new InventoryDatabaseHelper(this);

        // Open database for testing
        SQLiteDatabase db = helper.getWritableDatabase();

        // Put data together using a content values map
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemEntry.ITEM_NAME, nameString);
        contentValues.put(ItemEntry.ITEM_SUPPLIER, supplierString);
        contentValues.put(ItemEntry.ITEM_QUANTITY, quantityInt);
        contentValues.put(ItemEntry.ITEM_PRICE, priceInt);
        contentValues.put(ItemEntry.ITEM_IMAGE, imageInt);

        long rowsInserted = db.insert(ItemEntry.TABLE_NAME, null, contentValues);

        if (rowsInserted < 0) {
            Toast.makeText(this, "Error : item not added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Item added to inventory", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_edit_page.xmlxml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertItem();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
