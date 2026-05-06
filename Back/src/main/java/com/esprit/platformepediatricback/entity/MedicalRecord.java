package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_doctor_id")
    private Doctor primaryDoctor;
    
    @Column(nullable = false)
    private LocalDateTime creationDate;
    
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(columnDefinition = "TEXT")
    private String allergies;
    
    @Column(columnDefinition = "TEXT")
    private String chronicDiseases;
    
    @Column(columnDefinition = "TEXT")
    private String currentMedications;
    
    @Column(columnDefinition = "TEXT")
    private String familyHistory;
    
    @Column(columnDefinition = "TEXT")
    private String surgicalHistory;
    
    @Column(columnDefinition = "TEXT")
    private String vaccinationRecord;
    
    @Column
    private String bloodType;
    
    @Column
    private String rhFactor;
    
    @Column
    private Double height;
    
    @Column
    private Double weight;
    
    @Column
    private String bmi;
    
    @Column
    private String bloodPressure;
    
    @Column
    private String heartRate;
    
    @Column
    private String respiratoryRate;
    
    @Column
    private Double temperature;
    
    @Column
    private String emergencyContact;
    
    @Column
    private String emergencyPhone;
    
    @Column
    private String insuranceNumber;
    
    @Column
    private String insuranceProvider;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KidneyTransplant> kidneyTransplants;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dialysis> dialyses;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrowthMonitoring> growthMonitorings;
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime lastUpdated;
    
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public String getPatientFullName() {
        return patient != null ? patient.getFirstName() + " " + patient.getLastName() : "";
    }
    
    public String getPatientMedicalRecordNumber() {
        return patient != null ? patient.getMedicalRecordNumber() : "";
    }
    
    public Integer getPatientAge() {
        return patient != null ? patient.getAge() : null;
    }
    
    public String getPrimaryDoctorName() {
        return primaryDoctor != null ? primaryDoctor.getFullName() : "";
    }
    
    public Boolean isAdult() {
        Integer age = getPatientAge();
        return age != null && age >= 18;
    }
    
    public String getRiskLevel() {
        int riskScore = 0;
        
        if (chronicDiseases != null && !chronicDiseases.isEmpty()) riskScore += 2;
        if (allergies != null && !allergies.isEmpty()) riskScore += 1;
        if (surgicalHistory != null && !surgicalHistory.isEmpty()) riskScore += 1;
        
        if (riskScore >= 3) return "HIGH";
        if (riskScore >= 1) return "MEDIUM";
        return "LOW";
    }
    
    // Manually added getters/setters for Lombok compatibility
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Patient getPatient() {
        return this.patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Doctor getPrimaryDoctor() {
        return this.primaryDoctor;
    }
    
    public void setPrimaryDoctor(Doctor primaryDoctor) {
        this.primaryDoctor = primaryDoctor;
    }
    
    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getMedicalHistory() {
        return this.medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getAllergies() {
        return this.allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public String getChronicDiseases() {
        return this.chronicDiseases;
    }
    
    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }
    
    public String getCurrentMedications() {
        return this.currentMedications;
    }
    
    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }
    
    public String getFamilyHistory() {
        return this.familyHistory;
    }
    
    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }
    
    public String getSurgicalHistory() {
        return this.surgicalHistory;
    }
    
    public void setSurgicalHistory(String surgicalHistory) {
        this.surgicalHistory = surgicalHistory;
    }
    
    public String getVaccinationRecord() {
        return this.vaccinationRecord;
    }
    
    public void setVaccinationRecord(String vaccinationRecord) {
        this.vaccinationRecord = vaccinationRecord;
    }
    
    public String getBloodType() {
        return this.bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public String getRhFactor() {
        return this.rhFactor;
    }
    
    public void setRhFactor(String rhFactor) {
        this.rhFactor = rhFactor;
    }
    
    public Double getHeight() {
        return this.height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getWeight() {
        return this.weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public String getBmi() {
        return this.bmi;
    }
    
    public void setBmi(String bmi) {
        this.bmi = bmi;
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
    
    public String getRespiratoryRate() {
        return this.respiratoryRate;
    }
    
    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }
    
    public Double getTemperature() {
        return this.temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public String getEmergencyContact() {
        return this.emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getEmergencyPhone() {
        return this.emergencyPhone;
    }
    
    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }
    
    public String getInsuranceNumber() {
        return this.insuranceNumber;
    }
    
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    
    public String getInsuranceProvider() {
        return this.insuranceProvider;
    }
    
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }
    
    public Boolean getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
