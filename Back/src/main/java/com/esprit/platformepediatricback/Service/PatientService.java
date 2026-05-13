package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PatientRepository;
import com.esprit.platformepediatricback.Repository.DoctorRepository;
import com.esprit.platformepediatricback.entity.Patient;
import com.esprit.platformepediatricback.entity.Doctor;
import com.esprit.platformepediatricback.entity.Patient.PatientStatus;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    // Patient CRUD operations
    public Patient createPatient(Patient patient) {
        if (patientRepository.existsByMedicalRecordNumber(patient.getMedicalRecordNumber())) {
            throw new RuntimeException("Medical record number already exists");
        }
        
        patient.setRegistrationDate(LocalDateTime.now());
        patient.setLastVisitDate(LocalDateTime.now());
        patient.setDeleted(false);
        
        // Generate medical record number if not provided
        if (patient.getMedicalRecordNumber() == null || patient.getMedicalRecordNumber().isEmpty()) {
            patient.setMedicalRecordNumber(generateMedicalRecordNumber());
        }
        
        return patientRepository.save(patient);
    }
    
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id)
                .filter(patient -> !patient.isDeleted());
    }
    
    public Optional<Patient> getPatientByMedicalRecordNumber(String medicalRecordNumber) {
        return patientRepository.findByMedicalRecordNumber(medicalRecordNumber)
                .filter(patient -> !patient.isDeleted());
    }
    
    public List<Patient> getAllPatients() {
        return patientRepository.findByIsDeletedFalse();
    }
    
    public List<Patient> getPatientsByStatus(PatientStatus status) {
        return patientRepository.findByStatus(status);
    }
    
    public List<Patient> getPatientsByBloodType(String bloodType) {
        return patientRepository.findByBloodType(bloodType);
    }
    
    public List<Patient> getPatientsByDoctor(Doctor doctor) {
        return patientRepository.findActivePatientsByDoctor(doctor);
    }
    
    public Patient updatePatient(Long id, Patient patientDetails) {
        Optional<Patient> optionalPatient = patientRepository.findById(id)
                .filter(patient -> !patient.isDeleted());
        
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            
            // Check if medical record number is being changed and if it's already taken
            if (!patient.getMedicalRecordNumber().equals(patientDetails.getMedicalRecordNumber()) && 
                patientRepository.existsByMedicalRecordNumber(patientDetails.getMedicalRecordNumber())) {
                throw new RuntimeException("Medical record number already exists");
            }
            
            patient.setFirstName(patientDetails.getFirstName());
            patient.setLastName(patientDetails.getLastName());
            patient.setDateOfBirth(patientDetails.getDateOfBirth());
            patient.setGender(patientDetails.getGender());
            patient.setMedicalRecordNumber(patientDetails.getMedicalRecordNumber());
            patient.setBloodType(patientDetails.getBloodType());
            patient.setPhoneNumber(patientDetails.getPhoneNumber());
            patient.setEmail(patientDetails.getEmail());
            patient.setAddress(patientDetails.getAddress());
            patient.setEmergencyContactName(patientDetails.getEmergencyContactName());
            patient.setEmergencyContactPhone(patientDetails.getEmergencyContactPhone());
            patient.setAllergies(patientDetails.getAllergies());
            patient.setMedicalHistory(patientDetails.getMedicalHistory());
            patient.setCurrentMedications(patientDetails.getCurrentMedications());
            patient.setFamilyMedicalHistory(patientDetails.getFamilyMedicalHistory());
            patient.setWeight(patientDetails.getWeight());
            patient.setHeight(patientDetails.getHeight());
            patient.setInsuranceNumber(patientDetails.getInsuranceNumber());
            patient.setInsuranceProvider(patientDetails.getInsuranceProvider());
            patient.setStatus(patientDetails.getStatus());
            patient.setPrimaryDoctor(patientDetails.getPrimaryDoctor());
            patient.setNotes(patientDetails.getNotes());
            patient.setLastVisitDate(LocalDateTime.now());
            
            return patientRepository.save(patient);
        }
        return null;
    }
    
    public boolean deletePatient(Long id) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setDeleted(true);
            patientRepository.save(patient);
            return true;
        }
        return false;
    }
    
    public boolean permanentlyDeletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Search and filtering methods
    public List<Patient> searchPatientsByName(String name) {
        return patientRepository.findActivePatientsByNameContaining(name);
    }
    
    public List<Patient> getPatientsByDateOfBirthRange(LocalDate startDate, LocalDate endDate) {
        return patientRepository.findActivePatientsByDateOfBirthRange(startDate, endDate);
    }
    
    public List<Patient> getPatientsByRegistrationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return patientRepository.findActivePatientsByRegistrationDateRange(startDate, endDate);
    }
    
    public List<Patient> getPatientsByLastVisitDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return patientRepository.findActivePatientsByLastVisitDateRange(startDate, endDate);
    }
    
    public List<Patient> getPatientsByBloodTypeAndStatus(String bloodType, PatientStatus status) {
        return patientRepository.findActivePatientsByBloodTypeAndStatus(bloodType, status);
    }
    
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findActivePatientByEmail(email);
    }
    
    public Optional<Patient> getPatientByPhoneNumber(String phoneNumber) {
        return patientRepository.findActivePatientByPhoneNumber(phoneNumber);
    }
    
    public List<Patient> getPatientsByAllergy(String allergy) {
        return patientRepository.findActivePatientsByAllergy(allergy);
    }
    
    public List<Patient> getPatientsByMedication(String medication) {
        return patientRepository.findActivePatientsByMedication(medication);
    }
    
    public List<Patient> getPatientsOrderByRegistrationDate() {
        return patientRepository.findActivePatientsOrderByRegistrationDate();
    }
    
    public List<Patient> getPatientsOrderByLastVisitDate() {
        return patientRepository.findActivePatientsOrderByLastVisitDate();
    }
    
    // Medical record management
    public boolean updatePatientStatus(Long id, PatientStatus newStatus) {
        Optional<Patient> optionalPatient = patientRepository.findById(id)
                .filter(patient -> !patient.isDeleted());
        
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setStatus(newStatus);
            patient.setLastVisitDate(LocalDateTime.now());
            patientRepository.save(patient);
            return true;
        }
        return false;
    }
    
    public boolean assignDoctor(Long patientId, Long doctorId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId)
                .filter(patient -> !patient.isDeleted());
        
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        
        if (optionalPatient.isPresent() && optionalDoctor.isPresent()) {
            Patient patient = optionalPatient.get();
            Doctor doctor = optionalDoctor.get();
            patient.setPrimaryDoctor(doctor);
            patient.setLastVisitDate(LocalDateTime.now());
            patientRepository.save(patient);
            return true;
        }
        return false;
    }
    
    public boolean updateVitals(Long id, Double weight, Double height) {
        Optional<Patient> optionalPatient = patientRepository.findById(id)
                .filter(patient -> !patient.isDeleted());
        
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setWeight(weight);
            patient.setHeight(height);
            patient.setLastVisitDate(LocalDateTime.now());
            patientRepository.save(patient);
            return true;
        }
        return false;
    }
    
    public boolean updateMedicalHistory(Long id, String medicalHistory, String allergies, String currentMedications) {
        Optional<Patient> optionalPatient = patientRepository.findById(id)
                .filter(patient -> !patient.isDeleted());
        
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setMedicalHistory(medicalHistory);
            patient.setAllergies(allergies);
            patient.setCurrentMedications(currentMedications);
            patient.setLastVisitDate(LocalDateTime.now());
            patientRepository.save(patient);
            return true;
        }
        return false;
    }
    
    // Statistics
    public long getPatientCountByStatus(PatientStatus status) {
        return patientRepository.countActivePatientsByStatus(status);
    }
    
    public long getActivePatientCount() {
        return patientRepository.countActivePatients();
    }
    
    public long getPatientCountByBloodType(String bloodType) {
        return patientRepository.countActivePatientsByBloodType(bloodType);
    }
    
    // Utility methods
    private String generateMedicalRecordNumber() {
        return "MRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public List<Patient> getPediatricPatients() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        return patientRepository.findActivePatientsByDateOfBirthRange(eighteenYearsAgo, LocalDate.now());
    }
    
    public List<Patient> getAdultPatients() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        return patientRepository.findActivePatientsByDateOfBirthRange(LocalDate.of(1900, 1, 1), eighteenYearsAgo);
    }
}
