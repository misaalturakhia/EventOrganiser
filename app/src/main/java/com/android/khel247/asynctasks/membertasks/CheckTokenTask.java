package com.android.khel247.asynctasks.membertasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.MainActivity;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;

/**
 * Created by Misaal on 09/12/2014.
 */
public class CheckTokenTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private final MainActivity mActivity;
    private final LoginToken mToken;
    private MemberEndpoint memberEndpoint;

    public CheckTokenTask(MainActivity activity, Token authToken){
        this.mActivity = activity;
        this.mToken = Token.createMemberEndpointLoginToken(authToken);
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mActivity)){
            cancel(true);
            return;
        }
    }


    @Override
    protected void onCancelled(WrappedBoolean wrappedBoolean) {
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        memberEndpoint = EndpointUtil.getMemberEndpointService();
        WrappedBoolean result = null;

        try {
            result = memberEndpoint.checkTokenValidity(mToken).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean result) {
        if(result != null && !result.getResult()){
            mActivity.signOut();
        }
    }
}
