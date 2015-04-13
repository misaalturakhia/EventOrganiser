package com.android.khel247.fragments;

/**
 * Created by Misaal on 15/12/2014.
 */

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.khel247.R;
import com.android.khel247.RegisterActivity;
import com.android.khel247.asynctasks.membertasks.RegisterTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.dialogs.CountryPickerDialogFragment;
import com.android.khel247.dialogs.PositionPickerDialogFragment;
import com.android.khel247.model.RegisterCredentials;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;

import java.io.IOException;

import static com.android.khel247.utilities.GoogleAPIMethods.getBestLocationProvider;
import static com.android.khel247.utilities.GoogleAPIMethods.getCityName;
import static com.android.khel247.utilities.GoogleAPIMethods.getCountryName;
import static com.android.khel247.utilities.GoogleAPIMethods.getLastKnownLocation;
import static com.android.khel247.utilities.GoogleAPIMethods.getLocationManager;
import static com.android.khel247.utilities.UtilityMethods.formatTextInput;
import static com.android.khel247.utilities.UtilityMethods.isEmptyOrNull;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;
import static com.android.khel247.utilities.GoogleAPIMethods.playServicesCheck;


/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteProfileFragment extends Fragment implements
        PositionPickerDialogFragment.PositionDialogListener,
        CountryPickerDialogFragment.CountryDialogListener{

    private static final String LOG_TAG = CompleteProfileFragment.class.getSimpleName();
    private String username;
    private String email;
    private String password;
    private EditText mFirstNameTF;
    private EditText mLastNameTF;
    private EditText mPositionTF;
    private EditText mAreaTF;
    private EditText mCityTF;
    private Button registerBtn;
    private String country;
    private String city;
    private String firstName;
    private String lastName;
    private String position;
    private String area;
    private Fragment _this = this;
    private EditText mCountryTF;

    public CompleteProfileFragment() {
    }


    /**
     * A static method that let's you create a CompleteProfileFragment instance with the input parameters
     * @param username
     * @param emailAddress
     * @param password
     * @return
     */
    public static CompleteProfileFragment newInstance(String username, String emailAddress, String password){
        CompleteProfileFragment fragment = new CompleteProfileFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_USERNAME, username);
        args.putString(Constants.ARG_EMAIL, emailAddress);
        args.putString(Constants.ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null){
            username = args.getString(Constants.ARG_USERNAME);
            email = args.getString(Constants.ARG_EMAIL);
            password = args.getString(Constants.ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        // get the different view fields
        getViewsComponents(rootView);

        mPositionTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPositionPicker();
            }
        });
        mPositionTF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus)
                    showPositionPicker();
            }
        });

        mCountryTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryPicker();
            }
        });

        mCountryTF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus)showCountryPicker();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAction();
            }
        });

        try {
            populateLocationDetails();
        } catch (IOException e) {
            Log.v(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return rootView;
    }


    /**
     * Tries to retrieve the user's location using wifi or gps and populates the required fields
     * so that the user doesn't have to choose if possible
     */
    private void populateLocationDetails() throws IOException {
        if(!playServicesCheck(getActivity())){ // if play services check returns false, do nothing
            return;
        }

        LocationManager manager = getLocationManager(getActivity());
        String provider = getBestLocationProvider(getActivity());
        Location location = getLastKnownLocation(manager);
        if(location != null){
            fillLocationFields(location);
        }else{
            manager.requestLocationUpdates(provider, 2000,2000, new LocationListener() {
                LocationListener _this = this;
                @Override
                public void onLocationChanged(Location location) {
                    if(location != null){
                        try {
                            fillLocationFields(location);
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                        _this = null;
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            });
        }
    }


    /**
     * Uses the input location to populate the area, city and country fields in the form
     * @param location
     */
    private void fillLocationFields(Location location) throws IOException {
        String city = getCityName(location, getActivity());
        city = formatTextInput(city);
        mCityTF.setText(city);

        String countryName = getCountryName(location, getActivity());
        countryName = formatTextInput(countryName);
        mCountryTF.setText(countryName);

    }


    /**
     * Show the dialog that lets the user pick the country he lives in
     */
    private void showCountryPicker() {
        CountryPickerDialogFragment fragment = CountryPickerDialogFragment
                .newInstance(mCountryTF.getText().toString());
        fragment.setTargetFragment(_this, 0);
        fragment.show(getFragmentManager(), "getCountry");
    }


    /**
     * Shows the position picker dialog
     */
    private void showPositionPicker() {
        PositionPickerDialogFragment fragment = PositionPickerDialogFragment
                .newInstance(mPositionTF.getText().toString());
        fragment.setTargetFragment(_this, 0);
        fragment.show(getFragmentManager(), "getPosition");
    }

    /**
     * Get the instances of the view widgets and store them.
     * @param rootView
     */
    private void getViewsComponents(View rootView) {
        mFirstNameTF = (EditText)rootView.findViewById(R.id.fill_profile_firstname_tf);
        mLastNameTF = (EditText)rootView.findViewById(R.id.fill_profile_lastname_tf);
        mPositionTF = (EditText) rootView.findViewById(R.id.fill_profile_position_tf);
        mAreaTF = (EditText)rootView.findViewById(R.id.fill_profile_area_tf);
        mCityTF = (EditText)rootView.findViewById(R.id.fill_profile_city_tf);
        mCountryTF = (EditText)rootView.findViewById(R.id.fill_profile_country_tf);
        registerBtn = (Button)rootView.findViewById(R.id.fill_profile_register_btn);
    }

    /**
     * Register the user using the RegisterTask as the async task to communicate with the endpoint
     * @param rootView
     */
    private void registerAction(){

        firstName = mFirstNameTF.getText().toString();
        // capitalize first letter and make the others lowercase for firstname and lastname
        lastName = mLastNameTF.getText().toString();
        position = mPositionTF.getText().toString();
        area = mAreaTF.getText().toString();
        city = mCityTF.getText().toString();
        country = mCountryTF.getText().toString();

        if(isValidInput()){
            RegisterCredentials credentials = null;
            // create object that holds the input needed to register
            credentials = new RegisterCredentials(username, email, password, firstName, lastName,
                    position, area, city, country);

            RegisterTask task = new RegisterTask((RegisterActivity)getActivity(), credentials);
            task.execute();
        }
    }

    private boolean isValidInput() {
        if(isEmptyOrNull(firstName)){
            showShortToast(getActivity(), MessageService.ENTER_FIRST_NAME);
            return false;
        }else if(isEmptyOrNull(lastName)){
            showShortToast(getActivity(), MessageService.ENTER_LAST_NAME);
            return false;
        }else if(isEmptyOrNull(city)){
            showShortToast(getActivity(), MessageService.ENTER_CITY);
            return false;
        }else if(isEmptyOrNull(country)){
            showShortToast(getActivity(), MessageService.CHOOSE_COUNTRY);
            return false;
        }

        firstName = formatTextInput(firstName);
        lastName = formatTextInput(lastName);
        city = formatTextInput(city);
        if(!UtilityMethods.isEmptyOrNull(area))
            area = formatTextInput(area);

        return true;
    }

    @Override
    public void onPositionChosen(String positionValue) {
        mPositionTF.setText(positionValue);
    }

    @Override
    public void onCountryChosen(String countryValue) {
        mCountryTF.setText(countryValue);
    }

}

