package com.s22010020.Zabaki;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<VideoItem> videoList;

    public VideoAdapter(Context context, List<VideoItem> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoList.get(position);

        holder.textViewTitle.setText(videoItem.getTitle());

        holder.imageViewThumbnail.setImageResource(videoItem.getThumbnailResId());

        holder.itemView.setOnClickListener(v -> {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoItem.getVideoId()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoItem.getVideoId()));
            try {
                context.startActivity(appIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewTitle;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }
}
