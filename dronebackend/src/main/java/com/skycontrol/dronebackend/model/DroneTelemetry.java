package com.skycontrol.dronebackend.model;

public class DroneTelemetry {
    private int droneId;
    private double lat;
    private double lng;
    private double altitude;
    private double speed;
    private int battery;

    public DroneTelemetry() {
    }

    public DroneTelemetry(int droneId, double lat, double lng, double altitude, double speed, int battery) {
        this.droneId = droneId;
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;
       	this.speed = speed;
        this.battery = battery;
    }

    public int getDroneId() {
        return droneId;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public double getAltitude() {
        return altitude;
    }
    public double getSpeed() {
        return speed;
    }
    public int getBattery() {
        return battery;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setBattery(int battery) {
        this.battery = battery;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
}
