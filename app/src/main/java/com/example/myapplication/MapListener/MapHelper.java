package com.example.myapplication.MapListener;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapHelper {

    public static void addFaultLinesToMap(Context context, MapView mapView, String jsonFilePath) {
        try {
            // Read JSON file
            Log.d("MapHelper", "Reading JSON file: " + jsonFilePath);
            String json = readJsonFile(context, jsonFilePath);
            Log.d("MapHelper", "JSON data: " + json);

            // Parse JSON using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            // Extract features (assuming "features" is the key for the fault line data)
            JsonElement featuresElement = jsonObject.get("features");
            if (featuresElement == null || !featuresElement.isJsonArray()) {
                throw new JsonParseException("Invalid JSON format: 'features' not found or not an array");
            }

            // Iterate through features
            List<FaultLine> faultLines = new ArrayList<>();
            for (JsonElement featureElement : featuresElement.getAsJsonArray()) {
                faultLines.add(parseFaultLine(featureElement.getAsJsonObject()));
            }

            // Add polylines to map
            for (FaultLine faultLine : faultLines) {
                Polyline polyline = new Polyline(mapView);
                polyline.setPoints(faultLine.getPoints());
                mapView.getOverlayManager().add(polyline);
            }
            mapView.invalidate(); // Redraw map
        } catch (JsonParseException e) {
            Log.e("MapHelper", "JsonParseException: " + e.getMessage());
            Toast.makeText(context, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("MapHelper", "IOException: " + e.getMessage());
            Toast.makeText(context, "Error reading JSON file", Toast.LENGTH_SHORT).show();
        }
    }

    private static String readJsonFile(Context context, String jsonFilePath) throws IOException {
        Log.d("MapHelper", "Reading JSON file: " + jsonFilePath);
        AssetManager assetManager = context.getAssets();
        try (InputStream inputStream = assetManager.open(jsonFilePath)) {
            Log.d("MapHelper", "InputStream: " + inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }


    private static int getResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
    }


    // Helper class to represent a fault line with points
    private static class FaultLine {
        private List<GeoPoint> points;

        public FaultLine(List<GeoPoint> points) {
            this.points = points;
        }

        public List<GeoPoint> getPoints() {
            return points;
        }
    }

    private static FaultLine parseFaultLine(JsonObject featureObject) throws JsonParseException {
        JsonObject geometryObject = featureObject.getAsJsonObject("geometry");
        if (geometryObject == null || !geometryObject.get("type").getAsString().equals("LineString")) {
            throw new JsonParseException("Invalid feature geometry: not a LineString");
        }

        JsonElement coordinatesElement = geometryObject.get("coordinates");
        // Check if coordinates exist and are a JsonArray
        if (coordinatesElement == null || !coordinatesElement.isJsonArray()) {
            throw new JsonParseException("Invalid 'coordinates' element: not found or not an array");
        }

        JsonArray coordinatesArray = coordinatesElement.getAsJsonArray();
        List<GeoPoint> points = new ArrayList<>();
        for (JsonElement coordinateElement : coordinatesArray) {
            // Access coordinate element as JsonArray
            JsonArray coordinate = coordinateElement.getAsJsonArray();
            if (coordinate.size() != 2) {
                throw new JsonParseException("Invalid coordinate format: expected longitude, latitude");
            }
            double lon = coordinate.get(0).getAsDouble();
            double lat = coordinate.get(1).getAsDouble();
            points.add(new GeoPoint(lat, lon));
        }

        return new FaultLine(points);
    }
}
