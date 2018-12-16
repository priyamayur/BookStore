package com.example.user.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;

import com.example.user.bookstore.data.BookContract;
import com.example.user.bookstore.data.BookContract.BookEntry;
import android.widget.TextView;
import android.widget.Toast;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }



    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        Button saleButton =(Button) view.findViewById(R.id.sale_button);
        final TextView quantityView=(TextView) view.findViewById(R.id.quantity_main);
        TextView nameTextView = (TextView) view.findViewById(R.id.name_main);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_main);


        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int idIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
        final int col = cursor.getInt(idIndex);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 int quantity;

               if(!quantityView.getText().toString().isEmpty()){
                String quantityString = quantityView.getText().toString().trim();
                quantity= Integer.parseInt(quantityString);
                }
                else
                  quantity=0;

                if (quantity==0){
                    Toast.makeText(context, R.string.sold_error,
                            Toast.LENGTH_SHORT).show();

                }
                else
                {

                    quantity-= 1;
                    ContentValues values = new ContentValues();
                   values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    Uri updateUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, col);
                    int rowsUpdated = context.getContentResolver().update(updateUri, values, null, null);




                if (rowsUpdated == 0) {
                    Toast.makeText(context, R.string.editor_update_book_failed,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.sold_item,
                            Toast.LENGTH_SHORT).show();}

                }


            }
        });





        // Read the pet attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = String.valueOf(cursor.getInt(priceColumnIndex));
        String bookQuantity = String.valueOf(cursor.getInt(quantityColumnIndex));

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(bookPrice)) {
            bookPrice = context.getString(R.string.unknown_price);
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        quantityView.setText(bookQuantity);



    }


}
