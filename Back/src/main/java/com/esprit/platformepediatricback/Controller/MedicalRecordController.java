package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.MedicalRecordService;
import com.esprit.platformepediatricback.entity.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(medicalRecord);
            return ResponseEntity.ok(createdRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or @medicalRecordService.isOwner(#id, authentication.name)")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecordById(id);
        return medicalRecord.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<MedicalRecord> getMedicalRecordByPatientId(@PathVariable Long patientId) {
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecordByPatientId(patientId);
        return medicalRecord.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByDoctorId(@PathVariable Long doctorId) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByDoctorId(doctorId);
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getActiveMedicalRecords() {
        List<MedicalRecord> medicalRecords = medicalRecordService.getActiveMedicalRecords();
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> searchMedicalRecords(@RequestParam String name) {
        List<MedicalRecord> medicalRecords = medicalRecordService.searchMedicalRecords(name);
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/blood-type/{bloodType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByBloodType(@PathVariable String bloodType) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByBloodType(bloodType);
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/age-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(medicalRecords);
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate);
        return ResponseEntity.ok(medicalRecords);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or @medicalRecordService.isOwner(#id, authentication.name)")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord medicalRecordDetails) {
        try {
            MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordDetails);
            if (updatedRecord != null) {
                return ResponseEntity.ok(updatedRecord);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        boolean deleted = medicalRecordService.deleteMedicalRecord(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> activateMedicalRecord(@PathVariable Long id) {
        boolean activated = medicalRecordService.activateMedicalRecord(id);
        if (activated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> deactivateMedicalRecord(@PathVariable Long id) {
        boolean deactivated = medicalRecordService.deactivateMedicalRecord(id);
        if (deactivated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Long> getActiveMedicalRecordsCount() {
        long count = medicalRecordService.getActiveMedicalRecordsCount();
        return ResponseEntity.ok(count);
    }
}
