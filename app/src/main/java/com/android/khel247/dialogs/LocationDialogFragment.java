package com.android.khel247.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.khel247.GameLocationActivity;
import com.android.khel247.R;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.Location;
import com.android.khel247.utilities.GoogleAPIMethods;

import java.io.IOException;

import static com.android.khel247.utilities.UtilityMethods.isEmptyOrNull;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * Created by Misaal on 11/12/2014.
 */
public class LocationDialogFragment extends DialogFragment {

    private static final String PUBLIC_GAME_MESSAGE = "MUST MARK LOCATION ON MAP";
    private static final String NULL_TEXT = "null";
    private LocationChosen mLocationListener;
    private EditText mLocationNameTF;
    private String name;
    private EditText mLocationDescriptionTF;
    private Location mLocation;
    private boolean isPublic;
    private EditText mLatitudeTF;
    private EditText mLongitudeTF;
    private Address mAddress;


    public static LocationDialogFragment newInstance(boolean isPublic, Location location){
        LocationDialogFragment fragment = new LocationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(GameConstants.ARG_LOCATION, location);
        args.putBoolean(GameConstants.ARG_IS_PUBLIC_GAME, isPublic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLocationListener = (LocationChosen)getTargetFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLocationListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mLocation = (Location)bundle.getSerializable(GameConstants.ARG_LOCATION);
            if(mLocation != null){
                Double lat = mLocation.getLatitude();
                Double lon = mLocation.getLongitude();
                if(lat != null && lon != null){
                    try {
                        mAddress = GoogleAPIMethods.getAddress(getActivity(), lat,
                                lon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            isPublic = bundle.getBoolean(GameConstants.ARG_IS_PUBLIC_GAME);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        View dialogView = getDialogContentView();
        dialogBuilder.setView(dialogView)
                .setTitle("Location")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String description = mLocationDescriptionTF.getText().toString();
                        String name = mLocationNameTF.getText().toString();
                        String latitude = mLatitudeTF.getText().toString();
                        String longitude = mLongitudeTF.getText().toString();
                        if(name != null){
                            Location location = new Location(name, description, latitude, longitude);
                            mLocationListener.onLocationChosen(location);
                        }
                    }

                });

        return dialogBuilder.create();
    }


    private View getDialogContentView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_location_input, null);

        mLocationNameTF = (EditText) view.findViewById(R.id.dialog_location_name_tf);

        mLocationDescriptionTF = (EditText)view.findViewById(R.id.dialog_location_desc_tf);

        mLatitudeTF = (EditText)view.findViewById(R.id.dialog_location_latitude);

        mLongitudeTF = (EditText)view.findViewById(R.id.dialog_location_longitude);

        if(isPublic){ // if the game is public, tell the user that he must mark a location on maps
            TextView messageTextView = (TextView)view.findViewById(R.id.dialog_location_message_text);
            messageTextView.setText(PUBLIC_GAME_MESSAGE);
            messageTextView.setVisibility(View.VISIBLE);
        }

        fillFields(); // if possible use mLocation to fill in the fields


        Button mMapsBtn = (Button) view.findViewById(R.id.dialog_maps_btn);
        mMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsIntent = new Intent(getActivity(), GameLocationActivity.class);
                if(mAddress != null){
                    Bundle extras = new Bundle();
                    extras.putParcelable(Constants.ARG_GAME_LOCATION_ADDRESS, mAddress);
                    mapsIntent.putExtras(extras);
                }
                startActivityForResult(mapsIntent, GameConstants.GAME_LOCATION_REQUEST_CODE);
            }
        });

        return view;
    }


    /**
     *  if mLocation is not null, fill the different text fields with the data from mLocation
     */
    private void fillFields() {
        if(mLocation != null){
            String name = mLocation.getName();
            if(!isEmptyOrNull(name))
                mLocationNameTF.setText(mLocation.getName());

            String description = mLocation.getDescription();
            if(!isEmptyOrNull(description))
                mLocationDescriptionTF.setText(mLocation.getDescription());

            String latStr = String.valueOf(mLocation.getLatitude());
            if(!isEmptyOrNull(latStr) && !latStr.equals(NULL_TEXT))
                mLatitudeTF.setText(latStr);

            String lonStr = String.valueOf(mLocation.getLongitude());
            if(!isEmptyOrNull(lonStr)&& !latStr.equals(NULL_TEXT))
                mLongitudeTF.setText(String.valueOf(mLocation.getLongitude()));
        }
    }


    /** handle data sent back from the GameLocationActivity which lets the user mark a location to
     * play in
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GameConstants.GAME_LOCATION_REQUEST_CODE){
            if(data != null){
                useResultLocationData(data);
            }
        }
    }


    /** Uses the data returned by the activity. Displays latitude and longitude values, and checks
     * if the game name needs to be filled
     *
     * @param intent
     */
    private void useResultLocationData(Intent intent) {
        mAddress = intent.getExtras().getParcelable(Constants.ARG_GAME_LOCATION_ADDRESS);

        if(mAddress == null){
            showShortToast(getActivity(), "Couldn't get address");
            return;
        }

        String latitudeStr = String.valueOf(mAddress.getLatitude());
        if(!isEmptyOrNull(latitudeStr) && !latitudeStr.equals(NULL_TEXT)){
            mLatitudeTF.setText(latitudeStr);
        }else return;

        String longitudeStr = String.valueOf(mAddress.getLongitude());
        if(!isEmptyOrNull(longitudeStr) && !latitudeStr.equals(NULL_TEXT)){
            mLongitudeTF.setText(longitudeStr);
        }else return;

        String potentialNameText = getTextFromAddress(mAddress);
        if(isEmptyOrNull(potentialNameText)){
            return;
        }
        String nameText = mLocationNameTF.getText().toString();
        if(nameText.equals(potentialNameText)){
            return;
        }
        if(nameText.length() > 0 ){
            Dialog dialog = createYesNoDialog(potentialNameText);
            dialog.show();
        }else{
            mLocationNameTF.setText(potentialNameText);
        }
    }


    /** tries to fetch a name for the location from the address specified in the maps
     *
     * @param address
     * @return
     */
    private String getTextFromAddress(Address address) {
        if(!isEmptyOrNull(address.getThoroughfare())){
            return address.getThoroughfare();
        }else if(!isEmptyOrNull(address.getAddressLine(0))){
            return address.getAddressLine(0);
        }else if(!isEmptyOrNull(address.getPremises())){
            return address.getPremises();
        }else if(!isEmptyOrNull(address.getLocality())){
            return address.getLocality();
        }else if(!isEmptyOrNull(address.getFeatureName())){
            return address.getFeatureName();
        }else
            return "";
    }


    /**
     * Shows an alert dialog that asks the user if it should use the fetched location name as the game
     * name, sets the name if the user chooses YES.
     * @param text
     * @return
     */
    private Dialog createYesNoDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("Use Map Location information as Name?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLocationNameTF.setText(text);
                    }
                }).setNegativeButton("No", null);
        return builder.create();
    }


    /**
     * An interface that lets the dialogfragment send data back to its calling fragment
     */
    public interface LocationChosen{

        public void onLocationChosen(Location location);
    }

}
