package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.DonorRepository;
import com.esprit.platformepediatricback.Repository.RecipientRepository;
import com.esprit.platformepediatricback.Repository.TransplantRepository;
import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KidneyTransplantService {
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private RecipientRepository recipientRepository;
    
    @Autowired
    private TransplantRepository transplantRepository;
    
    // Donor CRUD operations
    public Donor createDonor(Donor donor) {
        donor.setRegistrationDate(LocalDateTime.now());
        return donorRepository.save(donor);
    }
    
    public Optional<Donor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }
    
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }
    
    public List<Donor> getDonorsByStatus(Donor.DonorStatus status) {
        return donorRepository.findByStatus(status);
    }
    
    public List<Donor> getDonorsByBloodType(String bloodType) {
        return donorRepository.findByBloodType(bloodType);
    }
    
    public Donor updateDonor(Long id, Donor donorDetails) {
        Optional<Donor> optionalDonor = donorRepository.findById(id);
        if (optionalDonor.isPresent()) {
            Donor donor = optionalDonor.get();
            donor.setFirstName(donorDetails.getFirstName());
            donor.setLastName(donorDetails.getLastName());
            donor.setDateOfBirth(donorDetails.getDateOfBirth());
            donor.setBloodType(donorDetails.getBloodType());
            donor.setPhoneNumber(donorDetails.getPhoneNumber());
            donor.setEmail(donorDetails.getEmail());
            donor.setAddress(donorDetails.getAddress());
            donor.setStatus(donorDetails.getStatus());
            donor.setMedicalHistory(donorDetails.getMedicalHistory());
            donor.setNotes(donorDetails.getNotes());
            return donorRepository.save(donor);
        }
        return null;
    }
    
    public boolean deleteDonor(Long id) {
        if (donorRepository.existsById(id)) {
            donorRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Donor> searchDonorsByName(String firstName, String lastName) {
        return donorRepository.findByNameContaining(firstName, lastName);
    }
    
    public Donor getDonorByEmail(String email) {
        return donorRepository.findByEmail(email);
    }
    
    public Donor getDonorByPhoneNumber(String phoneNumber) {
        return donorRepository.findByPhoneNumber(phoneNumber);
    }
    
    public List<Donor> getApprovedDonors() {
        return donorRepository.findApprovedDonorsOrderByRegistrationDate();
    }
    
    // Recipient CRUD operations
    public Recipient createRecipient(Recipient recipient) {
        recipient.setRegistrationDate(LocalDateTime.now());
        return recipientRepository.save(recipient);
    }
    
    public Optional<Recipient> getRecipientById(Long id) {
        return recipientRepository.findById(id);
    }
    
    public List<Recipient> getAllRecipients() {
        return recipientRepository.findAll();
    }
    
    public List<Recipient> getRecipientsByStatus(Recipient.RecipientStatus status) {
        return recipientRepository.findByStatus(status);
    }
    
    public List<Recipient> getRecipientsByBloodType(String bloodType) {
        return recipientRepository.findByBloodType(bloodType);
    }
    
    public Recipient updateRecipient(Long id, Recipient recipientDetails) {
        Optional<Recipient> optionalRecipient = recipientRepository.findById(id);
        if (optionalRecipient.isPresent()) {
            Recipient recipient = optionalRecipient.get();
            recipient.setPatient(recipientDetails.getPatient());
            recipient.setDateOfBirth(recipientDetails.getDateOfBirth());
            recipient.setBloodType(recipientDetails.getBloodType());
            recipient.setPhoneNumber(recipientDetails.getPhoneNumber());
            recipient.setEmail(recipientDetails.getEmail());
            recipient.setAddress(recipientDetails.getAddress());
            recipient.setStatus(recipientDetails.getStatus());
            recipient.setMedicalHistory(recipientDetails.getMedicalHistory());
            recipient.setNotes(recipientDetails.getNotes());
            recipient.setTransplantDate(recipientDetails.getTransplantDate());
            return recipientRepository.save(recipient);
        }
        return null;
    }
    
    public boolean deleteRecipient(Long id) {
        if (recipientRepository.existsById(id)) {
            recipientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Recipient> searchRecipientsByName(String name) {
        return recipientRepository.findByPatientNameContaining(name);
    }
    
    public List<Recipient> getWaitingRecipients() {
        return recipientRepository.findWaitingRecipientsOrderByRegistrationDate();
    }
    
    // Transplant CRUD operations
    public Transplant createTransplant(Transplant transplant) {
        transplant.setCreatedAt(LocalDateTime.now());
        transplant.setUpdatedAt(LocalDateTime.now());
        return transplantRepository.save(transplant);
    }
    
    public Optional<Transplant> getTransplantById(Long id) {
        return transplantRepository.findById(id);
    }
    
    public List<Transplant> getAllTransplants() {
        return transplantRepository.findAll();
    }
    
    public List<Transplant> getTransplantsByStatus(Transplant.TransplantStatus status) {
        return transplantRepository.findByStatus(status);
    }
    
    public List<Transplant> getTransplantsByDonor(Donor donor) {
        return transplantRepository.findByDonor(donor);
    }
    
    public List<Transplant> getTransplantsByRecipient(Recipient recipient) {
        return transplantRepository.findByRecipient(recipient);
    }
    
    public List<Transplant> getTransplantsBySurgeon(User surgeon) {
        return transplantRepository.findBySurgeon(surgeon);
    }
    
    public Transplant updateTransplant(Long id, Transplant transplantDetails) {
        Optional<Transplant> optionalTransplant = transplantRepository.findById(id);
        if (optionalTransplant.isPresent()) {
            Transplant transplant = optionalTransplant.get();
            transplant.setDonor(transplantDetails.getDonor());
            transplant.setRecipient(transplantDetails.getRecipient());
            transplant.setScheduledDate(transplantDetails.getScheduledDate());
            transplant.setActualDate(transplantDetails.getActualDate());
            transplant.setStatus(transplantDetails.getStatus());
            transplant.setSurgeon(transplantDetails.getSurgeon());
            transplant.setHospital(transplantDetails.getHospital());
            transplant.setPreOperativeNotes(transplantDetails.getPreOperativeNotes());
            transplant.setPostOperativeNotes(transplantDetails.getPostOperativeNotes());
            transplant.setComplications(transplantDetails.getComplications());
            transplant.setIsSuccessful(transplantDetails.getIsSuccessful());
            transplant.setUpdatedAt(LocalDateTime.now());
            return transplantRepository.save(transplant);
        }
        return null;
    }
    
    public boolean deleteTransplant(Long id) {
        if (transplantRepository.existsById(id)) {
            transplantRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Transplant> getTransplantsByHospital(String hospital) {
        return transplantRepository.findByHospital(hospital);
    }
    
    public List<Transplant> getScheduledTransplants() {
        return transplantRepository.findScheduledTransplantsOrderByDate();
    }
    
    public List<Transplant> getSuccessfulTransplants() {
        return transplantRepository.findByIsSuccessful(true);
    }
    
    // Matching and statistics
    public List<Donor> findCompatibleDonors(String bloodType) {
        return donorRepository.findByBloodTypeAndStatus(bloodType, Donor.DonorStatus.APPROVED);
    }
    
    public List<Recipient> findCompatibleRecipients(String bloodType) {
        return recipientRepository.findByBloodTypeAndStatus(bloodType, Recipient.RecipientStatus.WAITING);
    }
    
    public long getDonorCountByStatus(Donor.DonorStatus status) {
        return donorRepository.countByStatus(status);
    }
    
    public long getRecipientCountByStatus(Recipient.RecipientStatus status) {
        return recipientRepository.countByStatus(status);
    }
    
    public long getTransplantCountByStatus(Transplant.TransplantStatus status) {
        return transplantRepository.countByStatus(status);
    }
    
    public long getSuccessfulTransplantCount() {
        return transplantRepository.countSuccessfulTransplants();
    }
}
