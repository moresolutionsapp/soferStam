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
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyFiles extends AppCompatActivity {

    ActivityResultLauncher<Intent> chooseDirectoryFilesResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_files);


        chooseDirectoryFilesResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = null;
                            uri = data.getData();
                            uri.toString();
                            //Uri.parse("http://stackoverflow.com");
                            DBManager dbManager = new DBManager(CopyFiles.this);
                            dbManager.open();
                            dbManager.control_insert_is_direcotory_exist(uri.toString());
                            dbManager.close();
                            read_uri(uri);
                        }
                    }
                });

        Button copy_files = findViewById(R.id.copy_files);

        copy_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBManager dbManager = new DBManager(CopyFiles.this);
                dbManager.open();
                String uri_path = dbManager.is_directory_choose();
                if (!uri_path.trim().equals("")){
                    openDirectory();
                }
                read_uri(Uri.parse(uri_path));


            }
        });

    }

    private void read_uri(Uri uri) {
        ProgressBar pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        Uri dirUri = uri;
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree
                (dirUri, DocumentsContract.getTreeDocumentId(dirUri));
        DocumentFile tree = DocumentFile.fromTreeUri(this, childrenUri);
        boolean errorFound = false;
        //DocumentFile[] var25 = tree.listFiles();
        //DocumentFile[] var7 = var25;
        DocumentFile[] tree_files =  tree.listFiles();
        if (tree_files.length != 0) {
            DocumentFile[] files = tree.listFiles();

            for (int i= 0; i < tree_files.length; ++i) {
                DocumentFile file_or_directory = files[i];
                if (file_or_directory.isDirectory() && !errorFound) {
                    String direcotory_name = file_or_directory.getName();
                    create_dir(direcotory_name);
                    DocumentFile[] direcoty_files = file_or_directory.listFiles();

                    for (DocumentFile file : direcoty_files) {
                        copyFile(file);
                    }

                }
                else{
                    copyFile (file_or_directory);
                }
            }
        }
        pb.setVisibility(View.INVISIBLE);
    }

    private void create_dir(String new_dir) {
        File[] dirs = this.getExternalFilesDirs(null);
        File _dir = new File(dirs[0] + new_dir);
        if (!_dir.exists())
            _dir.mkdirs();
//        File emp_dir = new File(dirs[0] + "/images/" +new_dir + "/");
//        emp_dir.mkdirs();
    }


    private void copyFile(DocumentFile inputFile) {
        InputStream in = null;
        OutputStream out = null;
        String error = null;


        try {
            File[] dirs = this.getExternalFilesDirs(null);
            File file = new File(dirs[0] ,inputFile.getName());
            out = new FileOutputStream(file);
            in = new FileInputStream(
                    getContentResolver().openFileDescriptor(inputFile.getUri(), "r").getFileDescriptor());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file (You have now copied the file)
            out.flush();
            out.close();

        } catch (Exception fnfe1) {
            error = fnfe1.getMessage();
        }
    }


    //public void openDirectory(Uri uriToLoad) {
    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,false);

        chooseDirectoryFilesResultLauncher.launch(intent);
    }



}