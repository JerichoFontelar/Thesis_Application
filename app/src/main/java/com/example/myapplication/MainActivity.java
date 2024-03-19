package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.databinding.ActivityMainBinding;
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


        replaceFragment(new ExploreFragment());
        binding.bottomNavigationView.setBackground(null);



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.explore) {
                replaceFragment(new ExploreFragment());
            } else if (itemId == R.id.cluster) {
                replaceFragment(new ClusterFragment());
            } else if (itemId == R.id.trends) {
                replaceFragment(new TrendsFragment());
            } else if (itemId == R.id.about) {
                replaceFragment(new AboutFragment());
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