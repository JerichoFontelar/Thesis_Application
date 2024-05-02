package com.example.myapplication.earthquake_data_retrieval;

public class ClusterData {

    int id;
    Long Date;
    int Day;
    String Month;
    int Year;
    String Time;
    double Latitude;
    double Longitude;
    int Depth;
    double Mag;
    String Location;
    String Direction;
    String Province;
    double Distance;
    String Degrees;
    int y;


    public ClusterData() {
    }

    public ClusterData(Long Date, int Day, String Month,
                  int Year, String Time, double Latitude, double Longitude,
                  int Depth, double Magnitude, String Location,
                  String Direction, String Province, double Distance, String Degrees, int y) {

        this.Date = Date;
        this.Day = Day;
        this.Month = Month;
        this.Year = Year;
        this.Time = Time;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Depth = Depth;
        this.Mag = Magnitude;
        this.Location = Location;
        this.Direction = Direction;
        this.Province = Province;
        this.Distance = Distance;
        this.Degrees = Degrees;
        this.y = y;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Long getDate() {
        return Date;
    }

    public void setDate(Long date) {
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
        return Mag;
    }

    public void setMagnitude(int magnitude) {
        this.Mag = magnitude;
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

    public double getDistance() {
        return Distance;
    }

    public void setDistance(int distance) {
        this.Distance = distance;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
