package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.ml.common.*;
import com.esprit.platformepediatricback.ml.TransplantClusteringService;
import com.esprit.platformepediatricback.ml.TransplantClassificationService;
import com.esprit.platformepediatricback.ml.TransplantPredictionService;
import com.esprit.platformepediatricback.ml.TransplantRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kidney-transplants/ai")
@CrossOrigin(origins = "*")
public class KidneyTransplantAIController {

    @Autowired
    private TransplantClusteringService transplantClusteringService;

    @Autowired
    private TransplantClassificationService transplantClassificationService;

    @Autowired
    private TransplantPredictionService transplantPredictionService;

    @Autowired
    private TransplantRecommendationService transplantRecommendationService;

    @GetMapping("/clusters")
    public ResponseEntity<Map<Integer, ClusterResult>> getTransplantClusters() {
        Map<Integer, ClusterResult> clusters = transplantClusteringService.clusterTransplantsByRisk();
        return ResponseEntity.ok(clusters);
    }

    @GetMapping("/transplants/{transplantId}/cluster")
    public ResponseEntity<Map<String, Object>> getTransplantCluster(@PathVariable Long transplantId) {
        int cluster = transplantClusteringService.predictCluster(transplantId);
        Map<String, Object> response = new HashMap<>();
        response.put("transplantId", transplantId);
        response.put("clusterId", cluster);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transplants/{transplantId}/classify-outcome")
    public ResponseEntity<ClassificationResult> classifyOutcome(@PathVariable Long transplantId) {
        ClassificationResult result = transplantClassificationService.classifyOutcome(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/classify-risk")
    public ResponseEntity<ClassificationResult> classifyRisk(@PathVariable Long transplantId) {
        ClassificationResult result = transplantClassificationService.classifyRiskLevel(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/classify-complication")
    public ResponseEntity<ClassificationResult> classifyComplication(@PathVariable Long transplantId) {
        ClassificationResult result = transplantClassificationService.classifyComplicationRisk(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/predict-survival")
    public ResponseEntity<PredictionResult> predictGraftSurvival(@PathVariable Long transplantId) {
        PredictionResult result = transplantPredictionService.predictGraftSurvival(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/predict-rejection")
    public ResponseEntity<PredictionResult> predictRejectionRisk(@PathVariable Long transplantId) {
        PredictionResult result = transplantPredictionService.predictRejectionRisk(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/predict-creatinine")
    public ResponseEntity<PredictionResult> predictCreatinineTrend(@PathVariable Long transplantId) {
        PredictionResult result = transplantPredictionService.predictCreatinineTrend(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/predict-readmission")
    public ResponseEntity<PredictionResult> predictReadmission(@PathVariable Long transplantId) {
        PredictionResult result = transplantPredictionService.predictHospitalReadmission(transplantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transplants/{transplantId}/recommend-followup")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> recommendFollowUp(
            @PathVariable Long transplantId) {
        List<RecommendationResult.RecommendedItem> items = transplantRecommendationService.recommendFollowUpSchedule(transplantId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/transplants/{transplantId}/recommend-immunosuppression")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> recommendImmunosuppression(
            @PathVariable Long transplantId) {
        List<RecommendationResult.RecommendedItem> items = transplantRecommendationService.recommendImmunosuppressionPlan(transplantId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/transplants/{transplantId}/recommend-lifestyle")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> recommendLifestyle(
            @PathVariable Long transplantId) {
        List<RecommendationResult.RecommendedItem> items = transplantRecommendationService.recommendLifestyleAdjustments(transplantId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/transplants/{transplantId}/similar")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> findSimilar(
            @PathVariable Long transplantId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendationResult.RecommendedItem> items = transplantRecommendationService.findSimilarTransplants(transplantId, limit);
        return ResponseEntity.ok(items);
    }
}
