package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.MedicalRecord;
import com.esprit.platformepediatricback.Repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecord.setCreationDate(LocalDateTime.now());
        medicalRecord.setLastUpdated(LocalDateTime.now());
        return medicalRecordRepository.save(medicalRecord);
    }
    
    public Optional<MedicalRecord> getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }
    
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }
    
    public Optional<MedicalRecord> getMedicalRecordByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }
    
    public List<MedicalRecord> getMedicalRecordsByDoctorId(Long doctorId) {
        return medicalRecordRepository.findByPrimaryDoctorId(doctorId);
    }
    
    public List<MedicalRecord> getActiveMedicalRecords() {
        return medicalRecordRepository.findByIsActive(true);
    }
    
    public List<MedicalRecord> searchMedicalRecords(String name) {
        return medicalRecordRepository.findByPatientNameContaining(name);
    }
    
    public List<MedicalRecord> getMedicalRecordsByBloodType(String bloodType) {
        return medicalRecordRepository.findByBloodType(bloodType);
    }
    
    public List<MedicalRecord> getMedicalRecordsByAgeRange(Integer minAge, Integer maxAge) {
        return medicalRecordRepository.findByPatientAgeRange(minAge, maxAge);
    }
    
    public List<MedicalRecord> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordRepository.findByCreationDateRange(startDate, endDate);
    }
    
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord medicalRecordDetails) {
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findById(id);
        if (medicalRecordOptional.isPresent()) {
            MedicalRecord medicalRecord = medicalRecordOptional.get();
            
            // Update fields
            medicalRecord.setPrimaryDoctor(medicalRecordDetails.getPrimaryDoctor());
            medicalRecord.setMedicalHistory(medicalRecordDetails.getMedicalHistory());
            medicalRecord.setAllergies(medicalRecordDetails.getAllergies());
            medicalRecord.setChronicDiseases(medicalRecordDetails.getChronicDiseases());
            medicalRecord.setCurrentMedications(medicalRecordDetails.getCurrentMedications());
            medicalRecord.setFamilyHistory(medicalRecordDetails.getFamilyHistory());
            medicalRecord.setSurgicalHistory(medicalRecordDetails.getSurgicalHistory());
            medicalRecord.setVaccinationRecord(medicalRecordDetails.getVaccinationRecord());
            medicalRecord.setBloodType(medicalRecordDetails.getBloodType());
            medicalRecord.setRhFactor(medicalRecordDetails.getRhFactor());
            medicalRecord.setHeight(medicalRecordDetails.getHeight());
            medicalRecord.setWeight(medicalRecordDetails.getWeight());
            medicalRecord.setBmi(medicalRecordDetails.getBmi());
            medicalRecord.setBloodPressure(medicalRecordDetails.getBloodPressure());
            medicalRecord.setHeartRate(medicalRecordDetails.getHeartRate());
            medicalRecord.setTemperature(medicalRecordDetails.getTemperature());
            medicalRecord.setEmergencyContact(medicalRecordDetails.getEmergencyContact());
            medicalRecord.setEmergencyPhone(medicalRecordDetails.getEmergencyPhone());
            medicalRecord.setInsuranceNumber(medicalRecordDetails.getInsuranceNumber());
            medicalRecord.setInsuranceProvider(medicalRecordDetails.getInsuranceProvider());
            medicalRecord.setIsActive(medicalRecordDetails.getIsActive());
            medicalRecord.setLastUpdated(LocalDateTime.now());
            
            return medicalRecordRepository.save(medicalRecord);
        }
        return null;
    }
    
    public boolean deleteMedicalRecord(Long id) {
        if (medicalRecordRepository.existsById(id)) {
            medicalRecordRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean activateMedicalRecord(Long id) {
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findById(id);
        if (medicalRecordOptional.isPresent()) {
            MedicalRecord medicalRecord = medicalRecordOptional.get();
            medicalRecord.setIsActive(true);
            medicalRecord.setLastUpdated(LocalDateTime.now());
            medicalRecordRepository.save(medicalRecord);
            return true;
        }
        return false;
    }
    
    public boolean deactivateMedicalRecord(Long id) {
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findById(id);
        if (medicalRecordOptional.isPresent()) {
            MedicalRecord medicalRecord = medicalRecordOptional.get();
            medicalRecord.setIsActive(false);
            medicalRecord.setLastUpdated(LocalDateTime.now());
            medicalRecordRepository.save(medicalRecord);
            return true;
        }
        return false;
    }
    
    public long getActiveMedicalRecordsCount() {
        return medicalRecordRepository.countActiveMedicalRecords();
    }
    
    public boolean isOwner(Long medicalRecordId, String username) {
        Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findById(medicalRecordId);
        return medicalRecord.map(record -> 
            record.getPatient().getMedicalRecordNumber().equals(username)
        ).orElse(false);
    }
}
