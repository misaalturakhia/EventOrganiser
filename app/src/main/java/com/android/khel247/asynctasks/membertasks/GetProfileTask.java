package com.android.khel247.asynctasks.membertasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.asynctasks.NetworkTaskMethods;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.model.MemberData;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.ProfileForm;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;
import java.lang.reflect.Member;

/**
 * Created by Misaal on 27/12/2014.
 */
public class GetProfileTask extends AsyncTask<String, Void, ProfileForm> {


    private final Context context;
    private final GetProfileListener mListener;
    private MemberEndpoint memberEndpoint;
    private ProgressDialog mProgressDialog;


    public GetProfileTask(Context context, GetProfileListener listener){
        this.context = context;
        mListener = listener;
        if(mListener == null){
            throw new NullPointerException("Input GetProfileListener is null");
        }
    }

    @Override
    protected void onPreExecute() {
        if(!NetworkTaskMethods.internetCheck(context)){
            cancel(true);
            return;
        }
        mProgressDialog = NetworkTaskMethods.showLoadingDialog(context, DialogMessages.LOADING_PROFILE);
    }


    @Override
    protected void onCancelled(ProfileForm form) {
        UtilityMethods.dismissDialog(mProgressDialog);
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }



    @Override
    protected ProfileForm doInBackground(String... strings) {
        String profileUsername = strings[0];
        if(profileUsername == null){
            return null;
        }

        if(memberEndpoint == null)
            memberEndpoint = EndpointUtil.getMemberEndpointService();

        ProfileForm form = null;
        try {
            form = memberEndpoint.getMemberProfile(profileUsername).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }

        return form;
    }

    @Override
    protected void onPostExecute(ProfileForm profileForm) {
        mProgressDialog.dismiss();
        if(profileForm != null){
            MemberData data = new MemberData(profileForm);
            mListener.onProfileLoaded(data); // send profile data back to profile fragment
        }
    }

    public interface GetProfileListener{

        public void onProfileLoaded(MemberData data);
    }
}
