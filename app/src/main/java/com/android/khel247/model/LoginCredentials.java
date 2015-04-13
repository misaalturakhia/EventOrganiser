package com.android.khel247.model;

/** Holds the information required for Login
 * Created by Misaal on 07/11/2014.
 */
public class LoginCredentials {

    private String username;

    private String password;

    /**
     * hiding the default constructor
     */
    private LoginCredentials(){}

    public LoginCredentials(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}
