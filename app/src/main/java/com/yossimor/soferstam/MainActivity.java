package com.yossimor.soferstam;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    int p_parent_id=0;
    String p_menuDesc="";
    boolean child_is_files=false;
    private DBManager dbManager;
    private EditText etSearchbox;
    ActivityResultLauncher<Intent> editMenuActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearchbox = (EditText) findViewById(R.id.etSearchbox);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            p_parent_id = Integer.parseInt(b.getString("parent_id"));
            p_menuDesc = b.getString("menu_desc");
            child_is_files = b.getBoolean("child_is_files");


        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        if (p_parent_id==0){
            mTitle.setText("סופר סתם");
        }
        else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable the back button
            mTitle.setText(p_menuDesc);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editMenuActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            //int tt = data.getIntExtra("detect_result", 99);
                            load_recs(etSearchbox.getText());

                        }
                    }
                });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingAddButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                Intent intent=null;
                if (child_is_files){
                    intent = new Intent(MainActivity.this, LoadFile.class);
                }
                else{
                    intent = new Intent(MainActivity.this, EditMenu.class);
                }

                editMenuActivityResultLauncher.launch(intent);
            }


        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_load_files) {

                    Intent intent = new Intent(MainActivity.this, CopyFiles.class);
                    startActivity(intent);

                }



                return true;
            }
        });


        dbManager = new DBManager(this);
        dbManager.open();


        load_recs("");


        etSearchbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                load_recs(arg0);
            }


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });




    }

    private void load_recs(CharSequence arg0){

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);

        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Cursor cursor = dbManager.fetch_menu(p_parent_id,arg0);
        //dbManager.delete();
        RecyclerView.Adapter mAdapter;
        mAdapter = new MainActivityAdpater(cursor, new ClickListener() {
            @Override
            public void onPositionClicked(View view) {
                Bundle b = new Bundle();
                TextView tv = view.findViewById( R.id._id);
                b.putString("parent_id",  tv.getText().toString());
                tv = view.findViewById( R.id.menu_desc);
                b.putString("menu_desc",  tv.getText().toString());
                Intent intent;
                intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtras(b);
                editMenuActivityResultLauncher.launch(intent);

            }

            @Override
            public void onLongClicked(int position,View view) {
                Bundle b = new Bundle();
                TextView tv ;
                cursor.moveToPosition(position);
                @SuppressLint("Range")
                int child_is_files = cursor.getInt( cursor.getColumnIndex("child_is_files"));
                if (child_is_files==1){
                    tv = view.findViewById( R.id._id);
                    b.putString("parent_id",  tv.getText().toString());
                    tv = view.findViewById( R.id.menu_desc);
                    b.putString("menu_desc",  tv.getText().toString());
                    b.putBoolean("child_is_files",  true);                  Intent intent;
                    intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtras(b);
                    editMenuActivityResultLauncher.launch(intent);
                }
                else{
                    tv = view.findViewById( R.id._id);
                    b.putString("menu_id",  tv.getText().toString());
                    tv = view.findViewById( R.id.menu_desc);
                    b.putString("menu_desc",  tv.getText().toString());
                    tv = view.findViewById( R.id.parent_id);
                    b.putString("parent_id",  tv.getText().toString());
                    Intent intent;
                    intent = new Intent(MainActivity.this, EditMenu.class);
                    intent.putExtras(b);
                    editMenuActivityResultLauncher.launch(intent);
                }

            }


        });


        // getItemCount() have to return result greater than 0 (zero)
        recyclerView.setAdapter(mAdapter);


    }

    public interface ClickListener {


        void onPositionClicked(View view);

        void onLongClicked(int position,View view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }


            default:
                return false;
        }
    }
}