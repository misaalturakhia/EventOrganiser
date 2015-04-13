package com.android.khel247.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.android.khel247.constants.NotificationConstants;
import com.android.khel247.receivers.GcmBroadcastReceiver;
import com.android.khel247.utilities.UtilityMethods;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Misaal on 20/12/2014.
 */
public class GcmIntentService extends IntentService {

    private static final String SERVICE_NAME = "GcmIntentService";

    public GcmIntentService(){
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                analyzeGCMMessage(extras);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    /** Analyzes the Bundle of extras extracted from the incoming gcm message and calls the appropriate
     *  notification builder message
     *
     * @param extras : bundle of extras
     */
    private void analyzeGCMMessage(Bundle extras){
        String messageType = extras.getString(NotificationConstants.ARG_MESSAGE_TYPE);
        NotificationService service = new NotificationService();
        switch(messageType){
            case NotificationConstants.TYPE_GAME_INVITATION:
                service.buildGameInviteNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_JOINED_MEMBER:
                service.buildGameJoinedNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_LEFT_MEMBER:
                service.buildGameLeftNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_REJECT:
                service.buildGameRejectedNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_PROGRESS:
                service.buildGameProgressNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_CANCELLED:
                service.buildGameCancelledNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_SUBORGANISER_ADDED:
                service.buildSubOrganiserAddedNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_CONFIRMED:
                service.buildGameConfirmedNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_STARTED:
                service.buildGameStartedNotification(this, extras);
                break;
            case NotificationConstants.TYPE_GAME_WILL_START:
                service.buildGameWillStartNotification(this, extras);
            default:
                String message = "INVALID MESSAGE TYPE";
                UtilityMethods.showShortToast(this, message);
                break;
        }
    }


}
