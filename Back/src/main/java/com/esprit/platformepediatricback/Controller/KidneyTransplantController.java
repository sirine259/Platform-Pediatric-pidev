package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.KidneyTransplantService;
import com.esprit.platformepediatricback.Repository.DonorRepository;
import com.esprit.platformepediatricback.Repository.RecipientRepository;
import com.esprit.platformepediatricback.Repository.UserRepository;
import com.esprit.platformepediatricback.Service.PostTransplantFollowUpService;
import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transplant")
@CrossOrigin(origins = "*")
public class KidneyTransplantController {
    
    @Autowired
    private KidneyTransplantService transplantService;
    
    @Autowired
    private PostTransplantFollowUpService followUpService;
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private RecipientRepository recipientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Donor endpoints
    @PostMapping("/donors")
    public ResponseEntity<Donor> createDonor(@RequestBody Donor donor) {
        Donor createdDonor = transplantService.createDonor(donor);
        return ResponseEntity.ok(createdDonor);
    }
    
    @GetMapping("/donors/{id}")
    public ResponseEntity<Donor> getDonorById(@PathVariable Long id) {
        Optional<Donor> donor = transplantService.getDonorById(id);
        return donor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/donors")
    public ResponseEntity<List<Donor>> getAllDonors() {
        List<Donor> donors = transplantService.getAllDonors();
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/donors/status/{status}")
    public ResponseEntity<List<Donor>> getDonorsByStatus(@PathVariable Donor.DonorStatus status) {
        List<Donor> donors = transplantService.getDonorsByStatus(status);
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/donors/blood-type/{bloodType}")
    public ResponseEntity<List<Donor>> getDonorsByBloodType(@PathVariable String bloodType) {
        List<Donor> donors = transplantService.getDonorsByBloodType(bloodType);
        return ResponseEntity.ok(donors);
    }
    
    @PutMapping("/donors/{id}")
    public ResponseEntity<Donor> updateDonor(@PathVariable Long id, @RequestBody Donor donorDetails) {
        Donor updatedDonor = transplantService.updateDonor(id, donorDetails);
        if (updatedDonor != null) {
            return ResponseEntity.ok(updatedDonor);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/donors/{id}")
    public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
        boolean deleted = transplantService.deleteDonor(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/donors/search")
    public ResponseEntity<List<Donor>> searchDonorsByName(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        List<Donor> donors = transplantService.searchDonorsByName(
            firstName != null ? firstName : "",
            lastName != null ? lastName : ""
        );
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/donors/email/{email}")
    public ResponseEntity<Donor> getDonorByEmail(@PathVariable String email) {
        Donor donor = transplantService.getDonorByEmail(email);
        if (donor != null) {
            return ResponseEntity.ok(donor);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/donors/phone/{phoneNumber}")
    public ResponseEntity<Donor> getDonorByPhoneNumber(@PathVariable String phoneNumber) {
        Donor donor = transplantService.getDonorByPhoneNumber(phoneNumber);
        if (donor != null) {
            return ResponseEntity.ok(donor);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/donors/approved")
    public ResponseEntity<List<Donor>> getApprovedDonors() {
        List<Donor> donors = transplantService.getApprovedDonors();
        return ResponseEntity.ok(donors);
    }
    
    // Recipient endpoints
    @PostMapping("/recipients")
    public ResponseEntity<Recipient> createRecipient(@RequestBody Recipient recipient) {
        Recipient createdRecipient = transplantService.createRecipient(recipient);
        return ResponseEntity.ok(createdRecipient);
    }
    
    @GetMapping("/recipients/{id}")
    public ResponseEntity<Recipient> getRecipientById(@PathVariable Long id) {
        Optional<Recipient> recipient = transplantService.getRecipientById(id);
        return recipient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/recipients")
    public ResponseEntity<List<Recipient>> getAllRecipients() {
        List<Recipient> recipients = transplantService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }
    
    @GetMapping("/recipients/status/{status}")
    public ResponseEntity<List<Recipient>> getRecipientsByStatus(@PathVariable Recipient.RecipientStatus status) {
        List<Recipient> recipients = transplantService.getRecipientsByStatus(status);
        return ResponseEntity.ok(recipients);
    }
    
    @GetMapping("/recipients/blood-type/{bloodType}")
    public ResponseEntity<List<Recipient>> getRecipientsByBloodType(@PathVariable String bloodType) {
        List<Recipient> recipients = transplantService.getRecipientsByBloodType(bloodType);
        return ResponseEntity.ok(recipients);
    }
    
    @PutMapping("/recipients/{id}")
    public ResponseEntity<Recipient> updateRecipient(@PathVariable Long id, @RequestBody Recipient recipientDetails) {
        Recipient updatedRecipient = transplantService.updateRecipient(id, recipientDetails);
        if (updatedRecipient != null) {
            return ResponseEntity.ok(updatedRecipient);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/recipients/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        boolean deleted = transplantService.deleteRecipient(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/recipients/search/{name}")
    public ResponseEntity<List<Recipient>> searchRecipientsByName(@PathVariable String name) {
        List<Recipient> recipients = transplantService.searchRecipientsByName(name);
        return ResponseEntity.ok(recipients);
    }
    
    @GetMapping("/recipients/waiting")
    public ResponseEntity<List<Recipient>> getWaitingRecipients() {
        List<Recipient> recipients = transplantService.getWaitingRecipients();
        return ResponseEntity.ok(recipients);
    }
    
    // Transplant endpoints
    @PostMapping("/transplants")
    public ResponseEntity<Transplant> createTransplant(@RequestBody Transplant transplant) {
        Transplant createdTransplant = transplantService.createTransplant(transplant);
        return ResponseEntity.ok(createdTransplant);
    }
    
    @GetMapping("/transplants/{id}")
    public ResponseEntity<Transplant> getTransplantById(@PathVariable Long id) {
        Optional<Transplant> transplant = transplantService.getTransplantById(id);
        return transplant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/transplants")
    public ResponseEntity<List<Transplant>> getAllTransplants() {
        List<Transplant> transplants = transplantService.getAllTransplants();
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/status/{status}")
    public ResponseEntity<List<Transplant>> getTransplantsByStatus(@PathVariable Transplant.TransplantStatus status) {
        List<Transplant> transplants = transplantService.getTransplantsByStatus(status);
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/donor/{donorId}")
    public ResponseEntity<List<Transplant>> getTransplantsByDonor(@PathVariable Long donorId) {
        Optional<Donor> donorOptional = donorRepository.findById(donorId);
        if (donorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Transplant> transplants = transplantService.getTransplantsByDonor(donorOptional.get());
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/recipient/{recipientId}")
    public ResponseEntity<List<Transplant>> getTransplantsByRecipient(@PathVariable Long recipientId) {
        Optional<Recipient> recipientOptional = recipientRepository.findById(recipientId);
        if (recipientOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Transplant> transplants = transplantService.getTransplantsByRecipient(recipientOptional.get());
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/surgeon/{surgeonId}")
    public ResponseEntity<List<Transplant>> getTransplantsBySurgeon(@PathVariable Long surgeonId) {
        Optional<User> surgeonOptional = userRepository.findById(surgeonId);
        if (surgeonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Transplant> transplants = transplantService.getTransplantsBySurgeon(surgeonOptional.get());
        return ResponseEntity.ok(transplants);
    }
    
    @PutMapping("/transplants/{id}")
    public ResponseEntity<Transplant> updateTransplant(@PathVariable Long id, @RequestBody Transplant transplantDetails) {
        Transplant updatedTransplant = transplantService.updateTransplant(id, transplantDetails);
        if (updatedTransplant != null) {
            return ResponseEntity.ok(updatedTransplant);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/transplants/{id}")
    public ResponseEntity<Void> deleteTransplant(@PathVariable Long id) {
        boolean deleted = transplantService.deleteTransplant(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/transplants/hospital/{hospital}")
    public ResponseEntity<List<Transplant>> getTransplantsByHospital(@PathVariable String hospital) {
        List<Transplant> transplants = transplantService.getTransplantsByHospital(hospital);
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/scheduled")
    public ResponseEntity<List<Transplant>> getScheduledTransplants() {
        List<Transplant> transplants = transplantService.getScheduledTransplants();
        return ResponseEntity.ok(transplants);
    }
    
    @GetMapping("/transplants/successful")
    public ResponseEntity<List<Transplant>> getSuccessfulTransplants() {
        List<Transplant> transplants = transplantService.getSuccessfulTransplants();
        return ResponseEntity.ok(transplants);
    }
    
    // Post-Transplant Follow-up endpoints
    @GetMapping("/{id}/follow-ups")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByTransplant(@PathVariable Long id) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByTransplant(id);
        return ResponseEntity.ok(followUps);
    }
    
    @PostMapping("/{id}/follow-ups")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> createFollowUp(@PathVariable Long id, @RequestBody PostTransplantFollowUp followUp) {
        PostTransplantFollowUp createdFollowUp = followUpService.createFollowUpForTransplant(id, followUp);
        return ResponseEntity.ok(createdFollowUp);
    }
    
    @PutMapping("/{id}/follow-ups/{followUpId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> updateFollowUp(@PathVariable Long id, @PathVariable Long followUpId, @RequestBody PostTransplantFollowUp followUp) {
        PostTransplantFollowUp updatedFollowUp = followUpService.updateFollowUpForTransplant(id, followUpId, followUp);
        return ResponseEntity.ok(updatedFollowUp);
    }
    
    @DeleteMapping("/{id}/follow-ups/{followUpId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFollowUp(@PathVariable Long id, @PathVariable Long followUpId) {
        followUpService.deleteFollowUpForTransplant(id, followUpId);
        return ResponseEntity.ok().build();
    }
    
    // Matching and statistics endpoints
    @GetMapping("/matching/donors/{bloodType}")
    public ResponseEntity<List<Donor>> findCompatibleDonors(@PathVariable String bloodType) {
        List<Donor> donors = transplantService.findCompatibleDonors(bloodType);
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/matching/recipients/{bloodType}")
    public ResponseEntity<List<Recipient>> findCompatibleRecipients(@PathVariable String bloodType) {
        List<Recipient> recipients = transplantService.findCompatibleRecipients(bloodType);
        return ResponseEntity.ok(recipients);
    }
    
    @GetMapping("/stats/donors/status/{status}")
    public ResponseEntity<Long> getDonorCountByStatus(@PathVariable Donor.DonorStatus status) {
        long count = transplantService.getDonorCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/recipients/status/{status}")
    public ResponseEntity<Long> getRecipientCountByStatus(@PathVariable Recipient.RecipientStatus status) {
        long count = transplantService.getRecipientCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/transplants/status/{status}")
    public ResponseEntity<Long> getTransplantCountByStatus(@PathVariable Transplant.TransplantStatus status) {
        long count = transplantService.getTransplantCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/transplants/successful")
    public ResponseEntity<Long> getSuccessfulTransplantCount() {
        long count = transplantService.getSuccessfulTransplantCount();
        return ResponseEntity.ok(count);
    }
}
