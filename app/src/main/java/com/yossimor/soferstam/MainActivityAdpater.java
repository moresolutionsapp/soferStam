package com.yossimor.soferstam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivityAdpater extends RecyclerView.Adapter<MainActivityAdpater.MyViewHolder> {

    public Cursor cursor;
    private final MainActivity.ClickListener listener;



    public MainActivityAdpater(Cursor cursor,MainActivity.ClickListener listener)
    {
        this.cursor=cursor;
        this.listener = listener;


    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        public TextView _id;
        public TextView parent_id;
        public TextView menu_desc;
        public TextView child_is_files;


        private WeakReference<MainActivity.ClickListener> listenerRef;


        public MyViewHolder(@NonNull View itemView,
                                    TextView _id,
                                    TextView parent_id,
                                    TextView menu_desc,
                                    TextView child_is_files,
                                    MainActivity.ClickListener listener
                                    ) {
            super(itemView);
            this._id = _id  ;
            this.parent_id = parent_id  ;
            this.menu_desc = menu_desc;
            this.child_is_files = child_is_files;
            listenerRef = new WeakReference<>(listener);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);



        }

        @Override
        public void onClick(View view) {
            listenerRef.get().onPositionClicked(getAdapterPosition(),itemView);
        }

        @Override
        public boolean onLongClick(View view) {
            listenerRef.get().onLongClicked(getAdapterPosition(),itemView);
            return true;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainActivityAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_items, parent, false);
        TextView _id = v.findViewById(R.id._id);
        TextView parent_id = v.findViewById(R.id.parent_id);
        TextView menu_desc = v.findViewById(R.id.menu_desc);
        TextView child_is_files = v.findViewById(R.id.child_is_files);


        MainActivityAdpater.MyViewHolder vh =
                new MainActivityAdpater.MyViewHolder(v,_id,parent_id,menu_desc,child_is_files,listener);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(MainActivityAdpater.MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        cursor.moveToPosition(position);
        holder._id.setText(cursor.getString( cursor.getColumnIndex("_id")));
        holder.menu_desc.setText(cursor.getString( cursor.getColumnIndex("menu_desc")));
        holder.parent_id.setText(cursor.getString( cursor.getColumnIndex("parent_id")));
        holder.child_is_files.setText(cursor.getString( cursor.getColumnIndex("child_is_files")));




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return cursor.getCount();
    }




}
