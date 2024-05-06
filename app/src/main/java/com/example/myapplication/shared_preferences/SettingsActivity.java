package com.example.myapplication.shared_preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.window.OnBackInvokedDispatcher;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.shared_preferences.SettingsFragment;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                        .commit();



//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar menu = getSupportActionBar();
//        menu.setDisplayShowHomeEnabled(true);
//        menu.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

//    @Override
//    public SharedPreferenceDataSource getSharedPreferenceDataSource() {
//        // Get the default shared preferences for the application
//        SharedPreferences sharedPreferences = getSharedPreferences("earthquake_setting", MODE_PRIVATE);
//        // Return the singleton instance of SharedPreferenceDataSource
//        return SharedPreferenceDataSource.getInstance(sharedPreferences);
//    }
}