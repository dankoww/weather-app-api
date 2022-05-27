package com.example.weatherapp;

public class Locations {
    // variables for storing our data.
    private String locationName, locationInfo, locationPosition;

    public Locations() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public Locations(String locationName, String locationInfo, String locationPosition) {
        this.locationName = locationName;
        this.locationInfo = locationInfo;
        this.locationPosition = locationPosition;
    }

    // getter methods for all variables.
    public String getlocationName() {
        return locationName;
    }

    public void setlocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getlocationInfo() {
        return locationInfo;
    }

    // setter method for all variables.
    public void setlocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getlocationPosition() {
        return locationPosition;
    }

    public void setlocationPosition(String locationPosition) {
        this.locationPosition = locationPosition;
    }
}
