package com.android.khel247.asynctasks.membertasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.EndpointUtil;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.MemberEndpoint;
import com.khel247.backend.memberEndpoint.model.LoginToken;
import com.khel247.backend.memberEndpoint.model.WrappedBoolean;

import java.io.IOException;

/** Unregister the device registration Id on the server for the user
 * Created by Misaal on 20/12/2014.
 */
public class UnregisterDeviceTask extends AsyncTask<String, Void, Void> {

    private final LoginToken authToken;
    private final Context context;

    /**
     * Constructor
     * @param context
     * @param token
     */
    public UnregisterDeviceTask(Context context, Token token){
        this.authToken  = Token.createMemberEndpointLoginToken(token);
        this.context = context;
    }


    @Override
    protected void onCancelled(Void voids) {
        Toast.makeText(context, MessageService.DEVICE_NOT_CONNECTED, Toast.LENGTH_LONG).show();
    }


    @Override
    protected Void doInBackground(String... strings) {
        String regId = strings[0];
        if(regId == null){
            throw new IllegalArgumentException("Input argument is null");
        }
        MemberEndpoint memberEndpoint = EndpointUtil.getMemberEndpointService();

        try {
            memberEndpoint.unregisterDevice(regId, authToken).execute();
        } catch (IOException e) {
            UtilityMethods.handleSocketTimeOut(e, this);
            e.printStackTrace();
        }
        return null;
    }
}
