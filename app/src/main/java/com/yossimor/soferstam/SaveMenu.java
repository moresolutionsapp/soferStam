package com.yossimor.soferstam;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class SaveMenu extends AppCompatActivity {

    ActivityResultLauncher<Intent> chooseDirectoryFilesResultLauncher;
    String file_name = "save_menu.csv";
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_menu);

        chooseDirectoryFilesResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            //Uri uri = null;
                            uri = data.getData();
                            //read_uri(uri);
                            try {
                                export_menu_to_csv_file();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Button save_menu = findViewById(R.id.save_menu);

        save_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDirectory();
            }
        });


    }

    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,false);

        chooseDirectoryFilesResultLauncher.launch(intent);
    }

    @SuppressLint("Range")
    private void export_menu_to_csv_file() throws IOException {

        Uri treeUri = uri;
        DocumentFile pickedDir = DocumentFile.fromTreeUri(SaveMenu.this, treeUri);
        DocumentFile documentFile = pickedDir.findFile(file_name);
        if (documentFile == null)
            documentFile = pickedDir.createFile("application/excel", file_name);
        else{
            documentFile.delete();
            documentFile = pickedDir.createFile("application/excel", file_name);
        }

        OutputStream out = getContentResolver().openOutputStream(documentFile.getUri());

        String separator = System.getProperty("line.separator");
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch_menu();
        for (int i=0;i< cursor.getCount();++i){
            cursor.moveToPosition(i);
            String inputString;
            byte[] byteArrray;
            inputString =cursor.getString(cursor.getColumnIndex("_id")) +"~";
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            inputString =cursor.getString(cursor.getColumnIndex("parent_id")) + "~";
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            inputString =cursor.getString(cursor.getColumnIndex("menu_desc")) + "~";
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            inputString =cursor.getString(cursor.getColumnIndex("child_is_files")) + "~";
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            inputString =cursor.getString(cursor.getColumnIndex("is_files")) + "~";
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            inputString =cursor.getString(cursor.getColumnIndex("page_no")) ;
            byteArrray = inputString.getBytes();
            out.write(byteArrray);
            byteArrray = separator.getBytes();
            out.write(byteArrray);

        }

        out.flush();
        out.close();

    }


}