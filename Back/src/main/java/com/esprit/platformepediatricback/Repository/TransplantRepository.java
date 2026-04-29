package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.Transplant.TransplantStatus;
import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransplantRepository extends JpaRepository<Transplant, Long> {
    
    List<Transplant> findByStatus(TransplantStatus status);
    
    List<Transplant> findByDonor(Donor donor);
    
    List<Transplant> findByRecipient(Recipient recipient);
    
    List<Transplant> findBySurgeon(User surgeon);
    
    @Query("SELECT t FROM Transplant t WHERE t.hospital = :hospital")
    List<Transplant> findByHospital(@Param("hospital") String hospital);
    
    @Query("SELECT t FROM Transplant t WHERE t.scheduledDate BETWEEN :startDate AND :endDate")
    List<Transplant> findByScheduledDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transplant t WHERE t.actualDate BETWEEN :startDate AND :endDate")
    List<Transplant> findByActualDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transplant t WHERE t.isSuccessful = :success")
    List<Transplant> findByIsSuccessful(@Param("success") Boolean success);
    
    @Query("SELECT t FROM Transplant t WHERE t.status = :status AND t.scheduledDate >= :date")
    List<Transplant> findByStatusAndScheduledDateAfter(@Param("status") TransplantStatus status, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(t) FROM Transplant t WHERE t.status = :status")
    long countByStatus(@Param("status") TransplantStatus status);
    
    @Query("SELECT COUNT(t) FROM Transplant t WHERE t.isSuccessful = true")
    long countSuccessfulTransplants();
    
    @Query("SELECT t FROM Transplant t WHERE t.status = 'SCHEDULED' ORDER BY t.scheduledDate ASC")
    List<Transplant> findScheduledTransplantsOrderByDate();
    
    @Query("SELECT t FROM Transplant t WHERE t.donor.bloodType = :bloodType AND t.recipient.bloodType = :bloodType")
    List<Transplant> findByBloodTypeMatch(@Param("bloodType") String bloodType);
}
