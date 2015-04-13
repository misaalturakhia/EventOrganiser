package com.android.khel247.constants;

/**
 * Created by Misaal on 21/11/2014.
 */
public abstract class Constants {

    public static final String PACKAGE_NAME = "com.android.khel247";

    public static final String MY_PREFS_FILE_NAME = "Khel247PrefsFile";

    public static final String ARG_EMAIL = "email";

    public static final String ARG_USERNAME = "username";

    public static final String ARG_PASSWORD = "password";

    public static final String ARG_REG_ID = "registration Id";

    public static final String ARG_TOKEN = "authentication token";

    public static final String ARG_TOKEN_CHECKED = "Token Checked";

    public static final String ARG_GAME_LOCATION_ADDRESS = "Game Location";

    public static final String ARG_PEOPLE_LIST = "People List";


    public static final String SIGN_OUT_DIALOG_TITLE = "Sign Out";

    public static final String SIGN_OUT_DIALOG_BODY = "Are you sure?";

    public static final String DIALOG_DONE_BTN_TEXT = "Done";

    public static final String DIALOG_CANCEL_BTN_TEXT = "Cancel";

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String TIME_FORMAT = "HH:mm"; // capital HH signifies military format

    public static final int GAME_LOCATION_SUCCESS_CODE = 1;

    public static final int GAME_LOCATION_CANCEL_CODE = -1;

    public static final String ARG_IS_INVITE_ACCEPTED = "Accepted Invite";

    public static final String ARG_IS_INVITE_REJECTED = "Rejected Invite";

    // coordinates lie between +90 & -90, so for the error, just need a value outside the bounds
    public static final float LOCATION_COORDINATES_DEFAULT_ERROR = 100;

    public static final String ARG_GAME_INTENT_TYPE = "Game Intent Type";

    public static final String ARG_GAME_ORGANISER_USERNAME = "Game Organiser";

    public static final int INTENT_LOADED_GAME_DATA = 0;

    public static final int INTENT_LOAD_GAME_DATA = 1;

    public static final String ARG_GAME_KEY = "GameId";

    public static final String ARG_PROFILE_USERNAME = "Profile Username";
    public static final String ARG_GAME_MESSAGE = "Game Message";
    public static final String ARG_IS_NEGATIVE_PROGRESS = "Negative Progress";
    public static final String PREFS_ARG_DEVICE_REG_ID = "Device Reg Id";
    public static final String ARG_IS_OWN_CONTACTS = "Own Contacts";
}
