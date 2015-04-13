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

import com.android.khel247.MainActivity;
import com.android.khel247.RegisterActivity;
import com.android.khel247.account.AccountChecker;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.AndroidConstants;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.RegisterCredentials;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.services.PasswordEncryptionService;
import com.android.khel247.utilities.GoogleAPIMethods;
import com.android.khel247.utilities.UtilityMethods;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.RegisterForm;

import java.io.IOException;

/**
 * Created by Misaal on 07/11/2014.
 */
public class RegisterTask extends AsyncTask<Void, Void, Intent>{

    private static final String LOG_TAG = RegisterTask.class.getSimpleName();

    private MemberEndpoint memberEndpoint = null;
    private RegisterActivity mActivity;
    private RegisterCredentials credentials;
    private String username;
    private ProgressDialog mProgressDialog;
    private GoogleCloudMessaging gcm;
    private String regId;


    public RegisterTask(RegisterActivity activity, RegisterCredentials registerCredentials){
        this.mActivity = activity;
        this.credentials = registerCredentials;
    }


    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(mActivity)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(mActivity, DialogMessages.REGISTERING);
    }

    @Override
    protected void onCancelled(Intent intent) {
        UtilityMethods.dismissDialog(mProgressDialog);
        Toast.makeText(mActivity, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Intent doInBackground(Void... voids) {


        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        // connect to localhost/ whatever is specified
        if(memberEndpoint == null)
            memberEndpoint = EndpointUtil.getMemberEndpointService();

        // check if the device has play services enabled
        if(!GoogleAPIMethods.playServicesCheck(mActivity))
            return null;
        if(gcm == null)
            gcm = GoogleCloudMessaging.getInstance(mActivity);

        // get credentials
        username = credentials.getUsername();
        String password = credentials.getPassword();

        // generate salt and encrypt password
        PasswordEncryptionService service = new PasswordEncryptionService();

        String encryptedPassword = null;
        String salt = null;
        try {
            salt = service.getGeneratedSaltString();

            if(salt == null){
                throw new Exception("Salt is null!");
            }
            encryptedPassword = service.getEncryptedPasswordString(password, salt);
            if(encryptedPassword == null) {
                throw new Exception("EncryptedPassword is null!");
            }

            regId = gcm.register(AndroidConstants.SENDER_ID);
            if(regId == null){
                throw new Exception("Couldnt get RegistrationId from GCM");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        RegisterForm form = createRegisterForm(encryptedPassword, salt, regId, credentials);
        LoginToken token = null;
        try {
            token = memberEndpoint.registerMember(form).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        final Intent res = new Intent();
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, token.getUsername());
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountChecker.ACCOUNT_TYPE);
        res.putExtra(AccountManager.KEY_AUTHTOKEN,token.getAuthToken() );
        res.putExtra(AccountChecker.PARAM_USER_PASS, "");
        res.putExtra(Constants.ARG_REG_ID, regId);
        return res;
    }

    /**
     * Create RegisterForm object using the encrypted password, salt and the data from the
     * registerCredentials object
     * @param encPassword : encrypted password
     * @param salt : salt that is used to encrypt and authenticate the password
     * @param credentials : holds the user input from the UI
     * @return
     */
    private RegisterForm createRegisterForm(String encPassword, String salt, String regId, RegisterCredentials credentials) {
        RegisterForm form = new RegisterForm();
        form.setEmailAddress(credentials.getEmailAddress());
        form.setUsername(credentials.getUsername());
        form.setEncSalt(salt);
        form.setEncPassword(encPassword);
        form.setRegId(regId);
        form.setFirstName(credentials.getFirstName());
        form.setLastName(credentials.getLastName());
        form.setPosition(credentials.getPosition());
        form.setArea(credentials.getArea());
        form.setCity(credentials.getCity());
        form.setCountry(credentials.getCountry());
        return form;
    }

    @Override
    protected void onPostExecute(Intent result) {
        UtilityMethods.dismissDialog(mProgressDialog);
        if(result != null){
            saveRegIdToSharedPreferences();
            finishRegister(result);
            Intent mainIntent = new Intent(mActivity, MainActivity.class);
            mainIntent.putExtra(Constants.ARG_TOKEN_CHECKED, true);
            mActivity.startActivity(mainIntent);
        }
    }


    /**
     * Save the device's regId to the shared preferences for persistent use
     */
    private void saveRegIdToSharedPreferences() {
        SharedPreferences prefs = mActivity.getApplicationContext().
                getSharedPreferences(Constants.MY_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.ARG_REG_ID, regId).apply();
    }


    /**
     * Creates a new account and adds it to the account manager along with the login token .
     * @param intent
     */
    private void finishRegister(Intent intent) {
        // the username of the member identifies his account in the account manager. Thus, the
        // accountname is the username
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountChecker.PARAM_USER_PASS);
        // create account
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        AccountManager mAccountManager = AccountManager.get(mActivity);
        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authtokenType = AccountChecker.TOKEN_TYPE;
        // Creating the account on the device and setting the auth token we got
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        mAccountManager.addAccountExplicitly(account, accountPassword, null);
        mAccountManager.setAuthToken(account, authtokenType, authtoken);

        mActivity.setAccountAuthenticatorResult(intent.getExtras());
        mActivity.setResult(mActivity.RESULT_OK, intent);
        mActivity.finish();
    }
}
