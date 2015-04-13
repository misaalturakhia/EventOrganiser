package com.android.khel247.constants;

/**
 * Created by Misaal on 12/11/2014.
 */
public abstract class ResponseCodes {

    public static final int DEFAULT = 0;

    // success messages
    public static final int SUCCESS = 200;
    public static final int REGISTRATION_SUCCESS = 201;
    public static final int LOGIN_SUCCESS = 202;
    public static final int USERNAME_AVAILABLE = 203;
    public static final int PASSWORD_CHANGED = 204;


    // Client errors
    // input errors
    public static final int INVALID_INPUT = 400;

    // authorization errors
    public static final int INCORRECT_CREDENTIALS = 401;
    public static final int USERNAME_DOES_NOT_EXIST = 402;
    public static final int PASSWORD_INCORRECT = 403;
    public static final int INVALID_AUTH_TOKEN = 404;
    public static final int COULDNT_AUTHORIZE = 405;
    public static final int USER_DOES_NOT_EXIST = 406;
    public static final int GAME_ALREADY_PUBLIC = 407;

    // registration errors
    public static final int EMAIL_ALREADY_REGISTERED = 431;
    public static final int USERNAME_TAKEN = 432;


    //server errors
    public static final int SOMETHING_WENT_WRONG = 500;
    public static final int GAME_FULL = 501;
    public static final int GAME_CONFIRMED = 502;

}
