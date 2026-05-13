package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;

public class TestQuestion {
    private int id;

    @SerializedName("test_id")
    private int testId;

    @SerializedName("question_text")
    private String questionText;

    @SerializedName("option_a")
    private String optionA;

    @SerializedName("option_b")
    private String optionB;

    @SerializedName("option_c")
    private String optionC;

    @SerializedName("option_d")
    private String optionD;

    @SerializedName("correct_answer")
    private String correctAnswer;

    private int points;
    private int order;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTestId() { return testId; }
    public void setTestId(int testId) { this.testId = testId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}