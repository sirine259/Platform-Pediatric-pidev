package com.esprit.platformepediatricback.ml.common;

import java.util.Map;

public class ClassificationResult {
    private String predictedClass;
    private Map<String, Double> classProbabilities;
    private double confidence;
    private String details;

    public ClassificationResult() {}

    public ClassificationResult(String predictedClass, Map<String, Double> classProbabilities, double confidence) {
        this.predictedClass = predictedClass;
        this.classProbabilities = classProbabilities;
        this.confidence = confidence;
    }

    public String getPredictedClass() { return predictedClass; }
    public void setPredictedClass(String predictedClass) { this.predictedClass = predictedClass; }
    public Map<String, Double> getClassProbabilities() { return classProbabilities; }
    public void setClassProbabilities(Map<String, Double> classProbabilities) { this.classProbabilities = classProbabilities; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
