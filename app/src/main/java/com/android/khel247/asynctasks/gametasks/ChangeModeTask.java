package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.Location;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.LoginToken;
import com.khel247.backend.gameEndpoint.model.SwitchToPublicForm;
import com.khel247.backend.gameEndpoint.model.WrappedBoolean;

import java.io.IOException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 05/01/2015.
 */
public class ChangeModeTask extends AsyncTask<Void, Void, WrappedBoolean>{

    private final Context context;
    private final String gameKey;
    private final LoginToken loginToken;
    private final Location mLocation;
    private GameEndpoint gameEndpoint;
    private ProgressDialog mProgressDialog;

    public ChangeModeTask(Context context, Token token,  String webSafeGameKey, Location location){
        this.context = context;
        this.gameKey = webSafeGameKey;
        this.loginToken = createLoginToken(token);
        this.mLocation = location;
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
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, "Changing Mode ...");
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {

        gameEndpoint = EndpointUtil.getGameEndpointService();

        SwitchToPublicForm form = createForm();

        WrappedBoolean result = null;
        try {
            result = gameEndpoint.switchToPublic(form).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

    private SwitchToPublicForm createForm() {
        SwitchToPublicForm form = new SwitchToPublicForm();
        form.setLoginToken(loginToken);
        form.setWebSafeGameKey(gameKey);
        form.setGameLocation(Location.convertToGameLocation(mLocation));
        return form;
    }

    @Override
    protected void onPostExecute(WrappedBoolean wrappedBoolean) {
        dismissDialog(mProgressDialog);
    }
}
