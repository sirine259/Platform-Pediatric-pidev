package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    
    List<Consultation> findByMedicalRecordId(Long medicalRecordId);
    
    List<Consultation> findByDoctorId(Long doctorId);
    
    List<Consultation> findByStatus(String status);
    
    List<Consultation> findByConsultationType(String consultationType);
    
    @Query("SELECT c FROM Consultation c WHERE c.consultationDate BETWEEN :startDate AND :endDate")
    List<Consultation> findByConsultationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Consultation c WHERE c.medicalRecord.id = :medicalRecordId AND c.consultationDate BETWEEN :startDate AND :endDate")
    List<Consultation> findByMedicalRecordAndDateRange(@Param("medicalRecordId") Long medicalRecordId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Consultation c WHERE c.doctor.id = :doctorId AND c.consultationDate BETWEEN :startDate AND :endDate")
    List<Consultation> findByDoctorAndDateRange(@Param("doctorId") Long doctorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.status = 'COMPLETED'")
    long countCompletedConsultations();
    
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.status = 'SCHEDULED'")
    long countScheduledConsultations();
    
    @Query("SELECT c FROM Consultation c WHERE c.nextAppointment IS NOT NULL AND c.nextAppointment < :date")
    List<Consultation> findUpcomingAppointments(@Param("date") LocalDateTime date);
}
