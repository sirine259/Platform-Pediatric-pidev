package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.GrowthMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrowthMonitoringRepository extends JpaRepository<GrowthMonitoring, Long> {
    
    List<GrowthMonitoring> findByMedicalRecordId(Long medicalRecordId);
    
    List<GrowthMonitoring> findByDoctorId(Long doctorId);
    
    Optional<GrowthMonitoring> findTopByMedicalRecordIdOrderByMeasurementDateDesc(Long medicalRecordId);
    
    List<GrowthMonitoring> findByMedicalRecordIdOrderByMeasurementDate(Long medicalRecordId);
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.measurementDate BETWEEN :startDate AND :endDate")
    List<GrowthMonitoring> findByMeasurementDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.medicalRecord.id = :medicalRecordId AND gm.measurementDate BETWEEN :startDate AND :endDate")
    List<GrowthMonitoring> findByMedicalRecordAndDateRange(@Param("medicalRecordId") Long medicalRecordId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.growthAbnormalityDetected = true")
    List<GrowthMonitoring> findAbnormalGrowthCases();
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.medicalRecord.id = :medicalRecordId AND gm.growthAbnormalityDetected = true")
    List<GrowthMonitoring> findAbnormalGrowthByMedicalRecord(@Param("medicalRecordId") Long medicalRecordId);
    
    @Query("SELECT COUNT(gm) FROM GrowthMonitoring gm WHERE gm.growthAbnormalityDetected = true")
    long countAbnormalGrowthCases();
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.nutritionalStatus = :status")
    List<GrowthMonitoring> findByNutritionalStatus(@Param("status") String status);
    
    @Query("SELECT gm FROM GrowthMonitoring gm WHERE gm.nextCheckupDate < :date")
    List<GrowthMonitoring> findOverdueCheckups(@Param("date") LocalDate date);
    
    @Query("SELECT AVG(gm.weight) FROM GrowthMonitoring gm WHERE gm.medicalRecord.id = :medicalRecordId AND gm.measurementDate BETWEEN :startDate AND :endDate")
    Double getAverageWeightByDateRange(@Param("medicalRecordId") Long medicalRecordId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(gm.height) FROM GrowthMonitoring gm WHERE gm.medicalRecord.id = :medicalRecordId AND gm.measurementDate BETWEEN :startDate AND :endDate")
    Double getAverageHeightByDateRange(@Param("medicalRecordId") Long medicalRecordId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
