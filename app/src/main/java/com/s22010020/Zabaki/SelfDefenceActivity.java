package com.s22010020.Zabaki;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelfDefenceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoItem> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_self_defence);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();

        videoList.add(new VideoItem(
                "100 Amazing Self Defence Techniques",
                R.raw.sd_img2, // Replace VIDEO_ID with actual ID
                "Y-PhwJSv5HI" // Replace with actual YouTube video ID
        ));
        videoList.add(new VideoItem(
                "The Most Powerful Self Defence Techniques",
                R.raw.sd_img5, // Replace VIDEO_ID with actual ID
                "OU9nlBKKPmI" // Replace with actual YouTube video ID
        ));
        videoList.add(new VideoItem(
                "The Most Powerful Self Defence Techniques",
                R.raw.sd_img1,
                "aq3mnDRVIq0"
        ));
        videoList.add(new VideoItem(
                "10 Amazing Self Defence Techniques",
                R.raw.sd_img4,
                "B725c7vi1xk"
        ));
        videoList.add(new VideoItem(
                "4 Simple Self Defence Techniques",
                R.raw.sd_img3, // Replace VIDEO_ID with actual ID
                "ERMZRMqQmVI" // Replace with actual YouTube video ID
        ));
        videoList.add(new VideoItem(
                "15 Amazing Self Defence Drills And Techniques",
                R.raw.sd_img6, // Replace VIDEO_ID with actual ID
                "Ds3HSn6bajQ" // Replace with actual YouTube video ID
        ));

        // Add more videos as needed

        videoAdapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(videoAdapter);
    }
}
