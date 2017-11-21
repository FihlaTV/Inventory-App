package com.example.arjunvidyarthi.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arjun Vidyarthi on 19-Nov-17.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " (" +
                    ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ItemContract.ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL," +
                    ItemContract.ItemEntry.COLUMN_ITEM_PRICE+ " TEXT NOT NULL," +
                    ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL,"+
                    ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL," +
                    ItemContract.ItemEntry.COLUMN_ITEM_IMAGE + " TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;

    public static final int DATABASE_VERSION  = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ItemDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
