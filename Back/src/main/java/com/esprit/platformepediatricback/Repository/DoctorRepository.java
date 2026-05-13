package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // Find by user
    Optional<Doctor> findByUserId(Long userId);
    
    // Find by license number
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    // Find by specialization
    List<Doctor> findBySpecialization(String specialization);
    
    // Find by hospital
    List<Doctor> findByHospital(String hospital);
    
    // Find by department
    List<Doctor> findByDepartment(String department);
    
    // Find available doctors
    List<Doctor> findByIsAvailableTrue();
    
    // Find active doctors (user enabled + doctor available)
    @Query("SELECT d FROM Doctor d WHERE d.user.enabled = true AND d.isAvailable = true")
    List<Doctor> findActiveDoctors();
    
    // Find by specialization and hospital
    List<Doctor> findBySpecializationAndHospital(String specialization, String hospital);
    
    // Find by years of experience greater than
    List<Doctor> findByYearsOfExperienceGreaterThan(Integer years);
    
    // Count doctors by specialization
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialization = :specialization")
    Long countBySpecialization(@Param("specialization") String specialization);
    
    // Search doctors by name
    @Query("SELECT d FROM Doctor d WHERE d.user.firstName LIKE %:name% OR d.user.lastName LIKE %:name%")
    List<Doctor> searchByName(@Param("name") String name);
    
    // Find doctors by multiple criteria
    @Query("SELECT d FROM Doctor d WHERE " +
           "(:specialization IS NULL OR d.specialization = :specialization) AND " +
           "(:hospital IS NULL OR d.hospital = :hospital) AND " +
           "(:available IS NULL OR d.isAvailable = :available)")
    List<Doctor> findDoctorsByMultipleCriteria(
        @Param("specialization") String specialization,
        @Param("hospital") String hospital,
        @Param("available") Boolean available
    );
}
