package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Lesson implements Serializable {
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

    // ========== НОВЫЕ ПОЛЯ ==========
    @SerializedName("is_brics")
    private boolean isBrics;

    @SerializedName("is_contribution")
    private boolean isContribution;

    @SerializedName("contributor_first_name")
    private String contributorFirstName;

    @SerializedName("contributor_last_name")
    private String contributorLastName;

    @SerializedName("contributor_country")
    private String contributorCountry;
    // ================================

    // Геттеры и сеттеры существующие
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

    // ========== НОВЫЕ ГЕТТЕРЫ И СЕТТЕРЫ ==========
    public boolean isBrics() { return isBrics; }
    public void setBrics(boolean brics) { isBrics = brics; }

    public boolean isContribution() { return isContribution; }
    public void setContribution(boolean contribution) { isContribution = contribution; }

    public String getContributorFirstName() { return contributorFirstName; }
    public void setContributorFirstName(String contributorFirstName) { this.contributorFirstName = contributorFirstName; }

    public String getContributorLastName() { return contributorLastName; }
    public void setContributorLastName(String contributorLastName) { this.contributorLastName = contributorLastName; }

    public String getContributorCountry() { return contributorCountry; }
    public void setContributorCountry(String contributorCountry) { this.contributorCountry = contributorCountry; }

    public String getContributorFullName() {
        if (contributorFirstName != null && contributorLastName != null) {
            return contributorFirstName + " " + contributorLastName;
        } else if (contributorFirstName != null) {
            return contributorFirstName;
        } else if (contributorLastName != null) {
            return contributorLastName;
        }
        return "";
    }
    // ============================================
}