package com.s22010020.Zabaki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationManager {
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private final Context context;
    private final LocationListener listener; //Custom interface for callbacks

    //Interface to communicate location updates
    public interface LocationListener {
        void onLocationReceived(Location location);
        void onLocationPermissionDenied();
        void onLocationFailed(String errorMessage);
    }

    public LocationManager(Context context, LocationListener listener) {
        this.context = context;
        this.listener = listener;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        setupLocationCallback();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (listener != null) {
                        listener.onLocationReceived(location);
                    }
                }
            }
        };
    }

    public boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        if (!checkLocationPermissions()) {
            if (listener != null) {
                listener.onLocationPermissionDenied();
            }
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation() {
        if (!checkLocationPermissions()) {
            if (listener != null) {
                listener.onLocationPermissionDenied();
            }
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        if (listener != null) {
                            listener.onLocationReceived(location);
                        }
                    } else {
                        if (listener != null) {
                            listener.onLocationFailed("Last known location not available.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onLocationFailed("Failed to get last known location: " + e.getMessage());
                    }
                });
    }
}