package com.example.aditi.testing;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.aditi.testing.Contract.Entry.TABLE_NAME;

public class TaskContentProvider extends ContentProvider {

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private DbHelper mDbHelper;

    //declare uri paths

    public static final int NAME = 100;
    public static final int NAME_WITH_ID = 101;

    //create a static variable for the uri matcher

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mTaskDbHelper;


    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_TASKS, NAME);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_TASKS
                + "/#", NAME_WITH_ID);

        return uriMatcher;


    }


    @Override
    public boolean onCreate() {


        Context context = getContext();
        mDbHelper = new DbHelper(context);
        return true;

    }
    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case NAME:

                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(TABLE_NAME,
                        null,
                        values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId
                            (Contract.Entry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert "
                            +
                            "row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // COMPLETED (1) Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // COMPLETED (2) Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // COMPLETED (3) Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case NAME:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // COMPLETED (4) Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);


        // Keep track of the number of deleted tasks

        int tasksDeleted; // starts as 0


        // COMPLETED (2) Write the code to delete a single row of data
        switch (match){

            case NAME_WITH_ID:

                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME,"_id=?",
                        new String[]{id});
                break;

                default:
                    throw  new UnsupportedOperationException("Unknown Uri:"+uri);
        }
        if (tasksDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return tasksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int tasksUpdated;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case NAME_WITH_ID   :
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mTaskDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }




    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
