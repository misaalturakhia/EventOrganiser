package com.android.khel247;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.khel247.account.AccountChecker;
import com.android.khel247.adapters.NavDrawerListAdapter;
import com.android.khel247.asynctasks.membertasks.CheckTokenTask;
import com.android.khel247.asynctasks.membertasks.UnregisterDeviceTask;
import com.android.khel247.fragments.AccountSettingsFragment;
import com.android.khel247.fragments.HomeFragment;
import com.android.khel247.model.NavDrawerItem;
import com.android.khel247.model.Token;
import com.android.khel247.constants.Constants;
import com.google.android.gms.internal.is;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private boolean isTokenChecked = false;
    /**
     * The account manager instance used to retrieve tokens
     */
    private AccountManager mAccountManager;
    /**
     * The user's authentication token used for authorization
     */
    private Token authToken;

    private DrawerLayout drawerLayout;

    private ListView drawerListView;

    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;

    private CharSequence appTitle;

    private String[] drawerMenuTitles;

    private TypedArray drawerMenuIcons;

    private ArrayList<NavDrawerItem> drawerItemList;

    private NavDrawerListAdapter drawerListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAccountManager = AccountManager.get(this);

        if(getIntent() != null){
            isTokenChecked = getIntent().getBooleanExtra(Constants.ARG_TOKEN_CHECKED, false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authToken = getTokenAction(mAccountManager);
        handleToken();

        initDrawer();

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            removeAllPreviousFragments(manager);
            HomeFragment fragment = HomeFragment.newInstance(authToken);
            manager.beginTransaction().add(R.id.main_container, fragment)
                    .commit();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDrawer() {
        // set drawer and app title
        appTitle = drawerTitle = getTitle();

        loadDrawerTitlesAndIcons();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerListView = (ListView)findViewById(R.id.list_slidermenu);
        drawerListView.setOnItemClickListener(new DrawerListItemClickListener());

        drawerItemList = createDrawerItemList();

        // recycle typed array
        drawerMenuIcons.recycle();

        // initialising and setting the nav drawer list adapter to the listview object
        drawerListAdapter = new NavDrawerListAdapter(getApplicationContext(),
                drawerItemList);
        drawerListView.setAdapter(drawerListAdapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = createDrawerToggle();

        drawerLayout.setDrawerListener(drawerToggle);

    }

    /**
     * Create the ActionBarDrawerToggle object. Handle the actionbar title when the drawer is open
     * or closed.
     * @return
     */
    private ActionBarDrawerToggle createDrawerToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(appTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        return toggle;
    }

    /**
     * Creates the arraylist of NavDrawerItems to be put in the drawer menu list
     * @return
     */
    private ArrayList<NavDrawerItem> createDrawerItemList() {
        ArrayList<NavDrawerItem> itemList = new ArrayList<NavDrawerItem>();

        // populate list with the items to be displayed
        for(int i = 0; i < drawerMenuTitles.length; i++){
            itemList.add(new NavDrawerItem(drawerMenuTitles[i],
                    drawerMenuIcons.getResourceId(i, -1)));
        }

        return itemList;
    }

    /**
     * loads fields drawerMenuTitles & drawerMenuIcons with pre-defined resource arrays
     */
    private void loadDrawerTitlesAndIcons() {
        // load slide menu item titles
        drawerMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // load menu item icons
        drawerMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
    }


    @Override
    public void onStart() {
        super.onStart();
        handleToken();
    }

    private void handleToken(){
        if(authToken != null){
            if(!isTokenChecked){
                CheckTokenTask task = new CheckTokenTask(this, authToken);
                task.execute();
            }
        }else{
            signOut();
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
     * @throws ExecutionException
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
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();
        switch (id){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Signs out the user. In the process,
     */
    public void signOut() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra(AccountChecker.ARG_IS_ADDING_NEW_ACCOUNT, true);
        finish();
        if(authToken != null){
            unregisterDevice();
            Account[] accounts = mAccountManager.getAccountsByType(AccountChecker.ACCOUNT_TYPE);
            for(Account acc: accounts){
                if(acc.name.equals(authToken.getUsername())){
                    mAccountManager.removeAccount(acc, null, null);
                }
            }
            mAccountManager = AccountManager.get(this);
            loginIntent.putExtra(Constants.ARG_USERNAME, authToken.getUsername());
        }
        startActivity(loginIntent);
    }


    /**
     * Invoke a task that tells the server to remove the current registration id (which is fetched
     * from the sharedPreferences)
     */
    private void unregisterDevice() {
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        String regId = prefs.getString(Constants.ARG_REG_ID, null);
        if(regId != null){
            UnregisterDeviceTask task = new UnregisterDeviceTask(this, authToken);
            task.execute(regId);
            prefs.edit().remove(Constants.ARG_REG_ID); // remove from sharedpreferences
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListView);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getSupportActionBar().setTitle(appTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private class DrawerListItemClickListener implements ListView.OnItemClickListener{

        int lastClickedIndex = -1;
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            if(index == lastClickedIndex){
                drawerLayout.closeDrawer(drawerListView);
            }else{
                displayView(index);
                lastClickedIndex = index;
            }
        }

    }


    /**
     * Handles the creation of views on each listView item click at position index
     * @param index
     */
    public void displayView(int index) {
        Fragment fragment = null;
        switch(index){
            case 0:
                fragment = HomeFragment.newInstance(authToken);
                break;
            case 1:
                navigateToProfileActivity();
                break;
            case 2:
                navigateToPeopleActivity();
                break;
            case 3:
                navigateToFindGamesActivity();
                break;
            case 4:
                navigateToCreateGameActivity();
                break;
            case 5:
                fragment = AccountSettingsFragment.newInstance(authToken);
                break;
            case 6:
                signOutAction(index);
                break;
            default:
                break;
        }
        if(fragment != null)
            setFragmentInView(index, fragment);
    }


    /**
     * Starts the FindGamesActivity and lets the user find public games around him
     */
    private void navigateToFindGamesActivity() {
        Intent findIntent = new Intent(this, FindGamesActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        findIntent.putExtras(extras);
        startActivity(findIntent);
    }

    private void navigateToCreateGameActivity() {
        Intent createIntent = new Intent(this, CreateGameActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        createIntent.putExtras(extras);
        startActivity(createIntent);
    }


    /**
     * Navigates to the profile Activity which will display the user's own profile.
     */
    private void navigateToProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_TOKEN, authToken);
        extras.putString(Constants.ARG_PROFILE_USERNAME, authToken.getUsername());
        profileIntent.putExtras(extras);
        startActivity(profileIntent);
    }

    /**
     * Sets the input fragment as the view of the MainActivity class. Also, then updates the
     * selected item's color in the navigation drawer.
     * @param position
     * @param fragment
     */
    private void setFragmentInView(int position, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        removeAllPreviousFragments(fragmentManager);

        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment).commit();

        // update selected item and title, then close the drawer
        drawerListView.setItemChecked(position, true);
        drawerListView.setSelection(position);
        if(position == 0){
            String appTitle = getResources().getString(R.string.app_name);
            setTitle(appTitle); // set the title of the home activity to the App Name
        }else{
            setTitle(drawerMenuTitles[position]);
        }
        drawerLayout.closeDrawer(drawerListView);
    }

    private void removeAllPreviousFragments(FragmentManager fragmentManager) {
        List<Fragment> fragments =  fragmentManager.getFragments();

        if(fragments != null && !fragments.isEmpty()){
            for(Fragment fragment : fragments){
                if(fragment != null)
                    fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }


    /**
     * Displays an alert dialog that checks if the user really wants to sign out
     * @param position
     */
    private void signOutAction(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // set title
        dialogBuilder.setTitle(Constants.SIGN_OUT_DIALOG_TITLE);

        // set dialog message
        dialogBuilder
                .setMessage(Constants.SIGN_OUT_DIALOG_BODY)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        signOut();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // deselect item in drawerlistview
                        drawerListView.setItemChecked(position, false);
                        dialog.cancel(); //close the dialog

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = dialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /**
     * Uses an intent to navigate to the PeopleActivity. it also passes on the authentication token
     * of the user
     */
    private void navigateToPeopleActivity() {
        Intent peopleIntent = new Intent(this, PeopleActivity.class);
        Bundle tokenBundle = new Bundle();
        tokenBundle.putSerializable(Constants.ARG_TOKEN, authToken);
        peopleIntent.putExtras(tokenBundle);
        startActivity(peopleIntent);
    }

}
