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

/**
 * Created by Misaal on 30/12/2014.
 */
public class NumberPickerDialogFragment extends DialogFragment {
    private static final String ARG_IS_MIN_PICKER = "isMinPicker";
    private static final String ARG_MIN_VALUE = "MinValue";
    private static final String ARG_MAX_VALUE = "MaxValue";
    private static final String MIN_DIALOG_TITLE = "Pick Minimum Number of Players";
    private static final String MAX_DIALOG_TITLE = "Pick Maximum Number of Players";

    private int mMinValue;
    private boolean isMin;
    private int mMaxValue;
    private NumberPicker mPicker;
    private NumberChosen mNumberListener;

    public static NumberPickerDialogFragment newInstance(boolean isMin, int min, int max){
        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_MIN_PICKER, isMin);
        args.putInt(ARG_MIN_VALUE, min);
        args.putInt(ARG_MAX_VALUE, max);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args =  getArguments();
        if(args != null){
            isMin = args.getBoolean(ARG_IS_MIN_PICKER);
            mMinValue = args.getInt(ARG_MIN_VALUE);
            mMaxValue = args.getInt(ARG_MAX_VALUE);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNumberListener = (NumberChosen)getTargetFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNumberListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getNumberPickerView();
        String title = getTitleText();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title).setView(contentView)
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int value = mPicker.getValue();
                        mNumberListener.onNumberChosen(isMin, value);
                    }
                }).setNegativeButton(Constants.DIALOG_CANCEL_BTN_TEXT, null);
        return builder.create();
    }

    private String getTitleText() {
        if(isMin){
            return MIN_DIALOG_TITLE;
        }else{
            return MAX_DIALOG_TITLE;
        }
    }


    /**
     * Creates the view of the number picker
     * @return
     */
    private View getNumberPickerView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null);

        mPicker = (NumberPicker)view.findViewById(R.id.number_picker);
        mPicker.setMinValue(mMinValue);
        mPicker.setMaxValue(mMaxValue);
        if(isMin){
            mPicker.setValue(mMinValue);
        }else{
            mPicker.setValue(mMaxValue);
        }
        return view;
    }

    public interface NumberChosen{

        public void onNumberChosen(boolean isMin, int value);
    }

}
