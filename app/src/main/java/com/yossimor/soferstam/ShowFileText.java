package com.yossimor.soferstam;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.yossimor.soferstam.databinding.FragmentShowFileTextBinding;

import java.io.File;
import java.io.IOException;


public class ShowFileText extends Fragment {

    private String file_name;

    public static ShowFileText newInstance(String file_name) {
        ShowFileText fragment = new ShowFileText();
        Bundle args = new Bundle();
        args.putString("file_name", file_name);
        fragment.setArguments(args);
        return fragment;
    }


    private View mContentView;
    private View mControlsView;


    private FragmentShowFileTextBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            file_name = getArguments().getString("file_name");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentShowFileTextBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mVisible = true;
        //mControlsView = binding.fullscreenContentControls;
        //mContentView = binding.html;

        File[] dirs = ((AppCompatActivity)getActivity()).getExternalFilesDirs(null);
        String htmlPath = dirs[0] + "/html/" + file_name;
        //Bitmap myBitmap = BitmapFactory.decodeFile(htmlPath);


        File file = new File(htmlPath);





        ImageView myImage = (ImageView) view.findViewById(R.id.image_text);
        myImage.setImageURI(Uri.fromFile(file));
        //myImage.setImageBitmap(myBitmap);


//        WebView webView = view.findViewById(R.id.html);
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setAllowFileAccess(true);
//        File[] dirs = ((AppCompatActivity)getActivity()).getExternalFilesDirs(null);
//        String htmlPath = dirs[0] + "/html/" + file_name;
//        webView.loadUrl (htmlPath);


        hideSystemUI();






    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //hide();

    }

    @Override
    public void onPause() {
        super.onPause();
//        if (getActivity() != null && getActivity().getWindow() != null) {
//            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//            // Clear the systemUiVisibility flag
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
//        }
//        //show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView = null;
        mControlsView = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            hideSystemUI();
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }








    @Nullable
    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        try {

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
//            linearLayout.setVisibility(View.INVISIBLE);
//            root_view.setBackgroundColor(getColor(R.color.black));
        } catch (Exception e) {

        }
    }

}