package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

import java.text.NumberFormat;
import java.text.ParseException;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader value
    private static final int EXISTING_ITEM_LOADER = 555;

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
    private int mImage = ItemEntry.IMAGE_UNKNOWN;

    // If in edit mode, this is the current item uri
    private Uri mCurrentItemUri;

    // Sets if item has changed in some way, to keep user from exiting accidentally
    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get the intent that called this class and extract its data
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {

            // In add pet mode
            setTitle("Add an item");
            invalidateOptionsMenu();
        } else {

            // In edit item mode
            setTitle("Edit item");

            // Initiate loader
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mSupplierEditText = (EditText) findViewById(R.id.edit_item_supplier);
        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mImageSpinner = (Spinner) findViewById(R.id.spinner_image);

        // Detect whether or not the views have been touched and editted
        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mImageSpinner.setOnTouchListener(mTouchListener);

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

    /**
     * Gets values from edit text fields to insert item into database
     */
    private void saveItem() {

        // Return without saving an item if all fields are empty
        if (TextUtils.isEmpty(mNameEditText.getText()) &&
                TextUtils.isEmpty(mSupplierEditText.getText()) &&
                TextUtils.isEmpty(mQuantityEditText.getText()) &&
                TextUtils.isEmpty(mPriceEditText.getText()) &&
                mImage == ItemEntry.IMAGE_UNKNOWN) {

            return;
        }

        // Retrieve data from edit text fields
        String nameString = mNameEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int imageInt = mImage;

        // Get quantity and price as ints
        int quantityInt = getQuantityInt(quantityString);
        int priceInt = getPriceInt(priceString);

        // Put data together using a content values map
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemEntry.ITEM_NAME, nameString);
        contentValues.put(ItemEntry.ITEM_SUPPLIER, supplierString);
        contentValues.put(ItemEntry.ITEM_QUANTITY, quantityInt);
        contentValues.put(ItemEntry.ITEM_PRICE, priceInt);
        contentValues.put(ItemEntry.ITEM_IMAGE, imageInt);

        // Add item
        if (mCurrentItemUri == null) {

            // Insert new item
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, contentValues);

            if (newUri == null) {
                Toast.makeText(this, "Error : item not added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item added to inventory", Toast.LENGTH_SHORT).show();
            }
        } else {

            // Update item
            int rowsAffected = getContentResolver().update(mCurrentItemUri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "ERROR : Item not updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item successfully updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 
     * Gets a quantity int out of a string
     */
    private int getQuantityInt(String quantityString) {

        int quantity = 0;

        // If quantity is empty, assign it 0
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        return quantity;
    }

    /** 
     * Format and get a price
     */
    private int getPriceInt(String priceString) {

        // If there is no price, return 0
        if (TextUtils.isEmpty(priceString)) {
            return 0;
        }

        // Get the currency value from the edit text and change it into a number
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        Number priceNumber = null;
        try {
            priceNumber = currencyFormatter.parse(priceString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If at this point there is only a $
        if (priceNumber == null) {
            return 0;
        }

        // Change the price from a double to an int
        return (int) (priceNumber.doubleValue() * 100);
    }

    /**
     * Confirm that the user wants to delete an item
     */
    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
    }

    /**
     * Show dialog to say that something has changed.
     * This will be invoked upon back press.
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform deletion of item from database
     */
    private void deletePet() {

        if (mCurrentItemUri != null) {

            // Deletes this item entirely
            int mRowsDeleted = getContentResolver().delete(
                    mCurrentItemUri,
                    null,
                    null
            );

            // Show toast message
            if (mRowsDeleted == 0) {
                Toast.makeText(this, R.string.pet_not_deleted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.pet_deleted, Toast.LENGTH_SHORT).show();
            }

            // Exit this activity
            finish();
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
                // Insert this item into the database
                saveItem();
                // Exit activity
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                // Navigate back to parent activity (CatalogActivity)
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                // Set up on click listener for back on no changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {

                            // On positive button click
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create cursor for all columns
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Names of columns to call from the table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.ITEM_NAME,
                ItemEntry.ITEM_SUPPLIER,
                ItemEntry.ITEM_QUANTITY,
                ItemEntry.ITEM_PRICE,
                ItemEntry.ITEM_IMAGE
        };

        // Create and return a CursorLoader that will take care of creating a cursor for the data
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null, null, null);

    }

    /**
     * Populate edit text fields with data from cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor thisCursor) {

        // Move cursor to header row
        if (thisCursor.moveToFirst()) {

            // Get column indices
            int nameColumnIndex = thisCursor.getColumnIndex(ItemEntry.ITEM_NAME);
            int supplierColumnIndex = thisCursor.getColumnIndex(ItemEntry.ITEM_SUPPLIER);
            int quantityColumnIndex = thisCursor.getColumnIndex(ItemEntry.ITEM_QUANTITY);
            int priceColumnIndex = thisCursor.getColumnIndex(ItemEntry.ITEM_PRICE);
            int imageColumnIndex = thisCursor.getColumnIndex(ItemEntry.ITEM_IMAGE);

            // Extract data from those columns
            String itemName = thisCursor.getString(nameColumnIndex);
            String itemSupplier = thisCursor.getString(supplierColumnIndex);
            int itemQuantity = thisCursor.getInt(quantityColumnIndex);
            int priceQuantity = thisCursor.getInt(priceColumnIndex);
            int imageValue = thisCursor.getInt(imageColumnIndex);

            // Update edit text fields
            mNameEditText.setText(itemName);
            mSupplierEditText.setText(itemSupplier);
            mQuantityEditText.setText(String.valueOf(itemQuantity));

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
            mPriceEditText.setText(String.valueOf(currencyFormatter.format((double) priceQuantity / 100)));

            switch (imageValue) {
                case ItemEntry.IMAGE_CATEGORY_1:
                    mImageSpinner.setSelection(1);
                    break;

                case ItemEntry.IMAGE_CATEGORY_2:
                    mImageSpinner.setSelection(2);
                    break;

                case ItemEntry.IMAGE_CATEGORY_3:
                    mImageSpinner.setSelection(3);
                    break;
                default:
                    mImageSpinner.setSelection(0);
                    break;
            }
        }
    }

    /**
     * Clear out edit text fields
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mImageSpinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {

        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Show dialog if something has changed
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
