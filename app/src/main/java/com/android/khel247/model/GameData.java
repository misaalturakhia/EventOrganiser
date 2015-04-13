package com.android.khel247.model;

import com.android.khel247.utilities.UtilityMethods;
import com.google.api.client.util.DateTime;
import com.khel247.backend.gameEndpoint.model.GameForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.android.khel247.utilities.UtilityMethods.convertDateFormat;
import static com.android.khel247.utilities.UtilityMethods.convertToList;

/**
 * Created by Misaal on 08/12/2014.
 */
public class GameData implements Serializable{

    private String webSafeGameKey;

    private String organiserUsername;

    private String organiserName;

    private String name;

    private String format;

    private int duration;

    private Date dateTime;

    private Location location;

    private List<String> invitedMembers;

    private List<String> yesMembers;

    private List<String> noMembers;

    private boolean isPublic;

    private int minPlayers;

    private int maxPlayers;

    private List<Integer> notifyHours;

    private String status;

    private List<String> subOrganiserList;

    public GameData(){}


    /**
     * Creates the GameData object based on the data in the GameForm object
     * @param form
     */
    public GameData(GameForm form){
        populateFromGameForm(form);
    }

    /**
     *
     * @param name
     * @param format
     * @param duration
     * @param dateTime
     * @param location
     * @param invitedPlayers
     * @param isPublic
     */
    public GameData(String username, String name, String format, int duration, Date dateTime, Location location,
                    List<String> invitedPlayers, boolean isPublic, int minPlayers, int maxPlayers,
                    int[] notify, String status){
        this.organiserUsername = username;
        this.name = name;
        this.format = format;
        this.duration = duration;
        this.dateTime = dateTime;
        this.location = location;
        this.invitedMembers = invitedPlayers;
        this.isPublic = isPublic;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.notifyHours = convertToList(notify);
        this.status = status;
    }

    /**
     * Populates the GameCreationForm
     * @param data
     * @return
     */
    public static GameForm convertToGameForm(GameData data){
        GameForm form = new GameForm();
        form.setGameName(data.getName());
        form.setPublicGame(data.isPublic());
        form.setFormat(data.getFormat());
        form.setDuration(data.getDuration());
        form.setDateTime(new DateTime(data.getDateTime()));
        form.setLocation(Location.convertToGameLocation(data.getLocation()));
        form.setInvitedMemberList(data.getInvitedMembers());
        form.setComingList(data.getYesMembers());
        form.setNotComingList(data.getNoMembers());
        form.setMinCapacity(data.getMinPlayers());
        form.setMaxCapacity(data.getMaxPlayers());
        form.setNotifyHours(data.getNotifyHours());
        form.setStatus(data.getStatus()); // 0 = Created
        form.setSubOrganiserList(data.getSubOrganiserList());
        return form;
    }


    /** Populates the gamedata object with the data from GameForm
     *
     * @param form
     */
    public void populateFromGameForm(GameForm form){
        setWebSafeGameKey(form.getWebSafeGameKey());
        setName(form.getGameName());
        setOrganiserName(form.getOrganiserName());
        setOrganiserUsername(form.getOrganiserUsername());
        setPublic(form.getPublicGame());
        setFormat(form.getFormat());
        setDuration(form.getDuration());
        setDateTime(convertDateFormat(form.getDateTime()));
        setLocation(new Location(form.getLocation()));
        setStatus(form.getStatus());
        setInvitedMembers(form.getInvitedMemberList());
        setYesMembers(form.getComingList());
        setNoMembers(form.getNotComingList());
        setNotifyHours(form.getNotifyHours());
        setMinPlayers(form.getMinCapacity());
        setMaxPlayers(form.getMaxCapacity());
        setSubOrganiserList(form.getSubOrganiserList());
    }


    public boolean isPublic() {
        return isPublic;
    }


    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<String> getInvitedMembers() {
        if(invitedMembers == null){
            invitedMembers = new ArrayList<>();
        }
        return invitedMembers;
    }

    public void setInvitedMembers(List<String> invitedMembers) {
        this.invitedMembers = invitedMembers;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<Integer> getNotifyHours() {
        return notifyHours;
    }

    public void setNotifyHours(int[] notifyHours) {
        this.notifyHours = convertToList(notifyHours);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getYesMembers() {
        if(yesMembers == null){
            yesMembers = new ArrayList<>();
        }
        return yesMembers;
    }

    public void setYesMembers(List<String> yesMembers) {
        this.yesMembers = yesMembers;
    }

    public List<String> getNoMembers() {
        if(noMembers == null){
            noMembers = new ArrayList<>();
        }
        return noMembers;
    }

    public void setNoMembers(List<String> noMembers) {
        this.noMembers = noMembers;
    }

    public void setNotifyHours(List<Integer> notifyHours) {
        this.notifyHours = notifyHours;
    }

    public String getOrganiserUsername() {
        return organiserUsername;
    }

    public void setOrganiserUsername(String organiserUsername) {
        this.organiserUsername = organiserUsername;
    }

    public String getOrganiserName() {
        return organiserName;
    }

    public void setOrganiserName(String organiserName) {
        this.organiserName = organiserName;
    }

    public String getWebSafeGameKey() {
        return webSafeGameKey;
    }

    public void setWebSafeGameKey(String webSafeGameKey) {
        this.webSafeGameKey = webSafeGameKey;
    }

    public List<String> getSubOrganiserList() {
        return subOrganiserList;
    }

    public void setSubOrganiserList(List<String> subOrganiserList) {
        this.subOrganiserList = subOrganiserList;
    }
}

