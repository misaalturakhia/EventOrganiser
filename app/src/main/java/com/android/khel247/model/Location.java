package com.android.khel247.model;

import com.android.khel247.constants.Constants;
import com.android.khel247.utilities.UtilityMethods;
import com.khel247.backend.gameEndpoint.model.GameLocation;

import java.io.Serializable;

/**
 * Created by Misaal on 11/12/2014.
 */
public class Location implements Serializable{

    private String name;

    private String description;

    private Double latitude = null;

    private Double longitude = null;


    /**
     * Constructor
     * @param locationName
     */
    public Location(String locationName){
        this.name = locationName;
    }


    /**
     * Constructor
     * @param locationName
     * @param locationDescription
     */
    public Location(String locationName, String locationDescription){
        this(locationName);
        this.description = locationDescription;
    }

    /**
     * Constructor
     * @param locationName
     * @param locationDescription
     * @param locationLatitude
     * @param locationLongitude
     */
    public Location(String locationName, String locationDescription, String locationLatitude,
                    String locationLongitude){
        this(locationName, locationDescription);
        if(!UtilityMethods.isEmptyOrNull(locationLatitude)){
            this.latitude = Double.parseDouble(locationLatitude);
        }
        if(!UtilityMethods.isEmptyOrNull(locationLongitude)){
            this.longitude = Double.parseDouble(locationLongitude);
        }
    }


    /**
     * Populates the location object with the data from the GameLocation object
     * @param location
     */
    public Location(GameLocation location){
        populateFromGameLocation(location);
    }


    /**
     * populates the Location object using the data from the input GameLocation object
     * @param gameLocation
     */
    public void populateFromGameLocation(GameLocation gameLocation){
        if(gameLocation.getName() != null)
            setName(gameLocation.getName());
        if(gameLocation.getDescription() != null)
            setDescription(gameLocation.getDescription());
        if(gameLocation.getLatitude() != null && gameLocation.getLongitude() != null){
            setLatitude(gameLocation.getLatitude());
            setLongitude(gameLocation.getLongitude());
        }
    }


    /**
     * A static method that converts a Location object to a GameLocation object
     * @param location
     * @return
     */
    public static GameLocation convertToGameLocation(Location location){
        GameLocation gameLocation = new GameLocation();
        gameLocation.setName(location.getName());
        gameLocation.setDescription(location.getDescription());

        // if user has chosen latitude and longitude
        if(location.getLatitude() != null && location.getLongitude() != null){
            gameLocation.setLatitude(location.getLatitude());
            gameLocation.setLongitude(location.getLongitude());
        }
        return gameLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
