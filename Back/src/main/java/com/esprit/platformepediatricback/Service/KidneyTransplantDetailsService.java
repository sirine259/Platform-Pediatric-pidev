package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.KidneyTransplantRepository;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KidneyTransplantDetailsService {

    @Autowired
    private KidneyTransplantRepository kidneyTransplantRepository;
    
    @Autowired
    private KidneyTransplantService transplantService;

    // CRUD Operations for KidneyTransplant entity
    public KidneyTransplant createKidneyTransplantDetails(KidneyTransplant kidneyTransplant) {
        return kidneyTransplantRepository.save(kidneyTransplant);
    }

    public Optional<KidneyTransplant> getKidneyTransplantDetailsById(Long id) {
        return kidneyTransplantRepository.findById(id);
    }

    public List<KidneyTransplant> getAllKidneyTransplantDetails() {
        return kidneyTransplantRepository.findAll();
    }

    public KidneyTransplant updateKidneyTransplantDetails(Long id, KidneyTransplant kidneyTransplantDetails) {
        KidneyTransplant existing = kidneyTransplantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kidney transplant details not found"));
        
        // Update all fields
        existing.setSurgeryDate(kidneyTransplantDetails.getSurgeryDate());
        existing.setActualStartTime(kidneyTransplantDetails.getActualStartTime());
        existing.setActualEndTime(kidneyTransplantDetails.getActualEndTime());
        existing.setSurgeryDuration(kidneyTransplantDetails.getSurgeryDuration());
        existing.setTransplantType(kidneyTransplantDetails.getTransplantType());
        existing.setSurgeryApproach(kidneyTransplantDetails.getSurgeryApproach());
        existing.setKidneySource(kidneyTransplantDetails.getKidneySource());
        existing.setDonorKidneyType(kidneyTransplantDetails.getDonorKidneyType());
        existing.setRecipientKidneyType(kidneyTransplantDetails.getRecipientKidneyType());
        existing.setColdIschemiaTime(kidneyTransplantDetails.getColdIschemiaTime());
        existing.setWarmIschemiaTime(kidneyTransplantDetails.getWarmIschemiaTime());
        existing.setAnastomosisTime(kidneyTransplantDetails.getAnastomosisTime());
        existing.setVascularAnastomosis(kidneyTransplantDetails.getVascularAnastomosis());
        existing.setUreteralImplantation(kidneyTransplantDetails.getUreteralImplantation());
        existing.setSurgicalTechnique(kidneyTransplantDetails.getSurgicalTechnique());
        existing.setHospital(kidneyTransplantDetails.getHospital());
        existing.setOperatingRoom(kidneyTransplantDetails.getOperatingRoom());
        existing.setAnesthesiaType(kidneyTransplantDetails.getAnesthesiaType());
        existing.setAnesthesiaDuration(kidneyTransplantDetails.getAnesthesiaDuration());
        existing.setEstimatedBloodLoss(kidneyTransplantDetails.getEstimatedBloodLoss());
        existing.setBloodProductsUsed(kidneyTransplantDetails.getBloodProductsUsed());
        existing.setIntraOperativeComplications(kidneyTransplantDetails.getIntraOperativeComplications());
        existing.setImmediatePostOpComplications(kidneyTransplantDetails.getImmediatePostOpComplications());
        existing.setHospitalStayDuration(kidneyTransplantDetails.getHospitalStayDuration());
        existing.setPostOpMedications(kidneyTransplantDetails.getPostOpMedications());
        existing.setImmunosuppressionProtocol(kidneyTransplantDetails.getImmunosuppressionProtocol());
        existing.setHlaTyping(kidneyTransplantDetails.getHlaTyping());
        existing.setCrossmatchResults(kidneyTransplantDetails.getCrossmatchResults());
        existing.setPanelReactiveAntibodies(kidneyTransplantDetails.getPanelReactiveAntibodies());
        existing.setPeakCreatinineLevel(kidneyTransplantDetails.getPeakCreatinineLevel());
        existing.setCreatininePeakDate(kidneyTransplantDetails.getCreatininePeakDate());
        existing.setBaselineCreatinineLevel(kidneyTransplantDetails.getBaselineCreatinineLevel());
        existing.setLastDialysisDate(kidneyTransplantDetails.getLastDialysisDate());
        existing.setDelayedGraftFunction(kidneyTransplantDetails.getDelayedGraftFunction());
        existing.setPrimaryGraftFunction(kidneyTransplantDetails.getPrimaryGraftFunction());
        existing.setAcuteRejection(kidneyTransplantDetails.getAcuteRejection());
        existing.setRejectionDate(kidneyTransplantDetails.getRejectionDate());
        existing.setRejectionType(kidneyTransplantDetails.getRejectionType());
        existing.setRejectionTreatment(kidneyTransplantDetails.getRejectionTreatment());
        existing.setSurgicalSiteInfection(kidneyTransplantDetails.getSurgicalSiteInfection());
        existing.setInfectionDate(kidneyTransplantDetails.getInfectionDate());
        existing.setInfectionTreatment(kidneyTransplantDetails.getInfectionTreatment());
        existing.setGraftFailure(kidneyTransplantDetails.getGraftFailure());
        existing.setGraftFailureDate(kidneyTransplantDetails.getGraftFailureDate());
        existing.setFailureCause(kidneyTransplantDetails.getFailureCause());
        existing.setPatientSurvival(kidneyTransplantDetails.getPatientSurvival());
        existing.setPatientDeathDate(kidneyTransplantDetails.getPatientDeathDate());
        existing.setDeathCause(kidneyTransplantDetails.getDeathCause());
        existing.setGraftSurvivalMonths(kidneyTransplantDetails.getGraftSurvivalMonths());
        existing.setQualityOfLifeScore(kidneyTransplantDetails.getQualityOfLifeScore());
        existing.setSurgicalNotes(kidneyTransplantDetails.getSurgicalNotes());
        existing.setPostOperativeNotes(kidneyTransplantDetails.getPostOperativeNotes());
        existing.setFollowUpPlan(kidneyTransplantDetails.getFollowUpPlan());

        return kidneyTransplantRepository.save(existing);
    }

    public void deleteKidneyTransplantDetails(Long id) {
        kidneyTransplantRepository.deleteById(id);
    }

    // Business Operations
    public List<KidneyTransplant> getTransplantsByType(KidneyTransplant.TransplantType type) {
        return kidneyTransplantRepository.findByTransplantType(type);
    }

    public List<KidneyTransplant> getTransplantsByApproach(KidneyTransplant.SurgeryApproach approach) {
        return kidneyTransplantRepository.findBySurgeryApproach(approach);
    }

    public List<KidneyTransplant> getTransplantsByHospital(String hospital) {
        return kidneyTransplantRepository.findByHospital(hospital);
    }

    // Outcome Analysis
    public List<KidneyTransplant> getSuccessfulTransplants() {
        return kidneyTransplantRepository.findByPrimaryGraftFunction(true);
    }

    public List<KidneyTransplant> getFailedTransplants() {
        return kidneyTransplantRepository.findByGraftFailure(true);
    }

    public List<KidneyTransplant> getTransplantsWithRejection() {
        return kidneyTransplantRepository.findByAcuteRejection(true);
    }

    // Statistics
    public Long countByType(KidneyTransplant.TransplantType type) {
        return kidneyTransplantRepository.countByTransplantType(type);
    }

    public Long countSuccessful() {
        return kidneyTransplantRepository.countSuccessfulTransplants();
    }

    public Long countFailed() {
        return kidneyTransplantRepository.countFailedTransplants();
    }

    public Long countRejections() {
        return kidneyTransplantRepository.countRejections();
    }

    // Quality Metrics
    public Double getAverageSurgeryDuration() {
        return kidneyTransplantRepository.getAverageSurgeryDuration();
    }

    public Double getAverageHospitalStay() {
        return kidneyTransplantRepository.getAverageHospitalStay();
    }

    public Double getAverageGraftSurvival() {
        return kidneyTransplantRepository.getAverageGraftSurvival();
    }

    // Risk Analysis
    public List<KidneyTransplant> getHighRiskTransplants() {
        return kidneyTransplantRepository.findHighRiskTransplants(30, 1000);
    }

    // Helper Methods
    public String getTransplantRiskLevel(KidneyTransplant transplant) {
        return transplant.getRiskLevel();
    }

    public Boolean isSuccessful(KidneyTransplant transplant) {
        return transplant.isSuccessfulTransplant();
    }

    public String getTransplantStatus(KidneyTransplant transplant) {
        return transplant.getTransplantStatus();
    }
}
