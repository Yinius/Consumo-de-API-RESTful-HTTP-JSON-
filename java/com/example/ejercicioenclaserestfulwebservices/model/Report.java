package com.example.ejercicioenclaserestfulwebservices.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Transfer Object (POJO) that mirrors the JSON returned by the REST API.
 */
public class Report {
    @SerializedName("id")
    private int id;

    @SerializedName("municipality")
    private String municipality;

    @SerializedName("symptomSummary")
    private String symptomSummary;

    @SerializedName("riskLevel")
    private String riskLevel;

    // Constructor for creating new records (POST)
    public Report(String municipality, String symptomSummary, String riskLevel) {
        this.municipality = municipality;
        this.symptomSummary = symptomSummary;
        this.riskLevel = riskLevel;
    }

    // Getters
    public int getId() { return id; }
    public String getMunicipality() { return municipality; }
    public String getSymptomSummary() { return symptomSummary; }
    public String getRiskLevel() { return riskLevel; }
}
