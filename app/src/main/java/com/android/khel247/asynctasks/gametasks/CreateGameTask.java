package com.android.khel247.asynctasks.gametasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.khel247.GameActivity;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.GameForm;
import com.khel247.backend.gameEndpoint.model.WrappedGameKey;

import java.io.IOException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 08/12/2014.
 */
public class CreateGameTask extends AsyncTask<Void, Void, WrappedGameKey> {

    private static final String LOG_TAG = CreateGameTask.class.getSimpleName();
    private final FragmentActivity mActivity;
    private final GameData mGameData;
    private final Token token;
    private GameEndpoint mGameEndpoint;
    private ProgressDialog mProgressDialog;


    /**
     * Constructor
     * @param activity
     * @param token
     * @param data
     */
    public CreateGameTask(FragmentActivity activity, Token token, GameData data){
        this.mActivity = activity;
        this.token = token;
        this.mGameData = data;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mActivity)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(mActivity, DialogMessages.CREATING_GAME);
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedGameKey doInBackground(Void... voids) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   // to onPostExecute

        if(mGameData == null)
            return null;

        mGameEndpoint = EndpointUtil.getGameEndpointService();

        GameForm form = GameData.convertToGameForm(mGameData);
        form.setOrganiserUsername(token.getUsername());

        WrappedGameKey result = null;
        try {
           result = mGameEndpoint.createGame(token.getToken(), form).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    protected void onPostExecute(WrappedGameKey result) {
        dismissDialog(mProgressDialog);
        if(result != null){
            String gameKey = result.getWebSafeGameKey();
            Log.d(LOG_TAG, "GAME KEY : "+gameKey);
            if(gameKey == null){
                showShortToast(mActivity, MessageService.getMessageFromCode(result.getResponseCode()));
                return;
            }else {
                mGameData.setWebSafeGameKey(gameKey);
                navigateToGamePage();
                mActivity.finish();
            }
        }else{
            showShortToast(mActivity, "Result is null");
        }
    }

    /**
     * Navigate to the GameActivity. Add intent extras about the intent type, the user's authentication
     * token and the GameData object
     */
    private void navigateToGamePage() {
        Intent gameIntent = new Intent(mActivity, GameActivity.class);
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOADED_GAME_DATA);
        args.putSerializable(Constants.ARG_TOKEN, token);
        args.putSerializable(GameConstants.ARG_GAME_DATA, mGameData);
        gameIntent.putExtras(args);
        mActivity.startActivity(gameIntent);
    }
}
