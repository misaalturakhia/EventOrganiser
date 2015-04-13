package com.android.khel247.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** http://stackoverflow.com/questions/20214547/show-timepicker-with-minutes-intervals-in-android
 * Created by Misaal on 18/12/2014.
 */
public class CustomTimePickerDialog extends TimePickerDialog {

    public final static int TIME_PICKER_INTERVAL = 15;
    private TimePicker timePicker;
    private final OnTimeSetListener callback;


    /**
     * Constructor which displays time picker in the 24hour format
     * @param context
     * @param callBack
     * @param hourOfDay
     * @param minute
     */
    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
                                  int hourOfDay, int minute){
        this(context, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL, true);

    }


    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL,
                is24HourView);
        this.callback = callBack;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (callback != null && timePicker != null) {
            timePicker.clearFocus();
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion > Build.VERSION_CODES.KITKAT){
                callback.onTimeSet(timePicker, timePicker.getCurrentHour(),
                        getRoundedMinute(timePicker.getCurrentMinute())); // timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL
            } else{
                callback.onTimeSet(timePicker, timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
            }
        }
    }

    public static int getRoundedMinute(int minute) {
        int quotient = minute / TIME_PICKER_INTERVAL;
        int remainder = minute % TIME_PICKER_INTERVAL;
        switch (quotient){
            case 0:
                if(remainder != 0)
                    return 15;
                else return 0;
            case 1:
                if(remainder != 0)
                    return 30;
                else return 15;
            case 2:
                if(remainder != 0)
                    return 45;
                else return 30;
            case 3:
                if(remainder != 0)
                    return 00;
                else return 45;
            default:
                return 00;
        }

    }

    @Override
    protected void onStop() {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = (TimePicker) findViewById(timePickerField
                    .getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));
            if(mMinuteSpinner != null){
                mMinuteSpinner.setMinValue(0);
                mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
                List<String> displayedValues = new ArrayList<String>();
                for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                    displayedValues.add(String.format("%02d", i));
                }
                mMinuteSpinner.setDisplayedValues(displayedValues
                        .toArray(new String[0]));
                mMinuteSpinner.setWrapSelectorWheel(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
