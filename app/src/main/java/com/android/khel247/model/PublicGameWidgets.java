package com.android.khel247.model;

import android.location.Address;

/**
 * Created by Misaal on 29/12/2014.
 */
public class PublicGameWidgets {

    private String gameKeyStr;

    private Location location;

    Address address;

    public PublicGameWidgets(String webSafeGameKey, Location gameLocation){
        this.gameKeyStr = webSafeGameKey;
        this.location = gameLocation;
    }

}
