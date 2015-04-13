package com.android.khel247.asynctasks.gametasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.gameEndpoint.model.GameForm;
import com.khel247.backend.gameEndpoint.model.GameFormCollection;
import com.khel247.backend.gameEndpoint.model.LoginToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;

/**
 * Created by Misaal on 03/01/2015.
 */
public class GetAttendingGamesTask extends AsyncTask<Void, Void, List<GameData>>{

    private final Token authToken;
    private final ListView mListView;
    private final LinearLayout emptyViewLayout;
    private final FrameLayout mProgressLayout;
    private final Context context;
    private ArrayAdapter mAdapter;
    private GameEndpoint gameEndpoint;


    /** Constructor
     *
     * @param token : the user's authentication token
     * @param adapter : the adapter of the list
     * @param progressLayout : the framelayout that holds the progressbar
     * @param layout : the future empty view of the listview. It replaces the progressbar when the
     *               server call is complete
     * @param listView : the list that will be populated by the call to the server
     */
    public GetAttendingGamesTask(Context context, Token token, ArrayAdapter adapter, FrameLayout progressLayout,
                               LinearLayout layout, ListView listView) {
        this.context = context;
        this.authToken = token;
        this.mAdapter = adapter;
        this.mProgressLayout = progressLayout;
        this.emptyViewLayout = layout;
        this.mListView = listView;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mListView.setVisibility(View.GONE);
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected List<GameData> doInBackground(Void... voids) {

        LoginToken loginToken = new LoginToken(); // convert the token object into a login token
        loginToken.setUsername(authToken.getUsername());
        loginToken.setAuthToken(authToken.getToken());

        gameEndpoint = EndpointUtil.getGameEndpointService();

        GameFormCollection collection = null;
        try {
            collection = gameEndpoint.getAttendingGames(loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }
        if(collection == null){
            return null;
        }
        List<GameForm> gameList = collection.getItems();
        if(gameList == null)
            return null;

        List<GameData> gameDataList = new ArrayList<>();
        for(GameForm form : gameList){
            gameDataList.add(new GameData(form));
        }

        UtilityMethods.sortListByDate(gameDataList);
        return gameDataList;
    }


    /**
     *
     * @param gameDataList
     */
    @Override
    protected void onPostExecute(List<GameData> gameDataList) {
        mProgressLayout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mListView.setEmptyView(emptyViewLayout);

        if(gameDataList != null && gameDataList.size() > 0){
            mAdapter.clear();
            if(gameDataList.size() == 1){
                mAdapter.add(gameDataList.get(0));
            }else if(gameDataList.size() > 1){
                mAdapter.addAll(gameDataList);
            }
        }
    }

}
