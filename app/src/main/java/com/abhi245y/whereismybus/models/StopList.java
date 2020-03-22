package com.abhi245y.whereismybus.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Map;

public class StopList {
    private String bus_stop_name;
    private GeoPoint bus_stop_location;
    private List<String> bus_that_come_here;

    public StopList() {

    }

    public StopList(String bus_stop_name, GeoPoint bus_stop_location, List<String> bus_that_come_here) {
        this.bus_stop_name = bus_stop_name;
        this.bus_stop_location = bus_stop_location;
        this.bus_that_come_here = bus_that_come_here;
    }

    public String getBus_stop_name() {
        return bus_stop_name;
    }

    public void setBus_stop_name(String bus_stop_name) {
        this.bus_stop_name = bus_stop_name;
    }

    public GeoPoint getBus_stop_location() {
        return bus_stop_location;
    }

    public void setBus_stop_location(GeoPoint bus_stop_location) {
        this.bus_stop_location = bus_stop_location;
    }

    public List<String> getBus_that_come_here() {
        return bus_that_come_here;
    }

    public void setBus_that_come_here(List<String> bus_that_come_here) {
        this.bus_that_come_here = bus_that_come_here;
    }
}
