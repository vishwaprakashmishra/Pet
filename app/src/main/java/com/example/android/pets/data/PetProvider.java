package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

import static android.R.attr.id;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.N;

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
        // Set notification URI on the cursor.
        // so we know that content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
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

        // check that the name is not null
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        // checking if name is null or not
        if( name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);

        if( gender == null || !PetEntry.isValidGender(gender) ) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        // Checking for illegal weight if weight is present as less than 0
        if( weight != null && weight < 0 ) {
            throw new IllegalArgumentException("Pet must have a positive weight");
        }
        long id = db.insert(PetEntry.TABLE_NAME,null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        if( id == -1 ){
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet containers
        getContext().getContentResolver().notifyChange(uri,null);
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
    public int update(@NonNull Uri uri,
                      ContentValues values,
                      String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case PETS:
                updatePet(uri,values,selection,
                        selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?"
                // and select arguments will be a String array containing the actual
                // ID .
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri,values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not suppored for " + uri );

        }

    }

    /**
     * Update pets in he database with the given content valuees. Apply the changes to
     * specified in the selection and selection and selection arguments (which could be 0 or 1 both
     * Return the number of rows that were successfully update.
     * @param uri content uri
     * @param values ContentValues object passed as attributes to change
     * @param selection the where clause statement
     * @param selectionArgs Arguments for where clause
     */
    public int updatePet(Uri uri,
                          ContentValues values,
                          String selection,
                          String[] selectionArgs){
        //  validating all the entry to be inserted */
        // validating the pet name
        if( values.containsKey(PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if( name == null ) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        // validating gender of pet
        if( values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if( gender == null || !PetEntry.isValidGender(gender)){
                throw new IllegalArgumentException("illegal gender entered");
            }
        }
        // validating weight of the pets entered
        if( values.containsKey(PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if( weight != null && weight < 0 ) {
                throw new IllegalArgumentException( "Weight must be greater than 0");
            }
        }
        // No need to check the breed any value is valid including null
        if( values.size() == 0) {
            return 0;
        }
        // opening the db in writableDatabase mode
        SQLiteDatabase db = mDbHealper.getWritableDatabase();
        // running the update query
        int noOfUpdated = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
        return noOfUpdated;
    }
}
