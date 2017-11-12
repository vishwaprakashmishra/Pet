package com.example.android.pets;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;


/**
 * Displays list of pets that were entered and stored in the app.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    // database algorithm class
    private PetDbHelper mDbHelper;

    // Adapter for the cursor
    private PetCursorAdapter mAdapter;
    private static final int PET_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Creating instance for PetDbHelper for using getReadableDatabase and
        // getWritableDatabase function
        mDbHelper = new PetDbHelper(this);
        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the
        // list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        LoaderManager loaderManager = getLoaderManager();
        // initialize loaderManager with the callback as this class
        loaderManager.initLoader(PET_LOADER_ID,null,this);
    }

    private void displayDatabaseInfo() {
        // Perform this raw SQL query " SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        String[] columns = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,
                columns,
                null,
                null,
                null);
        Log.i("CatalogActivity", "loaded the cursor successfully");
        if( cursor == null ) {

            return;

        }

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of pet
        // data in the cursor.
        PetCursorAdapter adapter = new PetCursorAdapter(this, cursor);
        Log.i("CatalogActivity", "executed code for binding the listview and cursor");

        // Attach the adapter to the ListView
        // Adapter manages the closing of the adapter
        petListView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {
        // dummy content values
        ContentValues values = new ContentValues();
        // Adding the key-value map in values
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        // Insert values into db get the id of the inserted value
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future

        Uri newRowId = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * this will be called when the class again starts
     * meaning we can update the row count here
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // TODO: initialize all the element needed to query the data from the

        // ass the projection from pets table
        // reduced to only two rows which required for
        // improving the performance
        String[] columns = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};
        return new CursorLoader(this,
                PetEntry.CONTENT_URI,
                columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // find the list view which will be populated with
        // the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // setting the content of cursor with the PetCursorAdapter
        mAdapter = new PetCursorAdapter(this, cursor);
        petListView.setAdapter(mAdapter);
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: HANDLE onItemClick of the list item;
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO ; destroy the cursor for regaining memory
        loader.reset();
    }
}
