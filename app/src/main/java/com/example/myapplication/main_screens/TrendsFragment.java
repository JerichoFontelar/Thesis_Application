package com.example.myapplication.main_screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.recycler_view_forecast_articles.ForecastArticleChildModelClass;
import com.example.myapplication.recycler_view_forecast_articles.ForecastArticleParentAdapter;
import com.example.myapplication.recycler_view_forecast_articles.ForecastArticleParentModelClass;
import com.example.myapplication.recycler_view_horizontal.ChildModelClass;
import com.example.myapplication.recycler_view_horizontal.ParentAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentModelClass;
import com.example.myapplication.recycler_views_forecast.ForecastChildModelClass;
import com.example.myapplication.recycler_views_forecast.ForecastParentAdapter;
import com.example.myapplication.recycler_views_forecast.ForecastParentModelClass;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendsFragment extends Fragment {

    ArrayList<ForecastChildModelClass> arima_forecast;
    RecyclerView recyclerView;

    ArrayList<ForecastArticleParentModelClass> articleParentModelClassArrayList;

    ArrayList<ForecastArticleChildModelClass> forecast_articles;
    RecyclerView recyclerViewForecastArticles;

    ArrayList<ForecastParentModelClass> parentModelClassArrayList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendsFragment newInstance(String param1, String param2) {
        TrendsFragment fragment = new TrendsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trends, container, false);
        recyclerView = rootView.findViewById(R.id.image_horizontal_recycler_view); // Find by ID

        arima_forecast = new ArrayList<>();
        parentModelClassArrayList = new ArrayList<>();

        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_december_15_30, "Earthquake Frequency Forecast December 15-30"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_january1_15, "Earthquake Frequency Forecast January 1-15"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_january17_31, "Earthquake Frequency Forecast January 17-31"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_february1_15, "Earthquake Frequency Forecast February 1-15"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_february2_16, "Earthquake Frequency Forecast February 17- March 1"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_march2_16, "Earthquake Frequency Forecast March 2-16"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_march17_31, "Earthquake Frequency Forecast March 17-31"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_april1_15, "Earthquake Frequency Forecast April 1-15"));
        parentModelClassArrayList.add(new ForecastParentModelClass(arima_forecast));

        ForecastParentAdapter parentAdapter;
        parentAdapter = new ForecastParentAdapter(parentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();


        articleParentModelClassArrayList = new ArrayList<>();
        forecast_articles = new ArrayList<>();
        RecyclerView recyclerViewForecastArticles = rootView.findViewById(R.id.forecast_articles);

        forecast_articles.add(new ForecastArticleChildModelClass("ARIMA (Autoregressive Integrated Moving Average) is a statistical method for analyzing and forecasting time series data. It considers past values (Autoregressive), removes trends (Integrated) if necessary, and factors in past prediction errors (Moving Average) to make future predictions. ARIMA is popular for its simplicity and effectiveness in various fields like sales forecasting, finance, and website traffic prediction. However, it assumes data stability and may not handle complex patterns well.", "ARIMA"));
        forecast_articles.add(new ForecastArticleChildModelClass("While ARIMA isn't ideal for predicting earthquakes directly due to their sudden nature and non-linear relationships, it can be a supplementary tool in earthquake research. It can help analyze historical earthquake frequency for potential trends and model aftershock sequences, which often follow a decaying pattern. However, ARIMA's role is limited, and other methods are necessary for a more comprehensive understanding of earthquake phenomena.", "ARIMA in Earthquake Research"));

        articleParentModelClassArrayList.add(new ForecastArticleParentModelClass("ARIMA", forecast_articles));
        ForecastArticleParentAdapter forecastArticleParentAdapter;
        forecastArticleParentAdapter = new ForecastArticleParentAdapter(articleParentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerViewForecastArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewForecastArticles.setAdapter(forecastArticleParentAdapter);
        forecastArticleParentAdapter.notifyDataSetChanged();

        return rootView;
    }
}