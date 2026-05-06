package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.PatientService;
import com.esprit.platformepediatricback.entity.Patient;
import com.esprit.platformepediatricback.entity.Patient.PatientStatus;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    // Patient CRUD endpoints
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            Patient createdPatient = patientService.createPatient(patient);
            return ResponseEntity.ok(createdPatient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/medical-record/{medicalRecordNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> getPatientByMedicalRecordNumber(@PathVariable String medicalRecordNumber) {
        Optional<Patient> patient = patientService.getPatientByMedicalRecordNumber(medicalRecordNumber);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByStatus(@PathVariable PatientStatus status) {
        List<Patient> patients = patientService.getPatientsByStatus(status);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/blood-type/{bloodType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByBloodType(@PathVariable String bloodType) {
        List<Patient> patients = patientService.getPatientsByBloodType(bloodType);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@PathVariable Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        List<Patient> patients = patientService.getPatientsByDoctor(doctor);
        return ResponseEntity.ok(patients);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        try {
            Patient updatedPatient = patientService.updatePatient(id, patientDetails);
            if (updatedPatient != null) {
                return ResponseEntity.ok(updatedPatient);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        boolean deleted = patientService.deletePatient(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> permanentlyDeletePatient(@PathVariable Long id) {
        boolean deleted = patientService.permanentlyDeletePatient(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Search and filtering endpoints
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> searchPatientsByName(@RequestParam String name) {
        List<Patient> patients = patientService.searchPatientsByName(name);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/date-of-birth-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByDateOfBirthRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Patient> patients = patientService.getPatientsByDateOfBirthRange(startDate, endDate);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/registration-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByRegistrationDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Patient> patients = patientService.getPatientsByRegistrationDateRange(startDate, endDate);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/last-visit-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByLastVisitDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Patient> patients = patientService.getPatientsByLastVisitDateRange(startDate, endDate);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/blood-type/{bloodType}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByBloodTypeAndStatus(
            @PathVariable String bloodType, @PathVariable PatientStatus status) {
        List<Patient> patients = patientService.getPatientsByBloodTypeAndStatus(bloodType, status);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> getPatientByEmail(@PathVariable String email) {
        Optional<Patient> patient = patientService.getPatientByEmail(email);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/phone/{phoneNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> getPatientByPhoneNumber(@PathVariable String phoneNumber) {
        Optional<Patient> patient = patientService.getPatientByPhoneNumber(phoneNumber);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/allergy/{allergy}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByAllergy(@PathVariable String allergy) {
        List<Patient> patients = patientService.getPatientsByAllergy(allergy);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/medication/{medication}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsByMedication(@PathVariable String medication) {
        List<Patient> patients = patientService.getPatientsByMedication(medication);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/order/registration")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsOrderByRegistrationDate() {
        List<Patient> patients = patientService.getPatientsOrderByRegistrationDate();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/order/last-visit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPatientsOrderByLastVisitDate() {
        List<Patient> patients = patientService.getPatientsOrderByLastVisitDate();
        return ResponseEntity.ok(patients);
    }
    
    // Medical record management endpoints
    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> updatePatientStatus(@PathVariable Long id, @RequestParam PatientStatus newStatus) {
        boolean updated = patientService.updatePatientStatus(id, newStatus);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/assign-doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> assignDoctor(@PathVariable Long patientId, @RequestParam Long doctorId) {
        boolean assigned = patientService.assignDoctor(patientId, doctorId);
        if (assigned) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/vitals")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Void> updateVitals(
            @PathVariable Long id,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) Double height) {
        boolean updated = patientService.updateVitals(id, weight, height);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/medical-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> updateMedicalHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String medicalHistory,
            @RequestParam(required = false) String allergies,
            @RequestParam(required = false) String currentMedications) {
        boolean updated = patientService.updateMedicalHistory(id, medicalHistory, allergies, currentMedications);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Statistics endpoints
    @GetMapping("/stats/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Long> getPatientCountByStatus(@PathVariable PatientStatus status) {
        long count = patientService.getPatientCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Long> getActivePatientCount() {
        long count = patientService.getActivePatientCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/blood-type/{bloodType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Long> getPatientCountByBloodType(@PathVariable String bloodType) {
        long count = patientService.getPatientCountByBloodType(bloodType);
        return ResponseEntity.ok(count);
    }
    
    // Specialized endpoints
    @GetMapping("/pediatric")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getPediatricPatients() {
        List<Patient> patients = patientService.getPediatricPatients();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/adult")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<Patient>> getAdultPatients() {
        List<Patient> patients = patientService.getAdultPatients();
        return ResponseEntity.ok(patients);
    }
    
    // Patient profile endpoints
    @GetMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> getPatientProfile(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Patient> updatePatientProfile(@PathVariable Long id, @RequestBody Patient patientDetails) {
        try {
            Patient updatedPatient = patientService.updatePatient(id, patientDetails);
            if (updatedPatient != null) {
                return ResponseEntity.ok(updatedPatient);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
