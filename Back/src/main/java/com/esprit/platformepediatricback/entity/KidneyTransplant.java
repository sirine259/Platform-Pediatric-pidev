package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kidney_transplants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KidneyTransplant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transplant_id", nullable = false, unique = true)
    private Transplant transplant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @OneToMany(mappedBy = "kidneyTransplant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTransplantFollowUp> postTransplantFollowUps;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgeon_id", nullable = false)
    private User surgeon;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nephrologist_id")
    private User nephrologist;
    
    @Column(nullable = false)
    private LocalDateTime surgeryDate;
    
    @Column
    private LocalDateTime actualStartTime;
    
    @Column
    private LocalDateTime actualEndTime;
    
    @Column
    private Integer surgeryDuration; // en minutes
    
    @Enumerated(EnumType.STRING)
    private TransplantType transplantType;
    
    @Enumerated(EnumType.STRING)
    private SurgeryApproach surgeryApproach;
    
    @Enumerated(EnumType.STRING)
    private KidneySource kidneySource;
    
    @Column
    private String donorKidneyType; // LEFT, RIGHT, BOTH
    
    @Column
    private String recipientKidneyType; // LEFT, RIGHT, BOTH
    
    @Column
    private Integer coldIschemiaTime; // en minutes
    
    @Column
    private Integer warmIschemiaTime; // en minutes
    
    @Column
    private Integer anastomosisTime; // en minutes
    
    @Column
    private String vascularAnastomosis; // ARTERIAL, VENOUS, BOTH
    
    @Column
    private String ureteralImplantation; // URETHERONEOCYSTOSTOMY, URETEROURETEROSTOMY
    
    @Column
    private String surgicalTechnique; // OPEN, LAPAROSCOPIC, ROBOTIC
    
    @Column
    private String hospital;
    
    @Column
    private String operatingRoom;
    
    @Column
    private String anesthesiaType;
    
    @Column
    private String anesthesiaDuration;
    
    @Column
    private Integer estimatedBloodLoss; // en mL
    
    @Column
    private Integer bloodProductsUsed; // en unités
    
    @Column
    private String intraOperativeComplications;
    
    @Column
    private String immediatePostOpComplications;
    
    @Column
    private Integer hospitalStayDuration; // en jours
    
    @Column
    private String postOpMedications;
    
    @Column
    private String immunosuppressionProtocol;
    
    @Column
    private String hlaTyping;
    
    @Column
    private String crossmatchResults;
    
    @Column
    private String panelReactiveAntibodies;
    
    @Column
    private Integer peakCreatinineLevel;
    
    @Column
    private LocalDateTime creatininePeakDate;
    
    @Column
    private Integer baselineCreatinineLevel;
    
    @Column
    private LocalDateTime lastDialysisDate;
    
    @Column
    private Boolean delayedGraftFunction;
    
    @Column
    private Boolean primaryGraftFunction;
    
    @Column
    private Boolean acuteRejection;
    
    @Column
    private LocalDateTime rejectionDate;
    
    @Column
    private String rejectionType; // CELLULAR, HUMORAL, MIXED
    
    @Column
    private String rejectionTreatment;
    
    @Column
    private Boolean surgicalSiteInfection;
    
    @Column
    private LocalDateTime infectionDate;
    
    @Column
    private String infectionTreatment;
    
    @Column
    private Boolean graftFailure;
    
    @Column
    private LocalDateTime graftFailureDate;
    
    @Column
    private String failureCause;
    
    @Column
    private Boolean patientSurvival;
    
    @Column
    private LocalDateTime patientDeathDate;
    
    @Column
    private String deathCause;
    
    @Column
    private Integer graftSurvivalMonths;
    
    @Column
    private String qualityOfLifeScore;
    
    @Column(columnDefinition = "TEXT")
    private String surgicalNotes;
    
    @Column(columnDefinition = "TEXT")
    private String postOperativeNotes;
    
    @Column(columnDefinition = "TEXT")
    private String followUpPlan;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;


    // Enums pour les types spécifiques
    public enum TransplantType {
        LIVING_DONOR,
        DECEASED_DONOR,
        PREEMPTIVE,
        SIMULTANEOUS
    }
    
    public enum SurgeryApproach {
        OPEN,
        LAPAROSCOPIC,
        ROBOTIC_ASSISTED,
        MINIMALLY_INVASIVE
    }
    
    public enum KidneySource {
        LEFT,
        RIGHT,
        BOTH,
        EN_BLOC
    }
    
    // Méthodes utilitaires
    public String getTransplantStatus() {
        if (graftFailure) {
            return "FAILED";
        } else if (acuteRejection) {
            return "REJECTION";
        } else if (delayedGraftFunction) {
            return "DELAYED_FUNCTION";
        } else if (primaryGraftFunction) {
            return "FUNCTIONING";
        } else {
            return "UNKNOWN";
        }
    }
    
    public Integer getGraftAgeInMonths() {
        if (surgeryDate == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(surgeryDate, LocalDateTime.now());
    }
    
    public Boolean isSuccessfulTransplant() {
        return primaryGraftFunction && !graftFailure && !acuteRejection;
    }
    
    public String getRiskLevel() {
        int riskScore = 0;
        
        if (coldIschemiaTime != null && coldIschemiaTime > 30) riskScore += 1;
        if (donorAge() > 50) riskScore += 1;
        if (recipientAge() > 60) riskScore += 1;
        if (hlaMismatch()) riskScore += 2;
        
        if (riskScore >= 4) return "HIGH";
        if (riskScore >= 2) return "MEDIUM";
        return "LOW";
    }
    
    // Helper methods
    private Integer donorAge() {
        // Calculer l'âge du donneur basé sur la date de naissance
        return 45; // Placeholder
    }
    
    private Integer recipientAge() {
        // Calculer l'âge du receveur basé sur la date de naissance
        return 50; // Placeholder
    }
    
    private Boolean hlaMismatch() {
        return panelReactiveAntibodies != null && !panelReactiveAntibodies.isEmpty();
    }
    
    // Manually added getters/setters for Lombok compatibility
    public TransplantType getTransplantType() {
        return this.transplantType;
    }
    
    public void setTransplantType(TransplantType transplantType) {
        this.transplantType = transplantType;
    }
    
    public SurgeryApproach getSurgeryApproach() {
        return this.surgeryApproach;
    }
    
    public void setSurgeryApproach(SurgeryApproach surgeryApproach) {
        this.surgeryApproach = surgeryApproach;
    }
    
    public KidneySource getKidneySource() {
        return this.kidneySource;
    }
    
    public void setKidneySource(KidneySource kidneySource) {
        this.kidneySource = kidneySource;
    }
    
    public Integer getSurgeryDuration() {
        return this.surgeryDuration;
    }
    
    public void setSurgeryDuration(Integer surgeryDuration) {
        this.surgeryDuration = surgeryDuration;
    }
    
    public LocalDateTime getActualStartTime() {
        return this.actualStartTime;
    }
    
    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    
    public LocalDateTime getActualEndTime() {
        return this.actualEndTime;
    }
    
    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
    
    public String getDonorKidneyType() {
        return this.donorKidneyType;
    }
    
    public void setDonorKidneyType(String donorKidneyType) {
        this.donorKidneyType = donorKidneyType;
    }
    
    public String getRecipientKidneyType() {
        return this.recipientKidneyType;
    }
    
    public void setRecipientKidneyType(String recipientKidneyType) {
        this.recipientKidneyType = recipientKidneyType;
    }
    
    public Integer getColdIschemiaTime() {
        return this.coldIschemiaTime;
    }
    
    public void setColdIschemiaTime(Integer coldIschemiaTime) {
        this.coldIschemiaTime = coldIschemiaTime;
    }
    
    public Integer getWarmIschemiaTime() {
        return this.warmIschemiaTime;
    }
    
    public void setWarmIschemiaTime(Integer warmIschemiaTime) {
        this.warmIschemiaTime = warmIschemiaTime;
    }
    
    public Integer getAnastomosisTime() {
        return this.anastomosisTime;
    }
    
    public void setAnastomosisTime(Integer anastomosisTime) {
        this.anastomosisTime = anastomosisTime;
    }
    
    public String getVascularAnastomosis() {
        return this.vascularAnastomosis;
    }
    
    public void setVascularAnastomosis(String vascularAnastomosis) {
        this.vascularAnastomosis = vascularAnastomosis;
    }
    
    public String getUreteralImplantation() {
        return this.ureteralImplantation;
    }
    
    public void setUreteralImplantation(String ureteralImplantation) {
        this.ureteralImplantation = ureteralImplantation;
    }
    
    public String getSurgicalTechnique() {
        return this.surgicalTechnique;
    }
    
    public void setSurgicalTechnique(String surgicalTechnique) {
        this.surgicalTechnique = surgicalTechnique;
    }
    
    public String getHospital() {
        return this.hospital;
    }
    
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public String getOperatingRoom() {
        return this.operatingRoom;
    }
    
    public void setOperatingRoom(String operatingRoom) {
        this.operatingRoom = operatingRoom;
    }
    
    public String getAnesthesiaType() {
        return this.anesthesiaType;
    }
    
    public void setAnesthesiaType(String anesthesiaType) {
        this.anesthesiaType = anesthesiaType;
    }
    
    public String getAnesthesiaDuration() {
        return this.anesthesiaDuration;
    }
    
    public void setAnesthesiaDuration(String anesthesiaDuration) {
        this.anesthesiaDuration = anesthesiaDuration;
    }
    
    public Integer getEstimatedBloodLoss() {
        return this.estimatedBloodLoss;
    }
    
    public void setEstimatedBloodLoss(Integer estimatedBloodLoss) {
        this.estimatedBloodLoss = estimatedBloodLoss;
    }
    
    public Integer getBloodProductsUsed() {
        return this.bloodProductsUsed;
    }
    
    public void setBloodProductsUsed(Integer bloodProductsUsed) {
        this.bloodProductsUsed = bloodProductsUsed;
    }
    
    public String getIntraOperativeComplications() {
        return this.intraOperativeComplications;
    }
    
    public void setIntraOperativeComplications(String intraOperativeComplications) {
        this.intraOperativeComplications = intraOperativeComplications;
    }
    
    public String getImmediatePostOpComplications() {
        return this.immediatePostOpComplications;
    }
    
    public void setImmediatePostOpComplications(String immediatePostOpComplications) {
        this.immediatePostOpComplications = immediatePostOpComplications;
    }
    
    public Integer getHospitalStayDuration() {
        return this.hospitalStayDuration;
    }
    
    public void setHospitalStayDuration(Integer hospitalStayDuration) {
        this.hospitalStayDuration = hospitalStayDuration;
    }
    
    public String getPostOpMedications() {
        return this.postOpMedications;
    }
    
    public void setPostOpMedications(String postOpMedications) {
        this.postOpMedications = postOpMedications;
    }
    
    public String getImmunosuppressionProtocol() {
        return this.immunosuppressionProtocol;
    }
    
    public void setImmunosuppressionProtocol(String immunosuppressionProtocol) {
        this.immunosuppressionProtocol = immunosuppressionProtocol;
    }
    
    public String getHlaTyping() {
        return this.hlaTyping;
    }
    
    public void setHlaTyping(String hlaTyping) {
        this.hlaTyping = hlaTyping;
    }
    
    public String getCrossmatchResults() {
        return this.crossmatchResults;
    }
    
    public void setCrossmatchResults(String crossmatchResults) {
        this.crossmatchResults = crossmatchResults;
    }
    
    public String getPanelReactiveAntibodies() {
        return this.panelReactiveAntibodies;
    }
    
    public void setPanelReactiveAntibodies(String panelReactiveAntibodies) {
        this.panelReactiveAntibodies = panelReactiveAntibodies;
    }
    
    public Integer getPeakCreatinineLevel() {
        return this.peakCreatinineLevel;
    }
    
    public void setPeakCreatinineLevel(Integer peakCreatinineLevel) {
        this.peakCreatinineLevel = peakCreatinineLevel;
    }
    
    public LocalDateTime getCreatininePeakDate() {
        return this.creatininePeakDate;
    }
    
    public void setCreatininePeakDate(LocalDateTime creatininePeakDate) {
        this.creatininePeakDate = creatininePeakDate;
    }
    
    public Integer getBaselineCreatinineLevel() {
        return this.baselineCreatinineLevel;
    }
    
    public void setBaselineCreatinineLevel(Integer baselineCreatinineLevel) {
        this.baselineCreatinineLevel = baselineCreatinineLevel;
    }
    
    public LocalDateTime getLastDialysisDate() {
        return this.lastDialysisDate;
    }
    
    public void setLastDialysisDate(LocalDateTime lastDialysisDate) {
        this.lastDialysisDate = lastDialysisDate;
    }
    
    public Boolean getDelayedGraftFunction() {
        return this.delayedGraftFunction;
    }
    
    public void setDelayedGraftFunction(Boolean delayedGraftFunction) {
        this.delayedGraftFunction = delayedGraftFunction;
    }
    
    public Boolean getPrimaryGraftFunction() {
        return this.primaryGraftFunction;
    }
    
    public void setPrimaryGraftFunction(Boolean primaryGraftFunction) {
        this.primaryGraftFunction = primaryGraftFunction;
    }
    
    public Boolean getAcuteRejection() {
        return this.acuteRejection;
    }
    
    public void setAcuteRejection(Boolean acuteRejection) {
        this.acuteRejection = acuteRejection;
    }
    
    public LocalDateTime getRejectionDate() {
        return this.rejectionDate;
    }
    
    public void setRejectionDate(LocalDateTime rejectionDate) {
        this.rejectionDate = rejectionDate;
    }
    
    public String getRejectionType() {
        return this.rejectionType;
    }
    
    public void setRejectionType(String rejectionType) {
        this.rejectionType = rejectionType;
    }
    
    public String getRejectionTreatment() {
        return this.rejectionTreatment;
    }
    
    public void setRejectionTreatment(String rejectionTreatment) {
        this.rejectionTreatment = rejectionTreatment;
    }
    
    public Boolean getSurgicalSiteInfection() {
        return this.surgicalSiteInfection;
    }
    
    public void setSurgicalSiteInfection(Boolean surgicalSiteInfection) {
        this.surgicalSiteInfection = surgicalSiteInfection;
    }
    
    public LocalDateTime getInfectionDate() {
        return this.infectionDate;
    }
    
    public void setInfectionDate(LocalDateTime infectionDate) {
        this.infectionDate = infectionDate;
    }
    
    public String getInfectionTreatment() {
        return this.infectionTreatment;
    }
    
    public void setInfectionTreatment(String infectionTreatment) {
        this.infectionTreatment = infectionTreatment;
    }
    
    public Boolean getGraftFailure() {
        return this.graftFailure;
    }
    
    public void setGraftFailure(Boolean graftFailure) {
        this.graftFailure = graftFailure;
    }
    
    public LocalDateTime getGraftFailureDate() {
        return this.graftFailureDate;
    }
    
    public void setGraftFailureDate(LocalDateTime graftFailureDate) {
        this.graftFailureDate = graftFailureDate;
    }
    
    public String getFailureCause() {
        return this.failureCause;
    }
    
    public void setFailureCause(String failureCause) {
        this.failureCause = failureCause;
    }
    
    public Boolean getPatientSurvival() {
        return this.patientSurvival;
    }
    
    public void setPatientSurvival(Boolean patientSurvival) {
        this.patientSurvival = patientSurvival;
    }
    
    public LocalDateTime getPatientDeathDate() {
        return this.patientDeathDate;
    }
    
    public void setPatientDeathDate(LocalDateTime patientDeathDate) {
        this.patientDeathDate = patientDeathDate;
    }
    
    public String getDeathCause() {
        return this.deathCause;
    }
    
    public void setDeathCause(String deathCause) {
        this.deathCause = deathCause;
    }
    
    public Integer getGraftSurvivalMonths() {
        return this.graftSurvivalMonths;
    }
    
    public void setGraftSurvivalMonths(Integer graftSurvivalMonths) {
        this.graftSurvivalMonths = graftSurvivalMonths;
    }
    
    public String getQualityOfLifeScore() {
        return this.qualityOfLifeScore;
    }
    
    public void setQualityOfLifeScore(String qualityOfLifeScore) {
        this.qualityOfLifeScore = qualityOfLifeScore;
    }
    
    public String getSurgicalNotes() {
        return this.surgicalNotes;
    }
    
    public void setSurgicalNotes(String surgicalNotes) {
        this.surgicalNotes = surgicalNotes;
    }
    
    public String getPostOperativeNotes() {
        return this.postOperativeNotes;
    }
    
    public void setPostOperativeNotes(String postOperativeNotes) {
        this.postOperativeNotes = postOperativeNotes;
    }
    
    public String getFollowUpPlan() {
        return this.followUpPlan;
    }
    
    public void setFollowUpPlan(String followUpPlan) {
        this.followUpPlan = followUpPlan;
    }
    
    public LocalDateTime getSurgeryDate() {
        return this.surgeryDate;
    }
    
    public void setSurgeryDate(LocalDateTime surgeryDate) {
        this.surgeryDate = surgeryDate;
    }
    
    // Manually added getter for Lombok compatibility
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
    
    public List<PostTransplantFollowUp> getPostTransplantFollowUps() {
        return this.postTransplantFollowUps;
    }
    
    public void setPostTransplantFollowUps(List<PostTransplantFollowUp> postTransplantFollowUps) {
        this.postTransplantFollowUps = postTransplantFollowUps;
    }
}
