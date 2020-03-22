package com.abhi245y.whereismybus.models;

import java.time.Duration;

public class BusListModel {

    private String duration,distance;
    private String BusNo;
    private String Origin,Destination;
    private String fair;

    public BusListModel(){}

    public BusListModel(String  duration,String  distance, String busNo, String origin, String destination, String  fair) {
        this.duration = duration;
        this.distance= distance;
        BusNo = busNo;
        Origin = origin;
        Destination = destination;
        this.fair = fair;

    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBusNo() {
        return BusNo;
    }

    public void setBusNo(String busNo) {
        BusNo = busNo;
    }

    public String getOrigin() {
        return Origin;
    }

    public void setOrigin(String origin) {
        Origin = origin;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getFair() {
        return fair;
    }

    public void setFair(String fair) {
        this.fair = fair;
    }
}
