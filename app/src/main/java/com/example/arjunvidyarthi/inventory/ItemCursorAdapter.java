package com.example.arjunvidyarthi.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arjunvidyarthi.inventory.data.ItemContract;

/**
 * Created by Arjun Vidyarthi on 21-Nov-17.
 */

public class ItemCursorAdapter extends CursorAdapter {


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

        name.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME)));
        price.setText("$"+cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)));
        quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY))));
    }
}
