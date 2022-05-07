package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_file);

        et_fileName = findViewById(R.id.fileName);
        et_fileNameLayout = findViewById(R.id.fileNameLayout);
        et_pageNo = findViewById(R.id.pageNo);
        et_pageNoLayout = findViewById(R.id.pageNoLayout);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            p_file_id = Integer.parseInt(b.getString("file_id"));
            p_fileName = b.getString("file_name");
            et_fileName.setText(p_fileName);
            p_page_no = Integer.parseInt(b.getString("page_no"));
            if (!b.getString("page_no").trim().equals("")) {
                et_pageNo.setText(p_page_no);
                et_fileName.setText(p_fileName);
            }
            if (b.getString("parent_id").trim().equals("")){
                p_parent_id=0;
            }
            else{
                p_parent_id = Integer.parseInt(b.getString("parent_id"));
            }


        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText("עריכת תפריט");
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

}