package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.LoginToken;
import com.khel247.backend.gameEndpoint.model.WrappedBoolean;

import java.io.IOException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 29/12/2014.
 */
public class CancelGameTask extends AsyncTask<String, Void, WrappedBoolean>{


    private final Context context;
    private final LoginToken loginToken;
    private ProgressDialog mProgressDialog;
    private GameEndpoint gameEndpoint;

    public CancelGameTask(Context context, Token authToken) {
        this.context = context;
        this.loginToken = createLoginToken(authToken);
    }

    private LoginToken createLoginToken(Token authToken) {
        LoginToken token = new LoginToken();
        token.setUsername(authToken.getUsername());
        token.setAuthToken(authToken.getToken());
        return token;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, DialogMessages.CANCELLING_GAME);
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(String... strings) {
        String webSafeGameKey = strings[0];
        if(webSafeGameKey == null){
            return null;
        }

        gameEndpoint = EndpointUtil.getGameEndpointService();

        WrappedBoolean result = null;

        try {
            result = gameEndpoint.cancelGame(webSafeGameKey, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean result) {
        dismissDialog(mProgressDialog);
        if(result != null){
            if(!result.getResult()){
                UtilityMethods.showShortToast(context, MessageService.getMessageFromCode(result.getResponseCode()));
            }
        }
    }
}
