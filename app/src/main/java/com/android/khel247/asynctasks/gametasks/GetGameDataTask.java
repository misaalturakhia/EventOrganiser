package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.khel247.R;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.fragments.GameFragment;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.GameForm;
import com.khel247.backend.gameEndpoint.model.LoginToken;

import java.io.IOException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/** Fetches GameData from the server using the input gameId
 *
 * Created by Misaal on 24/12/2014.
 */
public class GetGameDataTask extends AsyncTask<String, Void, GameData> {

    private static final String LOG_TAG = GetGameDataTask.class.getSimpleName();
    private final ActionBarActivity mActivity;
    private final Token mToken;
    private GameEndpoint gameEndpoint;
    private ProgressDialog mProgressDialog;

    /**
     * Constructor
     * @param context :
     * @param data : the data object to be populated
     */
    public GetGameDataTask(ActionBarActivity activity, Token token){
        this.mActivity = activity;
        this.mToken = token;
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mActivity)){
            cancel(true);
            return;
        }
        //show progress dialog
        mProgressDialog.show();
    }

    @Override
    protected GameData doInBackground(String... strings) {
        if(strings == null || strings.length < 1){
            return null;
        }

        // get gameKey from task input parameters
        String gameKey = strings[0];

        gameEndpoint = EndpointUtil.getGameEndpointService();
        GameForm form = null;
        try {
            form = gameEndpoint.getGameData(gameKey).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }
        if(form == null){
            return null;
        }

        return new GameData(form);
    }

    @Override
    protected void onPostExecute(GameData data) {
        dismissDialog(mProgressDialog);

    }

}
