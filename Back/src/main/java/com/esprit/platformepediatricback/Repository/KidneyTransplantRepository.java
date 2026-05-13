package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KidneyTransplantRepository extends JpaRepository<KidneyTransplant, Long> {
    
    // Find by basic entities
    Optional<KidneyTransplant> findByTransplant(Transplant transplant);
    List<KidneyTransplant> findByDonor(Donor donor);
    List<KidneyTransplant> findByRecipient(Recipient recipient);
    List<KidneyTransplant> findBySurgeon(User surgeon);
    List<KidneyTransplant> findByNephrologist(User nephrologist);
    
    // Find by date range
    List<KidneyTransplant> findBySurgeryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<KidneyTransplant> findBySurgeryDateAfter(LocalDateTime date);
    List<KidneyTransplant> findBySurgeryDateBefore(LocalDateTime date);
    
    // Find by transplant type
    List<KidneyTransplant> findByTransplantType(KidneyTransplant.TransplantType transplantType);
    List<KidneyTransplant> findByKidneySource(KidneyTransplant.KidneySource kidneySource);
    
    // Find by surgery approach
    List<KidneyTransplant> findBySurgeryApproach(KidneyTransplant.SurgeryApproach surgeryApproach);
    
    // Find by hospital
    List<KidneyTransplant> findByHospital(String hospital);
    
    // Find by outcomes
    List<KidneyTransplant> findByPrimaryGraftFunction(Boolean primaryGraftFunction);
    List<KidneyTransplant> findByGraftFailure(Boolean graftFailure);
    List<KidneyTransplant> findByAcuteRejection(Boolean acuteRejection);
    List<KidneyTransplant> findBySurgicalSiteInfection(Boolean surgicalSiteInfection);
    
    // Find by survival
    List<KidneyTransplant> findByPatientSurvival(Boolean patientSurvival);
    List<KidneyTransplant> findByGraftSurvivalMonthsGreaterThan(Integer months);
    
    // Complex queries
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.transplant = :transplant AND " +
           "k.surgeryDate BETWEEN :startDate AND :endDate")
    List<KidneyTransplant> findByTransplantAndDateRange(
        @Param("transplant") Transplant transplant,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.surgeon = :surgeon AND " +
           "k.surgeryDate BETWEEN :startDate AND :endDate")
    List<KidneyTransplant> findBySurgeonAndDateRange(
        @Param("surgeon") User surgeon,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Statistics queries
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE k.transplantType = :type")
    Long countByTransplantType(@Param("type") KidneyTransplant.TransplantType type);
    
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE k.primaryGraftFunction = true")
    Long countSuccessfulTransplants();
    
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE k.graftFailure = true")
    Long countFailedTransplants();
    
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE k.acuteRejection = true")
    Long countRejections();
    
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE k.surgicalSiteInfection = true")
    Long countInfections();
    
    @Query("SELECT AVG(k.surgeryDuration) FROM KidneyTransplant k WHERE k.surgeryDuration IS NOT NULL")
    Double getAverageSurgeryDuration();
    
    @Query("SELECT AVG(k.hospitalStayDuration) FROM KidneyTransplant k WHERE k.hospitalStayDuration IS NOT NULL")
    Double getAverageHospitalStay();
    
    // Recent transplants
    @Query("SELECT k FROM KidneyTransplant k WHERE k.surgeryDate >= :since ORDER BY k.surgeryDate DESC")
    List<KidneyTransplant> findRecentTransplants(@Param("since") LocalDateTime since);
    
    // High-risk transplants
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "(k.coldIschemiaTime > :ischemiaTime OR " +
           "k.estimatedBloodLoss > :bloodLoss OR " +
           "k.delayedGraftFunction = true)")
    List<KidneyTransplant> findHighRiskTransplants(
        @Param("ischemiaTime") Integer ischemiaTime,
        @Param("bloodLoss") Integer bloodLoss
    );
    
    // Transplant outcomes by time period
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.surgeryDate BETWEEN :startDate AND :endDate AND " +
           "k.primaryGraftFunction = :success")
    List<KidneyTransplant> findSuccessfulTransplantsInPeriod(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("success") Boolean success
    );
    
    // Graft survival analysis
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.graftSurvivalMonths >= :months AND " +
           "k.primaryGraftFunction = true")
    List<KidneyTransplant> findLongTermGraftSurvival(@Param("months") Integer months);
    
    // Complications analysis
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.acuteRejection = true AND " +
           "k.rejectionDate BETWEEN :startDate AND :endDate")
    List<KidneyTransplant> findRejectionsInPeriod(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Surgeon performance
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.surgeon = :surgeon AND " +
           "k.primaryGraftFunction = true")
    List<KidneyTransplant> findSuccessfulTransplantsBySurgeon(@Param("surgeon") User surgeon);
    
    @Query("SELECT COUNT(k) FROM KidneyTransplant k WHERE " +
           "k.surgeon = :surgeon AND " +
           "k.primaryGraftFunction = true")
    Long countSuccessfulTransplantsBySurgeon(@Param("surgeon") User surgeon);
    
    // Hospital statistics
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.hospital = :hospital AND " +
           "k.surgeryDate BETWEEN :startDate AND :endDate")
    List<KidneyTransplant> findTransplantsByHospitalInPeriod(
        @Param("hospital") String hospital,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Quality metrics
    @Query("SELECT AVG(k.graftSurvivalMonths) FROM KidneyTransplant k WHERE " +
           "k.graftSurvivalMonths IS NOT NULL AND " +
           "k.primaryGraftFunction = true")
    Double getAverageGraftSurvival();
    
    @Query("SELECT k FROM KidneyTransplant k WHERE " +
           "k.qualityOfLifeScore >= :minScore ORDER BY k.qualityOfLifeScore DESC")
    List<KidneyTransplant> findHighQualityOfLifeTransplants(@Param("minScore") Integer minScore);
}
