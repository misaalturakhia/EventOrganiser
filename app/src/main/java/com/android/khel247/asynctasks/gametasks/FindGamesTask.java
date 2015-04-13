package com.android.khel247.asynctasks.gametasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Network;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.GameData;
import com.android.khel247.model.PublicGameWidgets;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.FindGamesForm;
import com.khel247.backend.gameEndpoint.model.GameForm;
import com.khel247.backend.gameEndpoint.model.GameFormCollection;
import com.khel247.backend.gameEndpoint.model.LoginToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 29/12/2014.
 */
public class FindGamesTask extends AsyncTask<Void, Void, List<GameData>> {

    private final Context context;
    private final LatLng latLng;
    private final double mDistance;
    private final LoginToken loginToken;
    private final String mFormat;
    private ProgressDialog mProgressDialog;
    private GameEndpoint gameEndpoint;

    public FindGamesTask(Context context, Token token, LatLng latLng, int distance, String format){
        this.context = context;
        this.latLng = latLng;
        this.mDistance = distance;
        this.loginToken = createLoginToken(token);
        this.mFormat = format;
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
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, "Finding Games ...");
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected List<GameData> doInBackground(Void... voids) {
        gameEndpoint = EndpointUtil.getGameEndpointService();

        FindGamesForm form = createForm();
        GameFormCollection collection = null;
        try {
            collection = gameEndpoint
                     .findGames(form).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        if(collection == null){
            return null;
        }
        // fetch list of gameForm objects from the server response object (collection)
        List<GameForm> gameList = collection.getItems();
        if(gameList == null)
            return null;

        // convert to list of GameData
        List<GameData> gameDataList = new ArrayList<>();
        for(GameForm gameForm : gameList){
            gameDataList.add(new GameData(gameForm));
        }
        return gameDataList;
    }

    private FindGamesForm createForm() {
        FindGamesForm form = new FindGamesForm();
        form.setLoginToken(loginToken);
        form.setLatitude(latLng.latitude);
        form.setLongitude(latLng.longitude);
        form.setDistance(mDistance);
        form.setFormat(mFormat);
        return form;
    }

    @Override
    protected void onPostExecute(List<GameData> resultList) {
        dismissDialog(mProgressDialog);
    }
}
