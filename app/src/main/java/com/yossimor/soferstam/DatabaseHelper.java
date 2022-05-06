package com.yossimor.soferstam;;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "soferStam.DB";
    // database version
    static final int DB_VERSION = 13;


    // Table Name
    public static final String menu = "menu";
    // Table columns
    public static final String _id = "_id";
    public static final String parent_id = "parent_id";
    public static final String menu_desc = "menu_desc";
    public static final String child_is_files = "child_is_files";


    // Creating table query
    private static final String CREATE_TABLE_menu =
            "create table IF NOT EXISTS " + menu +
                    "(" + _id + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + parent_id + " int , "
                    + menu_desc + " text ,"
                    + child_is_files + " int " +
                    ");";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_menu);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table menu");
        db.execSQL(CREATE_TABLE_menu);



    }



}