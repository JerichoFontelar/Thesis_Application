package com.example.myapplication;

import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.osmdroid.bonuspack.kml.KmlGeometry.parseGeoJSON;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.Manifest;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.osmdroid.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExploreFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private Preference magnitudeDialog, depthDialog, dateRangeDialog;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay mLocationOverlay;
    private MapView map = null;

    FloatingActionButton fab1;

    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        Context ctx = requireContext(); //Context ctx = getApplicationContext();;
        Activity act = requireActivity(); //Activity variable

        // Load osmdroid configuration before setting content view
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));



        //Map initialization
        MapView map = (MapView) view.findViewById(R.id.map);
        getMap(map);

        //Location Identification
        //Set all properties for location request
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(act);


        // Set map tile source, enable zoom controls, and multi-touch zooming
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);

        //request permission
        requestRuntimePermission();

        GeoPoint userPoint = new GeoPoint(12.85, 123.74);
        IMapController mapController = map.getController();
        mapController.animateTo((IGeoPoint) userPoint);
        //mapController.setCenter(userPoint);
        mapController.zoomTo(8.0);//6.5

//        GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
//        Marker marker = new Marker(map);
//        marker.setPosition(geoPoint);
        //marker.setIcon(R.drawable.home_pin_fill0_wght400_grad0_opsz24);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);


        ArrayList<Marker> markers = new ArrayList<>();

        double[][] points = new double[][] {
                {8.78, 126.97}, // Point 78739 coordinates (lat, lng)
                {8.8, 126.7}, // Point 78740 coordinates (lat, lng)
                {8.42, 126.59}, // Point 78741 coordinates (lat, lng)
                {8.6, 127.22}, // Point 78742 coordinates (lat, lng)
                {8.62, 127.13}, // Point 78743 coordinates (lat, lng)
                {10.86, 126.36}, // Point 78744 coordinates (lat, lng)
                {8.15, 127.06}, // Point 78745 coordinates (lat, lng)
                {8.73, 126.6}, // Point 78746 coordinates (lat, lng)
// Point 2 coordinates (lat, lng)
                // ... Add more points following the same format
        };
        // Assuming you have an array of coordinates (lat, lng) in `points`
        for (double[] point : points) {
            GeoPoint pointToMark = new GeoPoint(point[0], point[1]); // Access lat and lng from array
            Marker redDotMarker = new Marker(map);
            redDotMarker.setPosition(pointToMark);
            Drawable redDotIcon = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.red_dot_icon, ctx.getTheme());
            redDotMarker.setIcon(redDotIcon);

            // Add marker to the ArrayList
            markers.add(redDotMarker);
        }



        // Add all markers to the map
        map.getOverlays().addAll(markers);
        //map.invalidate();

        KmlDocument kmlDocument = new KmlDocument();
        // Read data from a file
        FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument
                .mKmlRoot
                .buildOverlay(map, null, null, kmlDocument);



        map.getOverlays().add(kmlOverlay);



        //Floating Action Button Initiation
        FloatingActionButton fabLocation = view
                .findViewById(R.id.fab_location);
        FloatingActionButton fabSetting = view
                .findViewById(R.id.fab_settings);
        //Bottom Navigation Initialization
        BottomAppBar bar = view.findViewById(R.id.bottomAppBar);


        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showDialog();

            }
        });

        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), SettingsActivity.class));
            }
        });
        // Inflate the layout for this fragment
        return view;
    }



    private void showDialog(){

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottom_sheet);

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                }
            }
        });
    }


    public void getMap(MapView mapView){
        map = mapView;
    }
    private void requestRuntimePermission(){

        Context context = requireActivity();

        if (ContextCompat.checkSelfPermission(context, FINE_LOCATION_PERMISSION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                WRITE_EXTERNAL_STORAGE_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request both permissions together
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    FINE_LOCATION_PERMISSION,
                    WRITE_EXTERNAL_STORAGE_PERMISSION
            }, REQUEST_PERMISSIONS_REQUEST_CODE);

        }else if((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), FINE_LOCATION_PERMISSION))){

            AlertDialog.Builder builder = new AlertDialog.Builder((context));
            builder.setMessage("This app requires Location and Write permission for particular feature to work as expected.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) ->{
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{FINE_LOCATION_PERMISSION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

            builder.show();

        }else if((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), WRITE_EXTERNAL_STORAGE_PERMISSION))){

            AlertDialog.Builder builder = new AlertDialog.Builder((context));
            builder.setMessage("This app requires Location and Write permission for particular feature to work as expected.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) ->{
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{WRITE_EXTERNAL_STORAGE_PERMISSION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

            builder.show();

        } else{
            ActivityCompat.requestPermissions(requireActivity(), new String[]{FINE_LOCATION_PERMISSION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Context ctx = getContext();
        Activity act = getActivity();


        if(requestCode==FINE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(ctx, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }


}