package com.example.myapplication.main_screens;

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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;

import com.example.myapplication.MapListener.MapSettingsViewModel;
import com.example.myapplication.MapListener.RequiresMapReload;
import com.example.myapplication.earthquake_data_retrieval.EarthquakeData;
import com.example.myapplication.R;
import com.example.myapplication.shared_preferences.SettingsActivity;
import com.example.myapplication.shared_preferences.SharedPreferenceDataSource;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ExploreFragment extends Fragment implements RequiresMapReload {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay mLocationOverlay;
    private MapView map = null;


    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    private SharedPreferenceDataSource sharedPrefDataSource;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public SharedPreferences earthquake_settings;
    public SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Disposable mapReloadDisposable;
    private Disposable mapOverlayDisposable;
    private MapSettingsViewModel mapSettingsViewModel;
    private static final String PREPROCESSED_DATA_FILE = "preprocessed_earthquake_data.json"; // Adjust filename as needed
    private static final String SHARED_PREF_FILTER_APPLIED = "filter_applied"; // Flag to check if filter applied


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mapSettingsViewModel = new ViewModelProvider(requireActivity()).get(MapSettingsViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mapOverlayDisposable != null && !mapOverlayDisposable.isDisposed()) {
//            mapOverlayDisposable.dispose();
//        }

        // Unregister shared preference listener
        //earthquake_settings.unregisterOnSharedPreferenceChangeListener(listener);

    }

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

        // Register shared preference change listener
//        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                if (key.equals("minMag") || key.equals("maxMag") || key.equals("minDepth") ||
//                        key.equals("maxDepth") || key.equals("minDate") || key.equals("maxDate")) {
//                    setDatabaseFromJSON(); // Trigger map reload on preference change
//                }
//            }
//        };
//        sharedPrefDataSource.registerOnSharedPreferenceChangeListener(listener);

        double[] magnitudeRange = sharedPrefDataSource.getMagnitudeRange();
        double minMag = magnitudeRange[0]; // Access the first element (index 0) for minimum value
        double maxMag = magnitudeRange[1]; // Access the second element (index 1) for maximum value
        Log.d("MyFragment", "Magnitude Range: " + minMag + "-" + maxMag);


        //Map initialization
        MapView map = view.findViewById(R.id.map);
        getMap(map);


        setDatabaseFromJSON();
        //Location Identification
        //Set all properties for location request
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(act);


        // Set map tile source, enable zoom controls, and multi-touch zooming
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        //request permission
        requestRuntimePermission();

        GeoPoint userPoint = new GeoPoint(12.85, 123.74);
        IMapController mapController = map.getController();
        mapController.animateTo(userPoint);
        //mapController.setCenter(userPoint);
        mapController.zoomTo(5.5);//6.5

//        GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
//        Marker marker = new Marker(map);
//        marker.setPosition(geoPoint);
//        marker.setIcon(R.drawable.home_pin_fill0_wght400_grad0_opsz24);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);


        // Add all markers to the map
        //map.getOverlays().addAll(markers);
        //map.invalidate();
//

        try {
            KmlDocument kmlDocument = new KmlDocument();

            // Read data from a file
            FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument
                    .mKmlRoot
                    .buildOverlay(map, null, null, kmlDocument);

            File localFile = kmlDocument.getDefaultPathForAndroid(getContext(), "raw/active_faults_2015.json");
            kmlDocument.saveAsGeoJSON(localFile);

            map.getOverlays().add(kmlOverlay);
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();  // For debugging, print stack trace
            Log.d("Map Error", e.getMessage());
        }


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
//                try {
//                    setDatabaseFromJson();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });

//        mapReloadDisposable = mapSettingsViewModel.getMapReloadObservable()
//                .subscribe(needsReload -> {
//                    if (needsReload) {
//                        Log.d("MyFragment", "Map reload triggered");
//                        // Reload map data or update map tiles based on changed settings
//                        // (e.g., filter data based on new magnitude/depth range, etc.)
//                        // This might involve fetching new data or updating map layers
//                        onMapReload(); // Call your map reload logic here
//                        setDatabaseFromJSON();
//                        //map.invalidate();
//                    }
//                });
        // Inflate the layout for this fragment
        return view;
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chooser);


        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.TOP);

        //map.getOverlays().clear();

    }


    public void wave_progress(){

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
                if (location != null) {
                    currentLocation = location;
                }
            }
        });
    }


    public void getMap(MapView mapView) {
        map = mapView;
    }

    private void requestRuntimePermission() {

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

        } else if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), FINE_LOCATION_PERMISSION))) {

            AlertDialog.Builder builder = new AlertDialog.Builder((context));
            builder.setMessage("This app requires Location and Write permission for particular feature to work as expected.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{FINE_LOCATION_PERMISSION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

            builder.show();

        } else if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), WRITE_EXTERNAL_STORAGE_PERMISSION))) {

            AlertDialog.Builder builder = new AlertDialog.Builder((context));
            builder.setMessage("This app requires Location and Write permission for particular feature to work as expected.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{WRITE_EXTERNAL_STORAGE_PERMISSION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

            builder.show();

        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{FINE_LOCATION_PERMISSION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Context ctx = getContext();
        Activity act = getActivity();


        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(ctx, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapReloadDisposable = mapSettingsViewModel.getMapReloadObservable()
                .subscribe(needsReload -> {
                    if (needsReload) {
                        Log.d("MyFragment", "Map reload triggered");
                        // Reload map data or update map tiles based on changed settings
                        // (e.g., filter data based on new magnitude/depth range, etc.)
                        // This might involve fetching new data or updating map layers
                        onMapReload(); // Call your map reload logic here
                    }
                });
        map.onResume(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapReloadDisposable != null && !mapReloadDisposable.isDisposed()) {
            mapReloadDisposable.dispose();
        }
        map.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }

    public void setDatabaseFromJSON() {
        //showDialog();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("earthquake_setting", Context.MODE_PRIVATE);
        sharedPrefDataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);

        Observable<List<EarthquakeData>> filteredDataObservable = Observable.create(emitter -> {
            // Use a JSON parsing library like GSON
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open("database/data_with_millis.json");
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(inputStream);

            try {
                EarthquakeData[] earthquakeDataArray = gson.fromJson(reader, EarthquakeData[].class);

                double[] magnitudeRange = sharedPrefDataSource.getMagnitudeRange();
                int[] depthRange = sharedPrefDataSource.getDepthRange();
                long[] dateRange = sharedPrefDataSource.getLongDateRange();

                List<EarthquakeData> filteredData = new ArrayList<>();
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
                emitter.onNext(filteredData);
                emitter.onComplete();
            } finally {
                inputStream.close();
            }
        });

        mapOverlayDisposable = filteredDataObservable
                .subscribeOn(Schedulers.io())  // Perform data processing on background thread
                .observeOn(Schedulers.newThread()) // Create markers on separate thread (optional)
                .subscribe(this::createMapOverlays, this::handleError); // Handle success and error
    }

    private void createMapOverlays(List<EarthquakeData> filteredData) {
        for (EarthquakeData earthquake : filteredData) {
            double magnitude = earthquake.getMagnitude();
            int depth = earthquake.getDepth();

            int color = getColor(depth, sharedPrefDataSource.getDepthRange());
            int radius = getRadius(magnitude, sharedPrefDataSource.getMagnitudeRange());

            Marker marker = new Marker(map); // Replace context with appropriate reference

            // Set marker position based on earthquake coordinates (assuming getters exist)
            marker.setPosition(new GeoPoint(earthquake.getLatitude(), earthquake.getLongitude()));
            marker.setTitle(String.format("M: %.1f, Depth: %d km", magnitude, depth)); // Set marker title

            // Set marker color and radius based on calculated values
            marker.setIcon(createMarkerIcon(color, radius));


            map.getOverlays().add(marker);
        }
        //waitForProgress();
    }

    private void handleError(Throwable throwable) {
        // Handle data retrieval or filtering error (e.g., display Toast message)
        Log.e("MyFragment", "Error setting database from JSON", throwable);
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

    private int getRadius(double magnitude, double[] magnitudeRange) {
        // Normalize magnitude to range between 0 and 1
        double normalizedMagnitude = (magnitude - magnitudeRange[0]) / (magnitudeRange[1] - magnitudeRange[0]);
        //double normalizedMagnitude = (magnitude - 1.0) / (7.4 - 1.0);
        // Define radius range based on normalized magnitude (e.g., larger for higher magnitudes)
        int minRadius = 10;
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


    @Override
    public void onMapReload() throws IOException {
        map.getOverlays().clear();
    }
}