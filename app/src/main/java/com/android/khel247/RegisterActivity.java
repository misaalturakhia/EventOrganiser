package com.android.khel247;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.khel247.fragments.RegisterFragment;

/**
 * The activity that facilities user registration
 */
public class RegisterActivity extends ActionBarActivity {

    private final String LOG_TAG = RegisterActivity.class.getSimpleName();

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse;
    private Bundle mResultBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(savedInstanceState  == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.register_container, new RegisterFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.register, menu);
        return false; // returned false to remove menu button
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
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
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
