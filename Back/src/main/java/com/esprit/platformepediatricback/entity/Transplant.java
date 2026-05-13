package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transplants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transplant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;
    
    @Column(nullable = false)
    private LocalDateTime scheduledDate;
    
    @Column
    private LocalDateTime actualDate;
    
    @Enumerated(EnumType.STRING)
    private TransplantStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgeon_id")
    private User surgeon;
    
    @Column(nullable = false)
    private String hospital;
    
    @Column(columnDefinition = "TEXT")
    private String preOperativeNotes;
    
    @Column(columnDefinition = "TEXT")
    private String postOperativeNotes;
    
    @Column(columnDefinition = "TEXT")
    private String complications;
    
    @Column
    private Boolean isSuccessful;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TransplantStatus.SCHEDULED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum TransplantStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        POSTPONED
    }
    
    // Manually added getters/setters for Lombok compatibility
    public Donor getDonor() {
        return this.donor;
    }
    
    public void setDonor(Donor donor) {
        this.donor = donor;
    }
    
    public Recipient getRecipient() {
        return this.recipient;
    }
    
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }
    
    public LocalDateTime getScheduledDate() {
        return this.scheduledDate;
    }
    
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalDateTime getActualDate() {
        return this.actualDate;
    }
    
    public void setActualDate(LocalDateTime actualDate) {
        this.actualDate = actualDate;
    }
    
    public TransplantStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(TransplantStatus status) {
        this.status = status;
    }
    
    public User getSurgeon() {
        return this.surgeon;
    }
    
    public void setSurgeon(User surgeon) {
        this.surgeon = surgeon;
    }
    
    public String getHospital() {
        return this.hospital;
    }
    
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public String getPreOperativeNotes() {
        return this.preOperativeNotes;
    }
    
    public void setPreOperativeNotes(String preOperativeNotes) {
        this.preOperativeNotes = preOperativeNotes;
    }
    
    public String getPostOperativeNotes() {
        return this.postOperativeNotes;
    }
    
    public void setPostOperativeNotes(String postOperativeNotes) {
        this.postOperativeNotes = postOperativeNotes;
    }
    
    public String getComplications() {
        return this.complications;
    }
    
    public void setComplications(String complications) {
        this.complications = complications;
    }
    
    public Boolean getIsSuccessful() {
        return this.isSuccessful;
    }
    
    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
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
    
    // Manually added setter for Lombok compatibility
    public void setId(Long id) {
        this.id = id;
    }
    
    public Transplant getTransplant() {
        return this;
    }
}
