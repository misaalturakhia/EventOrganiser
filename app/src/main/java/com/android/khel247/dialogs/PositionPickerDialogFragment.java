package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

/**
 * Created by Misaal on 15/12/2014.
 */
public class PositionPickerDialogFragment extends DialogFragment {

    private static final String ARG_PREVIOUS_POSITION = "Previous Position";
    private static final String DIALOG_TITLE = "Choose Position";
    private int defaultIndex = 0;
    private String mPreviousPosition;
    private PositionDialogListener mListener;

    public static PositionPickerDialogFragment newInstance(String previousPosition){
        PositionPickerDialogFragment fragment = new PositionPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREVIOUS_POSITION, previousPosition);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (PositionDialogListener)getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException("Target Fragment must implement dialogListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            mPreviousPosition = args.getString(ARG_PREVIOUS_POSITION);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] positionArray = getActivity().getResources().getStringArray(R.array.member_position_options);
        int index = UtilityMethods.getIndexOfString(positionArray, mPreviousPosition);
        if(index != -1)
            defaultIndex = index;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(DIALOG_TITLE)
                .setSingleChoiceItems(positionArray, defaultIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        mListener.onPositionChosen(positionArray[index]);
                        dismiss();
                    }
                })
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onPositionChosen(positionArray[defaultIndex]);
                    }
                });

        return builder.create();
    }

    public interface PositionDialogListener{

        public void onPositionChosen(String positionValue);
    }
}
