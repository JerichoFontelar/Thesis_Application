package com.example.myapplication.earthquake_data_retrieval;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStream;

public class VolcanoDataProcessor {

    public static void addDarkVioletTrianglesFromJSON(Context context, MapView mapView) throws JSONException {
        String jsonString = readJsonDataFromAsset(context);
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject volcanoJsonObject = jsonArray.getJSONObject(i);

            String volcanoName = volcanoJsonObject.getString("Volcano");
            double latitude = volcanoJsonObject.getDouble("Latitude");
            double longitude = volcanoJsonObject.getDouble("Longitude");

            // Create a custom triangle marker
            Marker marker = createTriangleMarker(context, mapView, latitude, longitude, volcanoName);

            mapView.getOverlays().add(marker);
        }
    }

    private static Marker createTriangleMarker(Context context, MapView mapView, double latitude, double longitude, String title) {
        // Define the size of the triangle
        int size = 25; // Adjust size as needed

        // Create a bitmap with the specified size
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Create a paint object for drawing
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#9400D3")); // Dark violet color
        paint.setStyle(Paint.Style.FILL);

        // Create a path for the triangle
        Path path = new Path();
        path.moveTo(size / 2, 0); // Top point
        path.lineTo(0, size); // Bottom-left point
        path.lineTo(size, size); // Bottom-right point
        path.lineTo(size / 2, 0); // Back to top point

        // Draw the triangle on the canvas
        canvas.drawPath(path, paint);

        // Convert the bitmap to a drawable
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        // Create a marker with the triangle drawable
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setTitle(title);
        marker.setIcon(drawable);

        return marker;
    }

    private static String readJsonDataFromAsset(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("database/volcanoes_phivolcs.json"); // Replace with your filename
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
