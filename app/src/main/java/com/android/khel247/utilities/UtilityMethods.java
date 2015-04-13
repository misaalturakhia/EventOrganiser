package com.android.khel247.utilities;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.khel247.model.GameData;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Misaal on 26/11/2014.
 */
public class UtilityMethods {

    /**
     * Checks if the input string is empty or null
     * @param str
     * @return
     */
    public static boolean isEmptyOrNull(String str){
        if(str == null || str.isEmpty())
            return true;
        else return false;
    }

    /**
     * Formats the name input. It makes the first letter upper case and the rest lower case
     * @param string
     * @return
     */
    public static String formatTextInput(String string){
        if(string != null){
            string = string.trim().toLowerCase();
            string = string.substring(0,1).toUpperCase() + string.substring(1);
        }
        return string;
    }


    /**
     * Detects if the input string is an email address
     * @param str
     * @return
     */
    public static boolean isEmailAddress(String str){
        if(!str.contains("@") || !str.contains(".")){
            return false;
        }
        if(Validator.isEmailValid(str)){
            return true;
        }else
            return false;
    }


    public static void showShortToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /** Creates text to display date from the input date details
     *
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static String createDateText(int day, int month, int year) {
        StringBuilder builder = new StringBuilder();
        if(day < 10)
            builder.append("0");
        builder.append(day);
        builder.append("/");
        if(month < 10){
            builder.append("0");
        }
        builder.append(month);
        builder.append("/");
        builder.append(year);
        return builder.toString();
    }

    /** Creates text to display time from the input time details
     *
     * @param minute
     * @param hour
     * @return
     */
    public static String createTimeText(int hour, int minute){
        StringBuilder builder = new StringBuilder();
        if(hour < 10)
            builder.append("0");

        builder.append(hour);
        builder.append(":");

        if(minute < 10){
            builder.append("0");
        }

        builder.append(minute);
        return builder.toString();
    }


    /**
     * Checks if the input string represents an integer
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') { // check if the ascii values are between the range for numbers
                return false;
            }
        }
        return true;
    }


    /**
     * Checks the input string array for the occurrence of the input string parameter.
     * This method doesn't use an search algorithms, just runs a for loop through the array
     *
     * @param array : array in which the sting has to be found
     * @param str : string whose index is to be found
     * @return : index of str, -1 if not found or error (input parameters are invalid (empty or null))
     */
    public static int getIndexOfString(String[] array, String str) {
        if(str == null || array == null || array.length < 1){
            return -1;
        }
        str = str.toLowerCase();
        for(int i = 0; i < array.length; i++){
            String item = array[i].toLowerCase();
            if(item.equals(str))
                return i;
        }
        return -1;
    }


    /**
     * Takes in an arra
     * @param dateTimeArray
     * @return
     */
    public static Date getDateFromArray(int[] dateTimeArray){
        checkDateTimeArray(dateTimeArray);
        Calendar calendar = Calendar.getInstance();
        int index = 0;
        calendar.set(dateTimeArray[index++], dateTimeArray[index++], dateTimeArray[index++],
                dateTimeArray[index++], dateTimeArray[index++]);
        return calendar.getTime();
    }


    /**
     * Checks if the input datetime array has only positive values
     * @param dateTimeArray
     */
    private static void checkDateTimeArray(int[] dateTimeArray) throws InvalidParameterException{
        if(dateTimeArray.length != 5){
            throw new InvalidParameterException("The input dateTime array must have 5 values only :- " +
                    "minute, hour, day, month, year");
        }

        for(int i : dateTimeArray){
            if (i < 0){
                throw new InvalidParameterException("The input array has negative values which is not" +
                        "acceptable.");
            }
        }
    }


    public static SpannableString getUnderLinedText(String text){
        SpannableString string = new SpannableString(text);
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        return string;
    }


    public static void dismissDialog(Dialog d){
        if(d != null && d.isShowing())
            d.dismiss();
    }


    public static List<Integer> convertToList(int[] array){
        if(array == null && array.length < 1){
            return null;
        }
        List<Integer> list = new ArrayList<>();
        for(int i: array){
            list.add(i);
        }
        return list;
    }


    public static List<String> convertToList(String[] array){
        if(array == null){
            return null;
        }
        List<String> list = new ArrayList<>();
        for(String str : array){
            list.add(str);
        }
        return list;
    }




    /**
     * sets the cursor to the end of the text in an edittext
     * @param editText
     */
    public static void setCursorToEndOfText(EditText editText){
        if(editText == null){
            return;
        }else if(editText.getText().toString().isEmpty()){
            return;
        }
        int position = editText.length();
        Editable text = editText.getText();
        Selection.setSelection(text, position);
    }

    /** Fetches the year value from the date by converting the date to string and then using substring
     *
     * @param date : input Date object
     * @return : -1 if date is null, year value otherwise
     */
    public static int getYearFromDate(Date date) {
        if(date == null){
            return -1; // default error value
        }
        String dateStr = getDateStringFromDate(date);
        int lastSlashIndex = dateStr.lastIndexOf("/");
        String yearStr = dateStr.substring(lastSlashIndex + 1, dateStr.length());
        return Integer.parseInt(yearStr);
    }


    /**Fetches the month value from the date by converting the date to string and then using substring
     *
     * @param date
     * @return
     */
    public static int getMonthFromDate(Date date){
        if(date == null){
            return -1;
        }
        String dateStr = getDateStringFromDate(date);
        int firstSlashIndex = dateStr.indexOf("/");
        int lastSlashIndex = dateStr.lastIndexOf("/");
        String monthStr = dateStr.substring(firstSlashIndex + 1, lastSlashIndex);
        return Integer.parseInt(monthStr);
    }


    /**Fetches the day value from the date by converting the date to string and then using substring
     *
     * @param date
     * @return
     */
    public static int getDayFromDate(Date date){
        if(date == null)
            return -1;
        String dateStr = getDateStringFromDate(date);
        int slashIndex = dateStr.indexOf("/");
        String dayStr = dateStr.substring(0, slashIndex);
        return Integer.parseInt(dayStr);
    }


    /**
     * Returns the date in a string format (dd/mm/yyyy) from the Date object
     * @param date : date object
     * @return
     */
    public static String getDateStringFromDate(Date date){
        if(date == null){
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // dd/MM/yyyy
        return dateFormat.format(date);
    }


    /**
     * Fetches the time string from a Date object
     * @param date
     * @return
     */
    public static String getTimeStringFromDate(Date date){
        if(date!=null){
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            return timeFormat.format(date);
        }
        return null;
    }


    /**
     * Converts degrees to radians
     * @param degrees
     * @return
     */
    public static double deg2radians(double degrees) {
        return degrees * (Math.PI/180);
    }


    /**
     * Converts from com.google.api.client.util.DateTime to java.util.Date
     * @param dateTime
     * @return Date object
     */
    public static Date convertDateFormat(DateTime dateTime){
        return new Date(dateTime.getValue());
    }


    /**
     * Sorts a list of game data objects by their date time in the ascending order
     * @param list
     */
    public static void sortListByDate(List<GameData> list){
        Collections.sort(list, new CustomDateComparator());
    }


    /**
     * Displays the exception in the log
     * @param logTag
     * @param e
     */
    public static void logException(String logTag, Exception e){
        Log.e(logTag, e.getMessage(), e);
    }


    /**
     * Finds the difference in  between the two dates. date2 - date1 is done. So if date1 is later,
     * the returned value will be negative.
     * @param date1 :
     * @param date2 :
     * @return : no of seconds between the two date objects.
     */
    public static long getDateDifferenceInSeconds(Date date1, Date date2){
        long date1Millis = date1.getTime();
        long date2Millis = date2.getTime();

        long milliDiff = date2Millis - date1Millis;
        long secondDiff = milliDiff / 1000;

        return secondDiff;
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDateDifferenceInMinutes(Date date1, Date date2){
        long secondDiff = getDateDifferenceInSeconds(date1, date2);
        return secondDiff / 60;
    }


    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDateDifferenceInHours(Date date1, Date date2){
        long secondDiff = getDateDifferenceInSeconds(date1, date2);
        return secondDiff / 3600;
    }



    /**
     * Converts a pixel value to a dp value according to the phone being used
     * @param context
     * @param px
     * @return : dp value
     */
    public static float dpFromPx(Context context, float px){
        return px / context.getResources().getDisplayMetrics().density;
    }


    /**
     * Converts a dp value to a pixel value according to the phone being used
     * @param context
     * @param dp
     * @return
     */
    public static float pxFromDp(Context context, float dp){
        return dp * context.getResources().getDisplayMetrics().density;
    }


    /**
     * Cancels the input task if the input exception object is of type SocketTimeoutException
     * @param e
     * @param task
     */
    public static void handleSocketTimeOut(IOException e, AsyncTask task) {
        if(e != null && e instanceof SocketTimeoutException){
            task.cancel(true);
        }
    }


    private static class CustomDateComparator implements Comparator<GameData>{


        @Override
        public int compare(GameData gameData, GameData gameData2) {
            return gameData.getDateTime().compareTo(gameData2.getDateTime());
        }
    }


}
