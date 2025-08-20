package com.s22010020.Zabaki;

import android.content.SharedPreferences;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.List;

public class HomeScreenActivity extends AppCompatActivity implements SensorEventListener {

    private DatabaseHelper dbHelper;
    private SensorManager sensorManager;
    private float lastX, lastY, lastZ;
    private long lastUpdateTime;
    private static final int SHAKE_THRESHOLD = 900; // To adjust sensitivity
    private static final int SHAKE_COUNT_THRESHOLD = 3; // Number of shakes mention
    private static final long SHAKE_RESET_TIME_MS = 1500; // Time window for shakes in milliseconds
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private int shakeCount = 0;
    private long lastShakeTime = 0;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE_SOS = 103;

    private static final String PREFS_NAME = "ZabakiPrefs";
    private static final String KEY_SOS_MESSAGE = "sos_message";

    private ImageButton playPauseButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView logoButton = findViewById(R.id.logoButton);
        ImageView instructionsImageView = findViewById(R.id.instructionsImageView);
        dbHelper = new DatabaseHelper(this);

        instructionsImageView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, InstructionActivity.class);
            startActivity(intent);
        });

        logoButton.setOnClickListener(v -> sendSOSMessage());

        // Request SMS permission if it's granted already then it won't request again
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }

        Button move = findViewById(R.id.liveLocationBtn);
        move.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, LiveLocationActivity.class);
            startActivity(intent);
        });
        move =findViewById(R.id.menuBtn);
        move.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "No accelerometer sensor found!", Toast.LENGTH_SHORT).show();
            }
        }

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);

        // Initialize Play/Pause button
        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setImageResource(R.drawable.ic_play); // Initial icon "Play"

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                stopMusic();
            } else {
                playMusic();
            }
        });

        //checking db null if yes it will alert the user
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not available. Please restart the app.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize LocationManager
        locationManager = new LocationManager(this, new LocationManager.LocationListener() {
            @Override
            public void onLocationReceived(Location location) {
                // This callback receives the location once fetched
                handleSosLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onLocationPermissionDenied() {
                Toast.makeText(HomeScreenActivity.this, "Location permission required for Track Your Current Location.", Toast.LENGTH_SHORT).show();
                // Request permission if not already done by checkAndRequestPermissions
                ActivityCompat.requestPermissions(HomeScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE_SOS);
            }

            @Override
            public void onLocationFailed(String errorMessage) {
                Toast.makeText(HomeScreenActivity.this, "Failed to get live location : " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
    // Implement Accelerometer sensor for shake
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            // Process the accelerometer data
            if ((currentTime - lastUpdateTime) > 100) {
                long timeDifference = currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float deltaZ = z - lastZ;

                lastX = x;
                lastY = y;
                lastZ = z;

                // Calculate shake speed
                float shakeSpeed = Math.abs(deltaX + deltaY + deltaZ) / timeDifference * 10000;

                if (shakeSpeed > SHAKE_THRESHOLD) {
                    long now = System.currentTimeMillis();

                    // Increment shake count
                    if (now - lastShakeTime < SHAKE_RESET_TIME_MS) {
                        shakeCount++;
                    } else {
                        shakeCount = 1; // Reset shake count if time window expires
                    }

                    lastShakeTime = now;

                    // Trigger action if shake count threshold is reached
                    if (shakeCount >= SHAKE_COUNT_THRESHOLD) {
                        shakeCount = 0; // Reset the count after triggering
                        if (!isPlaying) {
                            playMusic();
                        }
                    }
                }
            }
        }
    }

    //PlayMusic function
    private void playMusic() {
        try {
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.ic_pause); // Change to "Pause" icon when triggered to music playing
            Toast.makeText(this, "Music Playing", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error playing music: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //StopMusic Function
    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0); // Reset playback to the beginning
        }
        isPlaying = false;
        playPauseButton.setImageResource(R.drawable.ic_play); // Change to "Play" icon when music stop playing
        Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //---
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //SOS Message

    // sendSOSMessage to use the LocationManager
    private void sendSOSMessage() {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Contact> contacts = dbHelper.getAllContacts();
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No contacts saved. Please add contacts first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check location permissions first
        if (locationManager.checkLocationPermissions()) {
            // Permissions are granted, get the last known location
            Toast.makeText(this, "Fetching location...", Toast.LENGTH_SHORT).show();
            locationManager.getLastKnownLocation();
        } else {
            // Request location permissions (handled by the LocationListener callback)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE_SOS);
        }
    }

    // handle the location once received and send SMS
    private void handleSosLocation(double latitude, double longitude) {
        // Retrieve the custom SOS message from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String customMessage = prefs.getString(KEY_SOS_MESSAGE, "Help me!! I’m in Critical Situation");

        // Construct the SOS message with the fetched location
        String sosMessage = customMessage + " [My location: " +
                "http://maps.google.com/maps?q=" + latitude + "," + longitude + "]"; // Corrected Google Maps URL format

        List<Contact> contacts = dbHelper.getAllContacts(); // Re-fetch contacts or pass them
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No contacts found to send SOS message.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder recipients = new StringBuilder();
        int maxContacts = Math.min(5, contacts.size());

        for (int i = 0; i < maxContacts; i++) {
            recipients.append(contacts.get(i).getNumber());
            if (i < maxContacts - 1) {
                recipients.append(";");
            }
        }

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + recipients.toString()));
        smsIntent.putExtra("sms_body", sosMessage);

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
            Toast.makeText(this, "Opening messaging app with SOS message. Please click send!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No messaging app found to send SMS.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. SOS functionality will not work.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE_SOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted. Retrying...", Toast.LENGTH_SHORT).show();
                // If permission granted, retry sending SOS message
                sendSOSMessage();
            } else {
                Toast.makeText(this, "Location permission denied. Cannot send SOS with location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }





}