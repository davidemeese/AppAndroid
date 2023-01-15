package com.dam.tfg.model;

public class DataToSend {

    private String lat;
    private String lon;
    private String mat;
    private String vel;

    public DataToSend(String lat, String lon, String mat, String vel) {
        this.lat = lat;
        this.lon = lon;
        this.mat = mat;
        this.vel = vel;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getVel() {
        return vel;
    }

    public void setVel(String vel) {
        this.vel = vel;
    }

}
