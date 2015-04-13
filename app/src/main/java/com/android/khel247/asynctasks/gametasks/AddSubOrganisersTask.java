package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
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
import java.net.SocketTimeoutException;
import java.util.List;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 05/01/2015.
 */
public class AddSubOrganisersTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private final Context context;
    private final String gameKey;
    private final List<String> subOrganiserList;
    private final LoginToken loginToken;
    private ProgressDialog mProgressDialog;

    public AddSubOrganisersTask(Context context, Token token, String webSafeGameKey, List<String> subOrganisers){
        this.context = context;
        this.gameKey = webSafeGameKey;
        this.subOrganiserList = subOrganisers;
        this.loginToken = createLoginToken(token);
    }

    private LoginToken createLoginToken(Token token) {
        LoginToken loginToken = new LoginToken();
        loginToken.setUsername(token.getUsername());
        loginToken.setAuthToken(token.getToken());
        return loginToken;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, "Telling players to invite " +
                "their contacts ... ");
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {

        GameEndpoint gameEndpoint = EndpointUtil.getGameEndpointService();

        WrappedBoolean result = null;

        try {
            result = gameEndpoint.addSubOrganisers(gameKey, subOrganiserList, loginToken).execute();
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
}
