package com.example.arjunvidyarthi.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Arjun Vidyarthi on 19-Nov-17.
 */

public class ItemProvider extends ContentProvider {
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    public static final int ITEMS = 100;
    public static final int ITEM_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS+"/#", ITEM_ID);

    }
    private ItemDbHelper myDbHelper;
    @Override
    public boolean onCreate() {
        myDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = myDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case ITEMS:
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;

            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+ uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_NAME);

        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        String price = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);

        if(price == null){
            throw new IllegalArgumentException("Item requires a price.");
        }

        Integer quantity = contentValues.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);

        if(quantity == null || quantity<0 || quantity==0){
            throw new IllegalArgumentException("Item requires a valid quantity.");
        }

        String supplier = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Item requires a valid supplier contact.");
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {


        SQLiteDatabase database = myDbHelper.getWritableDatabase();

        long id = database.insert(ItemContract.ItemEntry.TABLE_NAME,null, values );

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = myDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_NAME)){
            String name = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            if(name==null){
                throw new IllegalArgumentException("Item requires a name.");
            }
        }

        if(values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)){
            String price = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            if(price == null){
                throw new IllegalArgumentException("Item requires a price.");
            }
        }

        if(values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY)){
            Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            if(quantity==null || quantity<0){
                throw new IllegalArgumentException("Item quantity invalid.");
            }
        }

        if (values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER)) {
            String supplier = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
            if (supplier == null ) {
                throw new IllegalArgumentException("Supplier contact invalid.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        return database.update(ItemContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
