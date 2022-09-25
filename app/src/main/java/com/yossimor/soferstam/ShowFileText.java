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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.yossimor.soferstam.databinding.FragmentShowFileTextBinding;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class ShowFileText extends Fragment implements PercentageDialog.ISelectedData {

    private String file_name;
    MyScrollView scrollView;
    double zoom_size=1;
    ImageView myImage;
    int initMyImageHeight;
    int initMyImageWidth;
    BottomNavigationView bottomNavigationView;
    private TabLayout tabs;
    private boolean  sysMenuVisible;
    int click_counter =0;
    boolean timerStart;
    int percentage = 100;



    public static ShowFileText newInstance(String file_name,int page) {
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
        DBManager dbManager = new DBManager(getActivity());
        dbManager.open();
        int image_size = dbManager.get_ImageSize();
        if (image_size>1){
            String htmlPathDeep = dirs[0] + "/html/" + image_size + "/" + file_name;
            File file = new File(htmlPathDeep);
            if (file.exists()){
                htmlPath = htmlPathDeep;
            }
        }
        dbManager.close();

        File file = new File(htmlPath);

        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (click_counter>=5){
                    if (((ShowFiles) getActivity()).isLocked){
                        //item.setTitle("נעל");
                        //showSystemUI();
                        ((ShowFiles) getActivity()).showMenu=true;
                        ((ShowFiles) getActivity()).show_menu();
                    }
                }
                click_counter=0;
                timerStart=false;
            }
        };

        myImage = (ImageView) view.findViewById(R.id.image_text);
        myImage.setImageURI(Uri.fromFile(file));
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (!timerStart && ((ShowFiles) getActivity()).isLocked){
                    timerStart=true;
                    timerHandler.postDelayed(timerRunnable,1000);
                }
                click_counter++;

            }
        });









        scrollView = (MyScrollView) view.findViewById(R.id.scrollView);

        scrollView.setScrolling(!((ShowFiles) getActivity()).isLocked); // to disable scrolling



        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        hideSystemUI();

        tabs = (TabLayout)((ShowFiles)getActivity()).findViewById(R.id.tabLayout);





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_locked) {
                    if (((ShowFiles) getActivity()).isLocked){
                        //item.setTitle("נעל");
                        ((ShowFiles) getActivity()).isLocked =!((ShowFiles) getActivity()).isLocked;
                        ((ShowFiles) getActivity()).update_menu_item("נעל");
                        scrollView.setScrolling(true); // to enable scrolling.
                        ((ShowFiles) getActivity()).viewPager.setPagingEnabled(true);

                    }
                    else{
                        ((ShowFiles) getActivity()).isLocked =!((ShowFiles) getActivity()).isLocked;
                        //item.setTitle("שחרר");
                        ((ShowFiles) getActivity()).update_menu_item("שחרר");
                        scrollView.setScrolling(false);
                        //linearLayout_BottomNavigationView.addView(bottomNavigationView);
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        hideSystemUI();
                        ((ShowFiles) getActivity()).hide_menu();
                        ((ShowFiles) getActivity()).showMenu=false;
                        //((ShowFiles) getActivity()).show_menu();
                    }



                }
                if (!((ShowFiles) getActivity()).isLocked){
                    if (item.getItemId() == R.id.action_zoom_in) {
                        if (initMyImageHeight==0){
                            initMyImageHeight=myImage.getHeight();
                            initMyImageWidth=myImage.getWidth();
                        }

                        zoom_size =zoom_size+0.05;

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
                        zoom_size = (zoom_size-0.05);
                        zoom_size = new BigDecimal(zoom_size).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        myImage.getLayoutParams().height = (int) (initMyImageHeight*zoom_size);
                        myImage.getLayoutParams().width = (int) (initMyImageWidth*zoom_size);
                        myImage.requestLayout();
                        DBManager dbManager = new DBManager(getActivity());
                        dbManager.open();
                        dbManager.updateZoomSize(zoom_size);
                        dbManager.close();

                    }

                    if (item.getItemId() == R.id.percentage) {
                        PercentageDialog percentageDialog = new PercentageDialog((zoom_size)*100);
                        percentageDialog.setTargetFragment(ShowFileText.this, 1);
                        percentageDialog.show(getActivity().getSupportFragmentManager(),"percentage");

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










    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showSystemUI() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                ((ShowFiles) getActivity()).showMenu=true;
                                ((ShowFiles) getActivity()).show_menu();
                                // adjustments to your UI, such as showing the action bar or
                                // other navigational controls.
                            } else {
                                // TODO: The system bars are NOT visible. Make any desired
                                // adjustments to your UI, such as hiding the action bar or
                                // other navigational controls.
                            }
                        }
                    });
//            linearLayout.setVisibility(View.INVISIBLE);
//            root_view.setBackgroundColor(getColor(R.color.black));
        } catch (Exception e) {

        }


    }


    @Override
    public void onSelectedData(Bundle bundle) {
        int mPercentage  = bundle.getInt("percentage",0);
        boolean change =  bundle.getBoolean("change");
        if (change){
            percentage=mPercentage;
            zoom_size=(double) percentage/100;
            if (initMyImageHeight==0){
                initMyImageHeight=myImage.getHeight();
                initMyImageWidth=myImage.getWidth();
            }
            myImage.getLayoutParams().height = (int) (initMyImageHeight*zoom_size);
            myImage.getLayoutParams().width = (int) (initMyImageWidth*zoom_size);
            myImage.requestLayout();
            DBManager dbManager = new DBManager(getActivity());
            dbManager.open();
            dbManager.updateZoomSize(zoom_size);
            dbManager.close();
        }
    }
}