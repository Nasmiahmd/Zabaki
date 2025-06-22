package com.s22010020.Zabaki;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastUpdateTime;
    private static final int SHAKE_THRESHOLD = 1200; // Adjust sensitivity
    private static final int SHAKE_COUNT_THRESHOLD = 3; // Number of shakes required
    private static final long SHAKE_RESET_TIME_MS = 1500; // Time window for shakes in milliseconds

    private int shakeCount = 0;
    private long lastShakeTime = 0;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Button move;
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
                Toast.makeText(HomeScreen.this, "Logo Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

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
    }

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

    private void playMusic() {
        mediaPlayer.start();
        isPlaying = true;
        playPauseButton.setImageResource(R.drawable.ic_pause); // Change to "Pause" icon
        Toast.makeText(this, "Music Playing", Toast.LENGTH_SHORT).show();
    }

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
}