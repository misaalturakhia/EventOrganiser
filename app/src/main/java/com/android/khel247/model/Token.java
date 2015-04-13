package com.android.khel247.model;

import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.memberEndpoint.model.LoginToken;

import java.io.Serializable;

/**
 * Created by Misaal on 25/11/2014.
 */
public class Token implements Serializable{

    /**
     * Authentication token of the account
     */
    private String token;

    /**
     * name of the account in the form of an email address
     */
    private String username;

    public Token(String authToken, String username){
        this.token = authToken;
        this.username = username;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Checks if the token object is non empty and non null
     * @param token
     * @return
     */
    public static boolean checkToken(Token token){
        if(token == null){
            return false;
        }else if(UtilityMethods.isEmptyOrNull(token.getUsername())){
            return false;
        }else if(UtilityMethods.isEmptyOrNull(token.getToken())){
            return false;
        }else return true;
    }

    public static LoginToken createMemberEndpointLoginToken(Token token){
        LoginToken loginToken = new LoginToken();
        loginToken.setAuthToken(token.getToken());
        loginToken.setUsername(token.getUsername());
        return loginToken;
    }
}
