package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.DoctorRepository;
import com.esprit.platformepediatricback.entity.Doctor;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserService userService;

    // CRUD Operations
    public Doctor createDoctor(Doctor doctor) {
        // Validate user exists and has DOCTOR role
        if (doctor.getUser() == null || doctor.getUser().getId() == null) {
            throw new RuntimeException("User is required for doctor");
        }
        
        User user = userService.getUserById(doctor.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != User.Role.DOCTOR) {
            throw new RuntimeException("User must have DOCTOR role");
        }
        
        // Check if doctor already exists for this user
        if (doctorRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Doctor already exists for this user");
        }
        
        doctor.setUser(user);
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Update fields
        doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setSubSpecialization(doctorDetails.getSubSpecialization());
        doctor.setHospital(doctorDetails.getHospital());
        doctor.setDepartment(doctorDetails.getDepartment());
        doctor.setYearsOfExperience(doctorDetails.getYearsOfExperience());
        doctor.setMedicalSchool(doctorDetails.getMedicalSchool());
        doctor.setGraduationDate(doctorDetails.getGraduationDate());
        doctor.setConsultationRoom(doctorDetails.getConsultationRoom());
        doctor.setContactNumber(doctorDetails.getContactNumber());
        doctor.setIsAvailable(doctorDetails.getIsAvailable());

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctorRepository.delete(doctor);
    }

    // Business Operations
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getDoctorsByHospital(String hospital) {
        return doctorRepository.findByHospital(hospital);
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue();
    }

    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findActiveDoctors();
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.searchByName(name);
    }

    public List<Doctor> getDoctorsByMultipleCriteria(String specialization, String hospital, Boolean available) {
        return doctorRepository.findDoctorsByMultipleCriteria(specialization, hospital, available);
    }

    public List<Doctor> getExperiencedDoctors(Integer minYears) {
        return doctorRepository.findByYearsOfExperienceGreaterThan(minYears);
    }

    // Availability Management
    public Doctor setDoctorAvailability(Long doctorId, Boolean available) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.setIsAvailable(available);
        return doctorRepository.save(doctor);
    }

    // Statistics
    public Long countDoctorsBySpecialization(String specialization) {
        return doctorRepository.countBySpecialization(specialization);
    }

    // Validation
    public boolean validateLicenseNumber(String licenseNumber) {
        return !doctorRepository.findByLicenseNumber(licenseNumber).isPresent();
    }

    // Helper methods
    public boolean isDoctorActive(Long doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        return doctor.isPresent() && doctor.get().isActive();
    }

    public String getDoctorFullName(Long doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        return doctor.map(Doctor::getFullName).orElse("Unknown Doctor");
    }
}
