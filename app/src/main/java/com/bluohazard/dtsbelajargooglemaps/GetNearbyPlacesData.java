package com.bluohazard.dtsbelajargooglemaps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
//        List<HashMap<String, String>> nearbyPlacesList = null;
//        DataParser dataParser = new DataParser();

        // Memparsing result (JSON String) menjadi array Place
        List<Place> nearbyPlacesList = Place.convertJsonString(result);

//        nearbyPlacesList = dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);

        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<Place> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            Place googlePlace = nearbyPlacesList.get(i);

            // Untuk bikin marker
            double lat = googlePlace.getLatitude(); // Double.parseDouble(googlePlace.get("lat"));
            double lng = googlePlace.getLongitude(); // Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.getName();
            String vicinity = googlePlace.getVicinity();

            // Gambar markernya
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }
}
