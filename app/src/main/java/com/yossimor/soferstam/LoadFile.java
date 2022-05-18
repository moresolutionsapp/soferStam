package com.yossimor.soferstam;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class LoadFile extends AppCompatActivity {

    TextInputEditText et_fileName;
    TextInputLayout et_fileNameLayout;
    TextInputEditText et_pageNo;
    TextInputLayout et_pageNoLayout;

    boolean can_save=true;
    String p_fileName="";
    int p_file_id;
    int p_parent_id;
    int p_page_no;
    ActivityResultLauncher<Intent> browseFilesResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_file);




        browseFilesResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String file_name = data.getStringExtra("file_name");
                            et_fileName.setText(file_name);

                        }
                    }
                });





        et_fileName = findViewById(R.id.fileName);
        et_fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        et_fileNameLayout = findViewById(R.id.fileNameLayout);
        et_pageNo = findViewById(R.id.pageNo);
        et_pageNoLayout = findViewById(R.id.pageNoLayout);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("file_id")!=null){
                p_file_id = Integer.parseInt(b.getString("file_id"));
            }
            else
            {
                p_file_id = 0;
            }
            p_parent_id = Integer.parseInt(b.getString("parent_id"));
            p_fileName = b.getString("file_name");
            et_fileName.setText(p_fileName);
            b.getInt("page_no");
            p_page_no = b.getInt("page_no");
            if (p_page_no!=0){
                et_pageNo.setText(String.valueOf(p_page_no));
            }


            et_fileName.setText(p_fileName);



        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText("טעינת קובץ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable the back button
        getSupportActionBar().setDisplayShowTitleEnabled(false);





        ImageView save_button;
        save_button = toolbar.findViewById(R.id.save);
        save_button.setVisibility(View.VISIBLE);
        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                if (can_save){
                    if (!hasEditTextError()){

                        can_save=false;
                        save_button.setVisibility(View.INVISIBLE);

                        update_menu();
                    }
                }


            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
//                if (can_save)
//                    exit_check_save();
                finish();
                return true;
            }


            default:
                return false;
        }
    }

    private boolean hasEditTextError(){
        boolean to_break= false;
        if (Objects.requireNonNull(et_fileName.getText()).toString().trim().equals("")){
            et_fileName.setText("");
            et_fileNameLayout.setError("נא בחר קובץ");
            to_break=true;
        }




        return to_break;
    }

    private void update_menu (){
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        int page_no=p_page_no;
        if (!et_pageNo.getText().toString().trim().equals("")) {
            page_no= Integer.parseInt(et_pageNo.getText().toString());
        }
        dbManager.files_insert_or_update(p_file_id,
                p_parent_id,
                et_fileName.getText().toString(),
                page_no);
        dbManager.close();
        Toast.makeText(this,"נשמר",Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }

    private void showFileChooser() {

        Intent intent=null;
        intent = new Intent(this, FilesList.class);
        browseFilesResultLauncher.launch(intent);

    }







}