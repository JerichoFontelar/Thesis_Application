package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceDataSource {
    static SharedPreferenceDataSource instance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPreferenceDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public static SharedPreferenceDataSource getInstance(SharedPreferences sharedPreferences) {
        if (instance == null) {
            instance = new SharedPreferenceDataSource(sharedPreferences);
        }
        return instance;
    }

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences("Earthquake_Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setMagnitudeRange(double minMag, double maxMag) {
        editor.putFloat("minMag", (float) minMag);
        editor.putFloat("maxMag", (float) maxMag);
        editor.apply();
    }

    public double[] getMagnitudeRange() {
        double[] range = new double[2];
        range[0] = sharedPreferences.getFloat("minMag", 5.0F);
        range[1] = sharedPreferences.getFloat("maxMag", 7.4F);
        return range;
    }

    public void setDepthRange(int minDepth, int maxDepth) {
        editor.putInt("minDepth", minDepth);
        editor.putInt("maxDepth", maxDepth);
        editor.apply();
    }

    public int[] getDepthRange() {
        int[] range = new int[2];
        range[0] = sharedPreferences.getInt("minDepth", 0);
        range[1] = sharedPreferences.getInt("maxDepth", 199);
        return range;
    }

    public void setDateRange(String minDate, String maxDate) {
        editor.putString("minDate", minDate);
        editor.putString("maxDate", maxDate);
        editor.apply();
    }

    public void setLongDateRange(long minDate, long maxDate) {
        editor.putLong("minDate", minDate);
        editor.putLong("maxDate", maxDate);
    }

    public long[] getLongDateRange() {
        long[] range = new long[2];
        range[0] = sharedPreferences.getLong("minDate", 1483200000000L);
        range[1] = sharedPreferences.getLong("maxDate", 1704038340000L);
        return range;
    }

    public String[] getDateRange() {
        String[] range = new String[2];
        range[0] = sharedPreferences.getString("minDate", "2017-01-01");
        range[1] = sharedPreferences.getString("maxDate", "2023-12-31");
        return range;
    }

    public void setClusteringAlgorithm(String algorithm) {
        editor.putString("clusteringAlgorithm", algorithm);
        editor.apply();
    }

    public String getClusteringAlgorithm() {
        return sharedPreferences.getString("clusteringAlgorithm", "DBSCAN");
    }

    public void setTimeSeriesAlgorithm(String algorithm) {
        editor.putString("timeSeriesAlgorithm", algorithm);
        editor.apply();
    }

    public String getTimeSeriesAlgorithm() {
        return sharedPreferences.getString("timeSeriesAlgorithm", "LSTM");
    }
}
