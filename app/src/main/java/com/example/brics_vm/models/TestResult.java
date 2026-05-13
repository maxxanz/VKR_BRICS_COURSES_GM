package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;

public class TestResult {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("test_id")
    private int testId;

    private int score;

    @SerializedName("total_questions")
    private int totalQuestions;

    private int percentage;
    private boolean passed;

    @SerializedName("completed_at")
    private String completedAt;

    // Геттеры и сеттеры
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTestId() { return testId; }
    public void setTestId(int testId) { this.testId = testId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}