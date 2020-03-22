package com.abhi245y.whereismybus.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class BusList {

    private String bus_no;
    private String bus_type;
    private GeoPoint bus_location;
    private List<String> bus_stop_list;

    public BusList(){

    }

    public BusList(String bus_no, String bus_type, List<String> bus_stop_list) {
        this.bus_no = bus_no;
        this.bus_type = bus_type;
        this.bus_stop_list = bus_stop_list;

    }

    public List<String> getBus_stop_list() {
        return bus_stop_list;
    }

    public void setBus_stop_list(List<String> bus_stop_list) {
        this.bus_stop_list = bus_stop_list;
    }

    public String getBus_no() {
        return bus_no;
    }

    public void setBus_no(String bus_no) {
        this.bus_no = bus_no;
    }

    public String getBus_type() {
        return bus_type;
    }

    public void setBus_type(String bus_type) {
        this.bus_type = bus_type;
    }

    public GeoPoint getBus_location() {
        return bus_location;
    }

    public void setBus_location(GeoPoint bus_location) {
        this.bus_location = bus_location;
    }
}
