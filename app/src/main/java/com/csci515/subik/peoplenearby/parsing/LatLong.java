package com.csci515.subik.peoplenearby.parsing;

/**
 * Created by subik on 3/13/18.
 */

public class LatLong {
    int id, cus_id;
    String latitude, longitude;

    public LatLong(int id, int cus_id, String latitude, String longitude) {
        this.id = id;
        this.cus_id = cus_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
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
}
