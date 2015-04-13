package com.android.khel247.asynctasks.membertasks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.LoginActivity;
import com.android.khel247.account.AccountChecker;
import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.services.PasswordEncryptionService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.model.MemberSalt;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.android.khel247.utilities.UtilityMethods.dismissDialog;
import static com.android.khel247.utilities.UtilityMethods.logException;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 28/12/2014.
 */
public class DeleteAccountTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private static final String LOG_TAG = DeleteAccountTask.class.getSimpleName();
    private final LoginToken loginToken;
    private final Context context;
    private final String mPassWord;
    private final Token token;
    private ProgressDialog mProgressDialog;

    public DeleteAccountTask(Context context, Token token, String password){
        this.context = context;
        this.token = token;
        this.loginToken = Token.createMemberEndpointLoginToken(token);
        this.mPassWord = password;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, DialogMessages.DELETING_ACCOUNT);
    }

    @Override
    protected void onCancelled() {
        dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected WrappedBoolean doInBackground(Void... voids) {

        MemberEndpoint memberEndpoint = EndpointUtil.getMemberEndpointService();

        MemberSalt saltObj = null;
        WrappedBoolean result = null;
        String encryptedPassword = null;
        try {
            saltObj = memberEndpoint.retrieveSalt(token.getUsername()).execute();
            if(saltObj == null){
                return null;
            }

            //encrypt the password
            encryptedPassword = encryptPassword(mPassWord, saltObj.getSaltStr());

            result = memberEndpoint.deactivateAccount(encryptedPassword, loginToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            logException(LOG_TAG, e);
        } catch (NoSuchAlgorithmException e) {
            logException(LOG_TAG, e);
        } catch (InvalidKeySpecException e) {
            logException(LOG_TAG, e);
        }

        return result;
    }


    @Override
    protected void onPostExecute(WrappedBoolean result) {
        dismissDialog(mProgressDialog);
        if(result != null){
            if(result.getResult()){ // account deleted
                accountCleanUpAction();
            }else{ // something went wrong
                String message = MessageService.getMessageFromCode(result.getResponseCode());
                showShortToast(context, message);
            }
        }else{
            showShortToast(context, MessageService.SOMETHING_WENT_WRONG);
        }



    }

    /**
     * Fetches the accountmanager and deletes the current account. Also redirects to the LoginActivity
     */
    private void accountCleanUpAction() {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AccountChecker.ACCOUNT_TYPE);
        for(Account acc: accounts){
            if(acc.name.equals(token.getUsername())){
                accountManager.removeAccount(acc, null, null);
                accountManager = AccountManager.get(context);
                Intent loginIntent = new Intent(context, LoginActivity.class);
                loginIntent.putExtra(AccountChecker.ARG_IS_ADDING_NEW_ACCOUNT, true);
                loginIntent.putExtra(Constants.ARG_USERNAME, "");
                context.startActivity(loginIntent);
            }
        }
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
}
