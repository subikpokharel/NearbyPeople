package com.csci515.subik.peoplenearby.parsing;

/**
 * Created by subik on 3/23/18.
 */

public class Appointment {

    private String from_name, to_name, time, latitude, longitude, resturant_name, resturant_address;
    private int status;

    public Appointment(String from_name, String to_name, String time, String latitude, String longitude, String resturant_name, String resturant_address, int status) {
        this.from_name = from_name;
        this.to_name = to_name;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.resturant_name = resturant_name;
        this.resturant_address = resturant_address;
        this.status = status;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getResturant_name() {
        return resturant_name;
    }

    public void setResturant_name(String resturant_name) {
        this.resturant_name = resturant_name;
    }

    public String getResturant_address() {
        return resturant_address;
    }

    public void setResturant_address(String resturant_address) {
        this.resturant_address = resturant_address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
