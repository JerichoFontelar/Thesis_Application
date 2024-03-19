package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "earthquake_database")

public class Earthquake {


    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "Date")
    String Date;

    @ColumnInfo(name = "Day")
    int Day;

    @ColumnInfo(name = "Month")
    String Month;


    @ColumnInfo(name = "Year")
    int Year;

    @ColumnInfo(name = "Time")
    String Time;

    @ColumnInfo(name = "Latitude")
    double Latitude;

    @ColumnInfo(name = "Longitude")
    double Longitude;

    @ColumnInfo(name = "Depth")
    int Depth;

    @ColumnInfo(name = "Mag")
    double Magnitude;

    @ColumnInfo(name = "Location")
    String Location;

    @ColumnInfo(name = "Direction")
    String Direction;

    @ColumnInfo(name = "Province")
    String Province;

    @ColumnInfo(name = "Degrees")
    String Degrees;

    @Ignore
    public Earthquake() {
    }

    public Earthquake(String Date, int Day, String Month,
                      int Year, String Time, double Latitude, double Longitude,
                      int Depth, double Magnitude, String Location,
                      String Direction, String Province, String Degrees) {

        this.Date = Date;
        this.Day = Day;
        this.Month = Month;
        this.Year = Year;
        this.Time = Time;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Depth = Depth;
        this.Magnitude = Magnitude;
        this.Location = Location;
        this.Direction = Direction;
        this.Province = Province;
        this.Degrees = Degrees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        this.Day = day;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        this.Month = month;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        this.Year = year;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public int getDepth() {
        return Depth;
    }

    public void setDepth(int depth) {
        this.Depth = depth;
    }

    public double getMagnitude() {
        return Magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.Magnitude = magnitude;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        this.Direction = direction;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        this.Province = province;
    }

    public String getDegrees() {
        return Degrees;
    }

    public void setDegrees(String degrees) {
        this.Degrees = degrees;
    }

}
