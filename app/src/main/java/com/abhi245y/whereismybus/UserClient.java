package com.abhi245y.whereismybus;

import android.app.Application;

import com.abhi245y.whereismybus.models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
