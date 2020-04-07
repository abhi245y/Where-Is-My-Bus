package com.abhi.testingwhb.model;


public class BusListModel {

    private String duration;
    private String distance;
    private String Origin;
    private String IntermediateStops;
    private String Destination;
    private String BusNo;
    private String fair;


    public BusListModel(String duration, String distance, String origin, String intermediateStops, String destination, String busNo, String fair) {
        this.duration = duration;
        this.distance = distance;
        Origin = origin;
        IntermediateStops = intermediateStops;
        Destination = destination;
        BusNo = busNo;
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

    public String getIntermediateStops() { return IntermediateStops; }

    public void setIntermediateStops(String intermediateStops) { IntermediateStops = intermediateStops; }

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
