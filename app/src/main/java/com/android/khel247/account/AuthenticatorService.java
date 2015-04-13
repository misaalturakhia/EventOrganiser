package com.android.khel247.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Misaal on 23/11/2014.
 */
public class AuthenticatorService extends Service {

    private MemberAuthenticator auth;

    @Override
    public void onCreate() {
        auth = new MemberAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return auth.getIBinder();
    }
}
