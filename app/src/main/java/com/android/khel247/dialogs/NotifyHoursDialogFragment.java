package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Misaal on 16/12/2014.
 */
public class NotifyHoursDialogFragment extends DialogFragment {

    private static final String ARG_DATE_TIME_ARRAY = "DateTime array";
    private static final String ARG_PREVIOUS_SELECTIONS = "Previously selected items";
    private static final String DIALOG_TITLE = "Notify before game";
    private static final long SECONDS_IN_HOUR = 3600;
    private static final long MILLISECONDS_IN_SECOND = 1000;

    private boolean useFullArray;
    private long hoursTillGame;
    private NotifyHoursDialogListener mListener;


    ArrayList<String> itemsSelected;
    private ArrayList<String> previousItems;

    public static NotifyHoursDialogFragment newInstance(int[] dateTimeArray, ArrayList<String> chosenItems){
        NotifyHoursDialogFragment fragment = new NotifyHoursDialogFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_DATE_TIME_ARRAY, dateTimeArray);
        args.putStringArrayList(ARG_PREVIOUS_SELECTIONS, chosenItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        int[] gameDateArray = new int[5];
        if(args != null){
            gameDateArray = args.getIntArray(ARG_DATE_TIME_ARRAY);
            previousItems = args.getStringArrayList(ARG_PREVIOUS_SELECTIONS);
        }

        useFullArray = analyzeDateArray(gameDateArray);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (NotifyHoursDialogListener)getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException("Target Fragment must implement dialogListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Calculates if the full array of notify hours should be displayed to the user as a choice
     * @param gameDateArray
     * @param currentDateArray
     * @return
     */
    private boolean analyzeDateArray(int[] gameDateArray) {
        long currentMillis = System.currentTimeMillis();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, gameDateArray[0]);
        cal.set(Calendar.MONTH, gameDateArray[1]);
        cal.set(Calendar.DAY_OF_MONTH, gameDateArray[2]);
        cal.set(Calendar.HOUR_OF_DAY, gameDateArray[3]);
        cal.set(Calendar.MINUTE, gameDateArray[4]);
        long gameMillis = cal.getTimeInMillis();

        long diffMillis = gameMillis - currentMillis;
        if(diffMillis < 26 * SECONDS_IN_HOUR * MILLISECONDS_IN_SECOND){
            hoursTillGame = diffMillis / (MILLISECONDS_IN_SECOND * SECONDS_IN_HOUR);
            return false;
        }else return true;

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] items = setupItemArray();
        boolean[] checked = setupCheckedArray(items);
        itemsSelected = new ArrayList<>();
        if(previousItems != null && previousItems.size() > 0)
            itemsSelected.addAll(previousItems);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(DIALOG_TITLE)
                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                        String item = items[index];
                        if(isChecked){ // if the item is checked
                            if(!itemsSelected.contains(item)) // if it is not in the list, add
                                itemsSelected.add(item);
                        }else{ // if the item is not checked
                            if(itemsSelected.contains(item)) // if it is present in the list, remove
                                itemsSelected.remove(item);
                        }
                    }
                })
                .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onNotifyHoursChosen(itemsSelected);
                    }
                });

        return builder.create();
    }


    /**
     * Sets up the checked array from items the user may have previously selected. Returns null
     * if previousItems == null or has no elements.
     * @param items : the item array to display in the dialog
     * @return : a boolean array of size = items.length which holds whether each item is checked or not
     */
    private boolean[] setupCheckedArray(String[] items) {
        if(previousItems == null || previousItems.size() < 1){
            return null;
        }
        boolean[] checked = new boolean[items.length];
        // parse through each item in the itemArray
        for(int i = 0; i < items.length; i++){
            String item = items[i];
            // check if the current item is present in the previousItems list
            if(previousItems.contains(item))
                checked[i] = true; // if yes, set the boolean value at that index to true
            else
                checked[i] = false; // else false
        }

        return checked;
    }

    /**
     * setup the item array to be displayed in the dialog
     * @return
     */
    private String[] setupItemArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.game_notify_before_time_long);
        if(useFullArray){
            return array;
        }else{
            array = modifyItemArray(array);
        }
        return array;
    }


    /**
     * modifies item array according to the number of hours left till the game
     * @param array
     * @return
     */
    private String[] modifyItemArray(String[] array) {
        List<String> items = new ArrayList<>(Arrays.asList(array));

        if(hoursTillGame < 26){
            items.remove(items.size() - 1);
        }
        if(hoursTillGame < 18){
            items.remove(items.size() - 1);
        }
        if(hoursTillGame < 12){
            items.remove(items.size() - 1);
        }
        if(hoursTillGame < 9)
            items.remove(items.size() - 1);
        if(hoursTillGame < 7)
            items.remove(items.size() - 1);
        for(int i = 4; i >= 1; i--){
            if(hoursTillGame < i + 1)
                items.remove(items.size() - 1);
        }

        return items.toArray(new String[items.size()]);
    }



    /**
     * calculates the hours left using the inputs provided
     * @param dayDiff
     * @param gameHour
     * @param currentHour
     * @return
     */
    private int calculateHoursLeft(int dayDiff, int gameHour, int currentHour) {
        return (24*dayDiff) - currentHour + gameHour;
    }

    public interface NotifyHoursDialogListener{

        public void onNotifyHoursChosen(ArrayList<String> items);
    }

}
