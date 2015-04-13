package com.android.khel247;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.khel247.constants.Constants;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.GoogleAPIMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static com.android.khel247.utilities.GoogleAPIMethods.getAddress;
import static com.android.khel247.utilities.GoogleAPIMethods.searchClosestAddress;
import static com.android.khel247.utilities.GoogleAPIMethods.zoomToCoordinates;
import static com.android.khel247.utilities.UtilityMethods.makeLongToast;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

public class GameLocationActivity extends ActionBarActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mGameLocationMarker;
    public static final float DEFAULT_ZOOM = 14;
    private SearchView mSearchView;
    private Context context = this;
    private Address mCurrentAddress = null;
    // a flag that holds if the address entered is from a previous time and needs to be marked
    private boolean isMarkedFromBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_location);
        Intent intent = getIntent();
        mCurrentAddress = intent.getParcelableExtra(Constants.ARG_GAME_LOCATION_ADDRESS);
        if(mCurrentAddress != null){
            isMarkedFromBefore = true;
        }else{
            populateCurrentLocation();
        }


        setUpMapIfNeeded();

        Button doneBtn = (Button)findViewById(R.id.game_location_done_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneAction();
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.game_location_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAction();
            }
        });
    }

    /**
     * Cancels the activity and returns no data
     */
    private void cancelAction() {
        setResult(Constants.GAME_LOCATION_CANCEL_CODE);
        finish();
    }


    /**
     * Returns the address pointed at by the marker and finishes the activity
     */
    private void doneAction() {
        if(mGameLocationMarker == null){
            showShortToast(context, MessageService.MARK_LOCATION);
            return;
        }
        LatLng latLng = mGameLocationMarker.getPosition();
        Address address = getAddress(this, latLng);
        if(address == null){
            showShortToast(this, "Couldn't find Address from input coordinates.");
            return;
        }
        // set the latitude and longitude of the marker because when getting address by coordinates,
        // it finds the nearest address and doesn't always use the exact same coordinates as pointed
        address.setLatitude(latLng.latitude);
        address.setLongitude(latLng.longitude);

        // create an intent that will hold the result data
        Intent resultIntent = new Intent();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_GAME_LOCATION_ADDRESS, address);
        resultIntent.putExtras(args);
        // return result data in the form of an intent with the success code
        setResult(Constants.GAME_LOCATION_SUCCESS_CODE, resultIntent);
        // finish the activity
        finish();
    }


    /**
     * populates the mCurrentLocation and mCurrentAddress fields
     */
    private void populateCurrentLocation() {
        if(GoogleAPIMethods.playServicesCheck(this)){
            Location currentLocation = GoogleAPIMethods.getLastKnownLocation(this);
            try {
                mCurrentAddress = getAddress(context, currentLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        centerAroundUserLocation();

    }


    /**
     * Centers the map around the user's current location with default zoom level
     */
    private void centerAroundUserLocation() {
        if(mCurrentAddress == null){
            return;
        }
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                double latitude = mCurrentAddress.getLatitude();
                double longitude = mCurrentAddress.getLongitude();
                zoomToCoordinates(mMap, latitude, longitude, DEFAULT_ZOOM);
                if(isMarkedFromBefore){
                    addMarkerToMap(latitude, longitude);
                }
                mMap.setOnCameraChangeListener(null);
            }
        });
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerToMap(latLng);
            }
        });
    }

    private void addMarkerToMap(LatLng latLng){
        mMap.clear();
        mGameLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Play here").draggable(true));
        mGameLocationMarker.showInfoWindow();
    }


    private void addMarkerToMap(double latitude, double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        addMarkerToMap(latLng);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchView);

        return true;
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setQueryHint("Search Locations");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String locationStr) {
                searchInputLocation(locationStr);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    /**
     * Use the search string provided by the user, retrieve matching addresses, choose the address
     * closest to the users location
     * @param locationStr: the user's entered search string
     */
    private void searchInputLocation(String locationStr) {
        // get addresslist using the searchstring. maxinmum 5 addresses are returned
        List<Address> addressList = GoogleAPIMethods.searchAddresses(this, locationStr);
        Address searchedAddress = null;
        if(addressList.size() > 1){ // if there are multiple addresses
            if(mCurrentAddress!= null){ // if we have the current address
                // search for the closest and return
                searchedAddress = searchClosestAddress(mCurrentAddress, addressList);
                // center and zoom the map to that address's geopt
            }else{
                // if we don't have an address to compare to, just return the first address
                searchedAddress = addressList.get(0);
            }
        }else if(addressList.size() == 1){ // only one address returned
            searchedAddress = addressList.get(0);
            // center and zoom the map to the address
        }else{
            makeLongToast(this, MessageService.COULDNT_FIND_LOCATION);
            return;
        }
        zoomToCoordinates(mMap, searchedAddress.getLatitude(), searchedAddress.getLongitude(),
                DEFAULT_ZOOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
