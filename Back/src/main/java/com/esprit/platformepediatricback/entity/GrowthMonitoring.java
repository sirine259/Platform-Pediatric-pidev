package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "growth_monitoring")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowthMonitoring {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(nullable = false)
    private LocalDate measurementDate;
    
    @Column
    private Double weight; // en kg
    
    @Column
    private Double height; // en cm
    
    @Column
    private Double bmi;
    
    @Column
    private Double headCircumference; // en cm (pour les nourrissons)
    
    @Column
    private Double armCircumference; // en cm
    
    @Column
    private String growthPercentile;
    
    @Column
    private String weightPercentile;
    
    @Column
    private String heightPercentile;
    
    @Column
    private String bmiPercentile;
    
    @Column
    private String headCircumferencePercentile;
    
    @Column
    private String bloodPressure;
    
    @Column
    private String heartRate;
    
    @Column
    private String developmentalMilestones;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    @Column
    private String nutritionalStatus; // NORMAL, UNDERWEIGHT, OVERWEIGHT, OBESE
    
    @Column
    private Boolean growthAbnormalityDetected = false;
    
    @Column(columnDefinition = "TEXT")
    private String abnormalityDetails;
    
    @Column
    private LocalDateTime nextCheckupDate;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (nutritionalStatus == null) {
            nutritionalStatus = "NORMAL";
        }
        // Calculer automatiquement le BMI
        if (weight != null && height != null && height > 0) {
            this.bmi = weight / Math.pow(height / 100, 2);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Recalculer le BMI si nécessaire
        if (weight != null && height != null && height > 0) {
            this.bmi = weight / Math.pow(height / 100, 2);
        }
    }
    
    // Méthodes utilitaires
    public String getGrowthStatus() {
        if (growthAbnormalityDetected) {
            return "ABNORMAL";
        } else if (nutritionalStatus.equals("UNDERWEIGHT") || nutritionalStatus.equals("OVERWEIGHT")) {
            return "ATTENTION";
        } else {
            return "NORMAL";
        }
    }
    
    public Boolean isHealthyWeight() {
        return bmi != null && bmi >= 18.5 && bmi <= 24.9;
    }
    
    public String getAgeCategory() {
        if (medicalRecord != null && medicalRecord.getPatientAge() != null) {
            Integer age = medicalRecord.getPatientAge();
            if (age <= 1) return "INFANT";
            if (age <= 5) return "TODDLER";
            if (age <= 12) return "CHILD";
            if (age <= 18) return "ADOLESCENT";
            return "ADULT";
        }
        return "UNKNOWN";
    }
    
    // Manually added getters/setters for Lombok compatibility
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MedicalRecord getMedicalRecord() {
        return this.medicalRecord;
    }
    
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
    
    public Doctor getDoctor() {
        return this.doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public LocalDate getMeasurementDate() {
        return this.measurementDate;
    }
    
    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }
    
    public Double getWeight() {
        return this.weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getHeight() {
        return this.height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getBmi() {
        return this.bmi;
    }
    
    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
    
    public Double getHeadCircumference() {
        return this.headCircumference;
    }
    
    public void setHeadCircumference(Double headCircumference) {
        this.headCircumference = headCircumference;
    }
    
    public Double getArmCircumference() {
        return this.armCircumference;
    }
    
    public void setArmCircumference(Double armCircumference) {
        this.armCircumference = armCircumference;
    }
    
    public String getGrowthPercentile() {
        return this.growthPercentile;
    }
    
    public void setGrowthPercentile(String growthPercentile) {
        this.growthPercentile = growthPercentile;
    }
    
    public String getWeightPercentile() {
        return this.weightPercentile;
    }
    
    public void setWeightPercentile(String weightPercentile) {
        this.weightPercentile = weightPercentile;
    }
    
    public String getHeightPercentile() {
        return this.heightPercentile;
    }
    
    public void setHeightPercentile(String heightPercentile) {
        this.heightPercentile = heightPercentile;
    }
    
    public String getBmiPercentile() {
        return this.bmiPercentile;
    }
    
    public void setBmiPercentile(String bmiPercentile) {
        this.bmiPercentile = bmiPercentile;
    }
    
    public String getHeadCircumferencePercentile() {
        return this.headCircumferencePercentile;
    }
    
    public void setHeadCircumferencePercentile(String headCircumferencePercentile) {
        this.headCircumferencePercentile = headCircumferencePercentile;
    }
    
    public String getBloodPressure() {
        return this.bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public String getHeartRate() {
        return this.heartRate;
    }
    
    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }
    
    public String getDevelopmentalMilestones() {
        return this.developmentalMilestones;
    }
    
    public void setDevelopmentalMilestones(String developmentalMilestones) {
        this.developmentalMilestones = developmentalMilestones;
    }
    
    public String getObservations() {
        return this.observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public String getRecommendations() {
        return this.recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getNutritionalStatus() {
        return this.nutritionalStatus;
    }
    
    public void setNutritionalStatus(String nutritionalStatus) {
        this.nutritionalStatus = nutritionalStatus;
    }
    
    public Boolean getGrowthAbnormalityDetected() {
        return this.growthAbnormalityDetected;
    }
    
    public void setGrowthAbnormalityDetected(Boolean growthAbnormalityDetected) {
        this.growthAbnormalityDetected = growthAbnormalityDetected;
    }
    
    public String getAbnormalityDetails() {
        return this.abnormalityDetails;
    }
    
    public void setAbnormalityDetails(String abnormalityDetails) {
        this.abnormalityDetails = abnormalityDetails;
    }
    
    public LocalDateTime getNextCheckupDate() {
        return this.nextCheckupDate;
    }
    
    public void setNextCheckupDate(LocalDateTime nextCheckupDate) {
        this.nextCheckupDate = nextCheckupDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
