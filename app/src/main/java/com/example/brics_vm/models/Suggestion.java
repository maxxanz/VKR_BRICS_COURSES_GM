package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Suggestion implements Serializable {
    private int id;

    @SerializedName("course_id")
    private int courseId;

    @SerializedName("suggested_by")
    private int suggestedBy;

    private String title;
    private String description;

    @SerializedName("video_url")
    private String videoUrl;

    @SerializedName("text_content")
    private String textContent;

    private String duration;
    private String status;

    @SerializedName("rejected_reason")
    private String rejectedReason;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("country")
    private String country;

    // ========== НОВОЕ ПОЛЕ ==========
    @SerializedName("course_title")
    private String courseTitle;
    // ================================

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getSuggestedBy() { return suggestedBy; }
    public void setSuggestedBy(int suggestedBy) { this.suggestedBy = suggestedBy; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectedReason() { return rejectedReason; }
    public void setRejectedReason(String rejectedReason) { this.rejectedReason = rejectedReason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // ========== НОВЫЙ ГЕТТЕР И СЕТТЕР ==========
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    // ==========================================

    public String getTeacherName() {
        return firstName + " " + lastName;
    }
}