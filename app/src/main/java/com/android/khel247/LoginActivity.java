package com.android.khel247;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.fragments.LoginFragment;
import com.android.khel247.constants.Constants;

/**
 * Takes care of user login. Added functionality from the AccountAuthenticatorActivity class so that
 * it can extend ActionBarActivity and use the functionality of the AccountAuthenticatorActivity .
 */
public class LoginActivity extends ActionBarActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private FragmentManager manager;
    private LoginFragment appLoginFragment;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the AccountAuthenticatorResponse
        getAccountAuthenticatorResponse();

        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            LoginFragment loginFragment = new LoginFragment();
            if(intent != null){
                String username = intent.getStringExtra(Constants.ARG_USERNAME);
                Bundle args = new Bundle();
                args.putString(Constants.ARG_USERNAME, username);
                loginFragment.setArguments(args);
            }

            // display fragment
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, loginFragment)
            .commit();

        }

    }

    /**
     * Retrieves the AccountAuthenticatorResponse from either the intent of the savedInstanceState,
     * if the savedInstanceState is non-zero.
     * Method of AccountAuthenticatorActivity
     * Got code from
     * <a href ="http://code.google.com/p/toolib/source/browse/trunk/framework/core/java/android/accounts/AccountAuthenticatorActivity.java?r=52">here</a>
     */
    private void getAccountAuthenticatorResponse() {
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }


    /**
     * Instantiates the different login fragments and adds them to a fragment transaction.
     * Use, when adding new types of login
     */
    private void displayFragments() {

        // create fragment instances
//        fbLoginFragment = new FacebookLoginFragment();
//        googleLoginFragment = new GoogleLoginFragment();
        appLoginFragment = new LoginFragment();

        // get fragment manager
        manager = getSupportFragmentManager();

        // create and begin fragment transaction
        FragmentTransaction transaction = manager.beginTransaction();

        // add fragments to the transaction
        transaction.add(R.id.login_container, appLoginFragment);
//        transaction.add(R.id.login_container, fbLoginFragment);
//        transaction.add(R.id.login_container, googleLoginFragment);

        // commit transaction
        transaction.commit();
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     * Method of AccountAuthenticatorActivity
     * Got code from
     * <a href ="http://code.google.com/p/toolib/source/browse/trunk/framework/core/java/android/accounts/AccountAuthenticatorActivity.java?r=52">here</a>
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}
