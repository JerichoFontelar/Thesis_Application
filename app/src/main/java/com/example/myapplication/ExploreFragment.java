package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.room.Room;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class ExploreFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private Preference magnitudeDialog, depthDialog, dateRangeDialog;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay mLocationOverlay;
    private MapView map = null;


    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    private SharedPreferenceDataSource sharedPrefDataSource;
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

        // Get SharedPreferences from context
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("earthquake_setting", MODE_PRIVATE);
        // Get singleton instance of data source
        sharedPrefDataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);

        double[] magnitudeRange = sharedPrefDataSource.getMagnitudeRange();
        double minMag = magnitudeRange[0]; // Access the first element (index 0) for minimum value
        double maxMag = magnitudeRange[1]; // Access the second element (index 1) for maximum value
        Log.d("MyFragment", "Magnitude Range: " + minMag + "-"+ maxMag);




        //Map initialization
        MapView map = view.findViewById(R.id.map);
        getMap(map);


        
        try {
            setDatabaseFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        mapController.animateTo(userPoint);
        //mapController.setCenter(userPoint);
        mapController.zoomTo(7.0);//6.5

//        GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
//        Marker marker = new Marker(map);
//        marker.setPosition(geoPoint);
        //marker.setIcon(R.drawable.home_pin_fill0_wght400_grad0_opsz24);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);



        // Add all markers to the map
        //map.getOverlays().addAll(markers);
        //map.invalidate();
//
//        KmlDocument kmlDocument = new KmlDocument();
//        // Read data from a file
//        FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument
//                .mKmlRoot
//                .buildOverlay(map, null, null, kmlDocument);
//
//
//
//        map.getOverlays().add(kmlOverlay);



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

    public void setDatabaseFromJson() throws IOException {
        // Use a JSON parsing library like GSON
        AssetManager assetManager = getContext().getAssets();
        InputStream inputStream = assetManager.open("database/final_earthquake_data.json");
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(inputStream);
        EarthquakeData[] earthquakeDataArray = gson.fromJson(reader, EarthquakeData[].class);
        Log.d("MyFragment", "Earthquake data array size: " + earthquakeDataArray.length);

        inputStream.close();

        //SharedPreferenceDataSource dataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);
        double[] magnitudeRange = sharedPrefDataSource.getMagnitudeRange();
        int[] depthRange = sharedPrefDataSource.getDepthRange();
        long[] dateRange = sharedPrefDataSource.getLongDateRange();

        List<EarthquakeData> filteredData = new ArrayList<>();
        Log.d("MyFragment", "Depth Range: " + depthRange[0] + "-"+ depthRange[1]);
        Log.d("MyFragment", "Date Range: " + dateRange[0] + "-"+ dateRange[1]);
        for (EarthquakeData earthquake : earthquakeDataArray) {
            double magnitude = earthquake.getMagnitude();
            int depth = earthquake.getDepth();
            long date = earthquake.getDate(); // Assuming date is a long in milliseconds

            if (magnitude >= magnitudeRange[0] && magnitude <= magnitudeRange[1] &&
                    depth >= depthRange[0] && depth <= depthRange[1] &&
                    date >= dateRange[0] && date <= dateRange[1]) {
                filteredData.add(earthquake);
            }
        }
        Log.d("MyFragment", "Earthquake: " + filteredData.size() );

        // Create markers and add them to mapview (assuming mapview is initialized)
        for (EarthquakeData earthquake : filteredData) {
            double magnitude = earthquake.getMagnitude();
            int depth = earthquake.getDepth();

            int color = getColor(depth, depthRange);
            int radius = getRadius(magnitude, magnitudeRange);

            Marker marker = new Marker(map); // Replace context with appropriate reference

            // Set marker position based on earthquake coordinates (assuming getters exist)
            marker.setPosition(new GeoPoint(earthquake.getLatitude(), earthquake.getLongitude()));
            marker.setTitle(String.format("M: %.1f, Depth: %d km", magnitude, depth)); // Set marker title

            // Set marker color and radius based on calculated values
            marker.setIcon(createMarkerIcon(color, radius));

            map.getOverlays().add(marker);
        }
    }

    // Helper functions for color and radius calculation
    private int getColor(int depth, int[] depthRange) {
        // Normalize depth to range between 0 and 1 (invert for lighter-to-darker)
        double normalizedDepth = 1.0 - ((depth - depthRange[0]) / (double) (depthRange[1] - depthRange[0]));

        // Define color gradient (e.g., yellow to red) based on normalized depth
        int[] colors = {Color.YELLOW, 0xFFFF9900, 0xFF800000}; // Darker red

        int color1 = colors[0]; // Starting color
        int color2 = colors[colors.length - 1]; // Ending color

        int alpha1 = (color1 >> 24) & 0xff;
        int red1 = (color1 >> 16) & 0xff;
        int green1 = (color1 >> 8) & 0xff;
        int blue1 = color1 & 0xff;

        int alpha2 = (color2 >> 24) & 0xff;
        int red2 = (color2 >> 16) & 0xff;
        int green2 = (color2 >> 8) & 0xff;
        int blue2 = color2 & 0xff;

        // Perform interpolation with additional checks for full range
        int alpha = interpolateColorComponent(alpha1, alpha2, normalizedDepth, 255);
        int red = interpolateColorComponent(red1, red2, normalizedDepth, 255);
        int green = interpolateColorComponent(green1, green2, normalizedDepth, 255);
        int blue = interpolateColorComponent(blue1, blue2, normalizedDepth, 255);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private int interpolateColorComponent(int color1, int color2, double fraction, int max) {
        int component = (int) (color1 + (fraction * (color2 - color1)));
        return Math.min(component, max); // Ensure component stays within 0-255 range
    }
    private int interpolateColor(int[] colors, double value) {
        // Ensure valid input (value between 0 and 1)
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Value must be between 0 and 1");
        }

        // Handle case with only one color
        if (colors.length == 1) {
            return colors[0];
        }

        // Calculate index of the starting color
        float index = (float) Math.floor(value * (colors.length - 1));

        // Extract start and end colors
        int color1 = colors[(int) index];
        int color2 = colors[(int) Math.ceil(index)];

        // Extract color components (assuming ARGB format)
        int alpha1 = (color1 >> 24) & 0xff;
        int red1 = (color1 >> 16) & 0xff;
        int green1 = (color1 >> 8) & 0xff;
        int blue1 = color1 & 0xff;

        int alpha2 = (color2 >> 24) & 0xff;
        int red2 = (color2 >> 16) & 0xff;
        int green2 = (color2 >> 8) & 0xff;
        int blue2 = color2 & 0xff;

        // Perform linear interpolation for each color component
        float fraction = (float) (value - index);
        int alpha = (int) (alpha1 + (fraction * (alpha2 - alpha1)));
        int red = (int) (red1 + (fraction * (red2 - red1)));
        int green = (int) (green1 + (fraction * (green2 - green1)));
        int blue = (int) (blue1 + (fraction * (blue2 - blue1)));

        // Combine interpolated components back into ARGB format
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }


    private int getRadius(double magnitude, double[] magnitudeRange) {
        // Normalize magnitude to range between 0 and 1
        double normalizedMagnitude = (magnitude - magnitudeRange[0]) / (magnitudeRange[1] - magnitudeRange[0]);

        // Define radius range based on normalized magnitude (e.g., larger for higher magnitudes)
        int minRadius = 1;
        int maxRadius = 50;
        return (int) (minRadius + (normalizedMagnitude * (maxRadius - minRadius)));
    }

    // Helper function for creating marker icon (assuming interpolateColor exists)
    private Drawable createMarkerIcon(int color, int radius) {
        Bitmap markerBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(markerBitmap);

        int strokeWidth = radius / 10; // Adjust stroke width as needed

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); // Set paint style to stroke
        paint.setStrokeWidth(strokeWidth);   // Set stroke width
        paint.setColor(Color.BLACK);       // Set outline color (adjust as needed)
        canvas.drawCircle(radius, radius, radius - strokeWidth / 2f, paint); // Draw stroke

        paint.setStyle(Paint.Style.FILL);    // Set paint style back to fill
        paint.setColor(color);              // Set fill color
        paint.setAlpha(215);                 // Set transparency (optional)
        canvas.drawCircle(radius, radius, radius - strokeWidth, paint); // Draw filled circle

        return new BitmapDrawable(requireContext().getResources(), markerBitmap);
    }


}