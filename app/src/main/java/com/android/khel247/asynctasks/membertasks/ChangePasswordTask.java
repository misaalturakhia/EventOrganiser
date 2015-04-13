package com.android.khel247.asynctasks.membertasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.model.Token;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.services.MessageService;
import com.android.khel247.services.PasswordEncryptionService;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.MemberSalt;
import com.khel247.backend.memberEndpoint.model.PasswordForm;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Misaal on 26/11/2014.
 */
public class ChangePasswordTask extends AsyncTask<Void, Void, WrappedBoolean> {

    private static final String LOG_TAG = ChangePasswordTask.class.getSimpleName();

    private final Token authToken;
    private final String currentPassword;
    private final String newPassword;
    private final Context context;
    private MemberEndpoint memberEndpoint;
    private ProgressDialog mProgressDialog;

    public ChangePasswordTask(Context context, Token token, String currentPW, String newPW ){
        this.context = context;
        this.authToken = token ;
        this.currentPassword = currentPW;
        this.newPassword = newPW;
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context);
    }

    @Override
    protected void onCancelled(WrappedBoolean wrappedBoolean) {
        UtilityMethods.dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }

    @Override
    protected WrappedBoolean doInBackground(Void... voids) {
        if (isCancelled()) // checks if the task has been cancelled. If yes, the execution never goes
            return null;   //  to onPostExecute

        // instantiate endpoint connection
        memberEndpoint = EndpointUtil.getMemberEndpointService();
        WrappedBoolean result = null;
        MemberSalt memberSalt = null;
        String encryptedCurrentPassword = null;
        String encryptedNewPassword = null;
        LoginToken token = createLoginToken(authToken.getUsername(), authToken.getToken());
        try { // get salt from the server
            memberSalt = memberEndpoint.retrieveSalt(authToken.getUsername()).execute();
            if(memberSalt == null){
                result = createWrappedBoolean(false, 500); // 500 = something went wrong
            }

            //encrypt the current password and new password
            encryptedCurrentPassword = encryptPassword(currentPassword, memberSalt.getSaltStr());
            encryptedNewPassword = encryptPassword(newPassword, memberSalt.getSaltStr());
            PasswordForm form = createPasswordForm(encryptedCurrentPassword, encryptedNewPassword, token);
            result = memberEndpoint.changePassword(form).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            logException(e);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            logException(e);
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            logException(e);
            e.printStackTrace();
        }

        if(result == null){
            return createWrappedBoolean(false, 500); // 500 = something went wrong
        }
        return result;
    }


    private WrappedBoolean createWrappedBoolean(boolean result, int code){
        WrappedBoolean bool = new WrappedBoolean();
        bool.setResult(result);
        bool.setResponseCode(code);
        return bool;
    }


    public LoginToken createLoginToken(String username, String token){
        LoginToken authToken = new LoginToken();
        authToken.setUsername(username);
        authToken.setAuthToken(token);
        return authToken;
    }


    private PasswordForm createPasswordForm(String currentPassword, String newPassword, LoginToken token){
        PasswordForm form = new PasswordForm();
        form.setCurrentPassword(currentPassword);
        form.setNewPassword(newPassword);
        form.setLoginToken(token);
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


    private static void logException(Exception e){
        Log.e(LOG_TAG, e.getMessage(), e);

    }


    @Override
    protected void onPostExecute(WrappedBoolean result) {
        UtilityMethods.dismissDialog(mProgressDialog);
        String resultMessage = MessageService.getMessageFromCode(result.getResponseCode());
        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
    }


}
