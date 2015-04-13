package com.android.khel247.constants;

/**
 * Created by Misaal on 12/11/2014.
 */
public abstract class AndroidConstants {

    // project number of khel247 on google console. Used to generate a device registration id via
    // google cloud console
    public static final String SENDER_ID = "1070451584498";

    // url to connect to the endpoints from the default emulator
    public static final String LOCALHOST_URL = "http://10.0.2.2:8080/_ah/api/";

    // url to connect to the endpoints from the geny motion emulator
    public static final String GENYMOTION_LOCALHOST_URL = "http://10.0.3.2:8080/_ah/api/";

    // url to connect to a physical device
    public static final String DEVICE_URL = "http://0.0.0.0:8080/_ah/api/";

    // url to connect to the deployed app engine server
    public static final String APP_SPOT_URL = "https://khel-247.appspot.com/_ah/api";

}
