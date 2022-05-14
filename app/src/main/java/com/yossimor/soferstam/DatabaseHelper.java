package com.yossimor.soferstam;;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "soferStam.DB";
    // database version
    static final int DB_VERSION = 19;







    // Table Name
    public static final String menu = "menu";
    // Table columns
    public static final String _id = "_id";
    public static final String parent_id = "parent_id";
    public static final String menu_desc = "menu_desc";
    public static final String child_is_files = "child_is_files";
    public static final String is_file = "is_files";
    public static final String page_no = "page_no";


    // Creating table query
    private static final String CREATE_TABLE_menu =
            "create table IF NOT EXISTS " + menu +
                    "(" + _id + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + parent_id + " int , "
                    + menu_desc + " text ,"
                    + child_is_files + " int ,"
                    + is_file + " int ,"
                    + page_no + " int " +
                    ");";


    // Table Name
    public static final String files_list = "files_list";
    // Table columns
    public static final String __id = "_id";
    public static final String file_name = "file_name";



    // Creating table query
    private static final String CREATE_TABLE_files_list =
            "create table IF NOT EXISTS " + files_list +
                    "(" + _id + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + file_name + " int  " +
                    ");";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_menu);
        db.execSQL(CREATE_TABLE_files_list);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("drop table menu");
        db.execSQL(CREATE_TABLE_menu);
        db.execSQL(CREATE_TABLE_files_list);

    }





}