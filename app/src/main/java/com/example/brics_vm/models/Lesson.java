package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Lesson implements Serializable {  // ← добавьте implements Serializable
    private int id;

    @SerializedName("course_id")
    private int courseId;

    private String title;
    private String description;

    @SerializedName("video_url")
    private String videoUrl;

    @SerializedName("text_content")
    private String textContent;

    private int order;
    private int duration;

    // Геттеры и сеттеры...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}