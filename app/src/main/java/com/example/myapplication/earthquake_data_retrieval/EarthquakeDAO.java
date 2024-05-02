package com.example.myapplication.earthquake_data_retrieval;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.earthquake_data_retrieval.Earthquake;

import java.util.List;


@Dao
public interface EarthquakeDAO {

    @Insert
    public void insert(Earthquake record);

    @Update
    public void update(Earthquake record);

    @Delete
    public void delete(Earthquake record);

    @Query("SELECT * FROM earthquake_database WHERE mag >= :minMag AND mag <= :maxMag")
    public List<Earthquake> getEarthquakeList(double minMag, double maxMag);

    @Query("SELECT * FROM earthquake_database")
    public List<Earthquake> getAllEarthquakes();


    @Query("SELECT * FROM earthquake_database " +
            "WHERE Mag >= :minMag AND Mag <= :maxMag " +
            "AND Depth >= :minDepth AND Depth <= :maxDepth " +
            "AND strftime('%Y-%m-%d', Date) >= :minDate AND strftime('%Y-%m-%d', Date) <= :maxDate " +
            "AND strftime('%Y', Date) >= :minYear AND strftime('%Y', Date) <= :maxYear")
    public List<Earthquake> getEarthquakeList(double minMag, double maxMag,
                                              int minDepth, int maxDepth,
                                              String minDate, String maxDate,
                                              int minYear, int maxYear);

}
