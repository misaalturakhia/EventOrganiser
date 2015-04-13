package com.android.khel247;

import android.content.Intent;
import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.Location;
import com.android.khel247.utilities.GoogleAPIMethods;
import com.android.khel247.utilities.UtilityMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import static com.android.khel247.utilities.GoogleAPIMethods.getAddress;
import static com.android.khel247.utilities.GoogleAPIMethods.zoomToCoordinates;

public class ViewLocationActivity extends FragmentActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mLocation;
    private Double mLatitude;
    private Double mLongitude;
    boolean isMapNeeded = false;
    public static final float DEFAULT_ZOOM = 14;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        Intent intent = getIntent();
        if(intent != null){
            mLocation = (Location)intent.getSerializableExtra(GameConstants.ARG_LOCATION);
        }

        if(intent == null || mLocation == null){
            finish();
        }

        String locationName = mLocation.getName();
        EditText nameText = (EditText)findViewById(R.id.view_location_name_tf);
        nameText.setText(locationName);

        String description = mLocation.getDescription();
        final TextView descTF = (TextView) findViewById(R.id.view_location_desc_tv);
        if(UtilityMethods.isEmptyOrNull(description)){
            descTF.setVisibility(View.GONE);
        }else{
            descTF.setText(description);
        }

        mLatitude = mLocation.getLatitude();
        mLongitude = mLocation.getLongitude();
        isMapNeeded = mLatitude != null && mLongitude != null;
        if(isMapNeeded)
            setUpMapIfNeeded();
        else{
            mMapFragment = ((SupportMapFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.view_location_map));
            getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMapNeeded){
            setUpMapIfNeeded();
            centerAroundLocation();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.view_location_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(mLocation.getName()));
    }


    /**
     * Centers the map around the user's current location with default zoom level
     */
    private void centerAroundLocation() {
        if(mLocation == null){
            return;
        }
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                zoomToCoordinates(mMap, mLatitude, mLongitude, DEFAULT_ZOOM);
                mMap.setOnCameraChangeListener(null);
            }
        });
    }
}

