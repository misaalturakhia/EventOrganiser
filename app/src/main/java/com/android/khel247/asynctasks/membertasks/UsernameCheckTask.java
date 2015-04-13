package com.android.khel247.asynctasks.membertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;

/** Connects to the endpoint and checks if the username is available
 * Created by Misaal on 21/11/2014.
 */
public class UsernameCheckTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private static final String LOG_TAG = UsernameCheckTask.class.getSimpleName();
    private final String username;
    private final Context context;
    private final Button registerBtn;


    private MemberEndpoint memberEndpoint = null;

    public UsernameCheckTask(Context context,String username, Button registerBtn){
        this.context = context;
        this.username = username;
        this.registerBtn = registerBtn;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
        }
    }

    @Override
    protected void onCancelled(WrappedBoolean result) {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        if(isCancelled())// if the task has been cancelled. also, onPostExecute will not be executed
            return null; // in this case

        // initiates the endpoint connection if it is null.
        memberEndpoint = EndpointUtil.getMemberEndpointService();
        WrappedBoolean result = null;
        try {
            // call MemberEndpoint api to check if the username is available
            result = memberEndpoint.usernameCheck(username).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean result) {
        if(result == null)
            return;
        if(!result.getResult()) { // if the result is false
            String resultMessage = MessageService.getMessageFromCode(result.getResponseCode());
            Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
            registerBtn.setEnabled(false);
        }else{
            registerBtn.setEnabled(true);
        }
    }
}
