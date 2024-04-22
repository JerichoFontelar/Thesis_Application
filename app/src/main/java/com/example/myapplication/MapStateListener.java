package com.example.myapplication;

import org.osmdroid.views.MapView;

import java.util.Map;

public interface MapStateListener {
    void saveMapState(MapView mapView);
    void restoreMapState(MapView mapView);

}
