package com.android.khel247.utilities;

import android.util.Log;

import java.util.Calendar;

import static com.android.khel247.utilities.UtilityMethods.isEmptyOrNull;

/**
 * Created by Misaal on 13/12/2014.
 */
public abstract class GameUtilities {

    private static final int[] DEFAULT_NOTIFY_ARRAY = {3, 9, 24};
    public static final String MINUTES = " minutes";

    /**
     *
     * @param format
     * @return
     */
    public static final int getTotalPlayersFromFormat(String format){
        if(!Validator.isFormatValid(format)) //checks if the format string is in the format "5v5" etc
            return -1;

        int indexOfV = format.indexOf("v"); // get index of character v
        // get the number on the left of the "v"
        int leftNumber = Integer.parseInt(format.substring(0, indexOfV));
        // get the number on the right of the "v"
        int rightNumber = Integer.parseInt(format.substring(indexOfV + 1, format.length()));

        int total = leftNumber + rightNumber;
        return total;
    }


    /**
     * For now, the min players is just the total players
     * @param totalPlayers
     * @return
     */
    public static final int getDefaultMinPlayersFromTotal(int totalPlayers){
        return totalPlayers;
    }

    /**
     * Gets the default minimum number of players from the format string
     * @param totalPlayers
     * @return
     */
    public static final int getDefaultMinPlayersFromFormat(String format){
        int total = getTotalPlayersFromFormat(format);
        return getDefaultMinPlayersFromTotal(total);
    }


    /**
     * Calculates the maximum number of players allowed to join the game from the totalPlayers parameter
     * @param totalPlayers
     * @return
     */
    public static final int getDefaultMaxPlayersFromTotal(int totalPlayers){
        int extraPlayersAllowed = totalPlayers / 5;
        int remainder = totalPlayers % 5;
        if(remainder >= 3)
            extraPlayersAllowed = extraPlayersAllowed + 2;
        else if(remainder > 0 && remainder < 3)
            extraPlayersAllowed++;

        return totalPlayers+extraPlayersAllowed;
    }


    /**Returns the minimum number of people the user is expected to invite (recommended)
     *
     * @param totalPlayers
     * @return
     */
    public static final int getMinRecommendedInvites(int totalPlayers){
        // should never have a remainder because totalPlayers should always be even
        int minInvites = totalPlayers * 4 / 3;
        int remainder = totalPlayers * 4 % 3;
        if(remainder != 0){
            minInvites++;
        }
        if(minInvites % 2 != 0)
            minInvites--;
        return minInvites;
    }


    /** Returns the maximum number of people the user is expected to invite (recommended)
     *
     * @param totalPlayers
     * @return
     */
    public static final int getMaxRecommendedInvites(int totalPlayers){
        int maxInvites = totalPlayers * 5 / 3;
        if(maxInvites % 2 != 0){
            maxInvites ++;
        }
        return maxInvites;
    }


    /**
     * The organiser needs to invite at least as many players as required by the format
     * @param totalPlayers
     * @return
     */
    public static final int getMinAllowedInvites(int totalPlayers){
        int min = totalPlayers * 4/5;
        int remainder =  totalPlayers * 4/5;
        if(remainder > 2)
            min++;

        return min;
    }

    /**
     * The organiser needs to invite at least as many players as required by the format
     * @param totalPlayers
     * @return
     */
    public static final int getMinAllowedInvites(String format){
        int totalPlayers = getTotalPlayersFromFormat(format);
        return getMinAllowedInvites(totalPlayers);
    }


    /**
     * get the maximum players that the user can invite to his game at one time
     * @param totalPlayers
     * @return
     */
    public static final int getMaxAllowedInvites(int totalPlayers){
        return totalPlayers * 2;
    }


    /**
     * get the maximum players that the user can invite to his game at one time
     * @param totalPlayers
     * @return
     */
    public static final int getMaxAllowedInvites(String format){
        int totalPlayers = getTotalPlayersFromFormat(format);
        return totalPlayers * 2;
    }

    /**
     * Checks the date and time of the game, and sets up a default notify array.
     * Operates on the assumption that the date time are validated (i.e not in the past)
     * @param dateTimeArray
     * @return
     */
    public static int[] setupDefaultNotifyValues(int[] dateTimeArray) {
        int[] defaultNotify = null;
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        if(currentYear != dateTimeArray[0]){ // different year
            return DEFAULT_NOTIFY_ARRAY;
        }
        int currentMonth = cal.get(Calendar.MONTH);
        if(currentMonth != dateTimeArray[1]){ // different month
            return DEFAULT_NOTIFY_ARRAY;
        }
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        if(dateTimeArray[2] - currentDay >= 2 ){ // the difference between the day fields is 2 or more
            return DEFAULT_NOTIFY_ARRAY;
        }
        // CALCULATE HOUR DIFFERENCE
        int hourDifference = 25; // random default.
        if(dateTimeArray[2] == currentDay){ // same day
            hourDifference = dateTimeArray[3] - currentHour;
        }else if(dateTimeArray[2] == currentDay + 1) // game day is tomorrow
            hourDifference = 24 - currentHour + dateTimeArray[3];

        if(hourDifference >= 25){
            return DEFAULT_NOTIFY_ARRAY;
        }if(hourDifference >= 18){
            return new int[]{3, 12};
        }if(hourDifference >= 12){
            return new int[]{3,9};
        }if(hourDifference >= 6){
            return new int[]{2,4};
        }if(hourDifference >= 3){
            return new int[]{2};
        }

        return DEFAULT_NOTIFY_ARRAY;
    }


    /**
     * Get duration from the string returned from the duration spinner
     * @param durationString
     * @return
     */
    public static int getDuration(String durationString) {
        if(isEmptyOrNull(durationString)){
            return 0;
        }
        durationString = durationString.replace(MINUTES, "").trim();
        if(durationString.isEmpty()){
            return 0;
        }
        return Integer.parseInt(durationString);
    }
}
