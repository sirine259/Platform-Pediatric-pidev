package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Manually added setter for Lombok compatibility
    public void setId(Long id) {
        this.id = id;
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false)
    private String licenseNumber;
    
    @Column(nullable = false)
    private String specialization;
    
    @Column
    private String subSpecialization;
    
    @Column
    private String hospital;
    
    @Column
    private String department;
    
    @Column
    private Integer yearsOfExperience;
    
    @Column
    private String medicalSchool;
    
    @Column
    private LocalDateTime graduationDate;
    
    @Column
    private String consultationRoom;
    
    @Column
    private String contactNumber;
    
    @Column
    private Boolean isAvailable = true;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public String getFullName() {
        return user != null ? user.getFirstName() + " " + user.getLastName() : "";
    }
    
    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }
    
    public Boolean isActive() {
        return user != null && user.getEnabled() && isAvailable;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getLicenseNumber() {
        return this.licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getSpecialization() {
        return this.specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getConsultationRoom() {
        return this.consultationRoom;
    }
    
    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }
    
    public String getContactNumber() {
        return this.contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public Boolean getIsAvailable() {
        return this.isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
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
    
    public Integer getYearsOfExperience() {
        return this.yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getMedicalSchool() {
        return this.medicalSchool;
    }
    
    public void setMedicalSchool(String medicalSchool) {
        this.medicalSchool = medicalSchool;
    }
    
    public LocalDateTime getGraduationDate() {
        return this.graduationDate;
    }
    
    public void setGraduationDate(LocalDateTime graduationDate) {
        this.graduationDate = graduationDate;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getSubSpecialization() {
        return this.subSpecialization;
    }
    
    public void setSubSpecialization(String subSpecialization) {
        this.subSpecialization = subSpecialization;
    }
    
    public String getHospital() {
        return this.hospital;
    }
    
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public String getDepartment() {
        return this.department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}
