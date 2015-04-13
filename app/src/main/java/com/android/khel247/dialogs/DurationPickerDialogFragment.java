package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

/**
 * Created by Misaal on 14/12/2014.
 */
public class DurationPickerDialogFragment extends DialogFragment {
    private static final String ARG_PREVIOUS_SELECTION = "Previous Value";
    private static final String DURATION_DIALOG_TITLE = "Choose Duration";
    private int defaultOptionIndex;

    private String mPreviousDuration;
    private DurationDialogListener mListener;

    public static DurationPickerDialogFragment newInstance(String previousDuration){
        DurationPickerDialogFragment fragment = new DurationPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREVIOUS_SELECTION, previousDuration);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            mPreviousDuration = args.getString(ARG_PREVIOUS_SELECTION);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (DurationDialogListener)getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException("Target Fragment must implement dialogListener!");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] durationArray = getActivity().getResources().getStringArray(R.array.game_duration);
        int index = UtilityMethods.getIndexOfString(durationArray, mPreviousDuration);
        if(index != -1)
            defaultOptionIndex = index;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(DURATION_DIALOG_TITLE)
                .setSingleChoiceItems(durationArray, defaultOptionIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDurationChosen(durationArray[i]);
                        dismiss();
                    }
                })
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDurationChosen(durationArray[defaultOptionIndex]);
                    }
                });

        return builder.create();
    }

    public interface DurationDialogListener{
        public void onDurationChosen(String durationValue);
    }
}
