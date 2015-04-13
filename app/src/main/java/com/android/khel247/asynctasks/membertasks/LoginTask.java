package com.android.khel247.asynctasks.membertasks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.khel247.LoginActivity;
import com.android.khel247.MainActivity;
import com.android.khel247.account.AccountChecker;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.AndroidConstants;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.LoginCredentials;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.services.MessageService;
import com.android.khel247.services.PasswordEncryptionService;
import com.android.khel247.utilities.GoogleAPIMethods;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginForm;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.MemberSalt;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;
import static com.android.khel247.utilities.UtilityMethods.handleSocketTimeOut;
import static com.android.khel247.utilities.UtilityMethods.isEmptyOrNull;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 07/11/2014.
 */
public class LoginTask extends AsyncTask<Void, Void, Intent> {

    private static final String LOG_TAG = LoginTask.class.getSimpleName();

    private MemberEndpoint memberEndpoint = null;
    private final LoginActivity mActivity;
    private LoginCredentials credentials;
    private ProgressDialog mProgressDialog;
    private GoogleCloudMessaging gcm;
    private String regId;

    /**
     * Constructor
     * @param loginActivity : the application context
     * @param loginCredentials : the object that holds login data
     */
    public LoginTask(LoginActivity loginActivity, LoginCredentials loginCredentials){
        this.mActivity = loginActivity;
        this.credentials = loginCredentials;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mActivity)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(mActivity, DialogMessages.LOGGING_IN);
    }

    @Override
    protected void onCancelled(Intent intent) {
        dismissDialog(mProgressDialog);
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Intent doInBackground(Void... voids) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        if(memberEndpoint == null)
            memberEndpoint = EndpointUtil.getMemberEndpointService();

        if(gcm == null)
            gcm = GoogleCloudMessaging.getInstance(mActivity);
        // get input credentials
        String username = credentials.getUsername();
        String passwordStr = credentials.getPassword();

        MemberSalt saltObj = null;
        String encryptedPassword = null;
        LoginToken token = null;
        try {

            // get salt from server
            saltObj = memberEndpoint.retrieveSalt(username).execute();
            if(saltObj.getSaltStr() == null){
                showToast(MessageService.SOMETHING_WENT_WRONG);
                return null;
            }

            //encrypt the password
            encryptedPassword = encryptPassword(passwordStr, saltObj.getSaltStr());

            // check if the device has play services enabled
            if(!GoogleAPIMethods.playServicesCheck(mActivity))
                return null;
            regId = gcm.register(AndroidConstants.SENDER_ID);
            if(isEmptyOrNull(regId)){
                showToast("Couldnt get RegId");
                return null;
            }

            // put the data into a LoginForm object
            LoginForm form = createLoginForm(username, encryptedPassword, regId);

            // use the loginMember endpoint to facilitate login
            token = memberEndpoint.loginMember(form).execute();
        } catch (IOException e) {
            handleSocketTimeOut(e, this);
            logException(e);
            e.printStackTrace();
            // trace in the LOG_CAT
        }catch (InvalidKeySpecException e){
            logException(e);
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            logException(e);
            e.printStackTrace();
        }
        if(isCancelled()){
            return null;
        }
        if(token == null ){
            showToast(MessageService.INVALID_CREDENTIALS);
            return null;
        }
        if(token.getErrorCode() != null && token.getErrorCode() > 300){
            showToast(MessageService.getMessageFromCode(token.getErrorCode()));
            return null;
        }

        final Intent res = new Intent();
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, token.getUsername());
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountChecker.ACCOUNT_TYPE);
        res.putExtra(AccountManager.KEY_AUTHTOKEN,token.getAuthToken() );
        res.putExtra(AccountChecker.PARAM_USER_PASS, "");
        return res;

    }


    /**
     *
     */
    private void showToast(final String message) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showShortToast(mActivity, message);
            }
        });
    }

    private void logException(Exception e) {
        Log.e(LOG_TAG, e.getMessage(), e);
    }

    /**Creates the LoginForm object which holds the login data and is sent when the endpoint is called
     *
     * @param username
     * @param encryptedPassword
     * @return
     */
    private LoginForm createLoginForm(String username, String encryptedPassword, String regId) {
        LoginForm form = new LoginForm();
        form.setUsername(username);
        form.setEncryptedPassword(encryptedPassword);
        form.setRegId(regId);
        return form;
    }

    /** Uses the PasswordEncryptionService to encrypt the password using the plain-text password and
     * the input salt.
     *
     * @param passwordStr : plain-text password
     * @param saltStr : salt
     * @return : encrypted password
     * @throws java.security.spec.InvalidKeySpecException
     * @throws java.security.NoSuchAlgorithmException
     */
    private String encryptPassword(String passwordStr, String saltStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PasswordEncryptionService service = new PasswordEncryptionService();
        return service.getEncryptedPasswordString(passwordStr, saltStr);
    }

    @Override
    protected void onPostExecute(Intent result) {
        dismissDialog(mProgressDialog);
        if(result != null){
            saveRegIdToSharedPreferences();
            finishLogin(result);
            Intent mainIntent = new Intent(mActivity, MainActivity.class);
            mainIntent.putExtra(Constants.ARG_TOKEN_CHECKED, true);
            mActivity.startActivity(mainIntent);
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountChecker.PARAM_USER_PASS);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        AccountManager mAccountManager = AccountManager.get(mActivity);
        if (mActivity.getIntent().getBooleanExtra(AccountChecker.ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = AccountChecker.TOKEN_TYPE;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        }
        mActivity.setAccountAuthenticatorResult(intent.getExtras());
        mActivity.setResult(mActivity.RESULT_OK, intent);
        mActivity.finish();
    }


    /**
     * Save the device's regId to the shared preferences for persistent use
     */
    private void saveRegIdToSharedPreferences() {
        SharedPreferences prefs = mActivity.getApplicationContext().
                getSharedPreferences(Constants.MY_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.ARG_REG_ID, regId).apply();
    }


}


