package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class EarthquakeData implements Parcelable {

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
    String Degrees;

    public EarthquakeData() {
    }

    public EarthquakeData(Long Date, int Day, String Month,
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
        this.Mag = Magnitude;
        this.Location = Location;
        this.Direction = Direction;
        this.Province = Province;
        this.Degrees = Degrees;
    }

    protected EarthquakeData(Parcel in) {
        id = in.readInt();
        Date = in.readLong();
        Day = in.readInt();
        Month = in.readString();
        Year = in.readInt();
        Time = in.readString();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        Depth = in.readInt();
        Mag = in.readDouble();
        Location = in.readString();
        Direction = in.readString();
        Province = in.readString();
        Degrees = in.readString();
    }

    public static final Creator<EarthquakeData> CREATOR = new Creator<EarthquakeData>() {
        @Override
        public EarthquakeData createFromParcel(Parcel in) {
            return new EarthquakeData(in);
        }

        @Override
        public EarthquakeData[] newArray(int size) {
            return new EarthquakeData[size];
        }
    };

    public int getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(Date);
        dest.writeInt(Day);
        dest.writeString(Month);
        dest.writeInt(Year);
        dest.writeString(Time);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeInt(Depth);
        dest.writeDouble(Mag);
        dest.writeString(Location);
        dest.writeString(Direction);
        dest.writeString(Province);
        dest.writeString(Degrees);
    }
}
