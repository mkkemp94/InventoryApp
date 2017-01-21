package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

/**
 * Created by kempm on 1/20/2017.
 */

public class InventoryProvider extends ContentProvider {

    // URI matcher code for the inventory table vs a single item
    private static final int INVENTORY = 100;
    private static final int ITEM_ID = 101;

    // Uri matcher to match a content uri to its corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer - will run first time anything from this class is called
    static {

        // Map correct code to uris depending on their type

        // "content://com.example.android.inventoryapp/items"
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, INVENTORY);

        // "content://com.example.android.inventoryapp/items/#
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    // Database helper object
    private InventoryDatabaseHelper mDbHelper;

    /**
     * Initialize the database helper
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryDatabaseHelper(getContext());
        return true;
    }

    /**
     * Read given values from table
     * @return a cursor to those values
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Initialize a cursor which will hold the query result
        Cursor cursor;

        // Match the uri to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {

            case INVENTORY:
                // Query the table directly
                cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_ID:
                // Extract out the id from the uri
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Perform query
                cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case INVENTORY:
                return ItemEntry.CONTENT_LIST_TYPE;

            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert values into table
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Find what kind or uri we have
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItem(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Do the actual inserting
     */
    private Uri insertItem(Uri uri, ContentValues contentValues) {

        // Check that the name is not null
        String name = contentValues.getAsString(ItemEntry.ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        // Check that the quantity is greater than or equal to 0
        Integer quantity = contentValues.getAsInteger(ItemEntry.ITEM_QUANTITY);
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("Item requires valid quantity");
        }

        // Check that the price is not null
        Integer price = contentValues.getAsInteger(ItemEntry.ITEM_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires a price");
        }

        // Check that the image is valid
        Integer image = contentValues.getAsInteger(ItemEntry.ITEM_IMAGE);
        if (image == null || !ItemEntry.isValidImage(image)) {
            throw new IllegalArgumentException("Item requires valid image");
        }

        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the item
        long rowId = db.insert(ItemEntry.TABLE_NAME, null, contentValues);

        // If the row id is -1, then the insertion failed
        if (rowId == -1) {
            Toast.makeText(getContext(), "ERROR : Insertion failed", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Return the new uri with row appended to end
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Find what kind or uri we have
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);

            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // Find what kind or uri we have
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);

            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues,selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {


        if (contentValues.containsKey(ItemEntry.ITEM_NAME)) {

            // Check that the name is not null
            String name = contentValues.getAsString(ItemEntry.ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (contentValues.containsKey(ItemEntry.ITEM_QUANTITY)) {

            // Check that the quantity is greater than or equal to 0
            Integer quantity = contentValues.getAsInteger(ItemEntry.ITEM_QUANTITY);
            if (quantity != null && quantity <= 0) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if (contentValues.containsKey(ItemEntry.ITEM_PRICE)) {

            // Check that the price is not null
            Integer price = contentValues.getAsInteger(ItemEntry.ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }

        if (contentValues.containsKey(ItemEntry.ITEM_IMAGE)) {

            // Check that the image is valid
            Integer image = contentValues.getAsInteger(ItemEntry.ITEM_IMAGE);
            if (image == null || !ItemEntry.isValidImage(image)) {
                throw new IllegalArgumentException("Item requires valid image");
            }
        }

        // If there are no values to update, don't bother the database
        if (contentValues.size() == 0) {
            return 0;
        }

        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the item and return the number of rows affected
        return db.update(ItemEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
}
