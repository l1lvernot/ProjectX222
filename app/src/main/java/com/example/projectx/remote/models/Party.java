package com.example.projectx.remote.models;


public class Party {
    private String partyTitle;
    private String partyDetails;
    private double longitude;
    private double latitude;
    private String address;
    private String time;

    public Party() {
        // Default constructor required for Firebase
    }

    public Party(String partyTitle, String partyDetails, double longitude, double latitude, String address, String time) {
        this.partyTitle = partyTitle;
        this.partyDetails = partyDetails;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.time = time;
    }

    public String getPartyTitle() {
        return partyTitle;
    }

    public void setPartyTitle(String partyTitle) {
        this.partyTitle = partyTitle;
    }

    public String getPartyDetails() {
        return partyDetails;
    }

    public void setPartyDetails(String partyDetails) {
        this.partyDetails = partyDetails;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
