package com.android.khel247;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.khel247.account.AccountChecker;
import com.android.khel247.asynctasks.gametasks.GetGameDataTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.fragments.GameFragment;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;

import java.util.concurrent.ExecutionException;

import static com.android.khel247.utilities.UtilityMethods.showShortToast;


/**
 * Displays game data.
 */
public class GameActivity extends ActionBarActivity {

    private static final String LOG_TAG = GameActivity.class.getSimpleName();
    private static final String GAME_FRAGMENT_TAG = "GameFragment";
    private int intentType = 0;
    private GameData data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent != null){
                intentType = intent.getIntExtra(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOAD_GAME_DATA);
                switch(intentType){
                    case Constants.INTENT_LOAD_GAME_DATA:
                        // handle incoming intent if it needs data to be fetched from the server,
                        // generally activated when starting the activity from a notification
                        loadGameDataAction(intent);
                        break;
                    case Constants.INTENT_LOADED_GAME_DATA:
                        // handle incoming intent if it already supplies the game data
                        loadedGameDataAction(intent);
                        break;
                    default:
                        // navigate to the main Activity if there is no supplied data
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                }
            }else{
                // navigate to the main Activity if there is no supplied data
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            }
        }
    }


    /**
     * Handles the case where the input intent already has the game data to be displayed.
     * Fetches the authentication token from the intent and the game data and displays the game fragment
     * @param intent
     */
    private void loadedGameDataAction(Intent intent) {
        Token token = (Token)intent.getSerializableExtra(Constants.ARG_TOKEN);
        data = (GameData)intent.getSerializableExtra(GameConstants.ARG_GAME_DATA);
        startGameFragment(token, data, null, false);
    }


    /**
     * loads the game fragment with the input data and authentication token
     * @param token
     * @param data
     */
    private void startGameFragment(Token token, GameData data, String gameMessage, boolean isNegativeProgress) {
        GameFragment gameFragment = GameFragment.newInstance(token, data, gameMessage, isNegativeProgress);
        // display fragment
        getSupportFragmentManager().beginTransaction().add(R.id.game_container, gameFragment,
                GAME_FRAGMENT_TAG).commit();
    }


    /**
     * Handles the intent type which specifies that the data needs to be loaded. Makes a network
     * call to the server to fetch the game data from the gameId found in the intent
     * @param intent
     */
    private void loadGameDataAction(Intent intent) {
        AccountManager manager = AccountManager.get(this);
        Token token = getTokenAction(manager);
        if(token == null){ // navigate to the MainActivity, it handles account details
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            return;
        }
        boolean isNegativeProgress = intent.getBooleanExtra(Constants.ARG_IS_NEGATIVE_PROGRESS, false);
        String gameKey = intent.getStringExtra(Constants.ARG_GAME_KEY);
        String gameMessage = intent.getStringExtra(Constants.ARG_GAME_MESSAGE);
        if(gameKey == null){
            showShortToast(this, MessageService.SOMETHING_WENT_WRONG);
            return;
        }
        GetGameDataTask task = new GetGameDataTask(this, token);
        task.execute(gameKey);
        try {
            GameData data = task.get();
            if(data != null){
                startGameFragment(token, data, gameMessage, isNegativeProgress);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    /**
     * Checks the account manager if it holds any accounts of this app. If no, it calls the
     * addNewAccount() that displays an AccountLoginActivity to take the user's credentials.
     * If it has 1 account, it returns the token from the accountmanager.
     * Else if it has multiple accounts registered, it gives the user the chance to choose which account
     *
     * @param manager : an instance of the account manager
     * @return : returns the String authToken of the selected account
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     */
    private Token getTokenAction(AccountManager manager){
        Token token = null;
        AccountChecker checker = new AccountChecker(this, manager);
        try {
            token = checker.getTokenFromAccountIfAvailable();
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return token;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_game, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
