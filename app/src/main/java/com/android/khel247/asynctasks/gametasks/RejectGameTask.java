package com.android.khel247.asynctasks.gametasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.LoginToken;
import com.khel247.backend.gameEndpoint.model.WrappedBoolean;

import java.io.IOException;

/**
 * Created by Misaal on 16/01/2015.
 */
public class RejectGameTask extends AsyncTask<Void, Void, WrappedBoolean> {


    private final LoginToken loginToken;
    private final String gameKeyStr;
    private final Context mContext;

    public RejectGameTask(Context context, Token token, String webSafeGameKey){
        this.mContext = context;
        this.loginToken = createLoginToken(token);
        this.gameKeyStr = webSafeGameKey;
    }

    private LoginToken createLoginToken(Token token) {
        LoginToken loginToken = new LoginToken();
        loginToken.setUsername(token.getUsername());
        loginToken.setAuthToken(token.getToken());
        return loginToken;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mContext)){
            cancel(true);
            return;
        }
    }

    @Override
    protected void onCancelled(WrappedBoolean wrappedBoolean) {
        Toast.makeText(mContext, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();

    }

    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        GameEndpoint gameEndpoint = EndpointUtil.getGameEndpointService();

        WrappedBoolean result = null;
        try {
            result = gameEndpoint.rejectInvite(gameKeyStr, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }
}
