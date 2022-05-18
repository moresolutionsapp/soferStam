package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class ShowFiles extends AppCompatActivity {

    public TabLayout tabLayout;
    public CustomViewPager viewPager;
    public TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files);

        int p_parent_id = 0;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            p_parent_id = Integer.parseInt(b.getString("parent_id"));

        }


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
//        viewPager.setPagingEnabled(false);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch_menu(p_parent_id,"");
        for (int i=0;i<cursor.getCount();i++){
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

    }
}