package com.android.khel247.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.khel247.GameActivity;
import com.android.khel247.MainActivity;
import com.android.khel247.R;
import com.android.khel247.ViewLocationActivity;
import com.android.khel247.asynctasks.gametasks.ConfirmGameTask;
import com.android.khel247.asynctasks.gametasks.AddSubOrganisersTask;
import com.android.khel247.asynctasks.gametasks.CancelGameTask;
import com.android.khel247.asynctasks.gametasks.ChangeModeTask;
import com.android.khel247.asynctasks.gametasks.InviteMoreTask;
import com.android.khel247.asynctasks.gametasks.JoinOrLeaveGameTask;
import com.android.khel247.asynctasks.gametasks.RejectGameTask;
import com.android.khel247.constants.Constants;
import com.android.khel247.constants.DialogMessages;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.dialogs.InvitePeopleDialogFragment;
import com.android.khel247.dialogs.LocationDialogFragment;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Location;
import com.android.khel247.model.PeopleItem;
import com.android.khel247.model.Token;
import com.android.khel247.services.MessageService;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.model.WrappedBoolean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.android.khel247.utilities.UtilityMethods.makeLongToast;
import static com.android.khel247.utilities.UtilityMethods.showShortToast;

/**
 * A fragment that displays the details of a created game. It allows different types of users
 * viewing the class to execute different functionality
 */
public class GameFragment extends Fragment implements InvitePeopleDialogFragment.InvitePeopleListener,
        LocationDialogFragment.LocationChosen{

    private static final String JOIN_BTN_TEXT = "Join";
    private static final String NO_BTN_TEXT = "No";
    private static final String LEAVE_BTN_TEXT = "Leave";
    private static final String SAVE_BTN_TEXT = "Save";
    private static final String INVITED_EMPTY_TEXT = "There are no people left who haven't answered " +
            "the invitation";
    private static final String COMING_EMPTY_TEXT = "There are no people who have accepted the invitation";
    private static final String NOT_COMING_EMPTY_TEXT = "The are no people who have rejected the invitation";
    private static final String INVITED_DIALOG_TITLE = "Invited Players";
    private static final String COMING_DIALOG_TITLE = "Players Coming";
    private static final String NOT_COMING_DIALOG_TITLE = "Players Not Coming";
    private static final String MODE_DIALOG_TITLE = "Change Mode";

    private Token authToken;
    private GameData mData;
    private UserType userType;
    private boolean isEdited = false;
    private GameFragment _this = this;

    private View rootView;
    private TextView mNameText;
    private TextView mModeText;
    private TextView mFormatText;
    private TextView mDurationText;
    private TextView mDateText;
    private TextView mTimeText;
    private TextView mLocationText;
    private LinearLayout mGameActionBar;

    private String mGameMessage;
    private boolean isNegativeProgress;
    private static final String INVITE_PEOPLE = "Invite more contacts";
    private static final String ASK_PLAYERS_TO_INVITE = "Ask members to invite others";
    private static final String SWITCH_TO_PUBLIC = "Switch game to public";
    private static final String CANCEL_GAME = "Cancel the game";
    private Button inviteMoreBtn;
    private TextView mInvitedTextView;
    private LinearLayout inviteMoreLayout;
    private AlertDialog mSuggestedActionsDialog;

    /**
     *
     * @param token
     * @param data
     * @return
     */
    public static GameFragment newInstance(Token token, GameData data, String gameMessage, boolean isNegativeProgress) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOKEN, token);
        args.putSerializable(GameConstants.ARG_GAME_DATA, data);
        args.putString(Constants.ARG_GAME_MESSAGE, gameMessage);
        args.putBoolean(Constants.ARG_IS_NEGATIVE_PROGRESS, isNegativeProgress);
        fragment.setArguments(args);
        return fragment;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            authToken = (Token)args.getSerializable(Constants.ARG_TOKEN);
            mData = (GameData)args.getSerializable(GameConstants.ARG_GAME_DATA);
            mGameMessage = args.getString(Constants.ARG_GAME_MESSAGE);
            isNegativeProgress = args.getBoolean(Constants.ARG_IS_NEGATIVE_PROGRESS);
        }else{
            showShortToast(getActivity(), MessageService.SOMETHING_WENT_WRONG + ". Didn't receive any data");
        }
        userType = getUserType();
    }


    /**
     * Checks if the user is an organiser, is invited to the game, is invited and not coming, is
     * invited and has rsvp'd yes and returns the type of user.
     *
     * @return : UserType
     */
    private UserType getUserType() {
        String username = authToken.getUsername();
        String organiserUsername = mData.getOrganiserUsername();
        if(organiserUsername.equals(username)){ // if organiser
            return UserType.ORGANISER;
        }
        List<String> invitedList = mData.getInvitedMembers();
        List<String> comingList = mData.getYesMembers();
        List<String> notComingList = mData.getNoMembers();
        List<String> subOrganiserList = mData.getSubOrganiserList();

        if(invitedList != null && invitedList.contains(username)){
            return UserType.INVITED;
        }else if(comingList != null && comingList.contains(username)){
            if(mData.isPublic()){ // if the game is public and the user is coming
                return UserType.SUB_ORGANISER;
            }else // if the game is private
                return UserType.COMING;
        }else if(notComingList != null && notComingList.contains(username)){
            return UserType.NOT_COMING; // user has rejected the invite
        }else if(subOrganiserList != null && subOrganiserList.contains(username)){
            return UserType.SUB_ORGANISER; // user is a suborganiser and the game is private
        }
        if(mData.isPublic()){
            // lets any user viewing the game who is not on any of the lists be able to join the game
            // if the game is public
            return UserType.INVITED;
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game, container, false);

        if(mGameMessage != null){
            showNotificationMessageDialog();
        }

        // set text to the organiserUsername TextView
        LinearLayout mOrganiserWidget = (LinearLayout)rootView.findViewById(R.id.game_organiser_widget);
        if(userType == UserType.ORGANISER){ // allow editing functionality
            addFunctionalityToWidget(R.id.game_mode_widget, getModeClickListener());
            mOrganiserWidget.setVisibility(View.GONE);
        }

        // adds on click functionality to the game location widget. Lets the user check the location
        // of the game
        addFunctionalityToWidget(R.id.game_location_widget, getLocationClickListener());

        // shows the user a list of invited players
        addFunctionalityToWidget(R.id.game_invited_people_widget,
                getPeopleDialogListener(mData.getInvitedMembers(), INVITED_EMPTY_TEXT,
                        INVITED_DIALOG_TITLE));

        // shows the user a list of the players who have confirmed their attendance
        addFunctionalityToWidget(R.id.game_coming_people_widget,
                getPeopleDialogListener(mData.getYesMembers(), COMING_EMPTY_TEXT, COMING_DIALOG_TITLE));

        // shows the user a list of players who have rejected the game invite
        addFunctionalityToWidget(R.id.game_not_coming_people_widget,
                getPeopleDialogListener(mData.getNoMembers(), NOT_COMING_EMPTY_TEXT,
                        NOT_COMING_DIALOG_TITLE));

        inviteMoreLayout = (LinearLayout)rootView.findViewById(R.id.game_invite_btn_layout);
        inviteMoreBtn = (Button)rootView.findViewById(R.id.game_invite_more_btn);
        inviteMoreBtn.setOnClickListener(getInviteMoreListener());

        String organiserName = mData.getOrganiserName();
        // set organiser name text to field
        populateTextView(R.id.game_organiser_username, organiserName);

        mNameText = populateTextView(R.id.game_name_text, mData.getName());

        if(mData.isPublic()){
            mModeText = populateTextView( R.id.game_mode_text, GameConstants.GAME_MODE_PUBLIC);
        }else
            mModeText = populateTextView(R.id.game_mode_text, GameConstants.GAME_MODE_PRIVATE);

        mFormatText = populateTextView(R.id.game_format_text, mData.getFormat());

        mDurationText = populateTextView(R.id.game_duration_text, String.valueOf(mData.getDuration()));

        String dateText = getDateText();
        mDateText = populateTextView(R.id.game_date_text, dateText);

        String timeText = getTimeText();
        mTimeText = populateTextView(R.id.game_time_text, timeText);

        String locationText = mData.getLocation().getName();
        mLocationText = populateTextView(R.id.game_location_text, locationText);

        String statusText = mData.getStatus();
        populateTextView(R.id.game_status_text, statusText);

        String invitedStr = getSizeString(mData.getInvitedMembers());
        mInvitedTextView = populateTextView(R.id.game_players_invited_text, invitedStr);

        String comingStr = getSizeString(mData.getYesMembers());
        populateTextView(R.id.game_players_coming_text, comingStr);

        String notComingStr = getSizeString(mData.getNoMembers());
        populateTextView(R.id.game_players_not_coming_text, notComingStr);

        setupActionBar();

        return rootView;
    }

    private View.OnClickListener getInviteMoreListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteMorePeople();
            }
        };
        return listener;
    }

    /**
     * Starts a dialog that displays the user's contacts and the people who have already been invited
     */
    private void inviteMorePeople() {
        List<String> invited = mData.getInvitedMembers();
        ArrayList<String> alreadyInvited;
        if(invited != null){
            alreadyInvited = new ArrayList<>(invited);
        }else{
            alreadyInvited = new ArrayList<>();
        }
        if(mData.getYesMembers() != null){
            alreadyInvited.addAll(mData.getYesMembers());
        }
        InvitePeopleDialogFragment fragment = InvitePeopleDialogFragment.newInstance(authToken,
                alreadyInvited, true);
        fragment.show(getFragmentManager(), "Invite more people");
        fragment.setTargetFragment(this, 0);
    }


    /**
     * Shows a dialog that is a popup of a list of choices the user can choose to do to react to
     * negative progress of the game
     */
    private void showNegativeProgressDialog() {
        String[] progressActionChoices = getActivity().getResources().getStringArray(R.array.negative_progress_options);
        final String[] choicesArray;
        if(mData.isPublic()){
            List<String> choiceList = UtilityMethods.convertToList(progressActionChoices);
            choiceList.remove(SWITCH_TO_PUBLIC);
            choicesArray = choiceList.toArray(new String[progressActionChoices.length - 1]);
        }else{
            choicesArray = progressActionChoices;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Suggested Actions").setItems(choicesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String choice = choicesArray[i];
                        implementChosenAction(choice);
                    }
                })
                .setNegativeButton(DialogMessages.DISMISS, null);
        mSuggestedActionsDialog = builder.create();
        mSuggestedActionsDialog.show();
    }


    /** Executes functionality based on the users choice of the list of actions in case the game's
     * progress is negative
     *
     * @param choice
     */
    private void implementChosenAction(String choice) {
        switch (choice){
            case INVITE_PEOPLE:
                inviteMorePeople();
                break;
            case ASK_PLAYERS_TO_INVITE:
                askPlayersToInvite();
                break;
            case SWITCH_TO_PUBLIC:
                switchToPublicGame();
                break;
            case CANCEL_GAME:
                cancelGame();
                break;
            default:
                showShortToast(getActivity(), "INVALID PROGRESS RESOLUTION ACTION");
        }
    }


    /**
     * Adds all the players coming to the game as sub-organisers and that lets them invite their own
     * contacts
     */
    private void askPlayersToInvite() {
        List<String> comingList = mData.getYesMembers();
        AddSubOrganisersTask task = new AddSubOrganisersTask(getActivity(), authToken, mData.getWebSafeGameKey(),
                comingList);
        task.execute();
        try {
            WrappedBoolean result = task.get();
            if(result != null && result.getResult()){
                showShortToast(getActivity(), "Players have been told (those who have confirmed their attendance).");
            }else{
                showShortToast(getActivity(), MessageService.getMessageFromCode(
                        result.getResponseCode()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    /**
     * Switches the game mode to public
     */
    private void switchToPublicGame() {
        changeModeAction();
    }

    /**
     * A popup dialog that displays the mGameMessage to the user
     */
    private void showNotificationMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Message").setMessage(mGameMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isNegativeProgress){
                            showNegativeProgressDialog();
                        }
                    }
                });
        builder.create().show();
    }


    /**
     * An onclick listener that displays a dialog with the list of invited people
     * @return
     */
    private View.OnClickListener getPeopleDialogListener(final List<String> people,
            final String emptyViewText, final String dialogTitle) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GamePeopleDialogFragment fragment = GamePeopleDialogFragment.newInstance(authToken,
                        people, emptyViewText, dialogTitle);
                fragment.show(getFragmentManager(), "getPeopleListInfo");
            }
        };
        return listener;
    }


    /**
     * Sets up the action bar buttons according to the type of user viewing the activity
     */
    private void setupActionBar() {
        mGameActionBar = (LinearLayout)rootView.findViewById(R.id.game_action_bar);
        switch(userType){
            case ORGANISER:
                inviteMoreLayout.setVisibility(View.VISIBLE);
                setupOrganiserActionBar(mGameActionBar);
                break;
            case INVITED:
                setupInvitedActionBar(mGameActionBar);
                break;
            case COMING:
                setupComingActionBar(mGameActionBar);
                break;
            case NOT_COMING:
                setupNotComingActionBar(mGameActionBar);
                break;
            case SUB_ORGANISER:
                inviteMoreLayout.setVisibility(View.VISIBLE);
                setupComingActionBar(mGameActionBar);
                break;
            default:
                mGameActionBar.setVisibility(View.GONE);
                break;
        }

    }


    /**
     * Gives a member who was invited but previously chose NO, to join up now.
     * @param layout
     */
    private void setupNotComingActionBar(LinearLayout layout) {
        Button joinGame = createActionBarButton(JOIN_BTN_TEXT, getJoinGameListener());
        layout.addView(joinGame);
    }


    /**
     * The action bar seen by a member who is coming to this game. It gives him the option to leave
     * @param layout
     */
    private void setupComingActionBar(LinearLayout layout) {
        Button leaveButton = createActionBarButton(LEAVE_BTN_TEXT, getLeaveGameListener());
        layout.addView(leaveButton);
    }


    /**
     * The action bar seen by a person invited to this game by the organiser. Shows a JOIN button
     * and a NO button
     * @param layout
     */
    private void setupInvitedActionBar(LinearLayout layout) {
        if(mData.getStatus().toLowerCase().equals("confirmed")){
            layout.setVisibility(View.GONE); // prevent users from joining once the game is confirmed
        }
        Button joinButton = createActionBarButton(JOIN_BTN_TEXT, getJoinGameListener());
        Button noButton = createActionBarButton(NO_BTN_TEXT, getNoJoinGameListener());
        layout.addView(noButton);
        layout.addView(joinButton);
    }


    /**
     * The action bar view for the Organiser of the game. Shows a confirm and cancel button which
     * lets the organiser CONFIRM the game to all members, or CANCEL the game altogether
     * @param layout
     */
    private void setupOrganiserActionBar(LinearLayout layout) {
        Button cancelButton = createActionBarButton("Cancel Game", getCancelGameListener());
        Button confirmButton = createActionBarButton("Confirm Game", getConfirmGameListener());
        layout.addView(cancelButton);
        if(!mData.getStatus().toLowerCase().equals("confirmed")){ // if game is not confirmed
            layout.addView(confirmButton);
        }
    }

    /**
     * Creates a button which has WIDTH = MATCH_PARENT, HEIGHT = MATCH_PARENT, WEIGHT = 1
     * Also sets margins around the button = R.dimen.game_widget_margin
     * @param text : text of the button
     * @param listener : an onclicklistener attached to the button
     * @return
     */
    public Button createActionBarButton(String text, View.OnClickListener listener){
        Button button = new Button(getActivity());
        //get dimen resource from the dimens.xml. So if the value is changed there, it will change
        // in these programmatically added widgets
        float floatMargin = getActivity().getResources().getDimension(R.dimen.game_widget_margin);
        int margin = Math.round(floatMargin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // set width
                LinearLayout.LayoutParams.MATCH_PARENT, // set height
                1); // set weight
        params.setMargins(margin, margin, margin, margin); // add margins all around
        button.setLayoutParams(params); // set params
        button.setText(text.toUpperCase());
        button.setOnClickListener(listener);
        return button;
    }


    /**
     * gets the size of the list, converts it to string. default value = "0" (if list is null etc)
     * @param list
     * @return :
     */
    private String getSizeString(List<String> list) {
        String comingStr = "0";
        if(list != null){
            comingStr = String.valueOf(list.size());
        }
        return comingStr;
    }

    /**
     * Fetches the date text using DateFormat("ddMMyyyy")
     * @return
     */
    private String getDateText() {
        Date date = mData.getDateTime();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        return dateFormat.format(date);
    }


    /** returns a string in the format hh:mm based on the game's date field
     *
     * @return
     */
    private String getTimeText() {
        Date date = mData.getDateTime();
        DateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        return dateFormat.format(date);
    }


    /** initializes a view by find the resourceId and sets the text to be displayed to it
     *
     * @param view
     * @param resourceIdOfTextView
     * @param textToBeDisplayed
     * @return
     */
    private TextView populateTextView( int resourceIdOfTextView, String textToBeDisplayed) {
        TextView textView = (TextView) rootView.findViewById(resourceIdOfTextView);
        textView.setText(textToBeDisplayed);
        return textView;
    }


    /**
     * Fetches the widget identified by the resourceId and sets the input OnClickListener to it
     * @param view : parentView
     * @param resourceId : id of the widget
     * @param listener : provides on click functionality
     * @return
     */
    private LinearLayout addFunctionalityToWidget(int resourceId, View.OnClickListener listener) {
        LinearLayout layout = (LinearLayout)rootView.findViewById(resourceId);
        layout.setOnClickListener(listener);
        return layout;
    }


    /** Shows a dialog that lets the user edit a location
     *
     * @return
     */
    private View.OnClickListener getLocationClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent(getActivity(), ViewLocationActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(GameConstants.ARG_LOCATION, mData.getLocation());
                locationIntent.putExtras(extras);
                startActivity(locationIntent);

//                LocationDialogFragment fragment = LocationDialogFragment.newInstance(mData.isPublic(),
//                        mData.getLocation());
//                fragment.setTargetFragment(_this, 0);
//                fragment.show(getFragmentManager(), "");

            }
        };
        return listener;
    }


    /**
     * Handles functionality when the organiser clicks on the Mode widget
     * @return
     */
    private View.OnClickListener getModeClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeModeAction();
            }
        };
        return listener;
    }


    /**
     * Pops up a dialog when the user tries to change the mode from private. If the mode is already
     * public, a toast message tells the user that the mode can't be changed from public to private
     */
    private void changeModeAction() {
        if(!mData.isPublic()) {// allow change in mode, only if the game mode is private
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(MODE_DIALOG_TITLE)
                    .setMessage("Switch Game Mode To Public?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // to switch to public, the user must have the location pointed
                            // by coordinates.
                            Double latitude = mData.getLocation().getLatitude();
                            if(latitude != null){ // if coordinates have been chosen (via pointing on maps)
                                changeMode();
                            }else { // if not chosen, make the user choose and set the new
                                // location to the mData object
                                makeLongToast(getActivity(), "To switch the game to public, " +
                                        "the location must be pointed on Maps. ");
                                showLocationInputDialog();
                            }
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
        }else{
            makeLongToast(getActivity(), "You can only switch a game from private to public.");
        }
    }


    /**
     * Sends the data to the server via ChangeModeTask asynctask and if the response is true,
     * it sets the game mode to public, sets the text in the mode widget to display PUBLIC
     */
    private void changeMode() {
        ChangeModeTask task = new ChangeModeTask(getActivity(), authToken,
                mData.getWebSafeGameKey(), mData.getLocation());
        task.execute();
        try {
            if (task.get() != null && task.get().getResult()) {
                mData.setPublic(true);
                mModeText.setText(GameConstants.GAME_MODE_PUBLIC);
                showShortToast(getActivity(), "Mode switched to Private, Location details stored");
            } else {
                showShortToast(getActivity(), "Something went wrong, couldnt change mode!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Displays the Dialog that lets the user choose and set a location for the game
     */
    private void showLocationInputDialog() {
        // display the dialog to take location input
        LocationDialogFragment fragment = LocationDialogFragment.newInstance(true,
                mData.getLocation());
        fragment.show(getFragmentManager(), "choose location coordinates");
        fragment.setTargetFragment(_this, 0);
    }


    /** returns a listener that holds the functionality to save the changes of the edited game
     *
     * @return
     */
    private View.OnClickListener getSaveBtnListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangesToGame();
            }
        };
        return listener;
    }

    private void saveChangesToGame() {
        //TODO:
    }


    /**
     * returns a listener that holds the functionality to let the user leave the game if he has
     * already joint it
     * @return
     */
    private View.OnClickListener getLeaveGameListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGame();
            }
        };
        return listener;
    }


    /**
     *
     */
    private void leaveGame() {
        JoinOrLeaveGameTask task = new JoinOrLeaveGameTask(getActivity(), authToken,
                mData.getWebSafeGameKey(), false);
        task.execute();
        WrappedBoolean result = null;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null && result.getResult()){
            showShortToast(getActivity(), "LEFT GAME!");
            navigateToMainActivity();
        }
    }


    /**
     * returns a listener that holds the functionality to let the user reject the invitation to the
     * game
     * @return
     */
    private View.OnClickListener getNoJoinGameListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectInvite();
            }
        };
        return listener;
    }


    /**
     * Calls a task that lets the user reject the game invite
     */
    private void rejectInvite() {
        RejectGameTask task = new RejectGameTask(getActivity(), authToken,
                mData.getWebSafeGameKey());
        task.execute();
        WrappedBoolean result = null;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null && result.getResult()){
            showShortToast(getActivity(), "REJECTED GAME INVITE!");
            navigateToMainActivity();
        }else if(result != null && !result.getResult()){
            showShortToast(getActivity(), MessageService.getMessageFromCode(result.getResponseCode()));
        }

    }

    private void navigateToMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }


    /**
     * returns a listener that holds the functionality to let the user join the game based on an
     * invite
     * @return
     */
    private View.OnClickListener getJoinGameListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptInvite();
            }
        };
        return listener;
    }

    private void acceptInvite() {
        JoinOrLeaveGameTask task = new JoinOrLeaveGameTask(getActivity(), authToken,
                mData.getWebSafeGameKey(), true);
        task.execute();
        WrappedBoolean result = null;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null && result.getResult()){
            showShortToast(getActivity(), "JOINED GAME!");
            reloadActivity();
        }

    }

    /**
     * Reloads the activity
     */
    private void reloadActivity() {
        Intent gameIntent = new Intent(getActivity(), GameActivity.class);
        gameIntent.putExtra(Constants.ARG_GAME_INTENT_TYPE, Constants.INTENT_LOAD_GAME_DATA);
        gameIntent.putExtra(Constants.ARG_GAME_KEY, mData.getWebSafeGameKey());

        // reload the game activity with a server call for the new data
        getActivity().finish();
        startActivity(gameIntent);
    }


    /**
     * returns a listener that holds the functionality to let the organiser cancel the game
     * @return
     */
    private View.OnClickListener getCancelGameListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
                builder.setTitle("Cancel Game").setMessage(DialogMessages.ARE_YOU_SURE_TEXT)
                        .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelGame();
                            }
                        }).setNegativeButton(Constants.DIALOG_CANCEL_BTN_TEXT, null);
                builder.create().show();
            }
        };
        return listener;
    }


    /**
     * Calls the cancel game task and reacts to the result
     */
    private void cancelGame() {
        CancelGameTask task = new CancelGameTask(getActivity(), authToken );
        task.execute(mData.getWebSafeGameKey());
        try {
            WrappedBoolean result = task.get();
            if(result != null){
                if(result.getResult()){
                    showShortToast(getActivity(), "Game Cancelled!");
                    navigateToMainActivity();
                }else
                    showShortToast(getActivity(), MessageService.getMessageFromCode(result.getResponseCode()));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * returns a listener that holds the functionality to let the organiser CONFIRM the game
     * @return
     */
    private View.OnClickListener getConfirmGameListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = buildConfirmDialogMessage();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 0);
                builder.setTitle("Confirm Game").setMessage(message)
                        .setPositiveButton(Constants.DIALOG_DONE_BTN_TEXT, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirmGame();
                            }
                        }).setNegativeButton(Constants.DIALOG_CANCEL_BTN_TEXT, null);
                builder.create().show();
            }
        };
        return listener;
    }


    /**
     * Builds the message that is displayed in the dialog that asks the user if he is sure he wants
     * to confirm the game
     * @return
     */
    private String buildConfirmDialogMessage() {
        StringBuilder builder = new StringBuilder();
        int coming = mData.getYesMembers().size();
        if(coming < mData.getMinPlayers()){
            builder.append("Only "+coming+" players have confirmed their presence, are ");
        }else{
            builder.append("Are ");
        }
        builder.append("you sure you want to confirm?");
        return builder.toString();
    }


    /**
     * Confirms the game by changing the game status and also sends a message to all the coming members.
     * No more players are allowed to join the game
     */
    private void confirmGame() {
        ConfirmGameTask task = new ConfirmGameTask(getActivity(), authToken, mData.getWebSafeGameKey());
        task.execute();
        try {
            WrappedBoolean result = task.get();
            if(result != null){
                if(result.getResult()){
                    showShortToast(getActivity(), "Game Confirmed!");
                    reloadActivity();
                }else{
                    showShortToast(getActivity(), MessageService.getMessageFromCode(result.getResponseCode()));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeopleInvited(List<PeopleItem> items) {
        List<String> invitedList = mData.getInvitedMembers();
        List<String> newInvites = new ArrayList<>();
        for(PeopleItem item : items){
            String username = item.getUsername();
            if(!invitedList.contains(username)){
                newInvites.add(username);
            }
        }
        if(newInvites.size() < 1){
            return;
        }

        InviteMoreTask task = new InviteMoreTask(getActivity(), authToken, mData.getWebSafeGameKey(),
                newInvites);
        task.execute();
        try {
            WrappedBoolean result = task.get();
            if(result != null){
                if(result.getResult()){
                    invitedList.addAll(newInvites);
                    // refresh text text that shows the number of people invited
                    mInvitedTextView.setText(getSizeString(invitedList));
                    showShortToast(getActivity(), "Invited!");
                }else{
                    showShortToast(getActivity(), MessageService.getMessageFromCode(result.getResponseCode()));
                }
            }else{
                showShortToast(getActivity(), "Result was null");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChosen(Location location) {
        if(location == null || location.getLatitude() == null){
            makeLongToast(getActivity(),"Must specify location on maps to switch the game to public");
            return;
        }
        mData.setLocation(location);
        changeMode();
    }

    /**
     * Differentiates the type of user viewing this game interface
     */
    private enum UserType {
        ORGANISER, INVITED, COMING, NOT_COMING, SUB_ORGANISER;
    }

}
