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
import com.khel247.backend.gameEndpoint.model.InvitesForm;
import com.khel247.backend.gameEndpoint.model.LoginToken;
import com.khel247.backend.gameEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.util.List;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 05/01/2015.
 */
public class InviteMoreTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private final Context context;
    private final String gameKey;
    private final List<String> invitedList;
    private final LoginToken loginToken;
    private ProgressDialog mProgressDialog;

    public InviteMoreTask(Context context, Token token, String webSafeGameKey, List<String> invitedList){
        this.context = context;
        this.gameKey = webSafeGameKey;
        this.invitedList = invitedList;
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
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, "Inviting ...");
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

        InvitesForm form = createInvitesForm();
        try {
            result = gameEndpoint.addInvites(gameKey, form).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return result;
    }

    private InvitesForm createInvitesForm() {
        InvitesForm form = new InvitesForm();
        form.setLoginToken(loginToken);
        form.setInvites(invitedList);
        return form;
    }

    @Override
    protected void onPostExecute(WrappedBoolean wrappedBoolean) {
        dismissDialog(mProgressDialog);
    }
}
