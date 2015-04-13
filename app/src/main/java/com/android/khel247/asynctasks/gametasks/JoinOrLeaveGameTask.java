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

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/** Makes a call to the server, and either removes or adds the user to the attending list of the game
 * specfied by gameKey
 * Created by Misaal on 23/12/2014.
 */
public class JoinOrLeaveGameTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private final Token token;
    private final Context context;
    private final boolean isJoin;
    private final String gameKey;

    public JoinOrLeaveGameTask(Context context, Token token, String gameKey,  boolean isJoin){
        this.context = context;
        this.token = token;
        this.isJoin = isJoin;
        this.gameKey = gameKey;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        if(gameKey == null)
            return null;

        LoginToken loginToken = createLoginToken();
        GameEndpoint gameEndpoint = EndpointUtil.getGameEndpointService();
        WrappedBoolean result = null;
        try {
            if(isJoin)
                result = gameEndpoint.joinGame(gameKey, loginToken).execute();
            else
                result = gameEndpoint.leaveGame(gameKey, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(WrappedBoolean bool) {
        if(bool != null && !bool.getResult()){
            if(isJoin)
                Toast.makeText(context, MessageService.COULDNT_JOIN_GAME, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, MessageService.COULDNT_LEAVE_GAME, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convert Token to LoginToken
     * @return
     */
    private LoginToken createLoginToken() {
        LoginToken loginToken = new LoginToken();
        loginToken.setUsername(token.getUsername());
        loginToken.setAuthToken(token.getToken());
        return loginToken;
    }
}
