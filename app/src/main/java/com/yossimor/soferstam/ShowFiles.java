package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ShowFiles extends AppCompatActivity {

    public TabLayout tabLayout;
    public CustomViewPager viewPager;
    public TabAdapter tabAdapter;
    public boolean isLocked = true;
    int p_parent_id = 0;
    String p_last_tab_num ;
    int last_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            p_parent_id = Integer.parseInt(b.getString("parent_id"));
            p_last_tab_num = b.getString("last_tab");
            if (p_last_tab_num!=null){
                last_tab=Integer.parseInt(p_last_tab_num);
            }
        }


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
//        viewPager.setPagingEnabled(false);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch_menu_files(p_parent_id);
        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
            @SuppressLint("Range") String file_name = cursor.getString( cursor.getColumnIndex("menu_desc"));
            Bundle bundle = new Bundle();
            bundle.putString("file_name", file_name);
            Fragment fragment = new ShowFileText();
            fragment.setArguments(bundle);
            tabAdapter.addFragment(fragment,"");
        }
        dbManager.close();

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setPagingEnabled(false);
        if (p_last_tab_num!=null){
            tabLayout.getTabAt(last_tab).select();
        }
        else{
            dbManager.open();
            dbManager.updateLastFile(p_parent_id,tabLayout.getSelectedTabPosition());
            dbManager.close();
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DBManager dbManager = new DBManager(ShowFiles.this);
                dbManager.open();
                dbManager.updateLastFile(p_parent_id,tabLayout.getSelectedTabPosition());
                dbManager.close();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    public void show_menu(){
        BottomNavigationView bottomNavigationView;
        for (int i=0;i<tabAdapter.getCount();i++){
            bottomNavigationView = (BottomNavigationView)  tabAdapter.getItem(i).getView().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);


        }



    }


    public void hide_menu(){
        BottomNavigationView bottomNavigationView;
        for (int i=0;i<tabAdapter.getCount();i++){
            bottomNavigationView = (BottomNavigationView)  tabAdapter.getItem(i).getView().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.INVISIBLE);
            viewPager.setPagingEnabled(false);
        }
    }


    public void update_menu_item(String title){
        BottomNavigationView bottomNavigationView;
        for (int i=0;i<tabAdapter.getCount();i++){
            bottomNavigationView = (BottomNavigationView)  tabAdapter.getItem(i).getView().findViewById(R.id.bottomNavigationView);
            MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.action_locked);
            menuItem.setTitle(title);
        }
    }




    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.w("LightWriter", "I WORK BRO.");
            return true;
        }
        return false;
    }
}