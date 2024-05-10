package com.example.myapplication.main_screens;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.earthquake_data_retrieval.ClusterData;
import com.example.myapplication.earthquake_data_retrieval.VolcanoDataProcessor;
import com.example.myapplication.recycler_view_horizontal.ChildModelClass;
import com.example.myapplication.recycler_view_horizontal.ParentAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentItemListener;
import com.example.myapplication.recycler_view_horizontal.ParentModelClass;
import com.example.myapplication.recycler_view_vertical.VerticalChildModelClass;
import com.example.myapplication.recycler_view_vertical.VerticalParentAdapter;
import com.example.myapplication.recycler_view_vertical.VerticalParentModelClass;
import com.example.myapplication.recycler_view_horizontal.ChildItemListener;
import com.example.myapplication.recycler_views_articles.ArticleChildModelClass;
import com.example.myapplication.recycler_views_articles.ArticleParentAdapter;
import com.example.myapplication.recycler_views_articles.ArticleParentModelClass;
import com.example.myapplication.shared_preferences.SharedPreferenceDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
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


    Dialog dialog;
    RecyclerView verticalRecyclerView;
    ArrayList<VerticalParentModelClass> verticalParentModelClassArrayList;
    ArrayList<VerticalChildModelClass> verticalChildModelClassArrayList;

    ArrayList<VerticalChildModelClass> verticalClusters;

    RecyclerView recyclerView;

    RecyclerView articleRecyclerView;
    ArrayList<ParentModelClass> parentModelClassArrayList;
    ArrayList<ChildModelClass> childModelClassArrayList;

    ArrayList<ChildModelClass> bestModelAccordingToSS;
    ArrayList<ChildModelClass> bestModelAccordingToDBI;
    ArrayList<ChildModelClass> bestModelAccordingToMI;
    ArrayList<ChildModelClass> latestList;

    ParentItemListener parentItemListener;
    TextView clusterTextView;

    TextView modelDescription;

    ArrayList<ArticleParentModelClass> articleParentModelClassArrayList;
    ArrayList<ArticleChildModelClass> articleChildModelClassArrayList;

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

        clusterTextView = getActivity().findViewById(R.id.cluster_text);
        modelDescription = getActivity().findViewById(R.id.model_description);

        // Modify text content
        //clusterTextView.setText("New Cluster Analysis");


        // Modify text size (in density-independent pixels - dp)
        //clusterTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


        if (Objects.equals(model, "Mini Batch K-Means")) {
            setTextView("Mini Batch K-Means Model 1.7");
            setArticlesForMiniBatch();
            setModelDescription("Best Performing Model in Silhouette Score Criteria");
            setOtherModelsForMiniBatch();
            //setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
            setDBSCAN("database/ClusterModels/MiniBatch_Model_1.7.1.json");
            //setMiniBatch();
            setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
            try {
                VolcanoDataProcessor.addDarkVioletTrianglesFromJSON(getContext(), map);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else if (Objects.equals(model, "DBSCAN")) {
            //setMiniBatch();
            setTextView("DBSCAN Model 1.1");
            setArticlesForDBSCAN();
            setModelDescription("Best Performing Model in Silhouette Score Criteria");
            setOtherModelsForDBSCAN();
            setDBSCAN("database/DBSCANmodels/clustering_dbscan1.1.json");
            setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.1.json", map);
            try {
                VolcanoDataProcessor.addDarkVioletTrianglesFromJSON(getContext(), map);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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

        //waitForProgress();
        requireActivity().runOnUiThread(() -> {
            mapView.invalidate(); // Refresh map view after adding markers
            showDialog(false);
        });
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
        //Log.d("ClusterFragment", "Marker Icon created");
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
                setTextView("Mini Batch K-Means Model 1.7");
                setModelDescription("Best Performing Model in Silhouette Score Criteria");
                Log.d("ClusterFragment", "Model 1.7 is selected, please wait for the map to load");
                setDBSCAN("database/ClusterModels/MiniBatch_Model_1.7.1.json");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_1.7.1.json", map);
                Toast.makeText(getContext(), "Model 1.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 1) {
                //map.getOverlays().clear();
                setTextView("Mini Batch K-Means Model 2.5");
                setModelDescription("Best Performing Model in Davies-Bouldin Index Criteria");
                Log.d("ClusterFragment", "Model 2.5 is selected, please wait for the map to load");
                setDBSCAN("database/ClusterModels/MiniBatch_Model_2.5.1.json");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.5.1.json", map);
                Toast.makeText(getContext(), "Model 2.5 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 2) {
                //map.getOverlays().clear();
                setTextView("Mini Batch K-Means Model 2.7");
                setModelDescription("Best Performing Model in Model Inertia Criteria");
                Log.d("ClusterFragment", "Model 2.7 is selected, please wait for the map to load");
                setDBSCAN("database/ClusterModels/MiniBatch_Model_2.7.1.json");
                setDatabaseFromJSON("database/ClusterModels/MiniBatch_Model_2.7.1.json", map);
                Toast.makeText(getContext(), "Model 2.7 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            }
        } else {
            if (childPosition == 0) {
                //map.getOverlays().clear();
                setTextView("DBSCAN Model 1.1");
                setModelDescription("Best Performing Model in Silhouette Score Criteria");
                Log.d("ClusterFragment", "Model 1.1 is selected, please wait for the map to load");
                setDBSCAN("database/DBSCANmodels/clustering_dbscan1.1.json");
                setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.1.json", map);
                Toast.makeText(getContext(), "Model 1.1 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            } else if (childPosition == 1) {
                //map.getOverlays().clear();
                setTextView("DBSCAN Model 1.4");
                setModelDescription("Best Performing Model in Davies-Bouldin Index Criteria");
                Log.d("ClusterFragment", "Model 1.4 is selected, please wait for the map to load");
                setDBSCAN("database/DBSCANmodels/clustering_dbscan1.4.json");
                setDatabaseFromJSON("database/DBSCANmodels/clustering_dbscan1.4.json", map);
                Toast.makeText(getContext(), "Model 1.4 is selected, please wait for the map to load", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setDBSCAN(String path) {
        showDialog(true);
        verticalRecyclerView = getActivity().findViewById(R.id.rv_vertical); // Find by ID

        VerticalParentAdapter verticalParentAdapter;

        // Initialize empty Lists for parent and child data
        List<VerticalChildModelClass> verticalChildModelClassArrayList = new ArrayList<>();
        List<VerticalParentModelClass> verticalParentModelClassArrayList = new ArrayList<>();

        // Set to store encountered provinces (for uniqueness check)
        Set<String> encounteredProvinces = new HashSet<>();

        try {
            // Load data from JSON file
            List<ClusterData> allData = parseDataFromJSON(path);

            // Map clusters to their unique provinces (alternative approach)
            Map<Integer, List<String>> uniqueProvincesPerCluster = new HashMap<>();

            // Track minimum and maximum values for each cluster (separately for magnitude and depth)
            Map<Integer, Double[]> clusterMagnitudeMinMax = new HashMap<>();
            Map<Integer, Double[]> clusterDepthMinMax = new HashMap<>();

            // Iterate through allData
            for (ClusterData earthquake : allData) {
                int cluster = earthquake.getY(); // Assuming `y` holds the cluster value
                String province = earthquake.getProvince();
                double magnitude = earthquake.getMagnitude(); // Assuming a getter exists (or replace with actual value)
                double depth = earthquake.getDepth(); // Assuming a getter exists (or replace with actual value)

                // Create a copy of encounteredProvinces for iteration
                Set<String> encounteredProvincesCopy = new HashSet<>(encounteredProvinces);

                // Check if province is unique (not encountered before)
                if (!encounteredProvincesCopy.contains(province)) {
                    encounteredProvinces.add(province); // Mark province as encountered

                    // Add province to child data list
                    List<VerticalChildModelClass> childData = new ArrayList<>();
                    childData.add(new VerticalChildModelClass(province));

                    // Update cluster-province mapping (if cluster doesn't exist, create a new list)
                    if (!uniqueProvincesPerCluster.containsKey(cluster)) {
                        uniqueProvincesPerCluster.put(cluster, new ArrayList<>());
                    }
                    uniqueProvincesPerCluster.get(cluster).add(province);
                }

                // Update cluster-specific min/max values (initialize if not existing)
                Double[] currentMagnitudeMinMax = clusterMagnitudeMinMax.get(cluster);
                if (currentMagnitudeMinMax == null) {
                    currentMagnitudeMinMax = new Double[]{Double.MAX_VALUE, Double.MIN_VALUE}; // Initialize with max and min values
                }
                clusterMagnitudeMinMax.put(cluster, new Double[]{Math.min(currentMagnitudeMinMax[0], magnitude), Math.max(currentMagnitudeMinMax[1], magnitude)});

                Double[] currentDepthMinMax = clusterDepthMinMax.get(cluster);
                if (currentDepthMinMax == null) {
                    currentDepthMinMax = new Double[]{Double.MAX_VALUE, Double.MIN_VALUE}; // Initialize with max and min values
                }
                clusterDepthMinMax.put(cluster, new Double[]{Math.min(currentDepthMinMax[0], depth), Math.max(currentDepthMinMax[1], depth)});
                // Log message with cluster number as tag and province information
                Log.d("CLUSTER_" + cluster, province);
            }

// Create VerticalParentModelClass objects based on uniqueProvincesPerCluster
            for (Map.Entry<Integer, List<String>> entry : uniqueProvincesPerCluster.entrySet()) {
                int clusterNumber = entry.getKey();
                List<String> uniqueProvinces = entry.getValue();
                String clusterName = "Cluster " + (clusterNumber + 1);

                // Log.d("CLUSTER_" + clusterNumber + 1, uniqueProvinces.toString());
                // Get min/max values for magnitude and depth from separate maps
                Double[] magnitudeMinMax = clusterMagnitudeMinMax.get(clusterNumber);
                Double[] depthMinMax = clusterDepthMinMax.get(clusterNumber);

                String magnitudeRange = String.format("Magnitude Range: %.2f - %.2f", magnitudeMinMax[0], magnitudeMinMax[1]); // Format magnitude range with 2 decimals
                String depthRange = String.format("Depth Range: %.2f - %.2f", depthMinMax[0], depthMinMax[1]); // Swap order for depth range (assuming depth increases downwards)

                // Create child data list
                List<VerticalChildModelClass> childData = new ArrayList<>();
                for (String uniqueProvince : uniqueProvinces) {
                    childData.add(new VerticalChildModelClass(uniqueProvince));
                }

                // Update VerticalParentModelClass constructor to include magnitude_range and depth_range
                verticalParentModelClassArrayList.add(new VerticalParentModelClass(clusterName, childData, magnitudeRange, depthRange));
            }

        } catch (Exception e) {
            // Handle the exception
            Log.d("ClusterFragment", "Error parsing JSON data: " + e.getMessage()); // Log error message with tag
            throw new RuntimeException("Error parsing JSON data. Please check the file format or data integrity.");
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
            Log.d("ClusterFragment", String.valueOf(e));
        }

        return earthquakeDataArray;
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

    public void setTextView(String text) {
        clusterTextView.setText(text);
    }

    public void setModelDescription(String text) {
        modelDescription.setText(text);
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

    private void showDialog(boolean show) {
        if (show) {
            dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_map_cluster);

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);

            // Set touch listener to prevent dismissal

            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void setArticlesForMiniBatch() {
        articleRecyclerView = getActivity().findViewById(R.id.article_rv_vertical); // Find by ID
        childModelClassArrayList = new ArrayList<>();
        articleParentModelClassArrayList = new ArrayList<>();
        articleChildModelClassArrayList = new ArrayList<>();

        //articleParentModelClassArrayList;
        //articleChildModelClassArrayList;

        ArticleParentAdapter parentAdapter;

        articleChildModelClassArrayList.add(new ArticleChildModelClass("K-means clustering groups similar data points. The standard approach can be slow for enormous datasets. Mini-batch K-means tackles this by processing data in smaller batches. Instead of using all the data at once to update cluster centers, it samples a small portion (mini-batch) and updates centers based on that sample. This speeds things up significantly, but might result in slightly less accurate clustering compared to the traditional method. It's a good choice for massive datasets where faster processing is a priority.", "MiniBatch Kmeans"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("In minibatch k-means measures how well data points are clustered. It considers both how close points are within a cluster (good) and how far away they are from points in other clusters (also good). Scores range from -1 to 1, with values closer to 1 indicating a well-separated, tightly packed clustering. Analyzing the average silhouette score across all minibatch data points helps evaluate the overall clustering quality. ", "Silhouette Score"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("The Davies-Bouldin Index (DBI) is a metric used to assess the validity of clustering solutions. It evaluates both the separation between clusters and their compactness. DBI calculates a ratio between the within-cluster scatter (average distance between points within a cluster) and the between-cluster separation (distance between cluster centroids). Lower DBI values indicate better clustering, meaning clusters are well-separated with points within each cluster being relatively close together. Conversely, higher DBI values suggest poorly separated clusters or clusters with uneven point distribution.", "Davies-Bouldin Index"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("Model inertia, in clustering, measures the total distance between data points and their assigned cluster centers. Lower inertia indicates tighter clusters, where points are on average closer to their cluster's center. However, inertia doesn't consider separation between clusters. It can be useful when comparing models with the same number of clusters (lower inertia suggests better compactness) or when combined with other metrics that address separation for a more complete picture of clustering quality.", "Model Inertia"));
        articleParentModelClassArrayList.add(new ArticleParentModelClass(articleChildModelClassArrayList));

        parentAdapter = new ArticleParentAdapter(articleParentModelClassArrayList, ClusterFragment.this.getContext());
        articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articleRecyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();

    }


    public void setArticlesForDBSCAN() {
        articleRecyclerView = getActivity().findViewById(R.id.article_rv_vertical); // Find by ID
        childModelClassArrayList = new ArrayList<>();
        articleParentModelClassArrayList = new ArrayList<>();
        articleChildModelClassArrayList = new ArrayList<>();

        //articleParentModelClassArrayList;
        //articleChildModelClassArrayList;

        ArticleParentAdapter parentAdapter;

        articleChildModelClassArrayList.add(new ArticleChildModelClass("DBScan (Density-Based Spatial Clustering of Applications with Noise) is a data clustering algorithm that groups data points based on density. It identifies high-density regions as clusters, separated by areas with fewer points. Unlike K-Means, DBScan doesn't require predefining the number of clusters and can handle outliers by classifying them as noise. However, it requires careful selection of parameters and can be computationally expensive for very large datasets. DBScan is useful for tasks like image segmentation, anomaly detection, and customer segmentation.", "DBSCAN"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("DBSCAN excels at finding clusters of high density in your data, but it also identifies points that don't belong to any well-defined cluster: these are outliers", "Cluster 0"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("The silhouette score, while not directly involved in DBScan's clustering process, helps evaluate the quality of the formed clusters afterwards. It considers how tightly packed points are within a cluster (cohesion) and how far away they are from other clusters (separation). Scores range from -1 to 1, with values closer to 1 indicating well-separated clusters with points tightly packed within each cluster. By calculating the average silhouette score across all points, you can assess the overall clustering quality and potentially refine DBScan's parameters for better results. ", "Silhouette Score"));
        articleChildModelClassArrayList.add(new ArticleChildModelClass("The Davies-Bouldin Index (DBI) is a metric used to assess the validity of clustering solutions. It evaluates both the separation between clusters and their compactness. DBI calculates a ratio between the within-cluster scatter (average distance between points within a cluster) and the between-cluster separation (distance between cluster centroids). Lower DBI values indicate better clustering, meaning clusters are well-separated with points within each cluster being relatively close together. Conversely, higher DBI values suggest poorly separated clusters or clusters with uneven point distribution.", "Davies-Bouldin Index"));
        //articleChildModelClassArrayList.add(new ArticleChildModelClass("Model inertia, in clustering, measures the total distance between data points and their assigned cluster centers. Lower inertia indicates tighter clusters, where points are on average closer to their cluster's center. However, inertia doesn't consider separation between clusters. It can be useful when comparing models with the same number of clusters (lower inertia suggests better compactness) or when combined with other metrics that address separation for a more complete picture of clustering quality.", "Model Inertia"));
        articleParentModelClassArrayList.add(new ArticleParentModelClass(articleChildModelClassArrayList));

        parentAdapter = new ArticleParentAdapter(articleParentModelClassArrayList, ClusterFragment.this.getContext());
        articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articleRecyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();

    }


    private void handleError (Throwable throwable){
            // Handle data retrieval or filtering error (e.g., display Toast message)
            Log.e("MyFragment", "Error setting database from JSON", throwable);
        }

    }





