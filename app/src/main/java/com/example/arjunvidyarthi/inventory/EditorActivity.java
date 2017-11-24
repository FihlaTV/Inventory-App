package com.example.arjunvidyarthi.inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arjunvidyarthi.inventory.data.ItemContract;

import java.io.IOException;

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
    private Button mQuantInc;
    private Button mQuantDec;
    private ImageView mItemImage;
    private Uri uriImg = null;
    private String imagePath;

    private Uri currentItemUri = null;

    private Boolean mItemChanged = false;

    private Bitmap Image;

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


        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quant);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier_contact);

        mImageButton = (Button) findViewById(R.id.image_button);
        mCallButton = (Button) findViewById(R.id.call_supplier_button);
        mDeleteButton = (Button) findViewById(R.id.item_delete);
        mSaveButton = (Button) findViewById(R.id.item_save);
        mQuantDec = (Button) findViewById(R.id.quant_dec);
        mQuantInc = (Button) findViewById(R.id.quant_inc);

        mItemImage = (ImageView) findViewById(R.id.imageView);

        mNameEditText.setOnTouchListener(mTouchListener);
        mImageButton.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mQuantInc.setOnTouchListener(mTouchListener);
        mQuantDec.setOnTouchListener(mTouchListener);

        getLoaderManager().initLoader(0, null, this);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select image"), 0);

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mNameEditText.getText().toString()) || TextUtils.isEmpty(mPriceEditText.getText().toString()) || TextUtils.isEmpty(mQuantityEditText.getText().toString()) || TextUtils.isEmpty(mSupplierEditText.getText().toString())) {
                    Toast.makeText(EditorActivity.this, "Please fill all the text fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveItem();
                finish();
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplier = mSupplierEditText.getText().toString().trim();

                if (TextUtils.isEmpty(supplier)) {
                    Toast.makeText(EditorActivity.this, "Empty phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentC = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplier));
                startActivity(intentC);

            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                builder.setMessage("Confirm deletion");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteItem();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        if (currentItemUri == null) {
            setTitle("Add an item");
            mDeleteButton.setVisibility(View.GONE);

        } else {
            setTitle("Edit item");
        }

        mQuantInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
                    mQuantityEditText.setText("1");
                    return;
                }
                int q = Integer.parseInt(mQuantityEditText.getText().toString());
                mQuantityEditText.setText(String.valueOf(q + 1));
                return;

            }
        });


        mQuantDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = Integer.parseInt(mQuantityEditText.getText().toString());
                if (q > 0) {
                    mQuantityEditText.setText(String.valueOf(q - 1));
                    return;
                } else if (TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
                    mQuantityEditText.setText("0");
                    Toast.makeText(EditorActivity.this, "Already 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                return;
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uriImg = data.getData();
                mItemImage.setImageURI(uriImg);
                mItemImage.invalidate();
            }
        }
    }


    private void deleteItem() {
        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void saveItem() {

        String itemName = mNameEditText.getText().toString().trim();
        String itemPrice = mPriceEditText.getText().toString().trim();
        int itemQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String itemSupplier = mSupplierEditText.getText().toString().trim();


        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, itemPrice);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER, itemSupplier);

        if (uriImg != null) {
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE, uriImg.toString());
        }

        if (currentItemUri == null) {

            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Insertion failed.", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Inserted item", Toast.LENGTH_SHORT).show();
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
                    ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,
                    ItemContract.ItemEntry.COLUMN_ITEM_IMAGE};

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
            mSupplierEditText.setText(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER)));

            try {
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE)));
                mItemImage.setImageURI(uri);
            } catch (NullPointerException e) {
                mItemImage.setImageResource(R.drawable.ic_add);
            } finally {
                return;
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mNameEditText.clearComposingText();
        mPriceEditText.clearComposingText();
        mQuantityEditText.clearComposingText();
        mSupplierEditText.clearComposingText();
        mItemImage.setImageResource(R.drawable.ic_add);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mItemChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have unsaved changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
