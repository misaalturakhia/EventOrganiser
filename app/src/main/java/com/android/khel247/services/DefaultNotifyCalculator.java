package com.android.khel247.services;

import com.android.khel247.utilities.UtilityMethods;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Misaal on 28/12/2014.
 */
public class DefaultNotifyCalculator {

    private static final int[] DEFAULT_LONG_NOTIFY_ARRAY = { 3, 9, 24};
    private static final int[] DEFAULT_MEDIUM_NOTIFY_ARRAY = {3, 9};
    private static final int[] DEFAULT_SHORT_NOTIFY_ARRAY = {2, 3, 6};
    private static final int[] DEFAULT_REALLY_SHORT_NOTIFY_ARRAY = {2, 3};

    private static final int NIGHT_END = 8;
    private static final int NIGHT_START = 0; // 12am
    private static final int NO_OF_NIGHT_HOURS = NIGHT_END - NIGHT_START;
    private static final int WORK_END = 18;
    private static final int WORK_START = 9;

    /**
     * The date and time of the game
     */
    private final Date gameDate;

    /**
     * the date and time of creation
     */
    private final Date creationDate;

    /**
     * difference in hours between creation date and game date
     */
    private int hoursToGame;

    /**
     * the difference in hours between the end of night and the start of the game
     */
    private final int hoursAfterNight;


    //    private
    public DefaultNotifyCalculator(Date scheduledDate, Date createdDate){
        this.creationDate = createdDate;
        this.gameDate = scheduledDate;
        this.hoursToGame = (int)UtilityMethods.getDateDifferenceInHours(creationDate, gameDate);
        this.hoursAfterNight = calculateHoursAfterNight();
    }


    /** Returns the calculated default notify array depending on how many hours are left for the
     *  game to start
     *
     * @return
     */
    public int[] getDefaultNotifyArray(){
        if(hoursToGame >= 30) { // game is more than 1 day away
            return getFullDayNotify();
        }else if(hoursToGame < 30 && hoursToGame >= 12){
            return getMediumDayNotify();
        }else{
            return getShortDayNotify();
        }
    }


    /** Calculates the how many hours after the end of night does the game start
     *
     * @return
     */
    private int calculateHoursAfterNight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(gameDate);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int diffFromNight = hourOfDay - NIGHT_END;
        if(diffFromNight < 0){ // the game is in the night
            // calculate difference with the night of the previous day
            diffFromNight = diffFromNight + 24;
        }

        return diffFromNight;
    }

    private int[] getShortDayNotify() {
        if(hoursToGame < 12 && hoursToGame >= 6){
            return DEFAULT_SHORT_NOTIFY_ARRAY;
        }else if(hoursToGame < 6 && hoursToGame > 3){ // less than 6 hours to game
            return DEFAULT_REALLY_SHORT_NOTIFY_ARRAY;
        }else if(hoursToGame > 2){
            return new int[]{2};
        }else{ // don't notify
            return new int[]{0};
        }
    }


    /**
     * returns a default array if the game is between 30 hours and 12 hours away
     * @return
     */
    private int[] getMediumDayNotify() {
        if(hoursAfterNight >= 9){ // if the game is in the evening
            return DEFAULT_MEDIUM_NOTIFY_ARRAY;
        }else if(hoursAfterNight < 9 && hoursAfterNight >= 4){ // game in the afternoon
            // notify 3 hours before, as soon as night is over and 24 hours before
            return new int[]{3, hoursAfterNight};
        }else if(hoursAfterNight <= 3){ // if game is in the morning
            // notify in the morning, notify the night before and 24 hours before game
            return new int[]{hoursAfterNight, hoursAfterNight + NO_OF_NIGHT_HOURS};
        }
        return DEFAULT_MEDIUM_NOTIFY_ARRAY;
    }


    /** returns a default array if the game is more than 30 hours away
     *
     * @return
     */
    private int[] getFullDayNotify() {
        if(hoursAfterNight >= 9){ // if the game is in the evening
            return DEFAULT_LONG_NOTIFY_ARRAY;
        }else if(hoursAfterNight < 9 && hoursAfterNight >= 4){ // game in the afternoon
            // notify 3 hours before, as soon as night is over and 24 hours before
            return new int[]{3, hoursAfterNight, 24};
        }else if(hoursAfterNight <= 3){ // if game is in the morning
            // notify in the morning, notify the night before and 24 hours before game
            return new int[]{hoursAfterNight, hoursAfterNight + NO_OF_NIGHT_HOURS , 24};
        }
        return DEFAULT_LONG_NOTIFY_ARRAY;
    }

}
