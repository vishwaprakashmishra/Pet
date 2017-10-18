package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for pets database
 * Created by vishwa on 10/16/17.
 */

public final class PetContract {
    // To prevent prevent someone
    // from instantiating the
    // Contract class
    // make the constructor private.
    private PetContract() {}

    /* Inner class that defines the table contents */
    public static final class PetEntry implements BaseColumns{
        /** Name of the database table for pets */
        public static final String TABLE_NAME = "pets";

        /**
         * Unique ID number for the pet ( only for use in the databaase table).
         *
         * Type: INTEGER
         */
        public static final String _ID  = BaseColumns._ID;

        /**
         * Name of the pet
         *
         * Type: TEXT
         */
        public final static String COLUMN_PET_NAME  = "name";

        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PET_BREED = "breed";

        /**
         * Gender of the pet.
         *
         * The only possible values are {@link #GENDER_MALE} , {@link #GENDER_UNKNOWN},
         * OR {@link #GENDER_FEMALE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PET_GENDER = "gender";

        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         *
         */
        public final static String COLUMN_PET_WEIGHT = "weight";

        /**
         * Possible values for the gender of the pet.
         *
         */
        public static final int GENDER_UNKNOWN = 0 ;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}
