package com.example.aditi.testing;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter
        .MyViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public RecyclerAdapter(Context context){
        this.mContext = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Indices for the _id, name

        int idIndex = mCursor.getColumnIndex(Contract.Entry._ID);
        int NameIndex = mCursor.getColumnIndex(Contract.Entry.COLUMN_NAME);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        final int id = mCursor.getInt(idIndex);
        String name = mCursor.getString(NameIndex);


        //Set values
        holder.itemView.setTag(id);
        holder.txt.setText(name);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }

        return temp;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.textView);
        }
    }
}
