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
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    int p_parent_id=0;
    String p_menuDesc="";
    boolean child_is_files=false;
    private DBManager dbManager;
    private EditText etSearchbox;
    ActivityResultLauncher<Intent> editMenuActivityResultLauncher;
    private CheckBox checkBox;
    BottomNavigationView bottomNavigationView;
    Cursor cursor;
    int click_counter =0;
    boolean timerStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearchbox = (EditText) findViewById(R.id.etSearchbox);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingAddButton);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        ImageView logo =  toolbar.findViewById(R.id.logo);
        checkBox = toolbar.findViewById(R.id.checkbox);

        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (click_counter>=7){
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(true);
                }
                click_counter=0;
                timerStart=false;
            }
        };

        mTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!timerStart && !checkBox.isChecked()){
                    timerStart=true;
                    timerHandler.postDelayed(timerRunnable,1500);
                }
                click_counter++;


            }
        });



        checkBox = toolbar.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    bottomNavigationView = findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.getMenu().clear(); //clear old inflated items.
                    bottomNavigationView.inflateMenu(R.menu.load);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                else{
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    bottomNavigationView.getMenu().clear(); //clear old inflated items.
                    bottomNavigationView.inflateMenu(R.menu.setting);
                    checkBox.setVisibility(View.INVISIBLE);
                }


            }
        });



        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("parent_id")!=null) {
                p_parent_id = Integer.parseInt(b.getString("parent_id"));
            }
            p_menuDesc = b.getString("menu_desc");
            child_is_files = b.getBoolean("child_is_files");
            checkBox.setChecked(b.getBoolean("checkBox"));

        }
        else{
            logo.setVisibility(View.VISIBLE);
        }



        setSupportActionBar(toolbar);
        if (p_parent_id==0){
            mTitle.setText("סופר סתם");
        }
        else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable the back button
            mTitle.setText(p_menuDesc.trim());
            checkBox.setVisibility(View.INVISIBLE);
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


        if (checkBox.isChecked()){
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {


                    Intent intent=null;
                    if (child_is_files){
                        intent = new Intent(MainActivity.this, LoadFile.class);
                        Bundle b = new Bundle();
                        b.putString("parent_id",  String.valueOf(p_parent_id));
                        b.putString("menu_desc",  p_menuDesc);
                        intent.putExtras(b);
                    }
                    else{
                        intent = new Intent(MainActivity.this, EditMenu.class);
                        Bundle b = new Bundle();
                        b.putString("menu_id",  "0");
                        b.putString("parent_id",  String.valueOf(p_parent_id));
                        intent.putExtras(b);

                    }

                    editMenuActivityResultLauncher.launch(intent);


            }


        });


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("Range")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (checkBox.isChecked()){
                    if (item.getItemId() == R.id.action_load_files) {

                        Intent intent = new Intent(MainActivity.this, CopyFiles.class);
                        startActivity(intent);

                    }
                    if (item.getItemId() == R.id.action_save_menu) {
                        Intent intent = new Intent(MainActivity.this, SaveMenu.class);
                        startActivity(intent);
                    }

                    if (item.getItemId() == R.id.action_load_menu) {
                        Intent intent = new Intent(MainActivity.this, CopyMenuCsvToDb.class);
                        startActivity(intent);
                    }

                }
                else{
                    if (item.getItemId() == R.id.action_last_file) {
                        DBManager dbManager = new DBManager(MainActivity.this);
                        dbManager.open();
                        Cursor cursor = dbManager.get_last_file();
                        dbManager.close();
                        Bundle b = new Bundle();
                        int last_parent_id = cursor.getInt(cursor.getColumnIndex("last_parent_id"));
                        if (last_parent_id!=0){
                            b.putString("parent_id",  String.valueOf(last_parent_id));
                            b.putString("last_tab",  String.valueOf(cursor.getInt(cursor.getColumnIndex("last_tab_num"))));
                            Intent intent;
                            intent = new Intent(MainActivity.this, ShowFiles.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }


                    }

                    if (item.getItemId() == R.id.action_image_size) {
                        Bundle b = new Bundle();
                        b.putString("parent_id",  "-1");
                        b.putString("menu_desc",  "גודל טקסט");
                        b.putBoolean("child_is_files",  false);
                        b.putBoolean("checkBox", checkBox.isChecked());
                        Intent intent;
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtras(b);
                        editMenuActivityResultLauncher.launch(intent);

                    }

                    if (item.getItemId() == R.id.action_screen_size) {

                    }
                }






                return true;
            }
        });


        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.control_rec_exist();
        int imageSize =dbManager.get_ImageSize();
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.action_image_size);
        menuItem.setTitle("גודל טקסט ("+ (imageSize) + ")");
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
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        cursor = dbManager.fetch_menu(p_parent_id,(child_is_files ? 1:0),arg0);
        //dbManager.delete();
        RecyclerView.Adapter mAdapter;
        mAdapter = new MainActivityAdpater(cursor, new ClickListener() {
            @SuppressLint("Range")
            @Override
            public void onPositionClicked(int position,View view) {
                Bundle b = new Bundle();
                cursor.moveToPosition(position);
                @SuppressLint("Range") int child_is_files = cursor.getInt( cursor.getColumnIndex("child_is_files"));
                @SuppressLint("Range") int is_files = cursor.getInt( cursor.getColumnIndex("is_files"));
                if (is_files==0){
                    if (child_is_files==1 )
                        if (!checkBox.isChecked()){
                            TextView tv = view.findViewById( R.id._id);
                            b.putString("parent_id",  tv.getText().toString());
                            Intent intent;
                            intent = new Intent(MainActivity.this, ShowFiles.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                        else {
                            TextView tv = view.findViewById( R.id._id);
                            b.putString("parent_id",  tv.getText().toString());
                            tv = view.findViewById( R.id.menu_desc);
                            b.putString("menu_desc",  tv.getText().toString());
                            b.putBoolean("child_is_files", true);
                            b.putBoolean("checkBox", checkBox.isChecked());
                            Intent intent;
                            intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(b);
                            editMenuActivityResultLauncher.launch(intent);



                        }
                    else{
                        //select image size
                        if (p_parent_id==-1){
                            TextView tv = view.findViewById( R.id._id);
                            int current_id = Integer.parseInt(tv.getText().toString());
                            for (int i=0;i<cursor.getCount();++i){
                                cursor.moveToPosition(i);
                                if (current_id==cursor.getInt(cursor.getColumnIndex("_id"))){
                                    DBManager dbManager2 = new DBManager(MainActivity.this);
                                    dbManager2.open();
                                    dbManager2.updateImageSize(i+1);
                                    dbManager2.close();
                                    MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.action_image_size);
                                    menuItem.setTitle("גודל טקסט ("+ (i+1) + ")"
                                    );

                                }
                            }

                        }
                        else{ // regular menu
                            TextView tv = view.findViewById( R.id._id);
                            b.putString("parent_id",  tv.getText().toString());
                            tv = view.findViewById( R.id.menu_desc);
                            b.putString("menu_desc",  tv.getText().toString());
                            b.putBoolean("child_is_files",  false);
                            b.putBoolean("checkBox", checkBox.isChecked());
                            Intent intent;
                            intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(b);
                            editMenuActivityResultLauncher.launch(intent);
                        }

                    }

                }





            }

            @SuppressLint("Range")
            @Override
            public void onLongClicked(int position,View view) {
                if (checkBox.isChecked()) {


                    Bundle b = new Bundle();
                    TextView tv;
                    cursor.moveToPosition(position);
                    @SuppressLint("Range")
                    int is_files = cursor.getInt(cursor.getColumnIndex("is_files"));
                    if (is_files == 1) {
                        tv = view.findViewById(R.id._id);
                        b.putString("file_id", tv.getText().toString());
                        tv = view.findViewById(R.id.menu_desc);
                        b.putString("file_name", tv.getText().toString());
                        tv = view.findViewById(R.id.parent_id);
                        b.putString("parent_id", tv.getText().toString());
                        b.putBoolean("child_is_files", true);
                        Intent intent;
                        @SuppressLint("Range") int page_no = cursor.getInt(cursor.getColumnIndex("page_no"));
                        b.putInt("page_no", page_no);
                        intent = new Intent(MainActivity.this, LoadFile.class);
                        intent.putExtras(b);
                        editMenuActivityResultLauncher.launch(intent);
                    } else {
                        tv = view.findViewById(R.id._id);
                        b.putString("menu_id", tv.getText().toString());
                        tv = view.findViewById(R.id.menu_desc);
                        b.putString("menu_desc", tv.getText().toString());
                        tv = view.findViewById(R.id.parent_id);
                        b.putString("parent_id", tv.getText().toString());
                        b.putInt("child_is_files", cursor.getInt(cursor.getColumnIndex("child_is_files")));
                        Intent intent;
                        intent = new Intent(MainActivity.this, EditMenu.class);
                        intent.putExtras(b);
                        editMenuActivityResultLauncher.launch(intent);
                    }

                }
            }


        });


        // getItemCount() have to return result greater than 0 (zero)
        recyclerView.setAdapter(mAdapter);


    }

    public interface ClickListener {


        void onPositionClicked(int position,View view);

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