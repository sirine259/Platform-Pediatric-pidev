package com.esprit.platformepediatricback.ml.common;

import java.util.Map;

public class PredictionResult {
    private String predictionType;
    private double predictedValue;
    private double confidence;
    private String unit;
    private Map<String, Object> details;

    public PredictionResult() {}

    public PredictionResult(String predictionType, double predictedValue, double confidence, String unit) {
        this.predictionType = predictionType;
        this.predictedValue = predictedValue;
        this.confidence = confidence;
        this.unit = unit;
    }

    public String getPredictionType() { return predictionType; }
    public void setPredictionType(String predictionType) { this.predictionType = predictionType; }
    public double getPredictedValue() { return predictedValue; }
    public void setPredictedValue(double predictedValue) { this.predictedValue = predictedValue; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
