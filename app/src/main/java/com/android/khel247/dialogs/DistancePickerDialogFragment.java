package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

/**
 * Created by Misaal on 06/01/2015.
 */
public class DistancePickerDialogFragment extends DialogFragment {

    private static final String PREVIOUS_DISTANCE = "Previous Distance";
    private static final int DEFAULT_DISTANCE = 5; // in kms
    private static final String DIALOG_TITLE = "Distance (kms)";
    private static final int DEFAULT_MIN_DISTANCE = 1;
    private static final int DEFAULT_MAX_DISTANCE = 20;

    private int mPreviousDistance;
    private DistanceChosen mListener;
    private NumberPicker mPicker;

    public static DistancePickerDialogFragment newInstance(int previousValue){
        DistancePickerDialogFragment fragment = new DistancePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(PREVIOUS_DISTANCE, previousValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            mPreviousDistance = args.getInt(PREVIOUS_DISTANCE, DEFAULT_DISTANCE);
        }

        mListener = (DistanceChosen)getActivity();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DistanceChosen)getTargetFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getNumberPickerView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(DIALOG_TITLE).setView(contentView)
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int value = mPicker.getValue();
                        mListener.onDistanceChosen(value);
                    }
                }).setNegativeButton(Constants.DIALOG_CANCEL_BTN_TEXT, null);
        return builder.create();
    }

    private View getNumberPickerView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null);

        mPicker = (NumberPicker)view.findViewById(R.id.number_picker);
        mPicker.setMinValue(DEFAULT_MIN_DISTANCE);
        mPicker.setMaxValue(DEFAULT_MAX_DISTANCE);
        mPicker.setValue(mPreviousDistance);

        return view;
    }


    public interface DistanceChosen{

        public void onDistanceChosen(int chosenValue);
    }
}
