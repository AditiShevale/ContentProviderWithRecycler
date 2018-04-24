package com.example.aditi.testing;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY
            = "com.example.aditi.testing";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_TASKS = "details";


    public static final class Entry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();


        // Task table and column names
        public static final String TABLE_NAME = "details";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_NAME = "name";


    }


}
