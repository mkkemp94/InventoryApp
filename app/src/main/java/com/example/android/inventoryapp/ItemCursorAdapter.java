package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

/**
 * Created by kempm on 1/22/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {

    // Constructor
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0); // 0 flags
    }

    /**
     * Make a new blank list view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_QUANTITY);

        String itemName = cursor.getString(nameColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
    }
}
