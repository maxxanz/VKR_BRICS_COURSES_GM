package com.example.brics_vm.models;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Course implements Serializable {
    private int id;
    private String title;
    private String subject;
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("creator_id")
    private int creatorId;
    private int duration;
    private double rating;
    private int studentsCount;
    private String createdAt;

    // Поля для создателя (из view) с аннотациями @SerializedName
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

    @SerializedName("course_number")
    private int courseNumber;

    // Пустой конструктор
    public Course() {}

    // Конструктор с параметрами для создания курса
    public Course(String title, String subject, String description, int courseNumber, int duration) {
        this.title = title;
        this.subject = subject;

        this.description = description;
        this.courseNumber = courseNumber;
        this.duration = duration;
    }

    // Геттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getCourseNumber() { return courseNumber; }
    @SerializedName("creator_id")
    public int getCreatorId() { return creatorId; }
    public int getDuration() { return duration; }
    public double getRating() { return rating; }
    public int getStudentsCount() { return studentsCount; }
    public String getCreatedAt() { return createdAt; }

    // Геттеры для создателя
    public String getCreatorFirstName() { return creatorFirstName; }
    public String getCreatorLastName() { return creatorLastName; }
    public String getCreatorCountry() { return creatorCountry; }
    public String getCreatorUniversity() { return creatorUniversity; }
    public String getCreatorType() { return creatorType; }

    // Полное имя создателя
    public String getCreatorFullName() {
        if (creatorFirstName != null && creatorLastName != null) {
            return creatorFirstName + " " + creatorLastName;
        } else if (creatorFirstName != null) {
            return creatorFirstName;
        } else if (creatorLastName != null) {
            return creatorLastName;
        }
        return "Информация отсутствует";
    }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCourseNumber(int courseNumber) { this.courseNumber = courseNumber; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setRating(double rating) { this.rating = rating; }
    public void setStudentsCount(int studentsCount) { this.studentsCount = studentsCount; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Сеттеры для создателя
    public void setCreatorFirstName(String creatorFirstName) { this.creatorFirstName = creatorFirstName; }
    public void setCreatorLastName(String creatorLastName) { this.creatorLastName = creatorLastName; }
    public void setCreatorCountry(String creatorCountry) { this.creatorCountry = creatorCountry; }
    public void setCreatorUniversity(String creatorUniversity) { this.creatorUniversity = creatorUniversity; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }

    // Вспомогательные методы
    public String getDurationDisplay() {
        int hours = duration / 60;
        int minutes = duration % 60;
        if (hours < 1) {
            return "1 ч";
        }
        if (minutes == 0) {
            return hours + " ч";
        }
        return hours + 1 + " ч";
    }

    public String getRatingDisplay() {
        return String.format("%.1f", rating);
    }

    // Метод для отображения номера курса
    public String getCourseNumberDisplay() {
        return courseNumber + " курс";
    }
}