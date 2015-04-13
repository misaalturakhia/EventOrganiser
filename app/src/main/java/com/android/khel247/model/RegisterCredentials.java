package com.android.khel247.model;

/** Holds the information needed for Registration
 * Created by Misaal on 07/11/2014.
 */
public class RegisterCredentials {

    private final String username;

    private String emailAddress;

    private String password;

    private String firstName;

    private String lastName;

    private String position;

    private String area;

    private String city;

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getPosition() {
        return position;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    private String country;

    /**
     * Constructor
     * @param username
     * @param emailAdd
     * @param pw
     * @param firstName
     * @param lastName
     * @param position
     * @param area
     * @param city
     * @param country
     */
    public RegisterCredentials(String username, String emailAdd, String pw, String firstName,
                               String lastName, String position, String area, String city, String country){
        this.username = username;
        this.emailAddress = emailAdd;
        this.password = pw;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.area = area;
        this.city = city;
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}
