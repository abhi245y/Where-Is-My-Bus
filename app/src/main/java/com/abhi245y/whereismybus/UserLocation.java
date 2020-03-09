package com.abhi245y.whereismybus;

import com.google.firebase.firestore.GeoPoint;

import android.os.Parcel;

public class UserLocation  {

    private GeoPoint User_location;
    private String User_id;

    public GeoPoint getUser_location() {
        return User_location;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public UserLocation(GeoPoint user_location,String user_id) {
        this.User_location = user_location;
        this.User_id = user_id;
    }

    public void setUser_location(GeoPoint user_location) {
        User_location = user_location;
    }

    public UserLocation(String user_id) {
        User_id = user_id;
    }

    public UserLocation() {

    }

    @Override
    public String toString() {
        return "UserLocation{" +
                ", geo_point=" + User_location +
                '}';
    }
}
