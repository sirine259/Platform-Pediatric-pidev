package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    Optional<MedicalRecord> findByPatientId(Long patientId);
    
    List<MedicalRecord> findByPrimaryDoctorId(Long doctorId);
    
    List<MedicalRecord> findByIsActive(boolean isActive);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.firstName LIKE %:name% OR mr.patient.lastName LIKE %:name%")
    List<MedicalRecord> findByPatientNameContaining(@Param("name") String name);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.creationDate BETWEEN :startDate AND :endDate")
    List<MedicalRecord> findByCreationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.isActive = true")
    long countActiveMedicalRecords();
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.bloodType = :bloodType")
    List<MedicalRecord> findByBloodType(@Param("bloodType") String bloodType);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.age() BETWEEN :minAge AND :maxAge")
    List<MedicalRecord> findByPatientAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
}
