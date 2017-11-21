package com.example.arjunvidyarthi.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arjunvidyarthi.inventory.data.ItemContract;
import com.example.arjunvidyarthi.inventory.data.ItemDbHelper;

/**
 * Created by Arjun Vidyarthi on 21-Nov-17.
 */

public class ItemCursorAdapter extends CursorAdapter {
    private static final String TAG = null;

    public ItemCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }



    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.item_name);
        TextView price = (TextView) view.findViewById(R.id.item_price);
        TextView quantity = (TextView) view.findViewById(R.id.item_quantity);
        ImageView img = (ImageView) view.findViewById(R.id.item_image);
        Button sold = (Button) view.findViewById(R.id.item_sold);
        final int quantityProduct = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY));
        final int pos = cursor.getPosition();
        name.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME)));
        price.setText("$"+cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)));
        quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY))));

       sold.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri productUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, pos+1);
               adjustProductQuantity(context, productUri, quantityProduct);
           }
       });

    }

    private void adjustProductQuantity(Context context, Uri productUri, int currentQuantityInStock) {

        int newQuantityValue = (currentQuantityInStock >= 1) ? currentQuantityInStock - 1 : 0;

        if (currentQuantityInStock == 0) {
            Toast.makeText(context.getApplicationContext(), "Already out of stock!", Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);
        Toast.makeText(context.getApplicationContext(), "Reduced quantity by one", Toast.LENGTH_SHORT).show();
        if (numRowsUpdated < 0) {
            Log.e(TAG, "Error in updating rows.");
        }
    }
}
