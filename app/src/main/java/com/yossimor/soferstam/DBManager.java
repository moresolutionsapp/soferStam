package com.yossimor.soferstam;;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import androidx.core.app.ShareCompat;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.Calendar;


public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public Cursor fetch_menu(int parent,CharSequence arg0) {
        String[] selectionArgs;
        selectionArgs = new String[] { Integer.toString(parent),"%" + arg0 + "%"};
        String Select = "select *"+
                " from menu "+
                " where parent_id = ? and" +
                " menu_desc like ? " +
                " order by _id";
        Cursor cursor = database.rawQuery(Select,selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void menu_insert_or_update (int _id,
                                       int parent_id ,
                                       String menu_desc,
                                       int child_is_files) {

        ContentValues contentValue = new ContentValues();
        if (_id!=0){
            contentValue.put(DatabaseHelper._id ,_id);
        }
        contentValue.put(DatabaseHelper.parent_id, parent_id);
        contentValue.put(DatabaseHelper.menu_desc, menu_desc);
        contentValue.put(DatabaseHelper.child_is_files, child_is_files);


        if(_id==0 ){
            database.insert(DatabaseHelper.menu, null, contentValue);
        }
        else{
            String[] whereArgs = new String[] {Integer.toString(_id)};
            database.update(DatabaseHelper.menu, contentValue,DatabaseHelper._id + "=?" ,whereArgs);
        }

    }



}