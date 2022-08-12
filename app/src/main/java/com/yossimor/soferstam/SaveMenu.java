package com.yossimor.soferstam;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.Intent;
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

    private void export_menu_to_csv_file() throws IOException {

        Uri treeUri = uri;
        DocumentFile pickedDir = DocumentFile.fromTreeUri(SaveMenu.this, treeUri);
        DocumentFile documentFile = pickedDir.findFile("Note");
        if (documentFile == null)
            documentFile = pickedDir.createFile("text/plain", "Note");

        OutputStream out = getContentResolver().openOutputStream(documentFile.getUri());
        //out.write(infos.get(i).getContent().getBytes());
        String inputString = "Hello World!";
        byte[] byteArrray = inputString.getBytes();
        out.write(byteArrray);
        out.flush();
        out.close();
//        File[] dirs = getExternalFilesDirs(null);
//        String csvFilePath = dirs[0] + "/" + file_name;
//        PrintWriter file;
//
//        OutputStream os = null;
//        try {
//            os = new FileOutputStream(csvFilePath,false);
//            os.write(239);
//            os.write(187);
//            os.write(191);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        file = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
//
//        file.append("col1");
//        file.append(',');
//        file.append("col2");
//        file.append(',');
//        //write_body();
//        file.flush();
//        file.close();
//        save_file();
    }


}