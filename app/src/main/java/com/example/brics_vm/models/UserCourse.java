package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;

public class UserCourse {
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("course_id")
    private int courseId;

    private String status;
    private Double result;

    @SerializedName("started_at")
    private String startedAt;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Поля курса (приходят напрямую из API, без вложенного объекта)
    private String title;
    private String subject;
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("course_number")
    private int courseNumber;

    private Integer duration;
    private Double rating;

    @SerializedName("students_count")
    private int studentsCount;

    @SerializedName("creator_id")
    private int creatorId;

    @SerializedName("creator_first_name")
    private String creatorFirstName;

    @SerializedName("creator_last_name")
    private String creatorLastName;

    @SerializedName("creator_country")
    private String creatorCountry;

    @SerializedName("creator_university")
    private String creatorUniversity;

    @SerializedName("creator_type")
    private String creatorType;

    // Геттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }

    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCourseNumber() { return courseNumber; }
    public void setCourseNumber(int courseNumber) { this.courseNumber = courseNumber; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public int getStudentsCount() { return studentsCount; }
    public void setStudentsCount(int studentsCount) { this.studentsCount = studentsCount; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatorFirstName() { return creatorFirstName; }
    public void setCreatorFirstName(String creatorFirstName) { this.creatorFirstName = creatorFirstName; }

    public String getCreatorLastName() { return creatorLastName; }
    public void setCreatorLastName(String creatorLastName) { this.creatorLastName = creatorLastName; }

    public String getCreatorCountry() { return creatorCountry; }
    public void setCreatorCountry(String creatorCountry) { this.creatorCountry = creatorCountry; }

    public String getCreatorUniversity() { return creatorUniversity; }
    public void setCreatorUniversity(String creatorUniversity) { this.creatorUniversity = creatorUniversity; }

    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }

    // Вспомогательные методы
    public String getCreatorFullName() {
        if (creatorFirstName != null && creatorLastName != null) {
            return creatorFirstName + " " + creatorLastName;
        } else if (creatorFirstName != null) {
            return creatorFirstName;
        } else if (creatorLastName != null) {
            return creatorLastName;
        }
        return "Автор не указан";
    }

    public int getProgressPercent() {
        if (result != null && result > 0) return result.intValue();
        if ("completed".equals(status)) return 100;
        if ("in_progress".equals(status)) return 50;
        return 0;
    }

    public String getStatusText() {
        switch (status) {
            case "saved": return "⭐ В избранном";
            case "in_progress": return "🔄 В процессе";
            case "completed": return "✅ Пройден";
            default: return "";
        }
    }

    public int getStatusColor() {
        switch (status) {
            case "saved": return android.R.color.holo_orange_dark;
            case "in_progress": return android.R.color.holo_blue_dark;
            case "completed": return android.R.color.holo_green_dark;
            default: return android.R.color.darker_gray;
        }
    }
}