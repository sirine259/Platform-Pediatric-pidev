package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostTransplantFollowUpRepository;
import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostTransplantFollowUpService {

    @Autowired
    private PostTransplantFollowUpRepository followUpRepository;
    
    @Autowired
    private KidneyTransplantDetailsService kidneyTransplantService;
    
    @Autowired
    private DoctorService doctorService;

    // CRUD Operations
    public PostTransplantFollowUp createFollowUp(PostTransplantFollowUp followUp) {
        // Validate kidney transplant exists
        if (followUp.getKidneyTransplant() == null || followUp.getKidneyTransplant().getId() == null) {
            throw new RuntimeException("Kidney transplant is required");
        }
        
        KidneyTransplant kidneyTransplant = kidneyTransplantService.getKidneyTransplantDetailsById(followUp.getKidneyTransplant().getId())
                .orElseThrow(() -> new RuntimeException("Kidney transplant not found"));
        
        // Validate doctor exists
        if (followUp.getDoctor() == null || followUp.getDoctor().getId() == null) {
            throw new RuntimeException("Doctor is required");
        }
        
        Doctor doctor = doctorService.getDoctorById(followUp.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Set entities
        followUp.setKidneyTransplant(kidneyTransplant);
        followUp.setDoctor(doctor);
        followUp.setCreatedAt(LocalDateTime.now());
        followUp.setUpdatedAt(LocalDateTime.now());
        
        return followUpRepository.save(followUp);
    }

    public Optional<PostTransplantFollowUp> getFollowUpById(Long id) {
        return followUpRepository.findById(id);
    }

    public List<PostTransplantFollowUp> getAllFollowUps() {
        return followUpRepository.findAll();
    }

    public PostTransplantFollowUp updateFollowUp(Long id, PostTransplantFollowUp followUpDetails) {
        PostTransplantFollowUp existingFollowUp = followUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow-up not found"));

        // Update fields
        existingFollowUp.setFollowUpDate(followUpDetails.getFollowUpDate());
        existingFollowUp.setClinicalNotes(followUpDetails.getClinicalNotes());
        existingFollowUp.setComplications(followUpDetails.getComplications());
        existingFollowUp.setCreatinineLevel(followUpDetails.getCreatinineLevel());
        existingFollowUp.setGfr(followUpDetails.getGfr());
        existingFollowUp.setBloodPressure(followUpDetails.getBloodPressure());
        existingFollowUp.setMedicationAdjustments(followUpDetails.getMedicationAdjustments());
        existingFollowUp.setIsFollowUpComplete(followUpDetails.getIsFollowUpComplete());
        existingFollowUp.setPatientAttended(followUpDetails.getPatientAttended());
        existingFollowUp.setFollowUpType(followUpDetails.getFollowUpType());
        existingFollowUp.setUpdatedAt(LocalDateTime.now());

        return followUpRepository.save(existingFollowUp);
    }

    public void deleteFollowUp(Long id) {
        PostTransplantFollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow-up not found"));
        followUpRepository.delete(followUp);
    }

    // Business Operations
    public List<PostTransplantFollowUp> getFollowUpsByKidneyTransplant(Long kidneyTransplantId) {
        KidneyTransplant kidneyTransplant = new KidneyTransplant();
        kidneyTransplant.setId(kidneyTransplantId);
        return followUpRepository.findByKidneyTransplant(kidneyTransplant);
    }

    public List<PostTransplantFollowUp> getFollowUpsByDoctor(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        return followUpRepository.findByDoctor(doctor);
    }

    public List<PostTransplantFollowUp> getFollowUpsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return followUpRepository.findByFollowUpDateBetween(startDate, endDate);
    }

    public List<PostTransplantFollowUp> getOverdueFollowUps() {
        return followUpRepository.findOverdueFollowUps(LocalDateTime.now());
    }

    public List<PostTransplantFollowUp> getUpcomingFollowUps(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        return followUpRepository.findUpcomingFollowUps(now, future);
    }

    public List<PostTransplantFollowUp> getTodayFollowUps() {
        return followUpRepository.findTodayFollowUps(LocalDateTime.now());
    }

    public List<PostTransplantFollowUp> getCompletedFollowUps() {
        return followUpRepository.findByIsFollowUpComplete(true);
    }

    public List<PostTransplantFollowUp> getPendingFollowUps() {
        return followUpRepository.findByIsFollowUpComplete(false);
    }

    // Kidney Function Monitoring
    public List<PostTransplantFollowUp> getFollowUpsWithHighCreatinine(Double threshold) {
        return followUpRepository.findByHighCreatinineLevel(threshold);
    }

    public List<PostTransplantFollowUp> getFollowUpsWithLowGFR(Double threshold) {
        return followUpRepository.findByLowGFR(threshold);
    }

    public List<PostTransplantFollowUp> getFollowUpsWithComplications() {
        return followUpRepository.findByComplications();
    }

    // Statistics
    public Long countFollowUpsByKidneyTransplant(Long kidneyTransplantId) {
        KidneyTransplant kidneyTransplant = new KidneyTransplant();
        kidneyTransplant.setId(kidneyTransplantId);
        return followUpRepository.countByKidneyTransplant(kidneyTransplant);
    }

    public Long countFollowUpsByDoctor(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        return followUpRepository.countByDoctor(doctor);
    }

    public Long countCompletedFollowUps() {
        return followUpRepository.countCompletedFollowUps();
    }

    public Long countOverdueFollowUps() {
        return followUpRepository.countOverdueFollowUps(LocalDateTime.now());
    }

    // Advanced Operations
    public List<PostTransplantFollowUp> getFollowUpsByMultipleCriteria(
            Long transplantId, Long doctorId, String followUpType, Boolean isComplete) {
        
        // This would require a custom query implementation
        // For now, return filtered results
        List<PostTransplantFollowUp> results = followUpRepository.findAll();
        
        if (transplantId != null) {
            results = results.stream()
                    .filter(f -> f.getTransplant().getId().equals(transplantId))
                    .toList();
        }
        
        if (doctorId != null) {
            results = results.stream()
                    .filter(f -> f.getDoctor().getId().equals(doctorId))
                    .toList();
        }
        
        if (followUpType != null) {
            results = results.stream()
                    .filter(f -> followUpType.equals(f.getFollowUpType()))
                    .toList();
        }
        
        if (isComplete != null) {
            results = results.stream()
                    .filter(f -> isComplete.equals(f.getIsFollowUpComplete()))
                    .toList();
        }
        
        return results;
    }

    // Alert Methods
    public List<PostTransplantFollowUp> getCriticalFollowUps() {
        // Combine multiple criteria for critical cases
        return followUpRepository.findAll().stream()
                .filter(f -> !f.getIsFollowUpComplete())
                .filter(f -> f.getFollowUpDate().isBefore(LocalDateTime.now().plusDays(1))) // Overdue or due tomorrow
                .filter(f -> f.getCreatinineLevel() != null && f.getCreatinineLevel() > 2.0) // High creatinine
                .toList();
    }

    // Helper Methods
    public boolean isKidneyFunctionNormal(Long followUpId) {
        Optional<PostTransplantFollowUp> followUp = followUpRepository.findById(followUpId);
        return followUp.map(PostTransplantFollowUp::isKidneyFunctionNormal).orElse(false);
    }

    public String getFollowUpStatus(Long followUpId) {
        Optional<PostTransplantFollowUp> followUp = followUpRepository.findById(followUpId);
        return followUp.map(PostTransplantFollowUp::getFollowUpStatus).orElse("NOT_FOUND");
    }
    
    // Additional missing methods
    public PostTransplantFollowUp completeFollowUp(Long followUpId, String clinicalNotes, String recommendations) {
        Optional<PostTransplantFollowUp> optionalFollowUp = followUpRepository.findById(followUpId);
        if (optionalFollowUp.isPresent()) {
            PostTransplantFollowUp followUp = optionalFollowUp.get();
            followUp.setIsFollowUpComplete(true);
            followUp.setClinicalNotes(clinicalNotes);
            followUp.setRecommendations(recommendations);
            followUp.setUpdatedAt(LocalDateTime.now());
            return followUpRepository.save(followUp);
        }
        return null;
    }
    
    public PostTransplantFollowUp scheduleNextFollowUp(Long followUpId, LocalDateTime nextDate, String followUpType) {
        Optional<PostTransplantFollowUp> optionalFollowUp = followUpRepository.findById(followUpId);
        if (optionalFollowUp.isPresent()) {
            PostTransplantFollowUp followUp = optionalFollowUp.get();
            followUp.setNextFollowUpDate(nextDate);
            followUp.setFollowUpType(followUpType);
            followUp.setUpdatedAt(LocalDateTime.now());
            return followUpRepository.save(followUp);
        }
        return null;
    }
    
    public Long countFollowUpsByTransplant(Long transplantId) {
        return followUpRepository.countByKidneyTransplantId(transplantId);
    }
    
    public List<PostTransplantFollowUp> getFollowUpsByTransplant(Long transplantId) {
        return followUpRepository.getFollowUpsByTransplant(transplantId);
    }
    
    public List<PostTransplantFollowUp> getIncompleteFollowUps() {
        return followUpRepository.findByIsFollowUpCompleteFalse();
    }
    
    public List<PostTransplantFollowUp> getFollowUpsByType(String followUpType) {
        return followUpRepository.findByFollowUpType(followUpType);
    }

    public void deleteFollowUpForTransplant(Long id, Long followUpId) {
    }

    public PostTransplantFollowUp createFollowUpForTransplant(Long id, PostTransplantFollowUp followUp) {
        return followUp;
    }

    public PostTransplantFollowUp updateFollowUpForTransplant(Long id, Long followUpId, PostTransplantFollowUp followUp) {
        return followUp;
    }
}
