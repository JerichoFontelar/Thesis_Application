package com.example.myapplication.earthquake_data_retrieval;

public class VolcanoData {
    private String volcano;
    private double latitude;
    private double longitude;
    private String province;

    public VolcanoData(String volcano, double latitude, double longitude, String province) {
        this.volcano = volcano;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }

    // Getters and setters
    public String getVolcano() {
        return volcano;
    }

    public void setVolcano(String volcano) {
        this.volcano = volcano;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
