package com.esprit.platformepediatricback.entity;

import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.Doctor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_transplant_follow_up")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTransplantFollowUp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kidney_transplant_id", nullable = false)
    private KidneyTransplant kidneyTransplant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(nullable = false)
    private LocalDateTime followUpDate;
    
    @Column(columnDefinition = "TEXT")
    private String clinicalNotes;
    
    @Column(columnDefinition = "TEXT")
    private String complications;
    
    @Column
    private Double creatinineLevel;
    
    @Column
    private Double gfr; // Glomerular Filtration Rate
    
    @Column
    private String bloodPressure;
    
    @Column
    private String medicationAdjustments;
    
    @Column
    private String labResults;
    
    @Column(columnDefinition = "TEXT")
    private String immunosuppressiveTreatment;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @Column
    private Boolean isFollowUpComplete = false;
    
    @Column
    private LocalDateTime nextFollowUpDate;
    
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    @Column
    private String followUpType; // ROUTINE, URGENT, EMERGENCY
    
    @Column
    private Boolean patientAttended;
    
    @Column(columnDefinition = "TEXT")
    private String patientFeedback;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (followUpType == null) {
            followUpType = "ROUTINE";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public String getFollowUpStatus() {
        if (isFollowUpComplete) {
            return "COMPLETED";
        } else if (followUpDate.isBefore(LocalDateTime.now())) {
            return "OVERDUE";
        } else if (followUpDate.isAfter(LocalDateTime.now())) {
            return "SCHEDULED";
        } else {
            return "TODAY";
        }
    }
    
    public Boolean isKidneyFunctionNormal() {
        if (creatinineLevel == null || gfr == null) {
            return null;
        }
        // Valeurs normales approximatives
        return creatinineLevel <= 1.2 && gfr >= 60;
    }
    
    // Manually added getters/setters for Lombok compatibility
    public KidneyTransplant getKidneyTransplant() {
        return this.kidneyTransplant;
    }
    
    public void setKidneyTransplant(KidneyTransplant kidneyTransplant) {
        this.kidneyTransplant = kidneyTransplant;
    }
    
    public Doctor getDoctor() {
        return this.doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public LocalDateTime getFollowUpDate() {
        return this.followUpDate;
    }
    
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
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
    
    public String getComplications() {
        return this.complications;
    }
    
    public void setComplications(String complications) {
        this.complications = complications;
    }
    
    public Double getCreatinineLevel() {
        return this.creatinineLevel;
    }
    
    public void setCreatinineLevel(Double creatinineLevel) {
        this.creatinineLevel = creatinineLevel;
    }
    
    public Double getGfr() {
        return this.gfr;
    }
    
    public void setGfr(Double gfr) {
        this.gfr = gfr;
    }
    
    public String getBloodPressure() {
        return this.bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public String getMedicationAdjustments() {
        return this.medicationAdjustments;
    }
    
    public void setMedicationAdjustments(String medicationAdjustments) {
        this.medicationAdjustments = medicationAdjustments;
    }
    
    public String getClinicalNotes() {
        return this.clinicalNotes;
    }
    
    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }
    
    public Boolean getIsFollowUpComplete() {
        return this.isFollowUpComplete;
    }
    
    public void setIsFollowUpComplete(Boolean isFollowUpComplete) {
        this.isFollowUpComplete = isFollowUpComplete;
    }
    
    public Boolean getPatientAttended() {
        return this.patientAttended;
    }
    
    public void setPatientAttended(Boolean patientAttended) {
        this.patientAttended = patientAttended;
    }
    
    public String getFollowUpType() {
        return this.followUpType;
    }
    
    public void setFollowUpType(String followUpType) {
        this.followUpType = followUpType;
    }
    
    public KidneyTransplant getTransplant() {
        return this.kidneyTransplant;
    }
    
    public LocalDateTime getNextFollowUpDate() {
        return this.nextFollowUpDate;
    }
    
    public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }
    
    public String getRecommendations() {
        return this.recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
}
