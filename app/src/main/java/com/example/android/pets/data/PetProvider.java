package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * class implement content providers.
 * Created by vishwa on 10/21/17.
 */

public class PetProvider extends ContentProvider {
    /** Database helper object */
    private PetDbHelper mDbHealper;

    private static final int PETS = 100;
    private static final int PETS_ID =  101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);

    }
    /** Tag for the log message */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate (){
        // Make sure the variable is a global variable, so it
        // ContentProvider methods.
        mDbHealper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given project
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHealper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match) {
            case PETS:
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection,
                        selectionArgs,null, null,
                        sortOrder);
                break;
            case PETS_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown " + uri ) ;

        }
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given Content provider.
     */

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHealper.getWritableDatabase();
        long id = db.insert(PetEntry.TABLE_NAME,null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }
    /**
     * Delete the data at the given selection and selection and.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * Updatees the data at the given selection and selection
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
