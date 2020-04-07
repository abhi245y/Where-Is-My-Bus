package com.abhi.testingwhb.model;


public class User {

    private String user_id;

    public User(String user_id) {
        this.user_id = user_id;
    }

    public User() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}

