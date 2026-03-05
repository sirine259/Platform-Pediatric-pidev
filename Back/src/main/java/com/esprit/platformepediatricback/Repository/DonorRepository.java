package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Donor.DonorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    
    List<Donor> findByStatus(DonorStatus status);
    
    List<Donor> findByBloodType(String bloodType);
    
    @Query("SELECT d FROM Donor d WHERE d.bloodType = :bloodType AND d.status = :status")
    List<Donor> findByBloodTypeAndStatus(@Param("bloodType") String bloodType, @Param("status") DonorStatus status);
    
    @Query("SELECT d FROM Donor d WHERE d.firstName LIKE %:firstName% OR d.lastName LIKE %:lastName%")
    List<Donor> findByNameContaining(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    @Query("SELECT d FROM Donor d WHERE d.email = :email")
    Donor findByEmail(@Param("email") String email);
    
    @Query("SELECT d FROM Donor d WHERE d.phoneNumber = :phoneNumber")
    Donor findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT d FROM Donor d WHERE d.registrationDate BETWEEN :startDate AND :endDate")
    List<Donor> findByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(d) FROM Donor d WHERE d.status = :status")
    long countByStatus(@Param("status") DonorStatus status);
    
    @Query("SELECT d FROM Donor d WHERE d.status = 'APPROVED' ORDER BY d.registrationDate ASC")
    List<Donor> findApprovedDonorsOrderByRegistrationDate();
}
