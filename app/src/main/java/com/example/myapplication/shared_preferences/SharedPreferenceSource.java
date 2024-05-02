package com.example.myapplication.shared_preferences;

public interface SharedPreferenceSource {
    // Methods to access and modify shared preferences
    double[] getMagnitudeRange();
    void setMagnitudeRange(double minMag, double maxMag);

    int[] getDepthRange();
    void setDepthRange(int minDepth, int maxDepth);

    long[] getLongDateRange();
    void setLongDateRange(long minDate, long maxDate);

    String getClusteringAlgorithm();
    void setClusteringAlgorithm(String algorithm);

    String getTimeSeriesAlgorithm();
    void setTimeSeriesAlgorithm(String algorithm);
}
