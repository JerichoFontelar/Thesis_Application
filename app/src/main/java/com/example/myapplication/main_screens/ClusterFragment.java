package com.example.myapplication.main_screens;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.earthquake_data_retrieval.ClusterData;
import com.example.myapplication.recycler_view_horizontal.ChildModelClass;
import com.example.myapplication.recycler_view_horizontal.ParentAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentItemListener;
import com.example.myapplication.recycler_view_horizontal.ParentModelClass;
import com.example.myapplication.recycler_view_vertical.VerticalChildModelClass;
import com.example.myapplication.recycler_view_vertical.VerticalParentAdapter;
import com.example.myapplication.recycler_view_vertical.VerticalParentModelClass;
import com.example.myapplication.recycler_view_horizontal.ChildItemListener;
import com.example.myapplication.shared_preferences.SharedPreferenceDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClusterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterFragment extends Fragment implements ChildItemListener {


    RecyclerView verticalRecyclerView;
    ArrayList<VerticalParentModelClass> verticalParentModelClassArrayList;
    ArrayList<VerticalChildModelClass> verticalChildModelClassArrayList;

    ArrayList<VerticalChildModelClass> verticalClusters;

    RecyclerView recyclerView;
    ArrayList<ParentModelClass> parentModelClassArrayList;
    ArrayList<ChildModelClass> childModelClassArrayList;

    ArrayList<ChildModelClass> bestModelAccordingToSS;
    ArrayList<ChildModelClass> bestModelAccordingToDBI;
    ArrayList<ChildModelClass> bestModelAccordingToMI;
    ArrayList<ChildModelClass> latestList;

    ParentItemListener parentItemListener;

    //private MarkerOverlayDisposable mapOverlayDisposable;
    private SharedPreferences sharedPreferences;
    private SharedPreferenceDataSource sharedPrefDataSource;

    private MapView map = null;

    private MapView mapCluster;
    private Disposable mapOverlayDisposable;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String retrievedData = "";

    public ClusterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClusterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClusterFragment newInstance(String param1, String param2) {
        ClusterFragment fragment = new ClusterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clear references and potentially unsubscribe from RxJava disposables
        verticalRecyclerView = null;
        if (mapOverlayDisposable != null && !mapOverlayDisposable.isDisposed()) {
            mapOverlayDisposable.dispose();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("earthquake_setting", Context.MODE_PRIVATE);
        SharedPreferenceDataSource sharedPrefDataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);

        String model = sharedPrefDataSource.getClusteringAlgorithm();
        Log.d("ClusterFragment", "Model: " + model);

        if (Objects.equals(model, "Mini Batch K-Means")) {
            setOtherModelsForMiniBatch();
            //setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
            setDBSCAN("database/ClusterModels/MiniBatch_Model_1.7.1.json");
            //setMiniBatch();
            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
        } else if (Objects.equals(model, "DBSCAN")) {
            //setMiniBatch();
            setOtherModelsForDBSCAN();
            setDBSCAN("database/DBSCANmodels/clustering_dbscan1.1.json");
            setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.1.json", map);
        }
        //setMiniBatch();
        //setOtherModelsForMiniBatch();


//        try {
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setAdapter(parentAdapter);
//            parentAdapter.notifyDataSetChanged();
//        }catch (Exception e){
//            Log.d("ClusterFragment", "RecyclerView not found!");
//            Log.d("ClusterFragment", e.getMessage());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cluster, container, false);

        Activity act = requireActivity(); //Activity variable
        Context ctx = requireContext(); //Context ctx = getApplicationContext();;
        // Load osmdroid configuration before setting content view
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //Map initialization
        map = view.findViewById(R.id.map_cluster);
        setMap(map);

        // Set map tile source, enable zoom controls, and multi-touch zooming
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);

        //map.setNestedScrollingEnabled(false);

        GeoPoint userPoint = new GeoPoint(12.85, 123.74);
        IMapController mapController = map.getController();
        mapController.animateTo(userPoint);
        //mapController.setCenter(userPoint);
        mapController.zoomTo(5.5);//6.5


        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        //setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.5.1.json", map);
//        if(onChildItemClicked(0, 0)==0){
//            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
//            Toast.makeText(getContext(), "Model 1.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
//        }else if(onChildItemClicked(1, 1) == 1) {
//            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.5.1.json", map);
//            Toast.makeText(getContext(), "Model 2.5 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
//        }else if(onChildItemClicked(2, 2) == 2) {
//            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.7.1.json", map);
//            Toast.makeText(getContext(), "Model 2.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
//        }else{
//            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.5.1.json", map);
//            Toast.makeText(getContext(), "Model 1.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
//        }


        return view;
    }

    public void setMap(MapView mapView) {
        mapCluster = mapView;
    }

    public MapView getMap() {
        return mapCluster;
    }

    public void setDatabaseFromJSON(String path, MapView mapView) {
        //showDialog();
        Log.d("ClusterFragment", "Loading data from JSON file: " + path);
        Log.d("ClusterFragment", "Map View: " + mapView);

        Observable<List<ClusterData>> filteredDataObservable = Observable.create(emitter -> {
            // Use a JSON parsing library like GSON
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open(path); //"assets/database/ClusterModels/MiniBatch_Model_1.7.json"
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(inputStream);

            try {
                ClusterData[] earthquakeDataArray = gson.fromJson(reader, ClusterData[].class);

                List<ClusterData> allData = new ArrayList<>(); // Create a list to hold all data
                for (ClusterData earthquake : earthquakeDataArray) {
                    allData.add(earthquake);  // Add all earthquake data to the list
                }

                emitter.onNext(allData); // Emit the complete list of earthquake data
                emitter.onComplete();
            } finally {
                inputStream.close();
            }
        });

        mapOverlayDisposable = filteredDataObservable
                .subscribeOn(Schedulers.io())  // Perform data processing on background thread
                .observeOn(Schedulers.newThread()) // Create markers on separate thread (optional)
                .subscribe(data -> createMapOverlays(mapView, data), this::handleError); // Handle success and error
    }

    private void createMapOverlays(MapView mapView, List<ClusterData> filteredData) {
        mapView.getOverlays().clear(); // Clear existing overlays
        //setDBSCAN(filteredData);
        for (ClusterData earthquake : filteredData) {
            int cluster = earthquake.getY(); // Assuming `y` holds the cluster value
            double mag = earthquake.getMagnitude();
            double depth = earthquake.getDepth();
            String province = earthquake.getProvince();

            int color = getColor(cluster);
            int radius = 10;//getRadius(earthquake.getMagnitude()); // Assuming `getMagnitude` exists

            //mapView.getOverlays().clear();
            Marker marker = new Marker(mapView); // Replace context with appropriate reference

            // Set marker position based on earthquake coordinates (assuming getters exist)
            marker.setPosition(new GeoPoint(earthquake.getLatitude(), earthquake.getLongitude()));
            marker.setTitle(String.format("Cluster: %d Magnitude: %.2f Depth: %.2f", cluster + 1, mag, depth)); // Set marker title

            // Set marker color and radius based on calculated values
            marker.setIcon(createMarkerIcon(color, radius));
            mapView.getOverlays().add(marker);
        }
        //waitForProgress();
        Log.d("ClusterFragment", "Map Overlays created, filtered data: " + filteredData.size());
    }

    // Helper functions for color and radius calculation

    private int getColor(int cluster) {
        switch (cluster) {
            case 0:
                return Color.argb(255, 204, 85, 0);
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.MAGENTA;
            case 4:
                return Color.CYAN;
            default:
                return Color.GRAY; // Default color for unknown clusters
        }
    }


    // Helper function for creating marker icon (assuming interpolateColor exists)
    private Drawable createMarkerIcon(int color, int radius) {
        Bitmap markerBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888); // Use ARGB_8888 for transparency
        Canvas canvas = new Canvas(markerBitmap);

        int strokeWidth = radius / 10; // Adjust stroke width as needed

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); // Set paint style to stroke
        paint.setStrokeWidth(strokeWidth);   // Set stroke width
        paint.setColor(Color.BLACK);       // Set outline color (adjust as needed)
        canvas.drawCircle(radius, radius, radius - strokeWidth / 2f, paint); // Draw stroke

        paint.setStyle(Paint.Style.FILL);    // Set paint style back to fill
        // Set a transparent color with desired alpha value (0-255)
        paint.setColor(color | (0x00000000));  // Combine color with alpha (0 for fully transparent)
        paint.setAlpha(128);                 // Set transparency (optional, adjust as needed)
        canvas.drawCircle(radius, radius, radius - strokeWidth, paint); // Draw filled circle
        Log.d("ClusterFragment", "Marker Icon created");
        return new BitmapDrawable(requireContext().getResources(), markerBitmap);
    }


    @Override
    public void onChildItemClicked(int parentPosition, int childPosition) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("earthquake_setting", Context.MODE_PRIVATE);
        SharedPreferenceDataSource sharedPrefDataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);

        String model = sharedPrefDataSource.getClusteringAlgorithm();
        Log.d("Model", "Model: " + model);

        if (Objects.equals(model, "Mini Batch K-Means")) {
            if (childPosition == 0) {
                //map.getOverlays().clear();
                Log.d("ClusterFragment", "Model 1.7 is selected, please wait for the map to load");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
                setDBSCAN("database/ClusterModels/MiniBatch_Model_1.7.1.json");
                Toast.makeText(getContext(), "Model 1.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 1) {
                //map.getOverlays().clear();
                Log.d("ClusterFragment", "Model 2.5 is selected, please wait for the map to load");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.5.1.json", map);
                setDBSCAN("database/ClusterModels/MiniBatch_Model_2.5.1.json");
                Toast.makeText(getContext(), "Model 2.5 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 2) {
                //map.getOverlays().clear();
                Log.d("ClusterFragment", "Model 2.7 is selected, please wait for the map to load");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.7.1.json", map);
                setDBSCAN("database/ClusterModels/MiniBatch_Model_2.7.1.json");
                Toast.makeText(getContext(), "Model 2.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            }
        } else {
            if (childPosition == 0) {
                //map.getOverlays().clear();
                Log.d("ClusterFragment", "Model 1.1 is selected, please wait for the map to load");
                setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.1.json", map);
                setDBSCAN("database/DBSCANmodels/clustering_dbscan1.1.json");
                Toast.makeText(getContext(), "Model 1.1 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 1) {
                //map.getOverlays().clear();
                Log.d("ClusterFragment", "Model 1.4 is selected, please wait for the map to load");
                setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.4.json", map);
                setDBSCAN("database/DBSCANmodels/clustering_dbscan1.4.json");
                Toast.makeText(getContext(), "Model 1.4 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setDBSCAN(String path) {
        verticalRecyclerView = getActivity().findViewById(R.id.rv_vertical); // Find by ID

        VerticalParentAdapter verticalParentAdapter;

        // Initialize empty Lists for parent and child data
        List<VerticalChildModelClass> verticalChildModelClassArrayList = new ArrayList<>();
        List<VerticalParentModelClass> verticalParentModelClassArrayList = new ArrayList<>();

        // HashMap to store clusters (key) and their unique child data (value)
        HashMap<Integer, HashSet<String>> uniqueProvincesPerCluster = new HashMap<>();

        // Set to store processed cluster numbers (for unique clusters)
        Set<Integer> processedClusters = new HashSet<>();

        // Map to track encountered provinces and their lowest cluster number
        HashMap<String, Integer> provinceClusterMap = new HashMap<>();

        // Load data from JSON file
        List<ClusterData> allData = parseDataFromJSON(path);

        // Iterate through allData, collecting unique clusters and building the HashMap
        for (ClusterData earthquake : allData) {
            int cluster = earthquake.getY(); // Assuming `y` holds the cluster value
            String province = earthquake.getProvince();

            // Check if cluster already exists in the HashMap
            if (!uniqueProvincesPerCluster.containsKey(cluster)) {
                uniqueProvincesPerCluster.put(cluster, new HashSet<>()); // Create a new HashSet for unique provinces
            }

            // Add province to the corresponding cluster's HashSet (only if unique within the cluster)
            if (!uniqueProvincesPerCluster.get(cluster).contains(province)) {
                uniqueProvincesPerCluster.get(cluster).add(province);
            }

            // Update provinceClusterMap (if not present or if lower cluster encountered)
            if (!provinceClusterMap.containsKey(province) || cluster < provinceClusterMap.get(province)) {
                provinceClusterMap.put(province, cluster);
            }
        }

        // **Explicitly handle Cluster 0**
        if (uniqueProvincesPerCluster.containsKey(0)) {
            String clusterName = "Cluster 1"; // Assuming clusters start from 1 (adjust as needed)
            List<VerticalChildModelClass> childData = new ArrayList<>();
            for (String uniqueProvince : uniqueProvincesPerCluster.get(0)) {
                childData.add(new VerticalChildModelClass(uniqueProvince));
            }
            verticalParentModelClassArrayList.add(new VerticalParentModelClass(clusterName, childData, 0));
        }

        // List to store cluster numbers in the order they appear
        List<Integer> clusterOrder = new ArrayList<>();
        for (ClusterData earthquake : allData) {
            int cluster = earthquake.getY(); // Assuming `y` holds the cluster value
            if (!processedClusters.contains(cluster)) {
                processedClusters.add(cluster);
                clusterOrder.add(cluster);
            }
        }

        // Create and add VerticalParentModelClass objects based on clusterOrder and uniqueProvincesPerCluster
        for (int clusterNumber : clusterOrder) {
            if (clusterNumber == 0) { // Skip Cluster 0 as it was handled explicitly
                continue;
            }
            String clusterName = "Cluster " + (clusterNumber + 1); // Assuming clusters start from 1
            List<VerticalChildModelClass> childData = new ArrayList<>();
            for (String uniqueProvince : uniqueProvincesPerCluster.get(clusterNumber)) {
                // Check if province is not already encountered in a lower cluster
                if (!provinceClusterMap.containsKey(uniqueProvince) || provinceClusterMap.get(uniqueProvince) == clusterNumber) {
                    childData.add(new VerticalChildModelClass(uniqueProvince));
                }
            }
            verticalParentModelClassArrayList.add(new VerticalParentModelClass(clusterName, childData, 0));
        }

        verticalClusters = new ArrayList<>(); // Not used in this approach

        verticalParentAdapter = new VerticalParentAdapter(verticalParentModelClassArrayList, ClusterFragment.this.getContext());
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //verticalRecyclerView.setLayoutManager(new CustomLayoutManager(ClusterFragment.this.getContext(), 5, LinearLayoutManager.HORIZONTAL, false));
        verticalRecyclerView.setAdapter(verticalParentAdapter);
        verticalParentAdapter.notifyDataSetChanged();
    }

    private List<ClusterData> parseDataFromJSON(String path) {
        List<ClusterData> earthquakeDataArray = new ArrayList<>();
        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open(path);
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(inputStream);

            earthquakeDataArray = gson.fromJson(reader, new TypeToken<List<ClusterData>>() {}.getType());
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return earthquakeDataArray;
    }



    public void setMiniBatch() {
        verticalRecyclerView = getActivity().findViewById(R.id.rv_vertical); // Find by ID
        verticalChildModelClassArrayList = new ArrayList<>();
        verticalParentModelClassArrayList = new ArrayList<>();
        verticalClusters = new ArrayList<>();
        VerticalParentAdapter verticalParentAdapter;
//
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));

        verticalParentModelClassArrayList.add(new VerticalParentModelClass("Cluster 1", verticalClusters, 1));

        verticalClusters.clear();
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));

        verticalParentModelClassArrayList.add(new VerticalParentModelClass("Cluster 2", verticalClusters, 0));

        verticalClusters.clear();
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Sarangani (Davao Occidental)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Maganoy (Shariff Aguak) (Capital) (Maguindanao)"));
        verticalClusters.add(new VerticalChildModelClass("Dipolog City (Zamboanga del Norte)"));

        verticalParentModelClassArrayList.add(new VerticalParentModelClass("Cluster 3", verticalClusters, 0));

        verticalParentAdapter = new VerticalParentAdapter(verticalParentModelClassArrayList, ClusterFragment.this.getContext());
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //verticalRecyclerView.setLayoutManager(new CustomLayoutManager(ClusterFragment.this.getContext(), 5, LinearLayoutManager.HORIZONTAL, false));
        verticalRecyclerView.setAdapter(verticalParentAdapter);
        verticalParentAdapter.notifyDataSetChanged();

    }

    public void setOtherModelsForDBSCAN() {
        recyclerView = getActivity().findViewById(R.id.rv_parent); // Find by ID
        childModelClassArrayList = new ArrayList<>();
        parentModelClassArrayList = new ArrayList<>();
        bestModelAccordingToDBI = new ArrayList<>();
        bestModelAccordingToSS = new ArrayList<>();
        bestModelAccordingToMI = new ArrayList<>();
        latestList = new ArrayList<>();

        ParentAdapter parentAdapter;

        //        TextView modelView = getActivity().findViewById(R.id.model_name);
        String modelName = "DBSCAN";
//        TextView dbiText= getActivity().findViewById(R.id.dbi);
        String dbiView = "DBI:";
//        TextView ssText = getActivity().findViewById(R.id.ss);
        String ssView = "SS:";
//        TextView miText = getActivity().findViewById(R.id.mi);
        String miView = "";
//        TextView clusterText = getActivity().findViewById(R.id.nc);
        String clusterView = "Clusters:";

        bestModelAccordingToSS.add(new ChildModelClass(R.drawable.cluster_model_logo, 1, modelName + " 1.1", ssView + " 0.486", miView + " ", dbiView + " 1.493", clusterView + " 2"));
        bestModelAccordingToSS.add(new ChildModelClass(R.drawable.cluster_model_logo, 1, modelName + " 1.4", ssView + " 1.011", miView + " ", dbiView + " -0.400", clusterView + " 5"));
        parentModelClassArrayList.add(new ParentModelClass(bestModelAccordingToSS));

//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,1, modelName + " 1.4", ssView + " 1.011", miView + " ", dbiView + " -0.400", clusterView + " 5"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,2, modelName + " 1.1", ssView + " 0.486", miView + " ", dbiView + " 1.493", clusterView + " 2"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,3, modelName + " 1.2", ssView + " 0.257", miView + " ", dbiView + " 2.032", clusterView + " 3"));
//        parentModelClassArrayList.add(new ParentModelClass("Best Davies-Bouldin Index", bestModelAccordingToDBI));

        parentAdapter = new ParentAdapter(parentModelClassArrayList, ClusterFragment.this.getContext(), this, parentItemListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();

    }

    public void setOtherModelsForMiniBatch() {
        recyclerView = getActivity().findViewById(R.id.rv_parent); // Find by ID
        childModelClassArrayList = new ArrayList<>();
        parentModelClassArrayList = new ArrayList<>();
        bestModelAccordingToDBI = new ArrayList<>();
        bestModelAccordingToSS = new ArrayList<>();
        bestModelAccordingToMI = new ArrayList<>();
        latestList = new ArrayList<>();

        ParentAdapter parentAdapter;

//        TextView modelView = getActivity().findViewById(R.id.model_name);
        String modelName = "MiniBatch-Kmeans";
//        TextView dbiText= getActivity().findViewById(R.id.dbi);
        String dbiView = "DBI:";
//        TextView ssText = getActivity().findViewById(R.id.ss);
        String ssView = "SS:";
//        TextView miText = getActivity().findViewById(R.id.mi);
        String miView = "MI:";
//        TextView clusterText = getActivity().findViewById(R.id.nc);
        String clusterView = "Clusters:";

        bestModelAccordingToSS.add(new ChildModelClass(R.drawable.cluster_model_logo, 1, modelName + " 1.7", ssView + " 0.753", miView + " 2342412.8", dbiView + " 0.381", clusterView + " 3"));
        bestModelAccordingToSS.add(new ChildModelClass(R.drawable.cluster_model_logo, 2, modelName + " 2.5", ssView + " 0.662", miView + " 1512769.3", dbiView + " 0.484", clusterView + " 4"));
        bestModelAccordingToSS.add(new ChildModelClass(R.drawable.cluster_model_logo, 1, modelName + " 2.7", ssView + " 0.662", miView + " 1504931.4", dbiView + " 0.485", clusterView + " 4"));

        parentModelClassArrayList.add(new ParentModelClass(bestModelAccordingToSS));
//
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,1, modelName + " 1.7", ssView + " 0.753", miView + " 2342412.8", dbiView + " 0.381", clusterView + " 3"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,2, modelName + " 2.5", ssView + " 0.662", miView + " 1512769.3", dbiView + " 0.484", clusterView + " 4"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,3, modelName + " 4.7", ssView + " 0.600", miView + " 6407483.2", dbiView + " 0.587", clusterView + " 4"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,4, modelName + " 1.3", ssView + " 0.614", miView + " 19087108.1", dbiView + " 0.675", clusterView + " 3"));
//        bestModelAccordingToDBI.add(new ChildModelClass(R.drawable.cluster_model_logo,5, modelName + " 3.3", ssView + " 0.753", miView + " 44812834.8", dbiView + " 0.701", clusterView + " 3"));
//
//        parentModelClassArrayList.add(new ParentModelClass("Best Davies-Bouldin Index", bestModelAccordingToDBI));
//
//        bestModelAccordingToMI.add(new ChildModelClass(R.drawable.cluster_model_logo,1, modelName + " 2.7", ssView + " 0.662", miView + " 1504931.4", dbiView + " 0.485", clusterView + " 4"));
//        bestModelAccordingToMI.add(new ChildModelClass(R.drawable.cluster_model_logo,2, modelName + " 1.7", ssView + " 0.753", miView + " 2342412.8", dbiView + " 0.381", clusterView + " 3"));
//        bestModelAccordingToMI.add(new ChildModelClass(R.drawable.cluster_model_logo,3,modelName + " 4.7", ssView + " 0.600", miView + " 6407483.2", dbiView + " 0.587", clusterView + " 4"));
//        bestModelAccordingToMI.add(new ChildModelClass(R.drawable.cluster_model_logo,4, modelName + " 2.3", ssView + " 0.514", miView + " 10525785.2", dbiView + " 0.654", clusterView + " 4"));
//        bestModelAccordingToMI.add(new ChildModelClass(R.drawable.cluster_model_logo,5, modelName + " 3.6", ssView + " 0.571", miView + " 11603757.8", dbiView + " 0.737", clusterView + " 3"));
//
//        parentModelClassArrayList.add(new ParentModelClass("Best Model Inertia", bestModelAccordingToMI));


        parentAdapter = new ParentAdapter(parentModelClassArrayList, ClusterFragment.this.getContext(), this, parentItemListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();

    }

        private void handleError (Throwable throwable){
            // Handle data retrieval or filtering error (e.g., display Toast message)
            Log.e("MyFragment", "Error setting database from JSON", throwable);
        }
    }