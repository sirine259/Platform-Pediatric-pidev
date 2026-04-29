package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.PostTransplantFollowUpService;
import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post-transplant-follow-up")
@CrossOrigin(origins = "*")
public class PostTransplantFollowUpController {

    @Autowired
    private PostTransplantFollowUpService followUpService;

    // CRUD Endpoints
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> createFollowUp(@RequestBody PostTransplantFollowUp followUp) {
        try {
            PostTransplantFollowUp createdFollowUp = followUpService.createFollowUp(followUp);
            return ResponseEntity.ok(createdFollowUp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> getFollowUpById(@PathVariable Long id) {
        Optional<PostTransplantFollowUp> followUp = followUpService.getFollowUpById(id);
        return followUp.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getAllFollowUps() {
        List<PostTransplantFollowUp> followUps = followUpService.getAllFollowUps();
        return ResponseEntity.ok(followUps);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> updateFollowUp(@PathVariable Long id, @RequestBody PostTransplantFollowUp followUpDetails) {
        try {
            PostTransplantFollowUp updatedFollowUp = followUpService.updateFollowUp(id, followUpDetails);
            return ResponseEntity.ok(updatedFollowUp);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFollowUp(@PathVariable Long id) {
        try {
            followUpService.deleteFollowUp(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Business Endpoints
    @GetMapping("/transplant/{transplantId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByTransplant(@PathVariable Long transplantId) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByTransplant(transplantId);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByDoctor(@PathVariable Long doctorId) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByDoctor(doctorId);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByDateRange(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByDateRange(startDate, endDate);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getOverdueFollowUps() {
        List<PostTransplantFollowUp> followUps = followUpService.getOverdueFollowUps();
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/upcoming/{days}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getUpcomingFollowUps(@PathVariable int days) {
        List<PostTransplantFollowUp> followUps = followUpService.getUpcomingFollowUps(days);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getTodayFollowUps() {
        List<PostTransplantFollowUp> followUps = followUpService.getTodayFollowUps();
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/incomplete")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getIncompleteFollowUps() {
        List<PostTransplantFollowUp> followUps = followUpService.getIncompleteFollowUps();
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/type/{followUpType}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByType(@PathVariable String followUpType) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByType(followUpType);
        return ResponseEntity.ok(followUps);
    }

    // Kidney Function Monitoring Endpoints
    @GetMapping("/high-creatinine/{level}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsWithHighCreatinine(@PathVariable Double level) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsWithHighCreatinine(level);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/low-gfr/{level}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsWithLowGFR(@PathVariable Double level) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsWithLowGFR(level);
        return ResponseEntity.ok(followUps);
    }

    @GetMapping("/with-complications")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsWithComplications() {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsWithComplications();
        return ResponseEntity.ok(followUps);
    }

    // Follow-up Management Endpoints
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> completeFollowUp(
            @PathVariable Long id,
            @RequestParam String clinicalNotes,
            @RequestParam(required = false) String recommendations) {
        try {
            PostTransplantFollowUp completedFollowUp = followUpService.completeFollowUp(id, clinicalNotes, recommendations);
            return ResponseEntity.ok(completedFollowUp);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/schedule-next")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<PostTransplantFollowUp> scheduleNextFollowUp(
            @PathVariable Long currentFollowUpId,
            @RequestParam LocalDateTime nextDate,
            @RequestParam(required = false) String followUpType) {
        try {
            PostTransplantFollowUp nextFollowUp = followUpService.scheduleNextFollowUp(currentFollowUpId, nextDate, followUpType);
            return ResponseEntity.ok(nextFollowUp);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Statistics Endpoints
    @GetMapping("/stats/transplant/{transplantId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countFollowUpsByTransplant(@PathVariable Long transplantId) {
        Long count = followUpService.countFollowUpsByTransplant(transplantId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Long> countFollowUpsByDoctor(@PathVariable Long doctorId) {
        Long count = followUpService.countFollowUpsByDoctor(doctorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/completed")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countCompletedFollowUps() {
        Long count = followUpService.countCompletedFollowUps();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/overdue")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Long> countOverdueFollowUps() {
        Long count = followUpService.countOverdueFollowUps();
        return ResponseEntity.ok(count);
    }

    // Advanced Filtering Endpoints
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getFollowUpsByMultipleCriteria(
            @RequestParam(required = false) Long transplantId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String followUpType,
            @RequestParam(required = false) Boolean isComplete) {
        List<PostTransplantFollowUp> followUps = followUpService.getFollowUpsByMultipleCriteria(
                transplantId, doctorId, followUpType, isComplete);
        return ResponseEntity.ok(followUps);
    }

    // Alert Endpoints
    @GetMapping("/critical")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<PostTransplantFollowUp>> getCriticalFollowUps() {
        List<PostTransplantFollowUp> followUps = followUpService.getCriticalFollowUps();
        return ResponseEntity.ok(followUps);
    }

    // Helper Endpoints
    @GetMapping("/{id}/kidney-function")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<Boolean> isKidneyFunctionNormal(@PathVariable Long id) {
        boolean isNormal = followUpService.isKidneyFunctionNormal(id);
        return ResponseEntity.ok(isNormal);
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'NURSE', 'ADMIN')")
    public ResponseEntity<String> getFollowUpStatus(@PathVariable Long id) {
        String status = followUpService.getFollowUpStatus(id);
        return ResponseEntity.ok(status);
    }
}
