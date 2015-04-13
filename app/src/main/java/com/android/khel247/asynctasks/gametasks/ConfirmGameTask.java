package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
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

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 08/01/2015.
 */
public class ConfirmGameTask extends AsyncTask<Void, Void, WrappedBoolean>{


    private final String gameKey;
    private final Context context;
    private final LoginToken loginToken;
    private ProgressDialog mProgressDialog;
    private GameEndpoint gameEndpoint;

    public ConfirmGameTask(Context context, Token token, String webSafeGameKey) {
        this.context = context;
        this.loginToken = createLoginToken(token);
        this.gameKey = webSafeGameKey;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, "Confirming game ...");
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        gameEndpoint = EndpointUtil.getGameEndpointService();

        WrappedBoolean result = null;
        try {
            result = gameEndpoint.confirmGame(gameKey, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean result) {
        dismissDialog(mProgressDialog);
    }

    private LoginToken createLoginToken(Token token) {
        LoginToken loginToken = new LoginToken();
        loginToken.setUsername(token.getUsername());
        loginToken.setAuthToken(token.getToken());
        return loginToken;
    }
}
