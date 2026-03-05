package com.pediatric.platform.controllers;

import com.pediatric.platform.services.ForumStatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/forum/statistics")
@CrossOrigin(origins = "http://localhost:4200")
public class ForumStatisticsController {

    private final ForumStatisticsService statisticsService;

    // Statistiques générales du forum
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        Map<String, Object> stats = statisticsService.getGeneralStatistics();
        return ResponseEntity.ok(stats);
    }

    // Utilisateurs les plus actifs
    @GetMapping("/most-active-users")
    public ResponseEntity<List<Map<String, Object>>> getMostActiveUsers() {
        List<Map<String, Object>> users = statisticsService.getMostActiveUsers();
        return ResponseEntity.ok(users);
    }

    // Posts les plus populaires
    @GetMapping("/most-popular-posts")
    public ResponseEntity<List<Map<String, Object>>> getMostPopularPosts() {
        List<Map<String, Object>> posts = statisticsService.getMostPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // Sujets tendance
    @GetMapping("/trending-topics")
    public ResponseEntity<List<Map<String, Object>>> getTrendingTopics() {
        List<Map<String, Object>> topics = statisticsService.getTrendingTopics();
        return ResponseEntity.ok(topics);
    }

    // Statistiques des votes
    @GetMapping("/votes")
    public ResponseEntity<Map<String, Object>> getVoteStatistics() {
        Map<String, Object> stats = statisticsService.getVoteStatistics();
        return ResponseEntity.ok(stats);
    }

    // Statistiques d'activité par période
    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getActivityByPeriod() {
        Map<String, Object> stats = statisticsService.getActivityByPeriod();
        return ResponseEntity.ok(stats);
    }

    // Dashboard complet (toutes les statistiques)
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> dashboard = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(dashboard);
    }

    // Statistiques par utilisateur spécifique
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        // Implémentation à ajouter si nécessaire
        Map<String, Object> userStats = new java.util.HashMap<>();
        userStats.put("message", "User statistics not implemented yet");
        return ResponseEntity.ok(userStats);
    }

    // Statistiques par post spécifique
    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getPostStatistics(@PathVariable Long postId) {
        // Implémentation à ajouter si nécessaire
        Map<String, Object> postStats = new java.util.HashMap<>();
        postStats.put("message", "Post statistics not implemented yet");
        return ResponseEntity.ok(postStats);
    }
}
