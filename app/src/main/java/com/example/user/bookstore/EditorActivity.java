package com.example.user.bookstore;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.user.bookstore.data.BookContract.BookEntry;

import com.example.user.bookstore.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int EXISTING_PET_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentBookUri;
    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;

    private ImageButton mAddButton;
    private ImageButton mCallSupplier;
    private int mQuantity = 0;

    private ImageButton mSubtractButton;
    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierName;

    private EditText mSupplierPhone;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentBookUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_prod_name);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mQuantityEditText = (EditText) findViewById(R.id.quantity);
        mSupplierName = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhone = (EditText) findViewById(R.id.supplier_phone);
        mAddButton = (ImageButton) findViewById(R.id.add_button);
        mSubtractButton = (ImageButton) findViewById(R.id.subtract_button);
        mCallSupplier=(ImageButton) findViewById(R.id.phone_button);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        mAddButton.setOnTouchListener(mTouchListener);
        mSubtractButton.setOnTouchListener(mTouchListener);




        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productQuantityString = mQuantityEditText.getText().toString();
                final int productQuantity;

                if (!productQuantityString.isEmpty()) {
                    productQuantity= Integer.parseInt(productQuantityString);
                } else {
                    productQuantity = 0;
                }

                if (mQuantityEditText == null) {
                    mQuantity = 0;
                    mQuantity = incrementQuantity(mQuantity);
                    mQuantityEditText.setText(String.valueOf(mQuantity));
                } else {
                    mQuantity = productQuantity;
                    mQuantity = incrementQuantity(mQuantity);
                    mQuantityEditText.setText(String.valueOf(mQuantity));
                }
            }
        });

        mSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productQuantityString = mQuantityEditText.getText().toString();
                final int productQuantity;

                if (!productQuantityString.isEmpty()) {
                    productQuantity= Integer.parseInt(productQuantityString);
                } else {
                    productQuantity = 0;
                }

                if (mQuantityEditText == null){
                    mQuantity = 0;
                    mQuantityEditText.setText(String.valueOf(mQuantity));
                    Toast.makeText(getApplicationContext(),
                            R.string.decrement_error,
                            Toast.LENGTH_SHORT).show();
                }

                else{
                    if(productQuantity == 0){
                        Toast.makeText(getApplicationContext(),
                                R.string.decrement_error,
                                Toast.LENGTH_SHORT).show();

                        }
                        else {
                        mQuantity = productQuantity;
                        mQuantity = decrementQuantity(mQuantity);
                        mQuantityEditText.setText(String.valueOf(mQuantity));

                    }
                }

            }
        });
        mCallSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhoneString = mSupplierPhone.getText().toString();

                if (supplierPhoneString.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            R.string.phone_error,
                            Toast.LENGTH_SHORT).show();

                } else {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + supplierPhoneString));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }

        });
    }



    private boolean saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
         String snameString = mSupplierName.getText().toString().trim();
        String sphoneString = mSupplierPhone.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(snameString)
                && TextUtils.isEmpty(sphoneString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }

        if(TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(snameString)
                || TextUtils.isEmpty(sphoneString))
        {
            Toast.makeText(this, getString(R.string.blank_fields),
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        else {
            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, snameString);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE, sphoneString);
            // If the weight is not provided by the user, don't try to parse the string into an


            // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
            if (mCurrentBookUri == null) {
                // This is a NEW pet, so insert a new pet into the provider,
                // returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_book_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                boolean check=saveBook();
                // Exit activity
                if (check == true)
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE

        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int snameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int sphoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index

            String sname = cursor.getString(snameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String sphone = cursor.getString(sphoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mSupplierName.setText(sname);
            mSupplierPhone.setText(sphone);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierName.setText("");
        mSupplierPhone.setText(""); // Select "Unknown" gender
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
    private int incrementQuantity(int quantity) {
        quantity += 1;
        return quantity;
    }

    private int decrementQuantity(int quantity) {
        quantity -= 1;
        return quantity;
    }
}
