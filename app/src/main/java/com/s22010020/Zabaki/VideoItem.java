package com.s22010020.Zabaki;
public class VideoItem {
    private String title;
    private int thumbnailResId;
    private String videoId;

    public VideoItem(String title, int thumbnailResId, String videoId) {
        this.title = title;
        this.thumbnailResId = thumbnailResId;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public String getVideoId() {
        return videoId;
    }
}
