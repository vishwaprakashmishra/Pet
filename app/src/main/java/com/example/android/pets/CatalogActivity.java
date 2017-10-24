/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import static com.example.android.pets.R.id.text_view_pet;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    // database algorithm class
    private PetDbHelper mDbHelper;
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
        mDbHelper = new PetDbHelper(this);
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // Create and/ or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Perform this raw SQL query " SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        String[] columns = { PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        /**
         * Cursor cursor = db.query(false,
                PetEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
         */
        Cursor cursor = getContentResolver().query( PetEntry.CONTENT_URI , columns, null, null, null);
        TextView displayView = (TextView) findViewById(text_view_pet);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed  - gender -weight
            //
            // In the while loop below, iterate through the rows of the
            // the information from each column in this order.
            displayView.setText("The pets table contains " + cursor.getCount() + "pets.\n\n");
            displayView.append(PetEntry._ID + " - " +
            PetEntry.COLUMN_PET_NAME + " - " +
                    PetEntry.COLUMN_PET_BREED + " - " +
                    PetEntry.COLUMN_PET_GENDER + " - " +
                    PetEntry.COLUMN_PET_WEIGHT + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or int value of th eword
                // at the current row the cursor is oon.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);

                // Desplay the values from each column of the current row in the cursor in teh
                displayView.append(("\n" + currentID + " - " + currentName + " - "
                + currentBreed + " - " + currentGender + " - "
                + currentWeight ));
            }

        } finally {
            // Always close the cursor when you're done reading from it This
            // releases all its
            // resources and makes it invalid.
            try{
                cursor.close();
            } catch (NullPointerException e ) {
                Log.e("CatalogActivity", e.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){
        // dummy content values
        ContentValues values = new ContentValues();
        // Adding the key-value map in values
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        // Insert values into db get the id of the inserted value
        Uri newRowId = getContentResolver().insert(PetEntry.CONTENT_URI,values);
        Log.i("CatalogActivity", " " + newRowId );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
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
}
