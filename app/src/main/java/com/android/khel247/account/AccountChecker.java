package com.android.khel247.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.khel247.model.Token;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Misaal on 23/11/2014.
 */
public class AccountChecker {

    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "Adding new account";
    public static final String ARG_AUTH_TYPE = "Token type";
    public static final String ARG_ACCOUNT_TYPE = "Account type";
    public static final String ACCOUNT_TYPE = "android.khel247.com";
    public static final String PARAM_USER_PASS = "UserPassword";
    public static final String TOKEN_TYPE = ACCOUNT_TYPE+".FullToken";

    private final AccountManager manager;
    private boolean mInvalidate;
    private AlertDialog mAlertDialog;
    private Activity activity;
    private final String LOG_TAG;

    public AccountChecker(Activity activity, AccountManager acctManager){
        this.manager = acctManager;
        this.activity = activity;
        this.LOG_TAG = activity.getClass().getSimpleName();
    }


    /**
     * Checks the account manager if it holds any accounts of this app. If no, it calls the
     * addNewAccount() that displays an AccountLoginActivity to take the user's credentials.
     * If it has 1 account, it returns the token from the accountmanager.
     * Else if it has multiple accounts registered, it gives the user the chance to choose which account
     * @return : returns the String authToken of the selected account
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Token getTokenFromAccountIfAvailable() throws ExecutionException, InterruptedException {
        // get all accounts stored by the manager
        Account[] accounts = manager.getAccountsByType(AccountChecker.ACCOUNT_TYPE);
        String authToken = null;
        if(accounts.length < 1){ // no accounts
            addNewAccount(AccountChecker.ACCOUNT_TYPE, AccountChecker.TOKEN_TYPE);
        }else if(accounts.length == 1){
            return getExistingAccountAuthToken(accounts[0], AccountChecker.TOKEN_TYPE);
        } else{
            Log.v(LOG_TAG, "Account name! : "+accounts[0].name);
            showAccountPicker(accounts, AccountChecker.TOKEN_TYPE, false);
        }
        return null;
    }

    /**
     * Adds a new account to the Account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = manager.addAccount(accountType, authTokenType,
                null, null, activity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();
                            Log.d(LOG_TAG, "AddNewAccount Bundle is " + bnd);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }, null);
    }


    /**Let the user select one from the multiple array of accounts. Request an auth token upon user
     * select. The invalidate param, lets the user, invalidate the token of the selected account
     *
     * @param availableAccounts
     * @param authTokenType
     * @param invalidate
     */
    private void showAccountPicker(final Account[] availableAccounts, final String authTokenType,
                                   final boolean invalidate) {
        mInvalidate = invalidate;

        if(availableAccounts.length > 0) {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(activity).setTitle("Pick Account").setAdapter(
                    new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_list_item_1
                            , name), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (invalidate)
                                invalidateAuthToken(availableAccounts[which], authTokenType);
                            else {
                                try {
                                    getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                                } catch (ExecutionException e) {
                                    Log.e(LOG_TAG, e.getMessage(), e);
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    Log.e(LOG_TAG, e.getMessage(), e);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).create();
            mAlertDialog.show();
        }
    }

    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    public Token getExistingAccountAuthToken(Account account, String authTokenType) throws ExecutionException, InterruptedException {
        final AccountManagerFuture<Bundle> future = manager.getAuthToken(account, authTokenType,
                null, activity, null, null);

        FutureTask<Token> task = new FutureTask<Token>(new Callable<Token>() {
            @Override
            public Token call() throws Exception {
                String authtoken = null;
                String username = null;
                try {
                    Bundle bnd = future.getResult();
                    authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    username = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);
                    Log.d(LOG_TAG, "GetToken Bundle is " + bnd);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return new Token(authtoken, username);
            }
        });
        new Thread(task).start();
        return task.get();

    }

    /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    public void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = manager.getAuthToken(account, authTokenType,
                null, activity, null,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    manager.invalidateAuthToken(account.type, authtoken);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     * @param accountType
     * @param authTokenType
     */
    public void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = manager.getAuthTokenByFeatures(accountType,
                authTokenType, null, activity, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.v(LOG_TAG, ((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
                            Log.d(LOG_TAG, "GetTokenForAccount Bundle is " + bnd);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(),e);
                            e.printStackTrace();
                        }
                    }
                }
                , null);
    }




}
