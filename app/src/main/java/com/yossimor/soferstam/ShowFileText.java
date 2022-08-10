package com.yossimor.soferstam;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yossimor.soferstam.databinding.FragmentShowFileTextBinding;

import java.io.File;



public class ShowFileText extends Fragment {

    private String file_name;
    MyScrollView scrollView;
    boolean isScroliing = false;
    double zoom_size=1;
    int initMyImageHeight;
    int initMyImageWidth;

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





        File[] dirs = ((AppCompatActivity)getActivity()).getExternalFilesDirs(null);
        String htmlPath = dirs[0] + "/html/" + file_name;

        File file = new File(htmlPath);

        ImageView myImage = (ImageView) view.findViewById(R.id.image_text);
        myImage.setImageURI(Uri.fromFile(file));




        hideSystemUI();

        scrollView = (MyScrollView) view.findViewById(R.id.scrollView);

        scrollView.setScrolling(isScroliing); // to disable scrolling





        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_scroll) {
                    scrollView.setScrolling(!isScroliing); // to enable scrolling.
                    isScroliing=!isScroliing;
                    if (!isScroliing){
                        item.setTitle("גלול");
                    }
                    else{
                        item.setTitle("אל תגלול");
                    }
                }
                if (item.getItemId() == R.id.action_zoom_in) {
                    if (initMyImageHeight==0){
                        initMyImageHeight=myImage.getHeight();
                        initMyImageWidth=myImage.getWidth();
                    }

                    zoom_size = zoom_size *1.1;

//                    myImage.getLayoutParams().height = (int) (myImage.getHeight()*1.1);
//                    myImage.getLayoutParams().width = (int) (myImage.getWidth()*1.1);
                    myImage.getLayoutParams().height = (int) (initMyImageHeight*zoom_size);
                    myImage.getLayoutParams().width = (int) (initMyImageWidth*zoom_size);
                    myImage.requestLayout();
                    DBManager dbManager = new DBManager(getActivity());
                    dbManager.open();
                    dbManager.updateZoomSize(zoom_size);
                    dbManager.close();





                }

                if (item.getItemId() == R.id.action_zoom_out) {
                    if (initMyImageHeight==0){
                        initMyImageHeight=myImage.getHeight();
                        initMyImageWidth=myImage.getWidth();
                    }
                    zoom_size = zoom_size *0.9;
//                    myImage.getLayoutParams().height = (int) (myImage.getHeight()*0.9);
//                    myImage.getLayoutParams().width = (int) (myImage.getWidth()*0.9);
                    myImage.getLayoutParams().height = (int) (initMyImageHeight*zoom_size);
                    myImage.getLayoutParams().width = (int) (initMyImageWidth*zoom_size);
                    myImage.requestLayout();
                    DBManager dbManager = new DBManager(getActivity());
                    dbManager.open();
                    dbManager.updateZoomSize(zoom_size);
                    dbManager.close();

                }

                if (item.getItemId() == R.id.reset_zoom) {
                    zoom_size=1;
                    if (initMyImageHeight==0){
                        initMyImageHeight=myImage.getHeight();
                        initMyImageWidth=myImage.getWidth();
                    }
                    myImage.getLayoutParams().height = (int) (initMyImageHeight);
                    myImage.getLayoutParams().width = (int) (initMyImageWidth);
                    myImage.requestLayout();
                    DBManager dbManager = new DBManager(getActivity());
                    dbManager.open();
                    dbManager.updateZoomSize(zoom_size);
                    dbManager.close();
                }

                if (item.getItemId() == R.id.last_zoom) {
                    if (initMyImageHeight==0){
                        initMyImageHeight=myImage.getHeight();
                        initMyImageWidth=myImage.getWidth();
                    }
                    DBManager dbManager = new DBManager(getActivity());
                    dbManager.open();
                    zoom_size=dbManager.get_last_zoom_size();
                    dbManager.close();
                    myImage.getLayoutParams().height = (int) (initMyImageHeight*zoom_size);
                    myImage.getLayoutParams().width = (int) (initMyImageWidth*zoom_size);
                    myImage.requestLayout();

                }



                return true;
            }
        });










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