package com.android.khel247.listeners;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import com.android.khel247.services.MessageService;

import java.util.Calendar;

import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 18/12/2014.
 */
public class DatePickerListener implements DatePickerDialog.OnDateSetListener {

    private final Context context;
    private boolean isGameToday;

    private DateChosenListener mListener;

    public DatePickerListener(Context context, Fragment fragment){
        this.context = context;
        mListener = (DateChosenListener)fragment;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH);
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        if(!isValidDateChosen(currentYear, currentMonth, currentDay, year, month, day)){
            showShortToast(context, MessageService.PAST_DATE_CHOSEN);
            return;
        }

        if(year == currentYear && month == currentMonth && day == currentDay){
            isGameToday = true;
        }else{
            isGameToday = false;
        }
        // use this listener interface to send data back to the calling fragment
        mListener.onDateChosen(year, month, day, isGameToday);

    }


    /**
     *
     * @param currentYear
     * @param currentMonth
     * @param currentDay
     * @param year
     * @param month
     * @param day
     * @return
     */
    private boolean isValidDateChosen(int currentYear, int currentMonth, int currentDay, int year, int month, int day) {
        int[] datesArray = {year, currentYear, month, currentMonth, day, currentDay};
        return isValidValues(datesArray, 0, datesArray.length);
    }

    /**
     *
     * @param array
     * @param index
     * @param max
     * @return
     */
    private boolean isValidValues(int[] array, int index, int max){
        if(index >= max){
            return true;
        }
        int a = array[index];
        int b = array[index + 1];
        if(a < b){
            return false;
        }else if(a > b){
            return true;
        }else{
            index = index + 2;
            return isValidValues(array, index, max);
        }
    }

    public interface DateChosenListener{

        public void onDateChosen(int year, int month, int day, boolean isToday);
    }

}
