package com.example.aditi.testing;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final int TASK_LOADER_ID = 0;
    private EditText edt;
    private Button btn;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerViewAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt = findViewById(R.id.editText);
        btn = findViewById(R.id.button);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerViewAdapter = new RecyclerAdapter(this);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.
                LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = Contract.Entry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri,null,null);

                getLoaderManager().restartLoader(TASK_LOADER_ID,null,
                        (LoaderManager.LoaderCallbacks<Object>) MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    public void addGuest(View view) {

        String input = edt.getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        contentValues.put(Contract.Entry.COLUMN_NAME, input);

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(Contract.Entry.CONTENT_URI,
                contentValues);
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        edt.getText().clear();
        getSupportLoaderManager().
                restartLoader(TASK_LOADER_ID, null, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {


                try {
                    return getContentResolver().query(Contract.Entry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }


            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }




    @Override
    public void onLoadFinished (android.support.v4.content.Loader< Cursor > loader, Cursor data){

        mRecyclerViewAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset (android.support.v4.content.Loader< Cursor > loader) {
        mRecyclerViewAdapter.swapCursor(null);
    }
}
