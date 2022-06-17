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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
                            read_uri(uri);
                        }
                    }
                });

        Button copy_files = findViewById(R.id.copy_files);

        copy_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDirectory();
            }
        });

    }

    private void read_uri(Uri uri) {
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        dbManager.files_list_delete_all();
        int files_counter=0;
        create_dir("html");
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
                    create_dir("html/"+direcotory_name);
                    DocumentFile[] direcoty_files = file_or_directory.listFiles();

                    for (DocumentFile file : direcoty_files) {
                        ++files_counter;
                        copyFile(direcotory_name,file);
                    }

                }
                else{
                    ++files_counter;
                    copyFile ("",file_or_directory);
                    dbManager.files_list_insert(file_or_directory.getName());
                }
            }
        }
        TextView files_counter_tv = findViewById(R.id.files_counter_text);
        files_counter_tv.setText("מספר הקבצים שהועתקו: " + files_counter);
        pb.setVisibility(View.INVISIBLE);
    }

    private void create_dir(String new_dir) {
        File[] dirs = this.getExternalFilesDirs(null);
        File _dir = new File(dirs[0] +"/"+ new_dir);
        if (!_dir.exists())
            _dir.mkdirs();
//        File emp_dir = new File(dirs[0] + "/images/" +new_dir + "/");
//        emp_dir.mkdirs();
    }


    private void copyFile(String directory,DocumentFile inputFile) {
        InputStream in = null;
        OutputStream out = null;
        String error = null;


        try {
            File[] dirs = this.getExternalFilesDirs(null);
            File file = new File(dirs[0] +"/html/" +directory,inputFile.getName());
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