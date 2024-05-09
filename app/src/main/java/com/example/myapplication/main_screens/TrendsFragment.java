package com.example.myapplication.main_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.example.myapplication.shared_preferences.SharedPreferenceDataSource;

import java.util.ArrayList;
import java.util.Objects;

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
        TextView textView = rootView.findViewById(R.id.timeseries_text);



        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("earthquake_setting", Context.MODE_PRIVATE);
        SharedPreferenceDataSource sharedPrefDataSource = SharedPreferenceDataSource.getInstance(sharedPreferences);

        String model = sharedPrefDataSource.getTimeSeriesAlgorithm();
        Log.d("Model", "Model: " + model);

        if (Objects.equals(model, "ARIMA")) {
            setARIMA(rootView);
            textView.setText("Model Name: ARIMA(1,1,2)");
        }else if(Objects.equals(model, "LSTM")){
            setLSTM(rootView);
            textView.setText("Model Name: LSTM");
        }
        return rootView;
    }

    public void setLSTM(View rootView){
        recyclerView = rootView.findViewById(R.id.image_horizontal_recycler_view); // Find by ID

        arima_forecast = new ArrayList<>();
        parentModelClassArrayList = new ArrayList<>();

        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_1, "December 15-30 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_2, "January 1-15 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_3, "January 17-31 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_4, "February 1-15 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_5, "February 17- March 1 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_6, "March 2-16 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_7, "March 17-31 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.lstm_8, "April 1-15 Forecast"));
        parentModelClassArrayList.add(new ForecastParentModelClass(arima_forecast));

        ForecastParentAdapter parentAdapter;
        parentAdapter = new ForecastParentAdapter(parentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();


        articleParentModelClassArrayList = new ArrayList<>();
        forecast_articles = new ArrayList<>();
        RecyclerView recyclerViewForecastArticles = rootView.findViewById(R.id.forecast_articles);

        forecast_articles.add(new ForecastArticleChildModelClass("Examining the provided earthquake data visualizations reveals a potential long-term increase in earthquake frequency . However, predicting the exact number of earthquakes for each day remains a formidable task . While the model exhibits some success in capturing the overall upward trend in earthquake occurrences , there are clear discrepancies between the predicted and actual values on a day-to-day basis. These discrepancies highlight the inherent limitations of current earthquake prediction models. Even though the model shows promise in capturing broader trends, it struggles with pinpointing the precise number of earthquakes that will occur on any given day.  To gain a more comprehensive understanding of the model's effectiveness, additional information regarding the specific timeframe it analyzes, the targeted magnitude range of earthquakes, and its performance metrics (like mean squared error) would be beneficial.", "Model Interpretation"));
        forecast_articles.add(new ForecastArticleChildModelClass("LSTMs, a specific type of Recurrent Neural Network (RNN), excel at analyzing time series data like earthquake frequencies. Unlike standard RNNs that struggle with long sequences, LSTMs have internal memory cells to remember past information and learn long-term dependencies within the data. This allows them to make predictions based on historical patterns. While LSTMs require more data and computational power than simpler models, their ability to capture these long-term trends makes them valuable for various time series forecasting tasks.", "LSTM"));
        forecast_articles.add(new ForecastArticleChildModelClass("LSTMs are showing promise in earthquake prediction research. Their ability to analyze time series data makes them adept at finding patterns in earthquake history, including frequency and potential precursors. This knowledge can be used to identify periods with a higher chance of future earthquakes, potentially improving forecasting accuracy. However, earthquake prediction remains complex due to underlying geological processes, and LSTM effectiveness hinges on the quality and quantity of earthquake data available for training.", "LSTM in Earthquake Research"));

        articleParentModelClassArrayList.add(new ForecastArticleParentModelClass("ARIMA", forecast_articles));
        ForecastArticleParentAdapter forecastArticleParentAdapter;
        forecastArticleParentAdapter = new ForecastArticleParentAdapter(articleParentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerViewForecastArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewForecastArticles.setAdapter(forecastArticleParentAdapter);
        forecastArticleParentAdapter.notifyDataSetChanged();
    }

    public void setARIMA(View rootView){
        recyclerView = rootView.findViewById(R.id.image_horizontal_recycler_view); // Find by ID

        arima_forecast = new ArrayList<>();
        parentModelClassArrayList = new ArrayList<>();

        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_december_15_30, "December 15-30 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_january1_15, "January 1-15 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_january17_31, "January 17-31 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_february1_15, "February 1-15 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_february2_16, "February 17- March 1 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_march2_16, "March 2-16 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_march17_31, "March 17-31 Forecast"));
        arima_forecast.add(new ForecastChildModelClass(R.drawable.arima_april1_15, "April 1-15 Forecast"));
        parentModelClassArrayList.add(new ForecastParentModelClass(arima_forecast));

        ForecastParentAdapter parentAdapter;
        parentAdapter = new ForecastParentAdapter(parentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAdapter);
        parentAdapter.notifyDataSetChanged();


        articleParentModelClassArrayList = new ArrayList<>();
        forecast_articles = new ArrayList<>();
        RecyclerView recyclerViewForecastArticles = rootView.findViewById(R.id.forecast_articles);

        forecast_articles.add(new ForecastArticleChildModelClass("Analyzing earthquake activity reveals a potential long-term increase in frequency, however, pinpointing the exact cause requires a broader timeframe and context. While a model successfully captures the general upward trend in earthquake frequencies over the past 15 days, discrepancies between predicted and actual occurrences highlight the inherent difficulty of earthquake prediction.  Metrics like mean squared error can further assess the model's effectiveness.  Even with advancements, earthquake prediction remains a complex task, and model performance can be influenced by factors like location and historical data.  Overall, the analysis suggests increased monitoring and preparedness are crucial, while acknowledging the model's limitations in precise day-to-day predictions.", "Model Interpretation"));
        forecast_articles.add(new ForecastArticleChildModelClass("ARIMA (Autoregressive Integrated Moving Average) is a statistical method for analyzing and forecasting time series data. It considers past values (Autoregressive), removes trends (Integrated) if necessary, and factors in past prediction errors (Moving Average) to make future predictions. ARIMA is popular for its simplicity and effectiveness in various fields like sales forecasting, finance, and website traffic prediction. However, it assumes data stability and may not handle complex patterns well.", "ARIMA"));
        forecast_articles.add(new ForecastArticleChildModelClass("While ARIMA isn't ideal for predicting earthquakes directly due to their sudden nature and non-linear relationships, it can be a supplementary tool in earthquake research. It can help analyze historical earthquake frequency for potential trends and model aftershock sequences, which often follow a decaying pattern. However, ARIMA's role is limited, and other methods are necessary for a more comprehensive understanding of earthquake phenomena.", "ARIMA in Earthquake Research"));

        articleParentModelClassArrayList.add(new ForecastArticleParentModelClass("ARIMA", forecast_articles));
        ForecastArticleParentAdapter forecastArticleParentAdapter;
        forecastArticleParentAdapter = new ForecastArticleParentAdapter(articleParentModelClassArrayList, TrendsFragment.this.getContext());
        recyclerViewForecastArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewForecastArticles.setAdapter(forecastArticleParentAdapter);
        forecastArticleParentAdapter.notifyDataSetChanged();
    }
}