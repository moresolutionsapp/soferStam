package com.yossimor.soferstam;

import android.annotation.SuppressLint;
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

    public void insertControl() {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.last_parent_id ,0);
        contentValue.put(DatabaseHelper.last_tab_num ,0);
        contentValue.put(DatabaseHelper.zoom_size, 1);
        contentValue.put(DatabaseHelper.image_size, 1);
        database.insert(DatabaseHelper.control, null, contentValue);

    }



    public void control_rec_exist(){
        String Select = "SELECT count(*)  from control ;";
        String[] selectionArgs;
        selectionArgs = new String[] {};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(Select,selectionArgs);
        cursor.moveToFirst();
        if (cursor.getInt(0)==0){
            insertControl();
        }
    }

    public void updateZoomSize (double zoom_size) {
        control_rec_exist();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.zoom_size, zoom_size);
        database.update(DatabaseHelper.control, contentValue, null , null);

    }

    public void updateImageSize (double image_size) {
        control_rec_exist();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.image_size, image_size);
        database.update(DatabaseHelper.control, contentValue, null , null);

    }

    public int get_ImageSize(){
        control_rec_exist();
        String Select = "SELECT image_size  from control ;";
        String[] selectionArgs;
        selectionArgs = new String[] {};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(Select,selectionArgs);
        cursor.moveToFirst();
        return  cursor.getInt(0);

    }

    public double get_last_zoom_size(){
        control_rec_exist();
        String Select = "SELECT zoom_size  from control ;";
        String[] selectionArgs;
        selectionArgs = new String[] {};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(Select,selectionArgs);
        cursor.moveToFirst();
        return  cursor.getDouble(0);

    }

    public Cursor get_last_file(){
        control_rec_exist();
        String Select = "SELECT last_parent_id,last_tab_num  from control ;";
        String[] selectionArgs;
        selectionArgs = new String[] {};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(Select,selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    public void updateLastFile(int  last_parent_id ,int last_tab_num) {
        control_rec_exist();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.last_parent_id ,last_parent_id);
        contentValue.put(DatabaseHelper.last_tab_num ,last_tab_num);
        database.update(DatabaseHelper.control, contentValue, null , null);

    }


    public Cursor fetch_menu(int parent,int is_files,CharSequence arg0) {
        String[] selectionArgs;
        selectionArgs = new String[] { Integer.toString(parent),Integer.toString(is_files),"%" + arg0 + "%"};
        String Select = "select *"+
                " from menu "+
                " where parent_id = ? and is_files=? and" +
                " menu_desc like ? " +
                " order by _id";
        Cursor cursor = database.rawQuery(Select,selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetch_menu() {
        String[] selectionArgs;
        selectionArgs = new String[] { };
        String Select = "select *"+
                " from menu ";

        Cursor cursor = database.rawQuery(Select,selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetch_menu_files(int parent) {
        String[] selectionArgs;
        selectionArgs = new String[] { Integer.toString(parent)};
        String Select = "select *"+
                " from menu "+
                " where parent_id = ? " +
                " order by page_no";
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
        contentValue.put(DatabaseHelper.is_files, 0);
        contentValue.put(DatabaseHelper.page_no, 0);


        if(_id==0 ){
            database.insert(DatabaseHelper.menu, null, contentValue);
        }
        else{
            String[] whereArgs = new String[] {Integer.toString(_id)};
            database.update(DatabaseHelper.menu, contentValue,DatabaseHelper._id + "=?" ,whereArgs);
        }

    }

    public void menu_insert (int _id,
                                       int parent_id ,
                                       String menu_desc,
                                       int child_is_files,
                                       int is_files,
                                       int page_no) {

        ContentValues contentValue = new ContentValues();

            contentValue.put(DatabaseHelper._id ,_id);

        contentValue.put(DatabaseHelper.parent_id, parent_id);
        contentValue.put(DatabaseHelper.menu_desc, menu_desc);
        contentValue.put(DatabaseHelper.child_is_files, child_is_files);
        contentValue.put(DatabaseHelper.is_files, is_files);
        contentValue.put(DatabaseHelper.page_no, page_no);



            database.insert(DatabaseHelper.menu, null, contentValue);



    }

    public Cursor fetch_files_list() {
        String Select = "select *"+
                " from files_list ";
        Cursor cursor = database.rawQuery(Select,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void files_list_insert (String file_name) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.file_name, file_name);
        database.insert(DatabaseHelper.files_list, null, contentValue);


    }

    public void files_list_delete_all () {

        database.delete(DatabaseHelper.files_list,null,null);


    }





    public Cursor fetch_files(int parent,CharSequence arg0) {
        String[] selectionArgs;
        selectionArgs = new String[] { Integer.toString(parent),"%" + arg0 + "%"};
        String Select = "select *"+
                " from files "+
                " where parent_id = ? and" +
                " file_name like ? " +
                " order by _id,page_no";
        Cursor cursor = database.rawQuery(Select,selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }





        public void files_insert_or_update (int _id,
                                       int parent_id ,
                                       String file_name,
                                       int page_no) {

        ContentValues contentValue = new ContentValues();
        if (_id!=0){
            contentValue.put(DatabaseHelper._id ,_id);
        }
        contentValue.put(DatabaseHelper.parent_id, parent_id);
        contentValue.put(DatabaseHelper.menu_desc, file_name);
        contentValue.put(DatabaseHelper.is_files, 1);
        contentValue.put(DatabaseHelper.child_is_files, 0);
        contentValue.put(DatabaseHelper.page_no, page_no);


        if(_id==0 ){
            database.insert(DatabaseHelper.menu, null, contentValue);
        }
        else{
            String[] whereArgs = new String[] {Integer.toString(_id)};
            database.update(DatabaseHelper.menu, contentValue,DatabaseHelper._id + "=?" ,whereArgs);
        }

    }



}