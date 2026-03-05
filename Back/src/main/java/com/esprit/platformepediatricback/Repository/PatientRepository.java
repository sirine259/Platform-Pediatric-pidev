package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Patient;
import com.esprit.platformepediatricback.entity.Doctor;
import com.esprit.platformepediatricback.entity.Patient.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);
    
    boolean existsByMedicalRecordNumber(String medicalRecordNumber);
    
    List<Patient> findByStatus(PatientStatus status);
    
    List<Patient> findByBloodType(String bloodType);
    
    List<Patient> findByPrimaryDoctor(Doctor doctor);
    
    List<Patient> findByIsDeletedFalse();
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND (p.firstName LIKE %:name% OR p.lastName LIKE %:name%)")
    List<Patient> findActivePatientsByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Patient> findActivePatientsByDateOfBirthRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.registrationDate BETWEEN :startDate AND :endDate")
    List<Patient> findActivePatientsByRegistrationDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.lastVisitDate BETWEEN :startDate AND :endDate")
    List<Patient> findActivePatientsByLastVisitDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.bloodType = :bloodType AND p.status = :status")
    List<Patient> findActivePatientsByBloodTypeAndStatus(@Param("bloodType") String bloodType, @Param("status") PatientStatus status);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.email = :email")
    Optional<Patient> findActivePatientByEmail(@Param("email") String email);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.phoneNumber = :phoneNumber")
    Optional<Patient> findActivePatientByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.primaryDoctor = :doctor")
    List<Patient> findActivePatientsByDoctor(@Param("doctor") Doctor doctor);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.isDeleted = false AND p.status = :status")
    long countActivePatientsByStatus(@Param("status") PatientStatus status);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.isDeleted = false")
    long countActivePatients();
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.isDeleted = false AND p.bloodType = :bloodType")
    long countActivePatientsByBloodType(@Param("bloodType") String bloodType);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.allergies LIKE %:allergy%")
    List<Patient> findActivePatientsByAllergy(@Param("allergy") String allergy);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false AND p.currentMedications LIKE %:medication%")
    List<Patient> findActivePatientsByMedication(@Param("medication") String medication);
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false ORDER BY p.registrationDate DESC")
    List<Patient> findActivePatientsOrderByRegistrationDate();
    
    @Query("SELECT p FROM Patient p WHERE p.isDeleted = false ORDER BY p.lastVisitDate DESC")
    List<Patient> findActivePatientsOrderByLastVisitDate();
}
