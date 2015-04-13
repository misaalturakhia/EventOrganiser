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

/**
 * Created by Misaal on 11/12/2014.
 */
public class UnpinContactTask extends AsyncTask<String, Void, WrappedBoolean> {

    private final LoginToken loginToken;
    private final Context context;
    private final ImageButton pinBtn;
    private MemberEndpoint memberEndpoint;

    public UnpinContactTask(Context context, Token token, ImageButton button ){
        this.context = context;
        this.loginToken = Token.createMemberEndpointLoginToken(token);
        this.pinBtn = button;
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
    protected WrappedBoolean doInBackground(String... strings) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        memberEndpoint = EndpointUtil.getMemberEndpointService();
        if(strings.length == 1){
            return unpinContact(strings[0]);
        }else if(strings.length > 1){
            return unpinContacts(strings);
        }else
            return null;

    }


    /**
     * Removes the member identified by username from the user's contact list
     * @param username : member's username who will be removed from the user's list of contacts
     * @return
     */
    private WrappedBoolean unpinContact(String username) {
        if(UtilityMethods.isEmptyOrNull(username)) // check user input
            return null;

        WrappedBoolean result = null;
        try {
            result = memberEndpoint.removeContact(username, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;

    }

    private WrappedBoolean unpinContacts(String[] usernames) {
        //TODO:
        return null;
    }


}
