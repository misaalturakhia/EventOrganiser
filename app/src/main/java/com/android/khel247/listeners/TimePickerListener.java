package com.android.khel247.listeners;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TimePicker;

import com.android.khel247.constants.GameConstants;
import com.android.khel247.services.MessageService;

import java.util.Calendar;

import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 18/12/2014.
 */
public class TimePickerListener implements TimePickerDialog.OnTimeSetListener {

    private final Context context;
    private final TimeChosenListener mListener;
    private final boolean isGameToday;

    public TimePickerListener(Context context, Fragment fragment, boolean isToday){
        this.context = context;
        this.mListener = (TimeChosenListener)fragment;
        this.isGameToday = isToday;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(isGameToday)
            if(!isValidTimeChosen(currentHour, hour)){
                showShortToast(context, MessageService.INVALID_TIME);
                return;
            }
        mListener.onTimeChosen(hour, minute);
    }


    /**
     * validates the time. Make sure that the hour and minute entered by the user is after the
     * current hour and minute
     * @param currentHour
     * @param currentMinute
     * @param hour
     * @param minute
     * @return
     */
    private boolean isValidTimeChosen(int currentHour, int hour) {
        int validGameHour = GameConstants.MIN_TIME_FOR_GAME + currentHour;
        if(hour < validGameHour && hour >= currentHour ){
            showShortToast(context, MessageService.GAME_MIN_TIME);
            return false;
        }else if(hour >= validGameHour){
            return true;
        }else{
            return false;
        }
    }



    public interface TimeChosenListener{

        public void onTimeChosen(int hour, int minute);
    }
}
