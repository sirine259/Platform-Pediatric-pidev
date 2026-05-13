package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.KidneyTransplantDetailsService;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kidney-transplants")
@CrossOrigin(origins = "*")
public class KidneyTransplantDetailsController {

    @Autowired
    private KidneyTransplantDetailsService kidneyTransplantDetailsService;

    // CRUD Endpoints
    @PostMapping
    @PreAuthorize("hasAnyRole('SURGEON', 'ADMIN')")
    public ResponseEntity<KidneyTransplant> createKidneyTransplant(@RequestBody KidneyTransplant kidneyTransplant) {
        try {
            KidneyTransplant created = kidneyTransplantDetailsService.createKidneyTransplantDetails(kidneyTransplant);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<KidneyTransplant> getKidneyTransplantById(@PathVariable Long id) {
        Optional<KidneyTransplant> transplant = kidneyTransplantDetailsService.getKidneyTransplantDetailsById(id);
        return transplant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getAllKidneyTransplants() {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getAllKidneyTransplantDetails();
        return ResponseEntity.ok(transplants);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SURGEON', 'ADMIN')")
    public ResponseEntity<KidneyTransplant> updateKidneyTransplant(@PathVariable Long id, @RequestBody KidneyTransplant kidneyTransplant) {
        try {
            KidneyTransplant updated = kidneyTransplantDetailsService.updateKidneyTransplantDetails(id, kidneyTransplant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteKidneyTransplant(@PathVariable Long id) {
        try {
            kidneyTransplantDetailsService.deleteKidneyTransplantDetails(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Type-based Endpoints
    @GetMapping("/type/{transplantType}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getTransplantsByType(@PathVariable KidneyTransplant.TransplantType transplantType) {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getTransplantsByType(transplantType);
        return ResponseEntity.ok(transplants);
    }

    @GetMapping("/approach/{surgeryApproach}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getTransplantsByApproach(@PathVariable KidneyTransplant.SurgeryApproach surgeryApproach) {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getTransplantsByApproach(surgeryApproach);
        return ResponseEntity.ok(transplants);
    }

    @GetMapping("/hospital/{hospital}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getTransplantsByHospital(@PathVariable String hospital) {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getTransplantsByHospital(hospital);
        return ResponseEntity.ok(transplants);
    }

    // Outcome Analysis Endpoints
    @GetMapping("/successful")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getSuccessfulTransplants() {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getSuccessfulTransplants();
        return ResponseEntity.ok(transplants);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getFailedTransplants() {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getFailedTransplants();
        return ResponseEntity.ok(transplants);
    }

    @GetMapping("/with-rejection")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getTransplantsWithRejection() {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getTransplantsWithRejection();
        return ResponseEntity.ok(transplants);
    }

    // Statistics Endpoints
    @GetMapping("/stats/type/{transplantType}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countByType(@PathVariable KidneyTransplant.TransplantType transplantType) {
        Long count = kidneyTransplantDetailsService.countByType(transplantType);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/successful")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countSuccessful() {
        Long count = kidneyTransplantDetailsService.countSuccessful();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/failed")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countFailed() {
        Long count = kidneyTransplantDetailsService.countFailed();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/rejections")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countRejections() {
        Long count = kidneyTransplantDetailsService.countRejections();
        return ResponseEntity.ok(count);
    }

    // Quality Metrics Endpoints
    @GetMapping("/metrics/surgery-duration")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Double> getAverageSurgeryDuration() {
        Double avg = kidneyTransplantDetailsService.getAverageSurgeryDuration();
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/metrics/hospital-stay")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Double> getAverageHospitalStay() {
        Double avg = kidneyTransplantDetailsService.getAverageHospitalStay();
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/metrics/graft-survival")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Double> getAverageGraftSurvival() {
        Double avg = kidneyTransplantDetailsService.getAverageGraftSurvival();
        return ResponseEntity.ok(avg);
    }

    // Risk Analysis Endpoints
    @GetMapping("/high-risk")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<List<KidneyTransplant>> getHighRiskTransplants() {
        List<KidneyTransplant> transplants = kidneyTransplantDetailsService.getHighRiskTransplants();
        return ResponseEntity.ok(transplants);
    }

    // Helper Endpoints
    @GetMapping("/{id}/risk-level")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<String> getTransplantRiskLevel(@PathVariable Long id) {
        Optional<KidneyTransplant> transplant = kidneyTransplantDetailsService.getKidneyTransplantDetailsById(id);
        if (transplant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String riskLevel = kidneyTransplantDetailsService.getTransplantRiskLevel(transplant.get());
        return ResponseEntity.ok(riskLevel);
    }

    @GetMapping("/{id}/is-successful")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Boolean> isTransplantSuccessful(@PathVariable Long id) {
        Optional<KidneyTransplant> transplant = kidneyTransplantDetailsService.getKidneyTransplantDetailsById(id);
        if (transplant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Boolean isSuccessful = kidneyTransplantDetailsService.isSuccessful(transplant.get());
        return ResponseEntity.ok(isSuccessful);
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<String> getTransplantStatus(@PathVariable Long id) {
        Optional<KidneyTransplant> transplant = kidneyTransplantDetailsService.getKidneyTransplantDetailsById(id);
        if (transplant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String status = kidneyTransplantDetailsService.getTransplantStatus(transplant.get());
        return ResponseEntity.ok(status);
    }
}
