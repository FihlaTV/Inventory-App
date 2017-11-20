package com.example.arjunvidyarthi.inventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arjunvidyarthi.inventory.data.ItemContract;

/**
 * Created by Arjun Vidyarthi on 19-Nov-17.
 */

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private Button mImageButton;
    private Button mCallButton;
    private Button mDeleteButton;
    private Button mSaveButton;
    private ImageView mItemImage;

    private Uri currentItemUri = null;

    private Boolean mItemChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        if(currentItemUri==null){
            setTitle("Add an item");
        } else{
            setTitle("Edit item");
        }

        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quant);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier_contact);

        mImageButton = (Button) findViewById(R.id.image_button);
        mCallButton = (Button) findViewById(R.id.call_supplier_button);
        mDeleteButton = (Button) findViewById(R.id.item_delete);
        mSaveButton = (Button) findViewById(R.id.item_save);

        mItemImage = (ImageView) findViewById(R.id.item_image);

        getLoaderManager().initLoader(0, null, this);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
                finish();
            }
        });
    }

    private void saveItem(){
        String itemName = mNameEditText.getText().toString().trim();
        String itemPrice = mPriceEditText.getText().toString().trim();
        int itemQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        int itemSupplier = Integer.parseInt(mSupplierEditText.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, itemPrice);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER, itemSupplier);

        if (currentItemUri == null) {

            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Insertion failed.",Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Inserted item",Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsUpdated = getContentResolver().update(currentItemUri, values, null, null);

            if (rowsUpdated == 0) {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (currentItemUri != null) {
            String[] projection = {ItemContract.ItemEntry._ID,
                    ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                    ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                    ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                    ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER};

            return new CursorLoader(this,
                    currentItemUri,
                    projection,
                    null,
                    null,
                    null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME)));
            mPriceEditText.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)));
            mQuantityEditText.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY))));
            mSupplierEditText.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER))));
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mNameEditText.clearComposingText();
        mPriceEditText.clearComposingText();
        mQuantityEditText.clearComposingText();
        mSupplierEditText.clearComposingText();
    }
}
