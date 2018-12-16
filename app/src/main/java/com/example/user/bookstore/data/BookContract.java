package com.example.user.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    private BookContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.user.bookstore";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "bookstore";


    public static final class BookEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "books";


        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_BOOK_NAME ="name";


        public final static String COLUMN_BOOK_PRICE = "price";


        public final static String COLUMN_BOOK_QUANTITY = "quantity";


        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        public final static String COLUMN_SUPPLIER_PHONE = "phone";


    }

}
