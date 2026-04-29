package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "dialyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dialysis {
    
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
    private LocalDateTime dialysisDate;
    
    @Column
    private String dialysisType; // HEMODIALYSIS, PERITONEAL_DIALYSIS
    
    @Column
    private Integer duration; // en minutes
    
    @Column
    private Double ultrafiltrationVolume; // en mL
    
    @Column
    private String dialysisFluid; // type de liquide de dialyse
    
    @Column
    private Double bloodFlowRate; // en mL/min
    
    @Column
    private Double dialysateFlowRate; // en mL/min
    
    @Column(columnDefinition = "TEXT")
    private String parameters;
    
    @Column(columnDefinition = "TEXT")
    private String complications;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @Column
    private Double preDialysisWeight;
    
    @Column
    private Double postDialysisWeight;
    
    @Column
    private Double preDialysisBloodPressure;
    
    @Column
    private Double postDialysisBloodPressure;
    
    @Column
    private String accessType; // FISTULA, GRAFT, CATHETER
    
    @Column
    private String accessSite;
    
    @Column(columnDefinition = "TEXT")
    private String medications;
    
    @Column
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    
    @Column
    private LocalDateTime nextDialysisDate;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "SCHEDULED";
        }
        if (dialysisType == null) {
            dialysisType = "HEMODIALYSIS";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    
    public LocalDateTime getDialysisDate() {
        return this.dialysisDate;
    }
    
    public void setDialysisDate(LocalDateTime dialysisDate) {
        this.dialysisDate = dialysisDate;
    }
    
    public String getDialysisType() {
        return this.dialysisType;
    }
    
    public void setDialysisType(String dialysisType) {
        this.dialysisType = dialysisType;
    }
    
    public Integer getDuration() {
        return this.duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Double getUltrafiltrationVolume() {
        return this.ultrafiltrationVolume;
    }
    
    public void setUltrafiltrationVolume(Double ultrafiltrationVolume) {
        this.ultrafiltrationVolume = ultrafiltrationVolume;
    }
    
    public String getDialysisFluid() {
        return this.dialysisFluid;
    }
    
    public void setDialysisFluid(String dialysisFluid) {
        this.dialysisFluid = dialysisFluid;
    }
    
    public Double getBloodFlowRate() {
        return this.bloodFlowRate;
    }
    
    public void setBloodFlowRate(Double bloodFlowRate) {
        this.bloodFlowRate = bloodFlowRate;
    }
    
    public Double getDialysateFlowRate() {
        return this.dialysateFlowRate;
    }
    
    public void setDialysateFlowRate(Double dialysateFlowRate) {
        this.dialysateFlowRate = dialysateFlowRate;
    }
    
    public String getParameters() {
        return this.parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public String getComplications() {
        return this.complications;
    }
    
    public void setComplications(String complications) {
        this.complications = complications;
    }
    
    public String getObservations() {
        return this.observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public Double getPreDialysisWeight() {
        return this.preDialysisWeight;
    }
    
    public void setPreDialysisWeight(Double preDialysisWeight) {
        this.preDialysisWeight = preDialysisWeight;
    }
    
    public Double getPostDialysisWeight() {
        return this.postDialysisWeight;
    }
    
    public void setPostDialysisWeight(Double postDialysisWeight) {
        this.postDialysisWeight = postDialysisWeight;
    }
    
    public Double getPreDialysisBloodPressure() {
        return this.preDialysisBloodPressure;
    }
    
    public void setPreDialysisBloodPressure(Double preDialysisBloodPressure) {
        this.preDialysisBloodPressure = preDialysisBloodPressure;
    }
    
    public Double getPostDialysisBloodPressure() {
        return this.postDialysisBloodPressure;
    }
    
    public void setPostDialysisBloodPressure(Double postDialysisBloodPressure) {
        this.postDialysisBloodPressure = postDialysisBloodPressure;
    }
    
    public String getAccessType() {
        return this.accessType;
    }
    
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    
    public String getAccessSite() {
        return this.accessSite;
    }
    
    public void setAccessSite(String accessSite) {
        this.accessSite = accessSite;
    }
    
    public String getMedications() {
        return this.medications;
    }
    
    public void setMedications(String medications) {
        this.medications = medications;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getNextDialysisDate() {
        return this.nextDialysisDate;
    }
    
    public void setNextDialysisDate(LocalDateTime nextDialysisDate) {
        this.nextDialysisDate = nextDialysisDate;
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
