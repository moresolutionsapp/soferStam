package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class EditMenu extends AppCompatActivity {

    TextInputEditText et_menuDesc;
    TextInputLayout et_menuDescLayout;
    CheckBox cb_child_is_files;

    boolean can_save=true;
    String p_menuDesc="";
    int p_menu_id;
    int p_parent_id;
    int p_child_is_files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);



        et_menuDesc = findViewById(R.id.menuDesc);
        et_menuDescLayout = findViewById(R.id.menuDescLayout);

        cb_child_is_files = findViewById(R.id.child_is_files);



        Bundle b = getIntent().getExtras();
        if (b != null) {
            p_menu_id = Integer.parseInt(b.getString("menu_id"));
            p_menuDesc = b.getString("menu_desc");
            p_child_is_files = b.getInt("child_is_files");
            cb_child_is_files.setChecked(p_child_is_files==1);
            et_menuDesc.setText(p_menuDesc);
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
        if (Objects.requireNonNull(et_menuDesc.getText()).toString().trim().equals("")){
            et_menuDesc.setText("");
            et_menuDesc.setError("נא הכנס שם תפריט");
            to_break=true;
        }




        return to_break;
    }

    private void update_menu (){
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        int child_is_files=0;
        if (cb_child_is_files.isChecked()){
            child_is_files=1;
        }
        dbManager.menu_insert_or_update(p_menu_id,
                p_parent_id,
                et_menuDesc.getText().toString(),
                child_is_files);
        dbManager.close();
        Toast.makeText(this,"נשמר",Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
}