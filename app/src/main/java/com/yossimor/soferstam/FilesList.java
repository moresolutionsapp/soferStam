package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FilesList extends AppCompatActivity {
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearchbox;
    private boolean come_from_manage=false;
    private final int LAUNCH_BRANCH_EDIT=1;


    final String[] from = new String[] {DatabaseHelper.file_name};

    final int[] to = new int[] {R.id.file_name};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_list);

        dbManager = new DBManager(this);
        dbManager.open();

        cursor = dbManager.fetch_files_list() ;
        adapter = new SimpleCursorAdapter(this, R.layout.activity_files_list_items, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView = findViewById(R.id.selectListView);
        listView.setAdapter(adapter);

    }
}