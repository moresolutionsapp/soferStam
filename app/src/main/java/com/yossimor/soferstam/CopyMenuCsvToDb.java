package com.yossimor.soferstam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.List;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class CopyMenuCsvToDb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_copy_menu_csv_to_db);
        DBManager dbManager = new DBManager (this);
        dbManager.open();
        Cursor cursor = dbManager.fetch_menu();
        if (cursor.getCount()==0){
            try {
                ArrayList<String[]> menu_scv_list = read();

                for (int i=0;i< menu_scv_list.size();++i){
                    final String [] s = menu_scv_list.get(i);
                    dbManager.menu_insert(Integer.parseInt(s[0]),
                            Integer.parseInt(s[1]),
                            s[2],
                            Integer.parseInt(s[3]),
                            Integer.parseInt(s[4]),
                            Integer.parseInt(s[5]));
                }
                dbManager.close();
                TextView tv = findViewById(R.id.tv);
                tv.setText("התפריט נטען צא מהאפליקציה וכנס");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            dbManager.close();
            finish();
        }
    }


    public ArrayList<String[]> read() throws FileNotFoundException {
        File[] dirs = getExternalFilesDirs(null);
        String csvFilePath = dirs[0] + "/html/save_menu.csv" ;
        File file = new File(csvFilePath);
        if (!file.exists()){
            TextView tv = findViewById(R.id.tv);
            tv.setText("קובץ תפריטים לא קיים , בצע העתק קבצים");
        }
        InputStream inputStream = new FileInputStream(file);
        ArrayList<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("~");
                //resultList.add(Arrays.toString(row));
                resultList.add(row);
                Log.d("VariableTag", row[0].toString());
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }
}