package com.android.khel247.asynctasks.membertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Misaal on 06/12/2014.
 */
public class PinContactTask extends AsyncTask<String, Void, WrappedBoolean> {

    private final LoginToken token;
    private Context context;
    private MemberEndpoint memberEndpoint;


    /**
     * constructor
     * @param context
     * @param token
     */
    public PinContactTask(Context context, Token token, ImageButton pinBtn){
        this.context = context;
        this.token = Token.createMemberEndpointLoginToken(token);
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
        }

    }


    @Override
    protected void onCancelled(WrappedBoolean wrappedBoolean) {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected WrappedBoolean doInBackground(String[] strings) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        memberEndpoint = EndpointUtil.getMemberEndpointService();
        if(strings.length == 1){
            return pinContact(strings[0]);
        }else if(strings.length > 1){
            return pinContacts(strings);
        }else
            return null;
    }

    /**
     * pin single contact
     */
    private WrappedBoolean pinContact(String username) {
        if(UtilityMethods.isEmptyOrNull(username)) // check user input
            return null;

        WrappedBoolean result = null;
        try {
            result = memberEndpoint.addContact(username, token).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean wrappedBoolean) {
        super.onPostExecute(wrappedBoolean);
    }

    /**
     * pin an array of contacts to the user's list
     * @param strings
     * @return
     */
    private WrappedBoolean pinContacts(String[] strings) {
        List<String> addList = Arrays.asList(strings);
        WrappedBoolean result = null;
        try {
            result = memberEndpoint.addContacts(addList, token).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

}
