package com.android.khel247.asynctasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.services.InternetCheckService;
import com.android.khel247.services.MessageService;

/**
 * Created by Misaal on 26/11/2014.
 */
public abstract class NetworkTaskMethods{

    private static final String DEFAULT_DIALOG_TITLE = "";
    private static final String DEFAULT_DIALOG_MESSAGE = "";


    /**
     * Shows a loading dialog
     * @param context
     * @return
     */
    public static ProgressDialog showLoadingDialog(Context context){
        return ProgressDialog.show(context, DEFAULT_DIALOG_TITLE, DEFAULT_DIALOG_MESSAGE);
    }

    /**
     * Shows a loading dialog with the input message body
     * @param context
     * @param messageBody
     * @return
     */
    public static ProgressDialog showLoadingDialog(Context context, String messageBody){
        if(messageBody == null)
            return ProgressDialog.show(context, DEFAULT_DIALOG_TITLE, DEFAULT_DIALOG_MESSAGE);

        return ProgressDialog.show(context, DEFAULT_DIALOG_TITLE, messageBody);
    }

    /**
     * Shows a loading dialog with the input title and message body
     * @param context
     * @param title
     * @param messageBody
     * @return
     */
    public static ProgressDialog showLoadingDialog(Context context, String title, String messageBody){
        if(title == null){
            title = DEFAULT_DIALOG_TITLE;
        }if(messageBody == null){
            messageBody = DEFAULT_DIALOG_MESSAGE;
        }
        return ProgressDialog.show(context, title, messageBody);
    }

    /**
     * Checks if the device is connected to the internet. If it isn't, it displays a toast displaying
     * the same.
     * @param context
     * @return
     */
    public static boolean internetCheck(Context context){
        InternetCheckService netService = new InternetCheckService(context);
        return netService.isConnectedToNetwork();
    }

}
