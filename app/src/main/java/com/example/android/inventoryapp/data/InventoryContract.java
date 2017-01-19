package com.example.android.inventoryapp.data;

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

    /**
     * Table to define each item in the inventory
     */
    public static final class ItemEntry {

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
    }
}
