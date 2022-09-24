package com.yossimor.soferstam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Random;


public class PercentageDialog  extends DialogFragment {
    private ISelectedData mCallback;
    AlertDialog.Builder builder;
    View view;
    int percentage;

    public PercentageDialog(double percentage){

        this.percentage=(int) percentage;
    }

    public interface ISelectedData {
        void onSelectedData(Bundle bundle);


    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

//        final Dialog dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        builder.setCancelable(true);
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_zoom, null);
        builder.setView(view);





        EditText ed_percentage = view.findViewById(R.id.percentage);
        ed_percentage.setText(String.valueOf(percentage));


        Button dialogButton = (Button) view.findViewById(R.id.ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("percentage", Integer.parseInt(ed_percentage.getText().toString()));
                bundle.putBoolean("change",true);

                mCallback.onSelectedData(bundle);

                dismiss();
            }
        });

        return builder.create();

    }






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (PercentageDialog.ISelectedData) getTargetFragment();
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }









}





