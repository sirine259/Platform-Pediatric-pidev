package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostTransplantFollowUpRepository extends JpaRepository<PostTransplantFollowUp, Long> {
    
    // Find by transplant
    List<PostTransplantFollowUp> findByKidneyTransplant(KidneyTransplant kidneyTransplant);
    List<PostTransplantFollowUp> findByKidneyTransplantOrderByFollowUpDateDesc(KidneyTransplant kidneyTransplant);
    
    // Find by transplant ID
    List<PostTransplantFollowUp> findByKidneyTransplantId(Long kidneyTransplantId);
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.kidneyTransplant.id = :kidneyTransplantId")
    List<PostTransplantFollowUp> getFollowUpsByTransplant(@Param("kidneyTransplantId") Long kidneyTransplantId);
    Long countByKidneyTransplantId(Long kidneyTransplantId);
    
    // Find by doctor
    List<PostTransplantFollowUp> findByDoctor(Doctor doctor);
    List<PostTransplantFollowUp> findByDoctorOrderByFollowUpDateDesc(Doctor doctor);
    
    // Find by date range
    List<PostTransplantFollowUp> findByFollowUpDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by status
    List<PostTransplantFollowUp> findByIsFollowUpComplete(Boolean isComplete);
    List<PostTransplantFollowUp> findByIsFollowUpCompleteFalse();
    
    // Find overdue follow-ups
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.followUpDate < :now AND f.isFollowUpComplete = false")
    List<PostTransplantFollowUp> findOverdueFollowUps(@Param("now") LocalDateTime now);
    
    // Find upcoming follow-ups
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.followUpDate BETWEEN :now AND :future AND f.isFollowUpComplete = false")
    List<PostTransplantFollowUp> findUpcomingFollowUps(@Param("now") LocalDateTime now, @Param("future") LocalDateTime future);
    
    // Find today's follow-ups
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE DATE(f.followUpDate) = DATE(:date) AND f.isFollowUpComplete = false")
    List<PostTransplantFollowUp> findTodayFollowUps(@Param("date") LocalDateTime date);
    
    // Find by follow-up type
    List<PostTransplantFollowUp> findByFollowUpType(String followUpType);
    
    // Find by kidney function indicators
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.creatinineLevel > :level")
    List<PostTransplantFollowUp> findByHighCreatinineLevel(@Param("level") Double level);
    
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.gfr < :level")
    List<PostTransplantFollowUp> findByLowGFR(@Param("level") Double level);
    
    // Find with complications
    List<PostTransplantFollowUp> findByComplicationsIsNotNull();
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.complications IS NOT NULL AND f.complications != ''")
    List<PostTransplantFollowUp> findByComplications();
    
    // Complex queries
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE " +
           "f.kidneyTransplant = :kidneyTransplant AND " +
           "f.doctor = :doctor AND " +
           "f.followUpDate BETWEEN :startDate AND :endDate")
    List<PostTransplantFollowUp> findByKidneyTransplantAndDoctorAndDateRange(
        @Param("kidneyTransplant") KidneyTransplant kidneyTransplant,
        @Param("doctor") Doctor doctor,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Statistics queries
    @Query("SELECT COUNT(f) FROM PostTransplantFollowUp f WHERE f.kidneyTransplant = :kidneyTransplant")
    Long countByKidneyTransplant(@Param("kidneyTransplant") KidneyTransplant kidneyTransplant);
    
    @Query("SELECT COUNT(f) FROM PostTransplantFollowUp f WHERE f.doctor = :doctor")
    Long countByDoctor(@Param("doctor") Doctor doctor);
    
    @Query("SELECT COUNT(f) FROM PostTransplantFollowUp f WHERE f.isFollowUpComplete = true")
    Long countCompletedFollowUps();
    
    @Query("SELECT COUNT(f) FROM PostTransplantFollowUp f WHERE f.isFollowUpComplete = false AND f.followUpDate < :now")
    Long countOverdueFollowUps(@Param("now") LocalDateTime now);
    
    // Latest follow-up for transplant
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.kidneyTransplant = :kidneyTransplant ORDER BY f.followUpDate DESC")
    List<PostTransplantFollowUp> findLatestFollowUpForKidneyTransplant(@Param("kidneyTransplant") KidneyTransplant kidneyTransplant);
    
    // Patient attendance tracking
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.patientAttended = false AND f.followUpDate < :now")
    List<PostTransplantFollowUp> findMissedAppointments(@Param("now") LocalDateTime now);
    
    // Medication adjustments tracking
    @Query("SELECT f FROM PostTransplantFollowUp f WHERE f.medicationAdjustments IS NOT NULL AND f.medicationAdjustments != ''")
    List<PostTransplantFollowUp> findWithMedicationAdjustments();
}
