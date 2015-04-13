package com.android.khel247.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.android.khel247.services.MessageService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.android.khel247.utilities.UtilityMethods.deg2radians;

/**
 * Created by Misaal on 15/12/2014.
 */
public abstract class GoogleAPIMethods {

    public static final int DEFAULT_NUMBER_ADDRESSES = 5;
    public static final long DEFAULT_MIN_DISTANCE = Long.MAX_VALUE;
    public static final int EARTH_RADIUS = 6371; // mean radius of the earth


    /**
     * Checks if Google Play Services is available, if not shows an errorDialog
     * @return : true - if available, false - if not
     */
    public static boolean playServicesCheck(Activity activity){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if(resultCode == ConnectionResult.SUCCESS){
            return true;
        }else{
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 1);
                if(errorDialog != null)
                    errorDialog.show();
            }else{
                UtilityMethods.showShortToast(activity, MessageService.PLAY_SERVICES_NOT_SUPPORTED);
            }
            return false;
        }
    }


    // LOCATION METHODS

    /**gets the last known cached location of the user's device
     *
     * @return
     */
    public static Location getLastKnownLocation(Context context){
        LocationManager locationManager = getLocationManager(context);
        // create criteria for location fetch
        Criteria criteria = new Criteria();
        // get best location provider
        String provider = getBestLocationProvider(locationManager, criteria);

        Location location =  locationManager.getLastKnownLocation(provider);
        if(location != null)
            return location;
        else{
            return getLastKnownLocationFromProviders(locationManager);
        }
    }


    private static Location getLastKnownLocationFromProviders(LocationManager locationManager){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }else{
                bestLocation = l;
                break;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }


    /**
     * Get last known location from the input location manager
     * @param manager
     * @return
     */
    public static Location getLastKnownLocation(LocationManager manager){
        Criteria criteria = new Criteria();
        String provider = getBestLocationProvider(manager, criteria);
        return manager.getLastKnownLocation(provider);
    }


    /**
     * Gets an address from the last known location
     * @param context
     * @return
     * @throws IOException
     */
    public static Address getAddressFromLastKnownLocation(Context context) throws IOException {
        Location location = getLastKnownLocation(context);
        return  getAddress(context, location);
    }


    /**
     * get address from the input location
     * @param location
     * @param context
     * @return
     * @throws IOException
     */
    public static Address getAddress(Context context, Location location) throws IOException {
        if(location == null){
            return null;
        }
        return getAddress(context, location.getLatitude(), location.getLongitude());
    }


    /**
     * get address from the input location
     * @param location
     * @param context
     * @return
     * @throws IOException
     */
    public static Address getAddress(Context context, double latitude, double longitude) throws IOException {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        if(Geocoder.isPresent()){
            List<Address> addresses = geoCoder.getFromLocation(latitude,
                    longitude, 1);

            if(addresses.size() > 0)
                return addresses.get(0);
        }
        return null;
    }


    /**
     * Get the best location provider from the location manager based on the input criteria
     * @param manager
     * @param criteria
     * @return
     */
    public static String getBestLocationProvider(LocationManager manager, Criteria criteria){
        return manager.getBestProvider(criteria, true);
    }


    /**
     * Gets the best location provider
     * @param context
     * @return
     */
    public static String getBestLocationProvider(Context context){
        LocationManager manager = getLocationManager(context);
        Criteria criteria = new Criteria();
        return getBestLocationProvider(manager, criteria);
    }

    /**
     * Get location manager
     * @param context
     * @return
     */
    public static LocationManager getLocationManager(Context context){
        return (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }


    /** gets the name of the country from the current location
     *
     * @param location
     * @param context
     * @return
     */
    public static String getCountryName(Location location, Context context) throws IOException {
        Address address = getAddress(context, location);
        if(address != null)
            return address.getCountryName();
        return null;
    }


    /**
     * Gets the city/town/village name of the input location
     * @param location
     * @param context
     * @return
     * @throws IOException
     */
    public static String getCityName(Location location, Context context) throws IOException {
        Address address = getAddress(context, location);
        if(address != null)
            if(address.getSubAdminArea() != null){
                return address.getSubAdminArea(); // was get address line 1
            }else if(UtilityMethods.isEmptyOrNull(address.getAddressLine(1))){
                String addressLine = address.getAddressLine(1);
                addressLine = addressLine.substring(0, addressLine.indexOf(",")).trim();
                return addressLine;
            }else{
                return address.getThoroughfare();
            }
        else
            return null;
    }


    /**
     * Get name of the area from the Location
     * @param location
     * @param context
     * @return
     * @throws IOException
     */
    public static String getAreaName(Context context, Location location) throws IOException {
        Address address = getAddress(context, location);

        if(address != null)
            return address.getLocality();
        else
            return null;
    }

    /**
     * Animatedly zooms to the zoom level centered around the input coordinates
     * @param map : the google map
     * @param latitude :
     * @param longitude :
     * @param zoomLevel : between 2.0f and 22.0f
     */
    public static void zoomToCoordinates(GoogleMap map, double latitude, double longitude, float zoomLevel){
        LatLng latLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
//        map.animateCamera();
    }


    public static List<Address> searchAddresses(Context context, String searchStr, int maxNoOfAddresses){
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocationName(
                    searchStr, maxNoOfAddresses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }


    public static List<Address> searchAddresses(Context context, String searchStr){
        return searchAddresses(context, searchStr, DEFAULT_NUMBER_ADDRESSES);
    }


    /**
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static long diffBetweenGeoPts(double lat1, double lon1, double lat2, double lon2){

            double latDiff = deg2radians(lat2 - lat1);  // deg2rad below
            double lonDiff = deg2radians(lon2-lon1);
            double a =
                    Math.sin(latDiff/2) * Math.sin(latDiff/2) +
                            Math.cos(deg2radians(lat1)) * Math.cos(deg2radians(lat2)) *
                                    Math.sin(lonDiff/2) * Math.sin(lonDiff/2)
                    ;
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = EARTH_RADIUS * c; // Distance in km
            return Math.round(d);
    }


    /**
     * searches for the closest Address from the list of Addresses to the firstAddress
     * @param firstAddress
     * @param addressList
     * @return
     */
    public static Address searchClosestAddress(Address firstAddress, List<Address> addressList){
        double lat1 = firstAddress.getLatitude();
        double lon1 = firstAddress.getLongitude();
        long minDistance = DEFAULT_MIN_DISTANCE; //
        int closestAddressIndex = -1;
        for(int i =0; i < addressList.size(); i++){
            Address address = addressList.get(i);
            double lat2 = address.getLatitude();
            double lon2 = address.getLongitude();
            long distance = diffBetweenGeoPts(lat1, lon1, lat2, lon2);
            if(distance < minDistance){
                minDistance = distance;
                closestAddressIndex = i;
            }
        }
        return addressList.get(closestAddressIndex);
    }


    public static Address getAddress(Context context, LatLng latLng){
        try {
            return getAddress(context, latLng.latitude, latLng.longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
