package com.android.khel247.utilities;

import android.text.TextUtils;

/**
 * Created by Misaal on 06/11/2014.
 */
public abstract class Validator {

    /**Validates email address input
     *
     * @return
     */
/*    public final static boolean isEmailValid(String email){
//        ^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$
        final String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

        return email.matches(emailRegex);
    }*/


    /**
     * Validates input email address
     * @param email :String
     * @return
     */
    public static final boolean isEmailValid(String email){
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     *
     * @param password
     * @return
     */
    public static final boolean isPasswordValid(String password){
        //TODO:
        return true;
    }

    public static final boolean isUsernameValid(String username){
        //TODO:
        return true;
    }


    /**
     * Checks the input format string to see if it is valid.
     * @param format : format string
     * @return : true if valid, false if invalid
     */
    public static final boolean isFormatValid(String format){
        format = format.trim().toLowerCase();
        if(!format.contains("v"))
            return false;
        //get characters on the left of the "v"
        String leftOfV = format.substring(0,format.indexOf("v"));

        // get characters on the right of the "v"
        String rightOfV = format.substring(format.indexOf("v")+1, format.length());

        // left has to be equal to right - (can't have 4v5 or av6 etc)
        if(!leftOfV.equals(rightOfV))
            return false;

        // both left and right have to be integers
        if(!UtilityMethods.isInteger(leftOfV) || !UtilityMethods.isInteger(rightOfV)){
            return false;
        }
        return true;
    }

}
