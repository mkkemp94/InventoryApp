package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class should not be extended. It just provides constants.
 *
 * Created by kempm on 1/19/2017.
 */

public final class InventoryContract {

    /**
     * Empty constructor SHOULD NOT be initialized
     */
    private InventoryContract() {}

    // Content authority is the unique name for the content provider
    // Base content uri is the actual uri
    // Path items is a possible path that will be appended to the base uri later
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    /**
     * Table to define each item in the inventory
     */
    public static final class ItemEntry implements BaseColumns {

        // The content uri to access data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        // Mime type of the CONTENT_URI for a list of items and single item
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Table name
        public static final String TABLE_NAME = "inventory";

        // Column names
        public static final String _ID = BaseColumns._ID;
        public static final String ITEM_NAME = "name";
        public static final String ITEM_SUPPLIER = "supplier";
        public static final String ITEM_QUANTITY = "quantity";
        public static final String ITEM_PRICE = "price";
        public static final String ITEM_IMAGE = "image";

        // Possible image values
        public static final int IMAGE_UNKNOWN = 0;
        public static final int IMAGE_CATEGORY_1 = 1;
        public static final int IMAGE_CATEGORY_2 = 2;
        public static final int IMAGE_CATEGORY_3 = 3;

        public static boolean isValidImage(int image) {
            return image == 0 || image ==1 || image == 2 || image == 3;
        }
    }
}
