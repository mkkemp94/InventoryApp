package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

import java.text.NumberFormat;

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
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView imageView = (ImageView) view.findViewById(R.id.category);

        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.ITEM_PRICE);
        int imageCoulmnIndex = cursor.getColumnIndex(ItemEntry.ITEM_IMAGE);

        String itemName = cursor.getString(nameColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);
        int itemPrice = cursor.getInt(priceColumnIndex);
        int itemImage = cursor.getInt(imageCoulmnIndex);

        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        priceTextView.setText(String.valueOf(currencyFormatter.format((double) itemPrice / 100)));

        switch (itemImage) {
            case 1:
                imageView.setImageResource(R.mipmap.ic_paw);
                break;
            case 2:
                imageView.setImageResource(R.mipmap.ic_mug);
                break;
            case 3:
                imageView.setImageResource(R.mipmap.ic_paint);
                break;
            default:
                imageView.setImageResource(R.mipmap.ic_wrench);
                break;
        }
    }
}
