package com.example.brics_vm.models;

import com.google.gson.annotations.SerializedName;

public class CountryRanking {

    @SerializedName("country")
    private String country;

    @SerializedName("avg_score")
    private double avg_score;

    @SerializedName("total_users")
    private int total_users;

    @SerializedName("total_tests")
    private int total_tests;

    @SerializedName("rank")
    private int rank;

    public CountryRanking() {}

    // Геттеры
    public String getCountry() { return country; }
    public double getAvg_score() { return avg_score; }
    public int getTotal_users() { return total_users; }
    public int getTotal_tests() { return total_tests; }
    public int getRank() { return rank; }

    // Для совместимости со старым адаптером
    public String getName() { return country; }
    public double getScore() { return avg_score; }
}