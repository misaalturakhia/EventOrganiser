package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

import java.util.List;

/**
 * Created by Misaal on 14/12/2014.
 */
public class FormatPickerDialogFragment extends DialogFragment {

    private FormatDialogListener mListener = null;

    private static final String ARG_FORMAT_CHOICE = "Chosen Format";
    private static final String FORMAT_DIALOG_TITLE = "Choose Format";
    private static final String ARG_IS_FIND_GAME_PICKER = "Find Game Picker";

    private boolean isFindGamePicker = false;

    private int defaultChoiceIndex = 0;
    private String previousChoice;

    public static FormatPickerDialogFragment newInstance(String formatChoice, boolean isFindGamePicker){
        FormatPickerDialogFragment fragment = new FormatPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FORMAT_CHOICE, formatChoice);
        args.putBoolean(ARG_IS_FIND_GAME_PICKER, isFindGamePicker);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (FormatDialogListener)getTargetFragment();
            if(mListener == null){
                mListener = (FormatDialogListener)getActivity();
            }
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
            previousChoice = args.getString(ARG_FORMAT_CHOICE);
            isFindGamePicker = args.getBoolean(ARG_IS_FIND_GAME_PICKER, false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] formatArray = getFormatArray();

        final int defaultIndex = UtilityMethods.getIndexOfString(formatArray, previousChoice);
        if(defaultIndex != -1)
            defaultChoiceIndex = defaultIndex;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(FORMAT_DIALOG_TITLE)
                .setSingleChoiceItems(formatArray, defaultChoiceIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index) {
                                mListener.onFormatChosen(formatArray[index]);
                                dismiss();
                            }
                        })
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onFormatChosen(formatArray[defaultChoiceIndex]);
                    }
                });

        return builder.create();
    }


    /** Gets an array of format values the user can choose from. The method depends on a flag (isFindGamePicker)
     * that adds an "All" option if true
     *
     * @return
     */
    private String[] getFormatArray() {
        String[] formatArray = getActivity().getResources().getStringArray(R.array.game_format);
        if(isFindGamePicker){
            List<String> formatList = UtilityMethods.convertToList(formatArray);
            formatList.add(0, "All");
            return formatList.toArray(new String[formatList.size()]);
        }else{
            return formatArray;
        }
    }


    public interface FormatDialogListener{

        public void onFormatChosen(String format);
    }
}
