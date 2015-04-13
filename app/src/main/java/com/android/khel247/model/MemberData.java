package com.android.khel247.model;

import com.khel247.backend.memberEndpoint.model.ProfileForm;

import java.util.List;

/**
 * Created by Misaal on 27/12/2014.
 */
public class MemberData {

    private String fullName;

    private String username;

    private String emailAddress;

    private String area;

    private String city;

    private String country;

    private String position;

    private int noOfContacts = 0;

    /**
     * Default Constructor
     */
    public MemberData(){}

    /**
     * Constructor
     * @param form
     */
    public MemberData(ProfileForm form){
        populateFromProfileForm(form);
    }

    public void populateFromProfileForm(ProfileForm form){
        setFullName(form.getFullName());
        setUsername(form.getUsername());
        setEmailAddress(form.getEmailAddress());
        setPosition(form.getPosition());
        setArea(form.getArea());
        setCity(form.getCity());
        setCountry(form.getCountry());
        setNoOfContacts(form.getNoOfContacts());
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public int getNoOfContacts() {
        return noOfContacts;
    }

    public void setNoOfContacts(int noOfContacts) {
        this.noOfContacts = noOfContacts;
    }
}
