package com.android.khel247;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.khel247.asynctasks.gametasks.FindGamesTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.dialogs.DistancePickerDialogFragment;
import com.android.khel247.dialogs.FormatPickerDialogFragment;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Token;
import com.android.khel247.services.GeoLocation;
import com.android.khel247.utilities.GoogleAPIMethods;
import com.android.khel247.utilities.UtilityMethods;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FindGamesActivity extends ActionBarActivity implements DistancePickerDialogFragment.DistanceChosen,
        FormatPickerDialogFragment.FormatDialogListener{

    private static final String DISTANCE_TEXT = "Distance (kms) : ";
    private static final int DEFAULT_DISTANCE = 2;
    private static final String FORMAT_TEXT = "Format : ";
    private static final String DEFAULT_FORMAT = "All";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Address mCurrentAddress;
    public static final float DEFAULT_ZOOM = 14;
    private int mDistance;
    private String mFormat;
    List<GameData> mGamesList;
    private EditText mDistanceTF;
    HashMap<Marker, GameData> markerDataMap;
    private FindGamesActivity _this = this;
    private Token mAuthToken;
    private EditText mFormatTf;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_games);
        Intent intent = getIntent();
        if(intent != null){
            mAuthToken = (Token)intent.getSerializableExtra(Constants.ARG_TOKEN);
        }else{
            finish();
        }

        mCurrentAddress = getCurrentLocationAddress();
        setUpMapIfNeeded();


        mDistance = DEFAULT_DISTANCE;

        mDistanceTF = (EditText)findViewById(R.id.find_games_distance_tf);
        mDistanceTF.setText(DISTANCE_TEXT + mDistance);
        mDistanceTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDistanceNumberPicker();
            }
        });

        mFormat = DEFAULT_FORMAT;
        mFormatTf = (EditText)findViewById(R.id.find_games_mode_tf);
        mFormatTf.setText(FORMAT_TEXT + DEFAULT_FORMAT);
        mFormatTf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormatPickerDialogFragment fragment = FormatPickerDialogFragment.newInstance(mFormat, true);
                fragment.show(getSupportFragmentManager(), "format picker");
            }
        });

        Button findGamesButton = (Button)findViewById(R.id.find_games_btn);
        findGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findGames();
            }
        });
    }


    /**
     * Displays a dialog that lets the user pick a number for the distance
     */
    private void showDistanceNumberPicker() {
        DistancePickerDialogFragment fragment = DistancePickerDialogFragment.newInstance(mDistance);
        fragment.show(getSupportFragmentManager(), "distance chooser");
    }


    /**
     *
     */
    private void findGames() {
        mMap.clear();
        mLatLng = getScreenCentreCoordinates();
        FindGamesTask task = new FindGamesTask(this, mAuthToken, mLatLng, mDistance, mFormat);
        task.execute();
        try {
            mGamesList = task.get();
            if(mGamesList != null){
                addMarkers();
                // adjusts the maps view based on the distance entered by the user and the coordinates
                // at the centre of the screen
                adjustZoom(mLatLng.latitude, mLatLng.longitude, mDistance);
            }else{
                UtilityMethods.showShortToast(this, "Couldnt find any games.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Uses the fetched list of GameData and sets up markers on the map to represent games
     */
    private List<Marker> addMarkers() {
        markerDataMap = new HashMap<>();
        List<Marker> markers = new ArrayList<>();
        for(GameData data : mGamesList){
            Marker marker = setupMarker(data);
            markers.add(marker);
            markerDataMap.put(marker, data);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final GameData data = markerDataMap.get(marker);
                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle(data.getName()).
                        setMessage("View game details?").
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                navigateToGameActivity(data);
                            }
                        }).
                        setNegativeButton("No", null);
                builder.create().show();
            }
        });
        return markers;
    }


    /**
     * Adjusts the zoom level of the map to show the entire radius of distance specified by the user
     * or finding games
     * @param latitude
     * @param longitude
     * @param distance
     */
    private void adjustZoom(double latitude, double longitude, double distance) {
        GeoLocation pt = GeoLocation.fromDegrees(latitude, longitude);
        GeoLocation[] boundingPts = pt.boundingCoordinates(distance, GeoLocation.EARTH_RADIUS_KM);

        LatLng latLng1 = new LatLng(boundingPts[0].getLatitudeInDegrees(), boundingPts[0].getLongitudeInDegrees());
        LatLng latLng2 = new LatLng(boundingPts[1].getLatitudeInDegrees(), boundingPts[1].getLongitudeInDegrees());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng1);
        builder.include(latLng2);
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }


    /**
     * Navigates to the game activity that displays the game details to the user and also lets him join
     * the game
     * @param data
     */
    private void navigateToGameActivity(GameData data) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOADED_GAME_DATA);
        extras.putSerializable(GameConstants.ARG_GAME_DATA, data);
        extras.putSerializable(Constants.ARG_TOKEN, mAuthToken);
        gameIntent.putExtras(extras);
        startActivity(gameIntent);
    }


    /**
     * Creates and displays the marker using the information in the input GameData object
     * @param data
     */
    private Marker setupMarker(GameData data) {
        double latitude = data.getLocation().getLatitude();
        double longitude = data.getLocation().getLongitude();
        String snippet = createSnippetText(data);
        Marker marker = mMap.addMarker(new MarkerOptions().title(data.getFormat()).snippet(snippet)
                .position(new LatLng(latitude, longitude)));
        marker.showInfoWindow();
        return marker;
    }


    /**
     * Creates the text of the snippet of the info window of the marker
     * @param data
     * @return
     */
    private String createSnippetText(GameData data) {
        String timeStr = UtilityMethods.getTimeStringFromDate(data.getDateTime());
        StringBuilder builder = new StringBuilder();
        builder.append("Time : ");
        builder.append(timeStr);
        builder.append(", Status : ");
        builder.append(data.getStatus());
        return builder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        centerAroundUserLocation();
    }

    /**
     * populates the mCurrentLocation and mCurrentAddress fields
     */
    private Address getCurrentLocationAddress() {
        if(GoogleAPIMethods.playServicesCheck(this)){
            Location currentLocation = GoogleAPIMethods.getLastKnownLocation(this);
            try {
                return GoogleAPIMethods.getAddress(this, currentLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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
                GoogleAPIMethods.zoomToCoordinates(mMap, latitude, longitude, DEFAULT_ZOOM);
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
        }
    }

    /**
     * adjusts the zoom according to the default distance setting
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mLatLng = getScreenCentreCoordinates();
        adjustZoom(mLatLng.latitude, mLatLng.longitude, mDistance);
    }


    /**
     *  Gets the coordinates of the center of the screen
     * @return
     */
    public LatLng getScreenCentreCoordinates(){
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        //  http://stackoverflow.com/questions/11597916/can-android-support-dps-less-than-1dp
        // answer mentions that dp to pixel conversion  = scale * dp * 0.5. Assuming scale = hdpi = 1.5
        // 110 is the combined height in dp of the views under the map
        int height = display.getHeight() - (int)UtilityMethods.pxFromDp(this, 110);
        Point point = new Point(width / 2, height / 2);
        return  mMap.getProjection().fromScreenLocation(point);
    }

    @Override
    public void onDistanceChosen(int chosenValue) {
        mDistance = chosenValue;
        mDistanceTF.setText(DISTANCE_TEXT + mDistance);
    }

    @Override
    public void onFormatChosen(String format) {
        if(format != null){
            mFormat = format;
            mFormatTf.setText(FORMAT_TEXT + mFormat);
        }
    }
}
