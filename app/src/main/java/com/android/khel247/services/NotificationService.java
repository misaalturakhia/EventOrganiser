package com.android.khel247.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.NotificationConstants;
import com.android.khel247.utilities.UtilityMethods;

/** A class that creates notification messages
 * Created by Misaal on 20/12/2014.
 */
public class NotificationService {
    private static final String LOG_TAG = NotificationService.class.getSimpleName();
    private static final String GAME_INVITATION_TITLE = "Game Invitation!";
    private static final String GAME_JOIN_TITLE = "Member Joined!";
    private static final String GAME_REJECT_TITLE = "Member Rejected!";
    private static final String GAME_LEFT_TITLE = "Member Left!";
    private static final String GAME_PROGRESS_TITLE = "Game Progress";

    private static final int GAME_INVITATION_ID = 1;
    private static final int GAME_JOINED_ID = 2;
    private static final int GAME_LEFT_ID = 3;
    private static final int GAME_REJECTED_ID = 4;
    private static final int GAME_CANCELLED_ID = 5;
    private static final int SUB_ORGANISER_ADDED_ID = 6;
    private static final int GAME_CONFIRMED = 7;
    private static final int GAME_STARTED = 8;
    private static final int GAME_WILL_START = 9;

    public static final String ARG_PROGRESS_TYPE = "Progress Type";
    public static final String PROGRESS_NEGATIVE = "negative";
    public static final String PROGRESS_POSITIVE = "positive";

    private Context context;

    /**
     *
     * @param context
     */
    public NotificationService(){
    }


    /**
     * builds the game invitation notification from the input message
     * @param extras : data from the message from the server
     */
    public void buildGameInviteNotification(Context context, Bundle extras){
        this.context = context;
        sendBasicNotification(extras, GAME_INVITATION_ID);
    }


    /**
     * Builds and displays a notification that tells the organiser that a member has joined the game
     * @param extras : data from the message from the server
     */
    public void buildGameJoinedNotification(Context context, Bundle extras){
        this.context = context;
        sendBasicNotification(extras, GAME_JOINED_ID);
    }

    /**Builds and displays a notification that tells the organiser that a member has left the game
     *
     * @param extras : data from the message from the server
     */
    public void buildGameLeftNotification(Context context, Bundle extras){
        this.context = context;
        sendBasicNotification(extras, GAME_LEFT_ID);
    }


    /**
     * Builds and displays a notification that tells the organiser that a member has left the game
     * @param extras : data from the message from the server
     */
    public void buildGameRejectedNotification(Context context, Bundle extras){
        this.context = context;
        sendBasicNotification(extras, GAME_REJECTED_ID);
    }


    /**
     * Builds and displays a notification that notifies the organiser of game progress
     * @param extras : data from the message from the server
     */
    public void buildGameProgressNotification(Context context, Bundle extras){
        this.context = context;
        String webSafeGameKey = getWebSafeGameKey(extras);
        String messageBody = getMessageBody(extras);
        String progressType = extras.getString(ARG_PROGRESS_TYPE);
        PendingIntent pIntent;
        if(progressType.equals(PROGRESS_NEGATIVE)){ // if negative progress is null, assign false
            pIntent = goToGameActivity(webSafeGameKey, messageBody, true);
        }else if(progressType.equals(PROGRESS_POSITIVE)){ // if not null, the progress message is negative
            pIntent = goToGameActivity(webSafeGameKey, messageBody, false);
        }else{
            pIntent = goToGameActivity(webSafeGameKey, messageBody, null);
        }
        createNotification(GAME_PROGRESS_TITLE, messageBody, pIntent, GAME_JOINED_ID);
    }


    /**
     * Builds and displays a notification that tells the member that a game has been cancelled
     * @param extras
     */
    public void buildGameCancelledNotification(Context context, Bundle extras) {
        this.context = context;
        String sender = getSenderOfMessage(extras);
        String messageBody = getMessageBody(extras);
        createNotification(sender, messageBody, null, GAME_CANCELLED_ID);
    }


    /**
     * Displays a notification that tells the user that he has been added as the suborganiser of a game
     * @param extras
     */
    public void buildSubOrganiserAddedNotification(Context context, Bundle extras) {
        this.context = context;
        sendBasicNotification(extras, SUB_ORGANISER_ADDED_ID);
    }


    /**
     * Displays a notification that tells the user that a specific game has been confirmed
     * @param extras
     */
    public void buildGameConfirmedNotification(Context context, Bundle extras) {
        this.context = context;
        sendBasicNotification(extras, GAME_CONFIRMED);
    }

    public void buildGameStartedNotification(Context context, Bundle extras) {
        this.context = context;
        sendBasicNotification(extras, GAME_STARTED);
    }

    public void buildGameWillStartNotification(Context context, Bundle extras) {
        this.context = context;
        sendBasicNotification(extras, GAME_WILL_START);
    }


    /**
     *
     * @param extras
     * @param type
     */
    private void sendBasicNotification(Bundle extras, int type){
        String sender = getSenderOfMessage(extras);
        String messageBody = getMessageBody(extras);
        String webSafeGameKey = getWebSafeGameKey(extras);
        String gameMessage = sender +" "+messageBody;
        PendingIntent pIntent = goToGameActivity(webSafeGameKey, gameMessage, null);
        createNotification(sender, messageBody, pIntent, type);
    }


    /**
     * Fetches the message Body from the bundle of extras
     * @param extras
     * @return
     */
    private String getMessageBody(Bundle extras){
        return extras.getString(NotificationConstants.ARG_MESSAGE_BODY);
    }


    /**
     * Fetches the sender details from the bundle
     * @param extras
     * @return
     */
    private String getSenderOfMessage(Bundle extras) {
        return extras.getString(NotificationConstants.ARG_MESSAGE_SENDER);
    }


    /**
     * Fetches the webSafeGameKey from the bundle of extras
     * @param extras
     * @return
     */
    private String getWebSafeGameKey(Bundle extras){
        return extras.getString(NotificationConstants.ARG_GAME_KEY);
    }


    /**
     * Creates a notification with the input title and messageBody and uses the input pending intent
     * to define its onclick functionality.
     *
     * @param title : notification title
     * @param messageBody : body of the notification
     * @param pIntent : pending intent that gives on click functionality
     * @param notificationId : id of the notification
     */
    private void createNotification(String title, String messageBody, PendingIntent pIntent, int notificationId){
        NotificationCompat.Builder builder  =  new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        // the flag_auto_cancel shuts the notification on clicking
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        if(pIntent != null){
            notification.contentIntent = pIntent;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        manager.notify(notificationId, notification);
    }

    /**
     * Creates the pendingIntent which adds On Click behaviour to the Game Invitation notification
     * @param gameKey
     * @return
     */
    private PendingIntent goToGameActivity(String gameKey, String gameMessage, Boolean isNegativeProgress) {
        Intent gameIntent = new Intent(context, com.android.khel247.GameActivity.class);
        if(isNegativeProgress != null){
            boolean progress = isNegativeProgress;
            gameIntent.putExtra(Constants.ARG_IS_NEGATIVE_PROGRESS, progress);
        }
        addGameIntentExtras(gameIntent, gameKey, gameMessage);
        PendingIntent pIntent = createPendingIntent(gameIntent);
        return pIntent;
    }

    /**
     * Creates a pending intent from the using the input Intent
     * @param gameIntent
     * @return
     */
    private PendingIntent createPendingIntent(Intent gameIntent) {
        return PendingIntent.getActivity(context, 0, gameIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Adds the input gameId and organiserUsername as extras to the intent
     * @param rejectIntent
     * @param gameKey
     * @param organiserUsername
     */
    private void addGameIntentExtras(Intent intent, String gameKey, String gameMessage) {
        intent.putExtra(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOAD_GAME_DATA);
        intent.putExtra(Constants.ARG_GAME_KEY, gameKey);
        intent.putExtra(Constants.ARG_GAME_MESSAGE, gameMessage);

    }
}
