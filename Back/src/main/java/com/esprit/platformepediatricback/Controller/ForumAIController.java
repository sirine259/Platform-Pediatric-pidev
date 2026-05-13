package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.ml.common.*;
import com.esprit.platformepediatricback.ml.ForumClusteringService;
import com.esprit.platformepediatricback.ml.ForumClassificationService;
import com.esprit.platformepediatricback.ml.ForumPredictionService;
import com.esprit.platformepediatricback.ml.ForumRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum/ai")
@CrossOrigin(origins = "*")
public class ForumAIController {

    @Autowired
    private ForumClusteringService forumClusteringService;

    @Autowired
    private ForumClassificationService forumClassificationService;

    @Autowired
    private ForumPredictionService forumPredictionService;

    @Autowired
    private ForumRecommendationService forumRecommendationService;

    @GetMapping("/clusters")
    public ResponseEntity<Map<Integer, ClusterResult>> getForumClusters() {
        Map<Integer, ClusterResult> clusters = forumClusteringService.clusterForumPosts();
        return ResponseEntity.ok(clusters);
    }

    @GetMapping("/posts/{postId}/cluster")
    public ResponseEntity<Map<String, Object>> getPostCluster(@PathVariable Long postId) {
        int cluster = forumClusteringService.predictCluster(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("postId", postId);
        response.put("clusterId", cluster);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/classify")
    public ResponseEntity<ClassificationResult> classifyText(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        ClassificationResult result = forumClassificationService.classifyText(title, content);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{postId}/classify")
    public ResponseEntity<ClassificationResult> classifyPost(@PathVariable Long postId) {
        ClassificationResult result = forumClassificationService.classifyPost(postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{postId}/predict-popularity")
    public ResponseEntity<PredictionResult> predictPopularity(@PathVariable Long postId) {
        PredictionResult result = forumPredictionService.predictPopularity(postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{postId}/predict-engagement")
    public ResponseEntity<PredictionResult> predictEngagement(@PathVariable Long postId) {
        PredictionResult result = forumPredictionService.predictEngagement(postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{postId}/predict-trend")
    public ResponseEntity<PredictionResult> predictTrend(@PathVariable Long postId) {
        PredictionResult result = forumPredictionService.predictTrendPotential(postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommendations/user/{userId}")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> recommendForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendationResult.RecommendedItem> items = forumRecommendationService.recommendPostsForUser(userId, limit);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/posts/{postId}/similar")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> getSimilarPosts(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendationResult.RecommendedItem> items = forumRecommendationService.getSimilarPosts(postId, limit);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<RecommendationResult.RecommendedItem>> getTrending(
            @RequestParam(defaultValue = "5") int limit) {
        List<RecommendationResult.RecommendedItem> items = forumRecommendationService.getTrendingPosts(limit);
        return ResponseEntity.ok(items);
    }
}
