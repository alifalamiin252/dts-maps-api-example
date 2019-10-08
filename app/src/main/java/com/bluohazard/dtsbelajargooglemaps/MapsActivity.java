package com.bluohazard.dtsbelajargooglemaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest; // Untuk meminta izin akses lokasi
    private FusedLocationProviderClient mFusedLocationClient;

    // Last location
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // Callback

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Init Fuse Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //stop location updates
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        this.showMarker();
        this.showLocationRequest();
        this.initGooglePlayServices();
    }

    private void showLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void showMarker() {
        LatLng place = new LatLng(21.4232364, 39.8237444);
        float zoomLevel = 17f;

        // Icon
        BitmapDrawable iconMarker = (BitmapDrawable) this.getResources().getDrawable(R.drawable.marker);
        Bitmap bitmapMarkerIcon = iconMarker.getBitmap();

        // Resize
        Bitmap resize = Bitmap.createScaledBitmap(bitmapMarkerIcon, 80, 80, false);

        // Jadikan BitmapDescriptor
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(resize);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.addMarker(new MarkerOptions().position(place)
                .title("Rumah Gue")
                .icon(markerDescriptor));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoomLevel));
    }

    private void initGooglePlayServices() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void onBtnFaskes_Click(View view) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();

        // Dapatkan lat dan lng dari mLastLocation
        double latitude = this.mLastLocation.getLatitude();
        double longitude = this.mLastLocation.getLongitude();

        String url = getUrl(latitude, longitude, "hospital");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(MapsActivity.this, "Rumah sakit terdekat", Toast.LENGTH_LONG).show();
    }

    public void onBtnPenginapan_Click(View view) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();

        // Dapatkan lat dan lng dari mLastLocation
        double latitude = this.mLastLocation.getLatitude();
        double longitude = this.mLastLocation.getLongitude();

        String url = getUrl(latitude, longitude, "hotel");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(MapsActivity.this, "Hotel terdekat", Toast.LENGTH_LONG).show();
    }

    public void onBtnMakan_Click(View view) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();

        // Dapatkan lat dan lng dari mLastLocation
        double latitude = this.mLastLocation.getLatitude();
        double longitude = this.mLastLocation.getLongitude();

        String url = getUrl(latitude, longitude, "restaurant");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(MapsActivity.this, "Restoran Terdekat", Toast.LENGTH_LONG).show();
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?
        // location=-33.8670522,151.1957362&radius=1500&type=restaurant&
        // keyword=cruise&key=AIzaSyBcO13wALUyiX_Ubah3-5YPPy4gVYauq6c

        String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String radius = "radius=1500";
        String location = "location=" + latitude + "," + longitude;
        String type = "type=" + placeType;
        String key = "key=AIzaSyBcO13wALUyiX_Ubah3-5YPPy4gVYauq6c";

        String requestUrl = baseUrl + radius + "&" + location + "&" + type + "&" + key;

        return requestUrl;
    }
}