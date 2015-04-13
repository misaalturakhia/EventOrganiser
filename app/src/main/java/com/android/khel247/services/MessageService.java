package com.android.khel247.services;

import android.content.res.Resources;

import com.android.khel247.constants.Constants;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.constants.ResponseCodes;


/**
 * Created by Misaal on 06/11/2014.
 */
public abstract class MessageService {

    //success messages
    public static final String SUCCESS = "Success!";

    // client error messages
    public static final String EMPTY_EMAIL = "Enter your Email Address";
    public static final String INVALID_EMAIL = "Invalid Email Address";

    public static final String EMPTY_PASSWORD = "Enter a password";
    public static final String INVALID_PASSWORD = "Invalid Password";
    public static final String PASSWORDS_DONT_MATCH = "Passwords don't match";

    public static final String EMPTY_USERNAME = "Enter a Username";
    public static final String INVALID_USERNAME = "Invalid Username";

    public static final String ENTER_FIRST_NAME = "Enter a first name";
    public static final String ENTER_LAST_NAME = "Enter a last name";
    public static final String ENTER_CITY = "Enter city";


    public static final String INVALID_CREDENTIALS = "Invalid Credentials!";

    public static final String DEVICE_NOT_CONNECTED = "You aren't connected to the Internet!";
    public static final String ERROR_SOMETHING_WRONG = "Something went wrong. Please try again.";

    public static final String FRAGMENT_ERROR = "Error in creating Fragment";

    public static final String SEARCH_OWN_USERNAME = "Don't search for your own username -_-";

    public static final String ENTER_LOCATION_NAME = "Enter a name for the location";

    public static final String ENTER_GAME_NAME = "Enter a game name.";
    public static final String ENTER_DATE = "Choose a date";
    public static final String ENTER_TIME = "Choose a time";
    public static final String INVALID_TIME = "Invalid time. It points to a date time value that is " +
            "in the past.";
    public static final String CHOOSE_LOCATION = "Enter a location";
    public static final String INVITE_PEOPLE = "Planning to play alone !? Invite Someone!";
    public static final String GAME_MIN_TIME = "The game has to be at least "+ GameConstants.MIN_TIME_FOR_GAME
            +" hour from now";

    public static final String PAST_DATE_CHOSEN = "Past Date chosen!";
    public static final String INVALID_DATE_CHOSEN = "Invalid Date Chosen! The date is either in the" +
            "past or the time of the game is before "+GameConstants.MIN_TIME_FOR_GAME + " hour from now";
    public static final String PLAY_SERVICES_NOT_SUPPORTED = "Google Play Services is not supported " +
            "on this device.";
    public static final String INVALID_LATITUDE = "Invalid Latitude value";
    public static final String INVALID_LONGITUDE = "Invalid Longitude value";
    public static final String COULDNT_FIND_LOCATION = "Couldnt find an appropriate location";
    public static final String MARK_LOCATION = "Please choose a game location by marking where you " +
            "want to play";
    public static final String PUBLIC_MODE_ENTER_LOCATION = "The game mode is public. Must mark a " +
            "location on the map";
    public static final String CHOOSE_DURATION = "Please choose duration";



    // server response code strings
    // server error
    public static final String INVALID_INPUT = "The input parameters are invalid or null";
    public static final String INCORRECT_CREDENTIALS = "The input credentials are incorrect";
    public static final String EMAIL_EXISTS = "The email address provided is already registered." +
            " Please enter another";
    public static final String USERNAME_DOES_NOT_EXIST = "There is no such registered Username. " +
            "Please enter another, or Register.";
    public static final String USERNAME_TAKEN = "The Username specified already exists." +
            " Please enter another";
    public static final String PASSWORD_INCORRECT = "The Password is incorrect";

    public static final String USERNAME_NULL = "Input username is NULL!";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong.";
    public static final String PROFILE_ERROR = "Did not receive the profile owner information";
    public static final String INVALID_AUTH_TOKEN = "The authentication tokens don't match";
    public static final String AUTHORIZATION_FAILURE = "Could not authorize the request";
    public static final String USER_DOES_NOT_EXIST = "User does not exist";
    public static final String COULDNT_LEAVE_GAME = "Couldn't leave game";
    public static final String COULDNT_JOIN_GAME = "Couldn't join game";

    // server success
    public static final String REGISTRATION_SUCCESS = "Registered";
    public static final String LOGIN_SUCCESS = "Logged In";
    public static final String USERNAME_AVAILABLE = "Username is available";
    public static final String PASSWORD_CHANGE_SUCCESS = "Your password has been changed";
    public static final String DEFAULT_SUCCESS = "Success";
    public static final String CHOOSE_COUNTRY = "Choose Country";
    private static final String GAME_ALREADY_PUBLIC = "The Game is already public";
    private static final String GAME_FULL = "Sorry, the game is full!";
    private static final String GAME_CONFIRMED = "Sorry, the game is already confirmed!";

    /**
     * Returns the error message which corresponds to the input errorCodes
     * @param errorCode
     * @return
     */
    public static String getMessageFromCode(int errorCode){
        switch(errorCode){
            case ResponseCodes.SUCCESS:
                return SUCCESS;
            case ResponseCodes.REGISTRATION_SUCCESS:
                return REGISTRATION_SUCCESS;
            case ResponseCodes.LOGIN_SUCCESS:
                return LOGIN_SUCCESS;
            case ResponseCodes.USERNAME_AVAILABLE:
                return USERNAME_AVAILABLE;
            case ResponseCodes.PASSWORD_CHANGED:
                return PASSWORD_CHANGE_SUCCESS;
            case ResponseCodes.INVALID_INPUT:
                return INVALID_INPUT;
            case ResponseCodes.INCORRECT_CREDENTIALS:
                return INCORRECT_CREDENTIALS;
            case ResponseCodes.USERNAME_DOES_NOT_EXIST:
                return USERNAME_DOES_NOT_EXIST;
            case ResponseCodes.PASSWORD_INCORRECT:
                return PASSWORD_INCORRECT;
            case ResponseCodes.INVALID_AUTH_TOKEN:
                return INVALID_AUTH_TOKEN;
            case ResponseCodes.COULDNT_AUTHORIZE:
                return AUTHORIZATION_FAILURE;
            case ResponseCodes.USER_DOES_NOT_EXIST:
                return USER_DOES_NOT_EXIST;
            case ResponseCodes.EMAIL_ALREADY_REGISTERED:
                return EMAIL_EXISTS;
            case ResponseCodes.USERNAME_TAKEN:
                return USERNAME_TAKEN;
            case ResponseCodes.DEFAULT:
                return DEFAULT_SUCCESS;
            case ResponseCodes.SOMETHING_WENT_WRONG:
                return SOMETHING_WENT_WRONG;
            case ResponseCodes.GAME_ALREADY_PUBLIC:
                return GAME_ALREADY_PUBLIC;
            case ResponseCodes.GAME_FULL:
                return GAME_FULL;
            case ResponseCodes.GAME_CONFIRMED:
                return GAME_CONFIRMED;
            default:
                throw new Resources.NotFoundException("Invalid Error Code");
        }

    }



}
