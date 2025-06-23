package com.s22010020.Zabaki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Location currentLocation;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE_LIVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        // Initialize CardViews
        CardView hospitalCardView = findViewById(R.id.hospitalCardView);
        CardView pharmacyCardView = findViewById(R.id.pharmacyCardView);
        CardView liveLocationCardView = findViewById(R.id.liveLocationCardView);
        CardView policeLocationCardView = findViewById(R.id.policeLocationCardView);


        hospitalCardView.setOnClickListener(v -> navigateToPlace("hospital"));
        pharmacyCardView.setOnClickListener(v -> navigateToPlace("pharmacy"));
        policeLocationCardView.setOnClickListener(v -> navigateToPlace("police station"));
        liveLocationCardView.setOnClickListener(v -> getLiveLocation());

        locationManager = new LocationManager(this, new LocationManager.LocationListener() {
            @Override
            public void onLocationReceived(Location location) {
                currentLocation = location; // Update activity's currentLocation
                if (myMap != null) {
                    showLocationOnMap(location);
                }
            }

            @Override
            public void onLocationPermissionDenied() {
                Toast.makeText(LiveLocationActivity.this, "Location permission required for live map.", Toast.LENGTH_SHORT).show();
                // Request permissions if not already handled by checkAndRequestPermissions()
                ActivityCompat.requestPermissions(LiveLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE_LIVE);
            }

            @Override
            public void onLocationFailed(String errorMessage) {
                Toast.makeText(LiveLocationActivity.this, "Failed to get live location: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

        LatLng ousl = new LatLng(8.02341519686238, 79.83395214243748);
        myMap.addMarker(new MarkerOptions().position(ousl).title("OUSL Puttalam"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ousl, 15f));

        //Start live location updates if permissions are already granted
        if (locationManager.checkLocationPermissions()) {
            locationManager.startLocationUpdates();
        } else {
            //Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE_LIVE);
        }
    }

    //getLiveLocation to simply start updates
    private void getLiveLocation() {
        if (locationManager.checkLocationPermissions()) {
            locationManager.startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE_LIVE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start location updates again if the activity resumes and permissions are granted
        if (locationManager.checkLocationPermissions()) {
            locationManager.startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates when the activity is paused
        locationManager.stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE_LIVE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required for live location updates", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLocationOnMap(Location location) {
        if (myMap == null) {
            return;
        }
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        myMap.clear(); // Clear existing markers including the OUSL marker
        myMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
    }

    private void navigateToPlace(String placeType) {
        if (currentLocation == null) {
            Toast.makeText(this, "Fetching current location. Please try again in a moment.", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("DefaultLocale") String uri = String.format("geo:%f,%f?q=%s", currentLocation.getLatitude(), currentLocation.getLongitude(), placeType);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps is not installed on your device.", Toast.LENGTH_SHORT).show();
        }
    }
}