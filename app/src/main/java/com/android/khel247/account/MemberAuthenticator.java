package com.android.khel247.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.khel247.LoginActivity;
import com.android.khel247.utilities.EndpointUtil;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginForm;
import com.khel247.backend.memberEndpoint.model.LoginToken;

import java.io.IOException;

/** An authenticator that lets the application interface with the Account Manager. It, adds accounts,
 *  gets tokens etc
 * Created by Misaal on 21/11/2014.
 */
public class MemberAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;
    private MemberEndpoint memberEndpoint;

    public MemberAuthenticator(Context context){
        super(context);
        this.mContext = context;
    }
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountChecker.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountChecker.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountChecker.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                     Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                memberEndpoint = EndpointUtil.getMemberEndpointService();
                try {
                    LoginToken token = memberEndpoint.loginMember(createLoginForm(account.name, password)).execute();
                    authToken = token.getAuthToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountChecker.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountChecker.ARG_AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return s;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                    Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                              Account account, String[] strings) throws NetworkErrorException {
        return null;
    }

    @NonNull
    @Override
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account)
            throws NetworkErrorException {
        final Bundle result = new Bundle();

        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);

        return result;
//        return super.getAccountRemovalAllowed(response, account);
    }

    /**Creates the LoginForm object which holds the login data and is sent when the endpoint is called
     *
     * @param username
     * @param encryptedPassword
     * @return
     */
    private LoginForm createLoginForm(String username, String encryptedPassword) {
        LoginForm form = new LoginForm();
        form.setUsername(username);
        form.setEncryptedPassword(encryptedPassword);
        return form;
    }

}
