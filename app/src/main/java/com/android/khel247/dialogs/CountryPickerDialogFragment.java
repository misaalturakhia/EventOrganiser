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

import static com.android.khel247.utilities.UtilityMethods.getIndexOfString;

/**
 * Created by Misaal on 15/12/2014.
 */
public class CountryPickerDialogFragment extends DialogFragment {

    private static final String ARG_PREVIOUS_COUNTRY_SELECTION = "Previous Country";
    private static final String DIALOG_TITLE = "Choose Country";
    private int defaultIndex = 0;
    private String mPreviousCountry;
    private CountryDialogListener mListener;

    public static CountryPickerDialogFragment newInstance(String previousValue){
        CountryPickerDialogFragment fragment = new CountryPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREVIOUS_COUNTRY_SELECTION, previousValue);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null)
            mPreviousCountry = args.getString(ARG_PREVIOUS_COUNTRY_SELECTION);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (CountryDialogListener)getTargetFragment();
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
        final String[] countryArray = getActivity().getResources().getStringArray(R.array.country_list);
        int index = getIndexOfString(countryArray, mPreviousCountry);
        if(index != -1)
            defaultIndex = index;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(DIALOG_TITLE)
                .setSingleChoiceItems(countryArray, defaultIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        mListener.onCountryChosen(countryArray[index]);
                        dismiss();
                    }
                })
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onCountryChosen(countryArray[defaultIndex]);
                    }
                });


        return builder.create();
    }

    public interface CountryDialogListener{

        public void onCountryChosen(String countryValue);
    }
}
