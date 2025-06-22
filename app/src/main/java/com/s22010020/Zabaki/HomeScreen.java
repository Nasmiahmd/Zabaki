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

import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.List;

public class HomeScreen extends AppCompatActivity implements SensorEventListener {

    private DatabaseHelper dbHelper;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastUpdateTime;
    private static final int SHAKE_THRESHOLD = 1200; // Adjust sensitivity
    private static final int SHAKE_COUNT_THRESHOLD = 3; // Number of shakes required
    private static final long SHAKE_RESET_TIME_MS = 1500; // Time window for shakes in milliseconds
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private static final int LOCATION_REQUEST_CODE = 102;
    private int shakeCount = 0;
    private long lastShakeTime = 0;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Button move;

    private static final String PREFS_NAME = "ZabakiPrefs";
    private static final String KEY_SOS_MESSAGE = "sos_message";

    private Contact contact;
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

        instructionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, InstructionActivity.class);
                startActivity(intent);
            }
        });



        // Set click listener for the circular logo
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSOSMessage();
            }
        });

        // Request SMS permission if not already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }

        move=findViewById(R.id.liveLocationBtn);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, LiveLocationActivity.class);
                startActivity(intent);
            }
        });
        move=findViewById(R.id.menuBtn);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "No accelerometer sensor found!", Toast.LENGTH_SHORT).show();
            }
        }

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound); // Replace `alert_sound` with your file in `res/raw`

        // Initialize Play/Pause button
        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setImageResource(R.drawable.ic_play); // Initial icon is "Play"

        // Set button click listener
        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                stopMusic();
            } else {
                playMusic();
            }
        });

        if (dbHelper == null) {
            Toast.makeText(this, "Database is not available. Please restart the app.", Toast.LENGTH_SHORT).show();
            return;
        }

    }
    // Implement Accelerometer sensor
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
            playPauseButton.setImageResource(R.drawable.ic_pause); // Change to "Pause" icon
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
        playPauseButton.setImageResource(R.drawable.ic_play); // Change to "Play" icon
        Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. SOS functionality will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //SOS Message

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SOS message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // Start LocationServicesActivity
        Intent intent = new Intent(HomeScreen.this, LiveLocationActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle result from LiveLocationActivity
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);

                // Retrieve the custom SOS message from SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String customMessage = prefs.getString(KEY_SOS_MESSAGE, "Help me!! I’m in Critical Situation");

                String sosMessage = customMessage +
                        "[https://maps.google.com/?q=](https://maps.google.com/?q=)" + latitude + "," + longitude;

                List<Contact> contacts = dbHelper.getAllContacts();
                if (contacts.isEmpty()) {
                    Toast.makeText(this, "No contacts found to send SOS message.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prepare phone numbers for the SMS intent
                StringBuilder recipients = new StringBuilder();
                int maxContacts = Math.min(5, contacts.size()); // Limit to max 5 contacts for SMS intent

                for (int i = 0; i < maxContacts; i++) {
                    recipients.append(contacts.get(i).getNumber());
                    if (i < maxContacts - 1) {
                        recipients.append(";"); // Separate multiple numbers with a semicolon
                    }
                }

                // Create an SMS Intent to launch the default messaging app
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + recipients.toString())); // Use smsto for recipients
                smsIntent.putExtra("sms_body", sosMessage); // Pre-fill the message body

                // Verify that an app exists to handle this intent
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                    Toast.makeText(this, "Opening messaging app with SOS message. Please click send!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No messaging app found to send SMS.", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Failed to get current location. Cannot send SOS message.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

