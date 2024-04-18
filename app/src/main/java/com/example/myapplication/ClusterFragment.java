package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClusterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClusterFragment extends Fragment {

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = view.findViewById(R.id.cluster_text);
        textView.setText("Hi");

        // Insecure approach for development only (not recommended for production)
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true; // Trust all hostnames
                    }
                })
                .build();

        //Retrofit Builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.50.75.159:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        //Instance for interface
        MyApiCall myAPICall = retrofit.create(MyApiCall.class);
        Call<DataModel> call = myAPICall.getData();
        //textView.setText("Hello");
        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                if (response.code() != 200) {
                    Log.d("MyTag", "response.code() != 200");
                    // Handle non-successful response on the main thread:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Check the connection");
                        }
                    });
                } else {
                    // Parse response and update text view on the main thread:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (textView != null) {
                                Log.d("MyTag", "Code passed in the not null block");
                                String jsony = "Title = " + response.body().getTitle() +
                                        "\n Body = " + response.body().getBody();
                                retrievedData = jsony;
                                textView.setText(jsony);
                            } else {
                                Log.d("MyTag", "Code passed in else block");
                                // Handle the case where TextView is null (e.g., log an error)
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable throwable) {
                Log.d("MyTag","passed in the failure block", throwable);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cluster, container, false);
        TextView textView = view.findViewById(R.id.cluster_text);
        textView.setText("Hello");

        Activity act = requireActivity(); //Activity variable
        Context ctx = requireContext(); //Context ctx = getApplicationContext();;
        // Load osmdroid configuration before setting content view
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //Map initialization
        MapView map = view.findViewById(R.id.map_cluster);

        // Set map tile source, enable zoom controls, and multi-touch zooming
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);

        GeoPoint userPoint = new GeoPoint(12.85, 123.74);
        IMapController mapController = map.getController();
        mapController.animateTo(userPoint);
        //mapController.setCenter(userPoint);
        mapController.zoomTo(7.0);//6.5


        return view;
    }

    public void setData(String data){
        retrievedData = data;
    }


}