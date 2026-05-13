package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Dialysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DialysisRepository extends JpaRepository<Dialysis, Long> {
    
    List<Dialysis> findByMedicalRecordId(Long medicalRecordId);
    
    List<Dialysis> findByDoctorId(Long doctorId);
    
    List<Dialysis> findByStatus(String status);
    
    List<Dialysis> findByDialysisType(String dialysisType);
    
    @Query("SELECT d FROM Dialysis d WHERE d.dialysisDate BETWEEN :startDate AND :endDate")
    List<Dialysis> findByDialysisDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT d FROM Dialysis d WHERE d.medicalRecord.id = :medicalRecordId AND d.dialysisDate BETWEEN :startDate AND :endDate")
    List<Dialysis> findByMedicalRecordAndDateRange(@Param("medicalRecordId") Long medicalRecordId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT d FROM Dialysis d WHERE d.nextDialysisDate < :date")
    List<Dialysis> findOverdueDialyses(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(d) FROM Dialysis d WHERE d.status = 'COMPLETED'")
    long countCompletedDialyses();
    
    @Query("SELECT COUNT(d) FROM Dialysis d WHERE d.status = 'SCHEDULED'")
    long countScheduledDialyses();
    
    @Query("SELECT d FROM Dialysis d WHERE d.accessType = :accessType")
    List<Dialysis> findByAccessType(@Param("accessType") String accessType);
    
    @Query("SELECT AVG(d.duration) FROM Dialysis d WHERE d.status = 'COMPLETED' AND d.dialysisDate BETWEEN :startDate AND :endDate")
    Double getAverageDurationByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
