package com.android.khel247.model;

/**
 * Created by Misaal on 02/12/2014.
 */
public class PeopleItem {

    private final boolean pinned;

    private final String username;

    private final String fullName;

    public PeopleItem(String username, String name, boolean isPinned){
        this.username = username;
        this.fullName = name;
        this.pinned = isPinned;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPinned() {
        return pinned;
    }



}
