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
        final int pos = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry._ID));

        name.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME)));
        price.setText("$"+cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)));
        quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY))));

        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri itemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, pos);
                decreaseProductQuantity(context, itemUri, quantityProduct);
            }
        });

        try {
            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE)));
            img.setImageURI(uri);
        } catch (NullPointerException e) {
            img.setImageResource(R.drawable.ic_add);
        } finally {
            return;
        }

    }

    private void decreaseProductQuantity(Context context, Uri itemUri, int currentQuantity) {

        int newQuantityValue = (currentQuantity >= 1) ? currentQuantity - 1 : 0;

        if (currentQuantity == 0) {
            Toast.makeText(context.getApplicationContext(), "Already out of stock!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(itemUri, contentValues, null, null);
        if (numRowsUpdated > 0) {
            Toast.makeText(context.getApplicationContext(), "Reduced quantity by one", Toast.LENGTH_SHORT).show();
        }
        if (numRowsUpdated <= 0) {
            Toast.makeText(context.getApplicationContext(), "Error in updating", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in updating rows.");
        }
    }
}
