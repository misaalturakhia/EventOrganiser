package com.android.khel247.asynctasks.gametasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.khel247.adapters.OrganisedGamesListAdapter;
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

/** Fetches the games organised by the user and adds them to the input listadapter
 * Created by Misaal on 22/12/2014.
 */
public class GetOrganisedGamesTask extends AsyncTask<Void, Void, List<GameData>>{

    private final Token token;

    private final ListView mListView;
    private final LinearLayout emptyViewLayout;
    private final FrameLayout mProgressLayout;
    private final Context context;

    private GameEndpoint gameEndpoint;
    private OrganisedGamesListAdapter mAdapter;


    /** Constructor
     *
     * @param token : the user's authentication token
     * @param adapter : the adapter of the list
     * @param progressLayout : the layout that holds the progress bar
     * @param layout : the future empty view of the listview. It replaces the progressbar when the
     *               server call is complete
     * @param listView : the list that will be populated by the call to the server
     */
    public GetOrganisedGamesTask(Context context, Token token, OrganisedGamesListAdapter adapter, FrameLayout progressLayout,
                                 LinearLayout layout, ListView listView){
        this.token = token;
        this.context = context;
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
        LoginToken loginToken = new LoginToken();
        loginToken.setUsername(token.getUsername());
        loginToken.setAuthToken(token.getToken());

        gameEndpoint = EndpointUtil.getGameEndpointService();
        GameFormCollection collection = null;
        try {
            collection = gameEndpoint.getOrganisedGames(loginToken).execute();
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

        if(gameDataList != null && gameDataList.size() > 0){ // if the result is null, let the adapter be
            mAdapter.clear();  // clear if its not null
            if(gameDataList.size() == 1){
                mAdapter.add(gameDataList.get(0));
            }else if(gameDataList.size() > 1)
                mAdapter.addAll(gameDataList);
        }
    }
}
