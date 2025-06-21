package com.s22010020.Zabaki;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

public class LiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private EditText editTextAddress;
    private Button btnSearch;
    private Geocoder geocoder;

    List<Address> listGeocorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_live_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    editTextAddress = findViewById(R.id.editTextaddress);
    btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> searchLocation());

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    geocoder = new Geocoder(this, Locale.getDefault());
}
@Override
public void onMapReady(@NonNull GoogleMap googleMap) {
    myMap = googleMap;
    // Configure map settings
    myMap.getUiSettings().setZoomControlsEnabled(true);
    myMap.getUiSettings().setCompassEnabled(true);

    // Set default location (OUSL Puttalam)
    LatLng ousl = new LatLng(8.02341519686238, 79.83395214243748);
    myMap.addMarker(new MarkerOptions().position(ousl).title("OUSL Puttalam"));
    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ousl, 15f));
}

private void searchLocation() {
    String location = editTextAddress.getText().toString().trim();

    if (location.isEmpty()) {
        Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
        return;
    }

    // Show loading state
    btnSearch.setEnabled(false);
    btnSearch.setText("Searching...");

    new Thread(() -> {
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);

            runOnUiThread(() -> {
                btnSearch.setEnabled(true);
                btnSearch.setText("Show location");

                if (addresses == null || addresses.isEmpty()) {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, "Geocoder service not available on this device",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Update map
                myMap.clear();
                myMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(location));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            });
        } catch (IOException e) {
            runOnUiThread(() -> {
                btnSearch.setEnabled(true);
                btnSearch.setText("Show location");
                Toast.makeText(this, "Network error. Please check your internet connection", Toast.LENGTH_SHORT).show();
            });
        } catch (IllegalArgumentException e) {
            runOnUiThread(() -> {
                btnSearch.setEnabled(true);
                btnSearch.setText("Show location");
                Toast.makeText(this, "Invalid address format", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                btnSearch.setEnabled(true);
                btnSearch.setText("Show location");
                Toast.makeText(this, "Geocoder service not available", Toast.LENGTH_SHORT).show();
            });
        }
    }).start();
}
}