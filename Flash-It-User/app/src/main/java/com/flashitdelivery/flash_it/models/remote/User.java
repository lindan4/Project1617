package com.flashitdelivery.flash_it.models.remote;

/**
 * Created by yon on 09/02/16.
 * This class contains the User model
 * Using the attributes contained by the JSON from the web api
 * returned by both me() and register()
 */
public class User {
    // the attributes are not camel case because their names
    // need to tbe the same as the JSON returned by the web api
    public int id;
    public String first_name;
    public String last_name;
    public String email;
    public String home_address;
    public String username;
}
