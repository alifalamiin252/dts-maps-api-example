package com.bluohazard.dtsbelajargooglemaps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place {
    private double latitude, longitude;
    private String vicinity, name;

    public Place(double latitude, double longitude, String vicinity, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.vicinity = vicinity;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Place> convertJsonString(String jsonString) {
        // Array kosong untuk menyimpan hasil parsingan place JSON
        ArrayList<Place> places = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray jResult = root.getJSONArray("results");

            // Untuk setiap place yang ada di jResult..
            for (int i = 0; i < jResult.length(); i++) {
                JSONObject jResultSekarang = jResult.getJSONObject(i);
                JSONObject jGeometry = jResultSekarang.getJSONObject("geometry");
                JSONObject jLocation = jGeometry.getJSONObject("location");

                double latitude = jLocation.getDouble("lat");
                double longitude = jLocation.getDouble("lng");
                String name = jResultSekarang.getString("name");
                String vicinity = jResultSekarang.getString("vicinity");

                Place nearbyPlace = new Place(latitude, longitude, vicinity, name);

                // Menambahkan place baru ke Array kosong
                places.add(nearbyPlace);
            }

        } catch (Exception e) {
            // Tampilkan errornya di console
        }

        return places;
    }
}
