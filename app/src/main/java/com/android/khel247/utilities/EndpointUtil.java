package com.android.khel247.utilities;

import com.android.khel247.R;
import com.android.khel247.constants.AndroidConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.khel247.backend.gameEndpoint.GameEndpoint;
import com.khel247.backend.memberEndpoint.MemberEndpoint;

import java.io.IOException;

/**
 * Created by Misaal on 21/11/2014.
 */
public abstract class EndpointUtil {

    private static final String ROOT_URL = AndroidConstants.APP_SPOT_URL;
    private static final String APP_NAME = "FootballApp";

    /**
     * Builds and lets the user access the MemberEndpoint api by connecting to the localhost
     * @param api
     * @return
     */
    public static MemberEndpoint getMemberEndpointService(){

        MemberEndpoint api = null;
        MemberEndpoint.Builder builder = new MemberEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhosts IP address in Android emulator
                // - 0.0.0.0 for actual device
                // - turn off compression when running against local devappserver
                .setRootUrl(ROOT_URL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                }).setApplicationName(APP_NAME);
        // end options for devappserver
        api = builder.build();
        return api;
    }

    /**
     * Builds and lets the user access the MemberEndpoint api by connecting to the localhost
     * @param api
     * @return
     */
    public static GameEndpoint getGameEndpointService(){
        GameEndpoint api = null;
        GameEndpoint.Builder builder = new GameEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhosts IP address in Android emulator
                // - 0.0.0.0 for actual device
                // - turn off compression when running against local devappserver
                .setRootUrl(ROOT_URL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                }).setApplicationName(APP_NAME);
        // end options for devappserver
        api = builder.build();
        return api;
    }
}
