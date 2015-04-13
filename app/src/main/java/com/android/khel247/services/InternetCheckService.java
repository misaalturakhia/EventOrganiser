package com.android.khel247.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.khel247.constants.AndroidConstants;

import java.net.InetAddress;

/** Provides methods to check if the device is connected to the internet (via wifi, )
 * Created by Misaal on 16/11/2014.
 */
public class InternetCheckService {

    private static final String LOG_TAG = InternetCheckService.class.getSimpleName();

    private static final String CONNECT_URL = AndroidConstants.APP_SPOT_URL;

    private Context context;

    /**
     * making the default constructor private
     */
    private InternetCheckService(){}

    /**Constructor
     *
     * @param activityContext
     */
    public InternetCheckService(Context activityContext){
        this.context = activityContext;
    }



    /**Checks if the phone is connected to either wifi or mobile internet
     *
     * @return : true if connected, false if not connected
     */
    public boolean isConnectedToNetwork(){
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            isConnected = true;
        }
        return isConnected;
    }

    public boolean pingURL(String url){
        // checks if a connection with the appspot url can be established
        try{
            InetAddress ipAddress = InetAddress.getByName(CONNECT_URL);
            return ipAddress.isReachable(5000);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return false;
    }


    /**
     * Checks if the device is connected to wifi
     * @return : true- if connected, false-if not connected
     */
    public boolean isWifiConnected(){
        return isConnected(ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Checks if the mobile device has mobile internet active
     * @return : true- if connected, false-if not connected
     */
    public boolean isMobileInternetConnected(){
        return isConnected(ConnectivityManager.TYPE_MOBILE);
    }

    /**Checks if the device is connected to the input network type
     *
     * @param networkType : eg. ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI ...
     * @return : true- if connected, false-if not connected
     */
    private boolean isConnected(int networkType){
        boolean isWifiConnected = false;
        //get network information according to the network type.
        NetworkInfo netInfo = getNetworkInfo(networkType);
        if(netInfo != null && netInfo.isConnected()){
            isWifiConnected = true;
        }
        return isWifiConnected;
    }

    /**
     * Gets network info about the input network type
     * @param networkType
     * @return : networkInfo about the input network type
     */
    private NetworkInfo getNetworkInfo(int networkType){
        ConnectivityManager connectivityManager = getConnectivityManager();
        NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return netInfo;
    }

    /**
     * Gets current active network info
     * @return : networkInfo about the input network type
     */
    private NetworkInfo getNetworkInfo(){
        return getConnectivityManager().getActiveNetworkInfo();
    }

    /**
     * Get the Connectivity Manager object
     * @return : connectivityManager
     */
    private ConnectivityManager getConnectivityManager(){
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
