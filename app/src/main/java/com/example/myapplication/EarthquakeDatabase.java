package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.File;
import java.util.List;

@Database(entities = {Earthquake.class}, version = 1)
public abstract class EarthquakeDatabase extends RoomDatabase {

    public abstract EarthquakeDAO getEarthquakeDAO();

    private static volatile EarthquakeDatabase INSTANCE;

    public static EarthquakeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EarthquakeDatabase.class) {
                if (INSTANCE == null) {
                    File dbFile = context.getDatabasePath("earthquake_db_v1.db");
                    // Check if database file exists
                    if (!dbFile.exists()) {
                        // Create database only if it doesn't exist
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        EarthquakeDatabase.class, "earthquake_db_v1.db")
                                .createFromAsset("database/final_earthquake_catalogue.db")
                                .build();
                        Log.d("MyTag", "Database file size: " + dbFile.length() + " bytes");
                        Log.d("MyTag", "Database file path: " + dbFile.getAbsolutePath());
                    } else {
                        // Database already exists, just open it
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        EarthquakeDatabase.class, "earthquake_db_v1.db")
                                .build();
                    }
                }
            }
        }
        return INSTANCE;
    }


    public List<Earthquake> getEarthquakeList(double minMag, double maxMag) {
        EarthquakeDAO earthquakeDAO = getEarthquakeDAO();
        //long count = earthquakeDAO.getEarthquakeList(minMag, maxMag);
        //Log.d("MyTag", "Number of earthquakes in the database: " + count);
        return earthquakeDAO.getEarthquakeList(minMag, maxMag);
    }

    public List<Earthquake> getEarthquakeList(double minMag, double maxMag,
                                              int minDepth, int maxDepth,
                                              String minDate, String maxDate,
                                              int minYear, int maxYear) {
        EarthquakeDAO earthquakeDAO = getEarthquakeDAO();
        return earthquakeDAO.getEarthquakeList(minMag, maxMag, minDepth, maxDepth, minDate, maxDate, minYear, maxYear);
    }

}
