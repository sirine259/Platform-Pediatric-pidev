package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.Recipient.RecipientStatus;
import com.esprit.platformepediatricback.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    
    List<Recipient> findByStatus(RecipientStatus status);
    
    List<Recipient> findByPatient(Patient patient);
    
    @Query("SELECT r FROM Recipient r WHERE r.bloodType = :bloodType AND r.status = :status")
    List<Recipient> findByBloodTypeAndStatus(@Param("bloodType") String bloodType, @Param("status") RecipientStatus status);
    
    @Query("SELECT r FROM Recipient r WHERE r.bloodType = :bloodType")
    List<Recipient> findByBloodType(@Param("bloodType") String bloodType);
    
    @Query("SELECT r FROM Recipient r WHERE r.patient.firstName LIKE %:name% OR r.patient.lastName LIKE %:name%")
    List<Recipient> findByPatientNameContaining(@Param("name") String name);
    
    @Query("SELECT r FROM Recipient r WHERE r.email = :email")
    Recipient findByEmail(@Param("email") String email);
    
    @Query("SELECT r FROM Recipient r WHERE r.phoneNumber = :phoneNumber")
    Recipient findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT r FROM Recipient r WHERE r.registrationDate BETWEEN :startDate AND :endDate")
    List<Recipient> findByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM Recipient r WHERE r.status = :status")
    long countByStatus(@Param("status") RecipientStatus status);
    
    @Query("SELECT r FROM Recipient r WHERE r.status = 'WAITING' ORDER BY r.registrationDate ASC")
    List<Recipient> findWaitingRecipientsOrderByRegistrationDate();
    
    @Query("SELECT r FROM Recipient r WHERE r.transplantDate IS NULL AND r.status = 'WAITING'")
    List<Recipient> findWaitingRecipientsWithoutTransplant();
}
