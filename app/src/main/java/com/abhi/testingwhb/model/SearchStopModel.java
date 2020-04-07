package com.abhi.testingwhb.model;

public class SearchStopModel {

    private String bus_stop_name;

    public SearchStopModel(){

    }

    public String getBus_stop_name() {
        return bus_stop_name;
    }

    public void setBus_stop_name(String bus_stop_name) {
        this.bus_stop_name = bus_stop_name;
    }

    public SearchStopModel(String bus_stop_name) {
        this.bus_stop_name = bus_stop_name;
    }
}
