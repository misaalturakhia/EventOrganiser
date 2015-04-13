package com.android.khel247.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.khel247.R;
import com.android.khel247.adapters.GameInvitesAdapter;
import com.android.khel247.asynctasks.gametasks.CreateGameTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.dialogs.DurationPickerDialogFragment;
import com.android.khel247.dialogs.FormatPickerDialogFragment;
import com.android.khel247.dialogs.InvitePeopleDialogFragment;
import com.android.khel247.dialogs.LocationDialogFragment;
import com.android.khel247.dialogs.NotifyHoursDialogFragment;
import com.android.khel247.dialogs.NumberPickerDialogFragment;
import com.android.khel247.listeners.DatePickerListener;
import com.android.khel247.listeners.TimePickerListener;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Location;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.services.DefaultNotifyCalculator;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.GameUtilities;
import com.android.khel247.utilities.UtilityMethods;
import com.android.khel247.utilities.Validator;

import static com.android.khel247.utilities.GameUtilities.getMaxAllowedInvites;
import static com.android.khel247.utilities.GameUtilities.getDefaultMaxPlayersFromTotal;
import static com.android.khel247.utilities.GameUtilities.getMinAllowedInvites;
import static com.android.khel247.utilities.GameUtilities.getDefaultMinPlayersFromTotal;
import static com.android.khel247.utilities.GameUtilities.getMinRecommendedInvites;
import static com.android.khel247.utilities.GameUtilities.getTotalPlayersFromFormat;
import static com.android.khel247.utilities.UtilityMethods.getDateFromArray;
import static com.android.khel247.utilities.UtilityMethods.getUnderLinedText;
import static com.android.khel247.utilities.UtilityMethods.isEmptyOrNull;
import static com.android.khel247.utilities.UtilityMethods.makeLongToast;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Let's the user create a game
 */
public class CreateGameFragment extends Fragment implements InvitePeopleDialogFragment.InvitePeopleListener,
        LocationDialogFragment.LocationChosen, FormatPickerDialogFragment.FormatDialogListener,
        DurationPickerDialogFragment.DurationDialogListener, NotifyHoursDialogFragment.NotifyHoursDialogListener,
        DatePickerListener.DateChosenListener, TimePickerListener.TimeChosenListener,
        NumberPickerDialogFragment.NumberChosen{

    private static final String LOG_TAG = CreateGameFragment.class.getSimpleName();
    private static final String MIN_PLAYERS = "Min Players: ";
    private static final String MAX_PLAYERS = "Max Players: ";
    private static final String BEFORE_GAME = " before the game";
    private static final String NOTIFY = "Notify: ";

    private Token authToken;
    private EditText mNameTF;
    private EditText mFormatTF;
    private EditText mDurationTF;
    private EditText mDateTF;
    private EditText mTimeTF;
    int[] dateTimeArray = {-1,-1,-1,-1,-1};
    private Date mDateTime;
    private EditText mInviteTF;
    private ListView mInvitedListView;
    private Button mCreateGameBtn;
    private ArrayList<String> mInviteList;
    private boolean isPublic = false;
    private String name;
    private String format;
    private int duration;
    private CreateGameFragment _this = this;
    private GameInvitesAdapter mAdapter;
    private EditText mLocationTF;
    private Location mLocation;
    private EditText mMinPlayersTF;
    private EditText mMaxPlayersTF;
    private EditText mNotifyTF;
    private CheckBox mLocatioNameCheckBox;
    private int mMinPlayers;
    private int mMaxPlayers;
    private int[] notifyHoursArray;
    private ArrayList<String> mChosenNotifyItems;
    private TextView mNotifyAddedText;
    private String mNamePreviousText;
    private boolean isGameToday = false;
    private CheckBox mOrganiserNotPlayingCheckBox;
    private boolean isOrganiserPlaying = true;
    private RadioGroup mRadioGroup;


    /**
     * Use this factory method to create a new instance of
     * this fragment and pass the user's authentication token as the parameter
     *
     * @param token user's authentication token.
     * @return A new instance of fragment CreateGameFragment.
     */
    public static CreateGameFragment newInstance(Token token) {
        CreateGameFragment fragment = new CreateGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor
     */
    public CreateGameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // get authToken
            authToken = (Token)getArguments().getSerializable(Constants.ARG_TOKEN);
        }
        mInviteList = new ArrayList<String>();
        mAdapter = new GameInvitesAdapter(getActivity(), new ArrayList<PeopleItem>(), mInviteList);

        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                int count = mAdapter.getCount();
                if(count == 0){
                    mInviteTF.setText("");
                }else{
                    mInviteTF.setText("Invited: "+String.valueOf(mAdapter.getCount()));

                }
            }
        });

        mChosenNotifyItems = new ArrayList<>();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_create_game, container, false);

        // the text field that takes as input the name of the game
        mNameTF = (EditText)rootView.findViewById(R.id.create_game_name_tf);

        // the radio button that signifies if the game type is public
        mRadioGroup = (RadioGroup)rootView.findViewById(R.id.create_game_mode_radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                switch(buttonId){
                    case R.id.create_game_public_radio:
                        isPublic = true;
                        return;
                    case R.id.create_game_private_radio:
                        isPublic = false;
                        return;
                }
            }
        });

        // the format of the game
        mFormatTF = (EditText)rootView.findViewById(R.id.create_game_format_tf);
        mFormatTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormatPickerDialogFragment fragment = FormatPickerDialogFragment
                        .newInstance(mFormatTF.getText().toString(), false);
                fragment.setTargetFragment(_this, 0);
                fragment.show(getFragmentManager(), "getFormat");
            }
        });


        // the duration of the game
        mDurationTF = (EditText)rootView.findViewById(R.id.create_game_duration_tf);
        mDurationTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DurationPickerDialogFragment fragment = DurationPickerDialogFragment
                        .newInstance(mDurationTF.getText().toString());
                fragment.setTargetFragment(_this, 0);
                fragment.show(getFragmentManager(), "getDuration");
            }
        });

        // date
        mDateTF = (EditText)rootView.findViewById(R.id.create_game_date_tf);
        mDateTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                Dialog dateDialog = createDatePickerDialog(day, month, year);
                dateDialog.show();
            }
        });

        // time
        mTimeTF = (EditText)rootView.findViewById(R.id.create_game_time_tf);
        mTimeTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                //add the 1 because the organised game cannot be less than one hour from now
                int hour = c.get(Calendar.HOUR_OF_DAY) + 1;
                int minute = c.get(Calendar.MINUTE);
                minute = CustomTimePickerDialog.getRoundedMinute(minute);
                if(minute == 0){
                    hour ++;
                }
                Dialog timeDialog =  createTimePickerDialog(minute, hour);
                timeDialog.show();
            }
        });

        mInviteTF = (EditText)rootView.findViewById(R.id.create_game_invite_tf);
        mInviteTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InvitePeopleDialogFragment inviteDialog = InvitePeopleDialogFragment
                        .newInstance(authToken, mInviteList, false);
                inviteDialog.setTargetFragment(_this, 0);
                inviteDialog.show(getFragmentManager(), "getInvites");
            }
        });

        mInvitedListView = (ListView)rootView.findViewById(R.id.create_game_invited_list_view);
        mInvitedListView.setAdapter(mAdapter);

        mLocationTF = (EditText)rootView.findViewById(R.id.create_game_location_tf);
        mLocationTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialogFragment fragment = LocationDialogFragment.newInstance(isPublic, mLocation);
                fragment.setTargetFragment(_this, 0);
                fragment.show(getFragmentManager(), "");
            }
        });

        final LinearLayout advancedOptionsLayout = (LinearLayout)rootView
                .findViewById(R.id.create_game_advanced_opts_layout);

        TextView advancedOptionsText = (TextView) rootView.findViewById(R.id.create_game_advanced_options);
        // set underlined text to make the textview look like a link
        String text = getActivity().getString(R.string.game_advanced_options);
        SpannableString str = getUnderLinedText(text);
        advancedOptionsText.setText(str);
        advancedOptionsText.setOnClickListener(new View.OnClickListener() {
            int clickCount = 0; // counts the number of clicks made on the field
            @Override
            public void onClick(View view) {
                clickCount++;
                if(clickCount % 2 == 0) // click count is even
                    advancedOptionsLayout.setVisibility(View.GONE);
                else // click count is odd
                    advancedOptionsLayout.setVisibility(View.VISIBLE);
            }
        });


        //Advanced Options view widgets
        mMinPlayersTF = (EditText)rootView.findViewById(R.id.create_game_min_people);
        mMinPlayersTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String format = mFormatTF.getText().toString();
                if(isEmptyOrNull(format)){
                    showShortToast(getActivity(), "Choose Format of Game First.");
                    return;
                }
                int minPlayersAllowed = GameUtilities.getMinAllowedInvites(format);
                int maxPlayersAllowed = GameUtilities.getMaxAllowedInvites(format);

                NumberPickerDialogFragment fragment = NumberPickerDialogFragment.newInstance(true,
                        minPlayersAllowed,maxPlayersAllowed);
                fragment.show(getFragmentManager(), "MinNumberPicker");
                fragment.setTargetFragment(_this, 0);
            }
        });

        mMaxPlayersTF = (EditText)rootView.findViewById(R.id.create_game_max_people);
        mMaxPlayersTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String format = mFormatTF.getText().toString();
                if(isEmptyOrNull(format)){
                    showShortToast(getActivity(), "Choose Format of Game First.");
                    return;
                }
                int minPlayersAllowed = GameUtilities.getMinAllowedInvites(format);
                int maxPlayersAllowed = GameUtilities.getMaxAllowedInvites(format);

                NumberPickerDialogFragment fragment = NumberPickerDialogFragment.newInstance(false,
                        minPlayersAllowed, maxPlayersAllowed);
                fragment.show(getFragmentManager(), "MaxNumberPicker");
                fragment.setTargetFragment(_this, 0);
            }
        });


        mNotifyTF = (EditText)rootView.findViewById(R.id.create_game_notify_hours_before);

        mNotifyTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // checks if the date and time have been chosen
                if(dateTimeArray[0] != -1 && dateTimeArray[3] != -1){
                    if(hoursTillGame() > 1){
                        NotifyHoursDialogFragment fragment = NotifyHoursDialogFragment.newInstance
                                (dateTimeArray, mChosenNotifyItems);
                        fragment.setTargetFragment(_this, 0);
                        fragment.show(getFragmentManager(), "getNotifyHours");
                    }else{
                        showShortToast(getActivity(), "No notify values can be set for a game this close");
                    }
                }else{
                    showShortToast(getActivity(), "Please choose Date and Time first.");
                }
            }
        });
        mNotifyAddedText = (TextView)rootView.findViewById(R.id.create_game_notify_added_text);

        mLocatioNameCheckBox = (CheckBox)rootView.findViewById(R.id.create_game_use_location_as_name);
        mLocatioNameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                locationCheckBoxAction(isChecked);
            }
        });

        mOrganiserNotPlayingCheckBox = (CheckBox) rootView.findViewById(R.id.create_game_organiser_not_playing);
        mOrganiserNotPlayingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isOrganiserPlaying = !isChecked;
            }
        });

        mCreateGameBtn = (Button)rootView.findViewById(R.id.create_game_btn);
        mCreateGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValidInput()){
                    GameData data = createGameData();
                    CreateGameTask task = new CreateGameTask(getActivity(), authToken, data);
                    task.execute();
                }
            }
        });

        return rootView;
    }


    /**
     * Gets a list of string values that denote the default notify values and so can be set as the
     * default choices in the NotifyHoursDialogFragment .
     * @param notifyHoursArray
     * @return
     */
    private ArrayList<String> getDefaultNotifyItems(int[] notifyHoursArray) {
        ArrayList<String> items = new ArrayList<>();
        String[] array = getActivity().getResources().getStringArray(R.array.game_notify_before_time_long);
        for(String str : array){
            for(int i : notifyHoursArray){
                if(str.contains(String.valueOf(i)+" ") && str.indexOf(i) == 0){
                    items.add(str);
                }
            }
        }
        return items;
    }

    private int hoursTillGame() {
        int i = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dateTimeArray[i++]);
        cal.set(Calendar.MONTH, dateTimeArray[i++]);
        cal.set(Calendar.DAY_OF_MONTH, dateTimeArray[i++]);
        cal.set(Calendar.HOUR_OF_DAY, dateTimeArray[i++]);
        cal.set(Calendar.MINUTE, dateTimeArray[i++]);
        return (int)UtilityMethods.getDateDifferenceInHours(new Date(), cal.getTime());
    }

    /** Sets the  name of the location to the name of the game if checked, if not, sets the original
     * name of the game
     *
     * @param isChecked
     */
    private void locationCheckBoxAction(boolean isChecked) {
        if(isChecked){
             setLocationNameToNameField();
        }else{
            removeLocationNameFromNameField();
        }
    }


    /**
     * sets the location name to the name text field
     */
    private void setLocationNameToNameField() {
        if(mLocation != null && !isEmptyOrNull(mLocation.getName())){
            mNamePreviousText = mNameTF.getText().toString();
            mNameTF.setText(mLocation.getName());
        }
    }


    /**
     * removes the location name put on the name text field and puts the old name value back
     */
    private void removeLocationNameFromNameField() {
        if(mNamePreviousText != null)
            mNameTF.setText(mNamePreviousText);
        else
            mNameTF.setText("");
    }


    /**
     * Create the dialog that will let the user choose the time of the game
     * @param minute
     * @param hour
     * @return
     */
    private Dialog createTimePickerDialog(final int currentMinute, final int currentHour) {
        Dialog timeDialog = new CustomTimePickerDialog(getActivity(),new TimePickerListener(getActivity(),
                this, isGameToday),currentHour, currentMinute, true);
        return timeDialog;
    }


    /**
     *  Create the dialog that will let the user choose the date of the game
     * @param day
     * @param month
     * @param year
     * @return
     */
    private Dialog createDatePickerDialog(final int currentDay, final int currentMonth, final int currentYear) {
        Dialog dateDialog = new DatePickerDialog(getActivity(), new DatePickerListener(getActivity(), this), currentYear, currentMonth, currentDay);
        return dateDialog;
    }


    /**
     *
     * @param array
     * @param index
     * @param max
     * @return
     */
    private boolean isValidValues(int[] array, int index, int max){
        if(index >= max){
            return true;
        }
        int a = array[index];
        int b = array[index + 1];
        if(a < b){
            return false;
        }else if(a > b){
            return true;
        }else{
            index = index + 2;
            return isValidValues(array, index, max);
        }
    }


    /**
     * Uses the user input to create a GameData Object
     * @return
     */
    private GameData createGameData() {
        GameData gameData = new GameData(authToken.getUsername(), name, format, duration, mDateTime,
                mLocation, mInviteList, isPublic, mMinPlayers, mMaxPlayers, notifyHoursArray,
                GameConstants.GAME_STATUS_CREATED);
        // add the organiser to the mInviteList if the isOrganiserPlaying flag is true
        if(isOrganiserPlaying){
            List<String> comingList = new ArrayList<>();
            String organiserUsername = authToken.getUsername();
            comingList.add(organiserUsername);
            gameData.setYesMembers(comingList);
        }
        return gameData;
    }

    /**
     * Checks if the input from the user is valid and displays a toast if it isnt
     * @return: true - if valid
     */
    private boolean isValidInput() {
        name = mNameTF.getText().toString();
        if(isEmptyOrNull(name)){
            showShortToast(getActivity(), MessageService.ENTER_GAME_NAME);
            return false;
        }

        format = mFormatTF.getText().toString();
        if(isEmptyOrNull(format) || !Validator.isFormatValid(format))
            return false;

        if(mDateTF.getText().toString().isEmpty()){
            showShortToast(getActivity(), MessageService.ENTER_DATE);
            return false;

        }if(mTimeTF.getText().toString().isEmpty()){
            showShortToast(getActivity(), MessageService.ENTER_TIME);
            return false;
        }if(!isValidDateTime()){
            makeLongToast(getActivity(), MessageService.INVALID_DATE_CHOSEN);
            return false;
        }
        // create datetime object after date and tis validated
        mDateTime = getDateFromArray(dateTimeArray);

        duration = GameUtilities.getDuration(mDurationTF.getText().toString());

        if(duration < 30){
            showShortToast(getActivity(), MessageService.CHOOSE_DURATION);
            return false;
        }

        if(!isValidLocation()){
            return false;
        }

        if(!isValidInviteList())
            return false;

        if(mMinPlayers > mMaxPlayers){
            showShortToast(getActivity(), "Min players cannot be more than max");
            return false;
        }

        if(notifyHoursArray == null){ //generate the default notify hours array
            DefaultNotifyCalculator service = new DefaultNotifyCalculator(mDateTime, new Date());
            notifyHoursArray = service.getDefaultNotifyArray();
        }

        return true;
    }

    /**
     * Validates the user's entered location
     * @return
     */
    private boolean isValidLocation() {
        if(mLocation == null){
            showShortToast(getActivity(), MessageService.CHOOSE_LOCATION);
            return false;
        }
        if(isEmptyOrNull(mLocation.getName())){
            showShortToast(getActivity(), MessageService.ENTER_LOCATION_NAME);
            return false;
        }

        if(!isValidCoordinates()){
            return false;
        }
        return true;
    }


    /**
     * Validates the coordinates entered by the user. If the game is public and the user has not chosen
     * a location on the map, a toast is displayed asking him to point to the location on a map
     * @return : boolean
     */
    private boolean isValidCoordinates() {
        Double lat = mLocation.getLatitude();
        Double lon = mLocation.getLongitude();

        // if user does not enter coordinates
        if(lat == null || lon == null ){
            if(isPublic){ // public games must have location marked by coordinates
                showShortToast(getActivity(), MessageService.PUBLIC_MODE_ENTER_LOCATION);
                return false;
            }
        }else{
            // coordinate values must be between -90 and 90
            if(lat > GameConstants.GEOPT_UPPER_LIMIT || lat < GameConstants.GEOPT_LOWER_LIMIT){
                showShortToast(getActivity(), MessageService.INVALID_LATITUDE);
                return false;
            }else if(lon > GameConstants.GEOPT_UPPER_LIMIT || lon < GameConstants.GEOPT_LOWER_LIMIT){
                showShortToast(getActivity(), MessageService.INVALID_LONGITUDE);
                return false;
            }
        }
        return true;
    }


    /**
     * Compares the chosen date time with the current date time and makes sure the date time is not
     * in the past and that the game organised is at least 1 hour from current time
     * @return
     */
    private boolean isValidDateTime() {
        Calendar cal = Calendar.getInstance();
        int i = -1;
        int[] compareDateTimeArray = {dateTimeArray[++i], cal.get(Calendar.YEAR), dateTimeArray[++i],
        cal.get(Calendar.MONTH), dateTimeArray[++i], cal.get(Calendar.DAY_OF_MONTH), dateTimeArray[++i],
        cal.get(Calendar.HOUR_OF_DAY) + GameConstants.MIN_TIME_FOR_GAME, dateTimeArray[++i], cal.get(Calendar.MINUTE)};

        return isValidValues(compareDateTimeArray, 0, compareDateTimeArray.length);
    }

    /**
     *
     * @return
     */
    private boolean isValidInviteList() {
        int noOfInvites = mInviteList.size();
        if(isOrganiserPlaying)
            noOfInvites++; // add the organiser
        int totalPlayers = getTotalPlayersFromFormat(format);
        int minPlayers = getMinAllowedInvites(totalPlayers);
        int maxPlayers = getMaxAllowedInvites(totalPlayers);
        int minRecommendedInvites = getMinRecommendedInvites(totalPlayers);

        String inviteText = null;
        if(!isPublic){ // only for private games, in public games, you can choose to invite no one
            if (noOfInvites < 1) { // handle no invites
                inviteText = MessageService.INVITE_PEOPLE + ". For a "+format + " game, it is " +
                        "recommended that you invite at least "+minRecommendedInvites+ " players.";
                makeLongToast(getActivity(), inviteText);
                return false;
            }else if(noOfInvites < minPlayers){ // if the number of invites is less than min players needed
                inviteText = "For a "+format+" game, you have to invite a minimum of "+minPlayers + " players";
                showShortToast(getActivity(), inviteText);
                return false;
            }
        }

        // control on number of people invited, to prevent spamming of events
        if(noOfInvites > maxPlayers){
            inviteText = "Sorry, but you cannot invite more than "+maxPlayers+" to a "+format+
                    " game.";
            showShortToast(getActivity(), inviteText);
            return false;
        }
        return true;
    }

    /**
     * Creates the text to be set to the location text field.
     * @return : text
     */
    private String createLocationText() {
        if(mLocation == null)
            return "";

        String name = mLocation.getName();
        if(isEmptyOrNull(name)){
            return "";
        }else
            return name;
    }


    /**
     * Fetches the games format information from the FormatPickerDialogFragment
     * @param format
     */
    @Override
    public void onFormatChosen(String format) {
        if(format != null){
            mFormatTF.setText(format);
            calculateAndSetMinMax(format);
        }
    }

    /**
     * From the format, calculate and set the min number of players to be invited and the max
     */
    private void calculateAndSetMinMax(String format) {
        int totalPlayers = 0;
        if(Validator.isFormatValid(format)){
            totalPlayers = getTotalPlayersFromFormat(format);
            if(totalPlayers != -1){
                setDefaultMin(totalPlayers);
                setDefaultMax(totalPlayers);
            }
        }
    }

    /**
     * sets the default minimum value of players = total players got from format
     * @param totalPlayers
     */
    private void setDefaultMin(int totalPlayers) {
        int defaultMin = getDefaultMinPlayersFromTotal(totalPlayers);
        setMinValue(defaultMin);
    }

    private void setMinValue(int value){
        mMinPlayers = value;
        mMinPlayersTF.setText(MIN_PLAYERS + String.valueOf(mMinPlayers));
    }

    /**
     * Sets the default maximum value
     * @param totalPlayers
     */
    private void setDefaultMax(int totalPlayers){
        int defaultMax = getDefaultMaxPlayersFromTotal(totalPlayers);
        setMaxValue(defaultMax);
    }

    private void setMaxValue(int value){
        mMaxPlayers = value;
        mMaxPlayersTF.setText(MAX_PLAYERS + String.valueOf(mMaxPlayers));
    }

    /**
     * Fetches the list of people invited from the InvitePeopleDialogFragment
     * @param items
     */
    @Override
    public void onPeopleInvited(List<PeopleItem> items) {
        if(items == null){
            return;
        }
        mAdapter.clear(); //clears the listview of all items in it

        // adds the items in the input list to the mAdapters
        if(items.size() == 1){
            mAdapter.add(items.get(0));
        }else if(items.size() > 1)
            mAdapter.addAll(items);
    }

    /**
     * Fetches the location details from the LocationDialogFragment
     * @param location
     */
    @Override
    public void onLocationChosen(Location location) {
        if(location == null){
            return;
        }
        if(isPublic && location.getLatitude() == null){
            showShortToast(getActivity(), "Need to choose location on map for Public games");
            return;
        }

        mLocation = location;
        String locationText = createLocationText();
        mLocationTF.setText(locationText);
    }

    /**
     * Fetches the duration from the DurationPickerDialogFragment
     * @param durationValue
     */
    @Override
    public void onDurationChosen(String durationValue) {
        if(durationValue != null){
            mDurationTF.setText(durationValue);
        }
    }

    /**
     * Get's the list of chosen options as to when the user wants alerts (before a game) about game
     * status etc
     * @param items
     */
    @Override
    public void onNotifyHoursChosen(ArrayList<String> items) {
        mChosenNotifyItems.clear();
        mChosenNotifyItems.addAll(items);
        notifyHoursArray = new int[items.size()];
        for(int i = 0; i < items.size(); i++){ // get the notify hour values from the strings in the array
            String item = items.get(i);
            notifyHoursArray[i] = getHourValueFromString(item);
        }
        setNotifyText(notifyHoursArray);
    }

    /**
     * Sorts the notifyHours array and creates the text to be displayed in the mNotifyTF
     * @param notify
     */
    private void setNotifyText(int[] notify){
        // sort the array
        Arrays.sort(notify);
        String notifyText = buildText(notify);
        // set the string to the text field
        mNotifyTF.setText(notifyText);
    }


    /**
     * Builds up the string to be displayed as the text in the mNotifyTF EditText
     * @param notifyHoursArray
     * @return
     */
    private String buildText(int[] notifyHoursArray) {
        int length = notifyHoursArray.length;
        if(length == 0){ // when nothing is chosen
            return "";
        }
        // to build string that will display the users selection in the text field
        StringBuilder builder = new StringBuilder();
        builder.append(NOTIFY);
        for(int i = 0; i < length; i++){
            builder.append(notifyHoursArray[i]);
            if(i == length - 2)
                builder.append(" & ");
            if(i < length - 2){
                builder.append(", ");
            }
        }
        // in the case that the hour value is 1
        if(length == 1 && notifyHoursArray[0] == 1){
            mNotifyAddedText.setText(" hour" + BEFORE_GAME);
        }else // for all other hour values
            mNotifyAddedText.setText(" hours"+BEFORE_GAME);

        return builder.toString();
    }

    /**
     * Strips away the part of the string including and after the space (" ")
     * @param item
     * @return
     */
    private int getHourValueFromString(String item) {
        String hour = item.substring(0, item.indexOf(" "));
        return Integer.parseInt(hour);
    }

    @Override
    public void onDateChosen(int year, int month, int day, boolean isToday) {
        isGameToday = isToday;
        dateTimeArray[0] = year;
        dateTimeArray[1] = month;
        dateTimeArray[2] = day;
        // set the chosen date as text in the text field, added 1 to the month field because it starts
        // with 0 for January
        String dateText = UtilityMethods.createDateText(day, month + 1, year);
        mDateTF.setText(dateText);
        setDefaultNotifyIfPossible();
    }

    private void setDefaultNotifyIfPossible() {
        if(dateTimeArray[0] != -1 && dateTimeArray[3] != -1){
            if(hoursTillGame() > 1){
                setDefaultNotify();
                return;
            }
        }
        mNotifyTF.setText("");
    }


    /**
     * Calculates the default notify array according to the time and date chosen and sets it as the
     * default choices in the mNotifyTF.
     */
    private void setDefaultNotify() {
        mDateTime = getDateFromArray(dateTimeArray);
        DefaultNotifyCalculator cal = new DefaultNotifyCalculator(mDateTime, new Date());
        notifyHoursArray = cal.getDefaultNotifyArray();
        if(notifyHoursArray[0] != 0){
            mChosenNotifyItems = getDefaultNotifyItems(notifyHoursArray);
            setNotifyText(notifyHoursArray);
        }
    }

    @Override
    public void onTimeChosen(int hour, int minute) {
        dateTimeArray[3] = hour;
        dateTimeArray[4] = minute;
        // set the chosen time as text in the text field
        String timeText = UtilityMethods.createTimeText(hour, minute);
        mTimeTF.setText(timeText);
        setDefaultNotifyIfPossible();
    }

    @Override
    public void onNumberChosen(boolean isMin, int value) {
        if(isMin){
            setMinValue(value);
        }else{
            setMaxValue(value);
        }
    }
}
