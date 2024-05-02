package com.example.myapplication.main_screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.earthquake_data_retrieval.Earthquake;
import com.example.myapplication.earthquake_data_retrieval.EarthquakeDAO;
import com.example.myapplication.earthquake_data_retrieval.EarthquakeData;
import com.example.myapplication.earthquake_data_retrieval.EarthquakeDatabase;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.shared_preferences.SettingsFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    EarthquakeDatabase earthquakeDB = null;
    List<Earthquake> tentativeList = null;
    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        try {
//            setDatabaseFromJson();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        //okhttp
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url("https://192.168.43.133:5000/").build();
//        TextView textView = findViewById(R.id.cluster_text);
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                try {
//                    //Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
//                    Log.d("MyTag", "Failed to connect to server", e);
//                }catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    textView.setText(response.body().string());
//                    textView.setText(response.body().toString());
//                }
//            }
//        });

//        // Insecure approach for development only (not recommended for production)
//        OkHttpClient client = new OkHttpClient.Builder()
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true; // Trust all hostnames
//                    }
//                })
//                .build();
//
//
//        //Retrofit Builder
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://192.168.43.133:5000")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
//
//        //Instance for interface
//        MyApiCall myAPICall = retrofit.create(MyApiCall.class);
//        Call<DataModel> call = myAPICall.getData();
//        TextView textView = (TextView) findViewById(R.id.cluster_text);
//        //textView.setText("Hello");
//
//
//
//        call.enqueue(new Callback<DataModel>() {
//            @Override
//            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
//                if (response.code() != 200) {
//                    Log.d("MyTag", "response.code() != 200");
//                    // Handle non-successful response on the main thread:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText("Check the connection");
//                        }
//                    });
//                } else {
//                    // Parse response and update text view on the main thread:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (textView != null) {
//                                Log.d("MyTag", "Code passed in the not null block");
//                                String jsony = "Title = " + response.body().getTitle() +
//                                        "\n Body = " + response.body().getBody();
//                                textView.setText(jsony);
//                            } else {
//                                Log.d("MyTag", "Code passed in else block");
//                                // Handle the case where TextView is null (e.g., log an error)
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataModel> call, Throwable throwable) {
//                Log.d("MyTag","passed in the failure block", throwable);
//            }
//        });



        replaceFragment(new ExploreFragment());
        binding.bottomNavigationView.setBackground(null);



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (itemId == R.id.explore) {
                if (!(currentFragment instanceof ExploreFragment)) {
                    replaceFragment(new ExploreFragment());
                }
            } else if (itemId == R.id.cluster) {
                if (!(currentFragment instanceof ClusterFragment)) {
                    replaceFragment(new ClusterFragment());
                }
            } else if (itemId == R.id.trends) {
                if (!(currentFragment instanceof TrendsFragment)) {
                    replaceFragment(new TrendsFragment());
                }
            } else if (itemId == R.id.about) {
                if (!(currentFragment instanceof SettingsFragment)) {
                    replaceFragment(new SettingsFragment());
                }
            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void prepopulateDatabase() {
        Log.d("MyTag", "Prepopulating database");
        Observable<EarthquakeDatabase> dbObservable = Observable.fromCallable(() -> {
            return EarthquakeDatabase.getDatabase(this);
        });
        Log.d("MyTag", "Database created");

        Disposable disposable = dbObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeDB -> {
                    Log.d("MyTag", "Query is running");
                    //earthquakeDB = this.earthquakeDB;
                    EarthquakeDAO earthquakeDAO = earthquakeDB.getEarthquakeDAO();
                    Log.d("MyTag", "Code passed through here");
                    //setDB(this.earthquakeDB);
                    Log.d("MyTag", "Database set");
                    // Update the UI with the earthquakeDAO

                    try {
                        List<Earthquake> tentativeList = earthquakeDAO.getAllEarthquakes();
                        Log.d("MyTag", "Tentative list size: " + tentativeList.size());
                    } catch (Exception e) {
                        Log.e("MyTag", "Error getting tentative list", e);
                    }
                }, Throwable::printStackTrace);

        disposables.add(disposable);
    }

    public void setDB(EarthquakeDatabase db) {
        this.earthquakeDB = db;
    }

    public void setTentativeList(List<Earthquake> list) {
        this.tentativeList = list;
        Log.d("MyTag", "Earthquake list size: " + tentativeList.size());
    }

    public void setDatabaseFromJson() throws IOException {
        // Use a JSON parsing library like GSON
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream inputStream = assetManager.open("database/final_earthquake_data.json");
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(inputStream);
        EarthquakeData[] earthquakeDataArray = gson.fromJson(reader, EarthquakeData[].class);
        Log.d("MyTag", "Earthquake data array size: " + earthquakeDataArray.length);

        inputStream.close();

        List<EarthquakeData> filteredData = new ArrayList<>();
        double minMag = 5.0;  // Replace with your desired minimum magnitude

        for (EarthquakeData earthquake : earthquakeDataArray) {
            if (earthquake.getMagnitude() >= 1) {
                filteredData.add(earthquake);
            }
        }

        Log.d("MyTag", "Earthquake: " + filteredData.size() );

    }
}