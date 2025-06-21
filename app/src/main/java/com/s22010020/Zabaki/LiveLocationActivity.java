package com.s22010020.Zabaki;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationClient;

    private CardView hospitalCardView, pharmacyCardView, liveLocationCardView, policeLocationCardView;
    private Location currentLocation;

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveLocationActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize CardViews
        hospitalCardView = findViewById(R.id.hospitalCardView);
        pharmacyCardView = findViewById(R.id.pharmacyCardView);
        liveLocationCardView = findViewById(R.id.liveLocationCardView);
        policeLocationCardView = findViewById(R.id.policeLocationCardView);

        // Set click listeners
        hospitalCardView.setOnClickListener(v -> navigateToPlace("hospital"));
        pharmacyCardView.setOnClickListener(v -> navigateToPlace("pharmacy"));
        policeLocationCardView.setOnClickListener(v -> navigateToPlace("police station"));
        liveLocationCardView.setOnClickListener(v -> getLiveLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

        // Set default location (OUSL Puttalam)
        LatLng ousl = new LatLng(8.02341519686238, 79.83395214243748);
        myMap.addMarker(new MarkerOptions().position(ousl).title("OUSL Puttalam"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ousl, 15f));
        getLiveLocation(); // Get the live location immediately after the map is ready
    }

    private void getLiveLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                showLocationOnMap(location);
            } else {
                Toast.makeText(this, "Unable to fetch location. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showLocationOnMap(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        myMap.clear();
        myMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
    }

    private void navigateToPlace(String placeType) {
        if (currentLocation == null) {
            Toast.makeText(this, "Fetching current location. Please try again in a moment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = String.format("google.navigation:q=%s&mode=d", placeType);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps is not installed on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLiveLocation();
            } else {
                Toast.makeText(this, "Permission denied. Unable to show live location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
