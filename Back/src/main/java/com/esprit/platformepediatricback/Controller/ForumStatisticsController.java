package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.ForumStatisticsService;
import com.esprit.platformepediatricback.dto.ForumStatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/forum-statistics")
@CrossOrigin(origins = "http://localhost:4200")
public class ForumStatisticsController {

    private final ForumStatisticsService statisticsService;

    // Statistiques générales du forum
    @GetMapping("/general")
    public ResponseEntity<ForumStatisticsDTO> getGeneralStatistics() {
        ForumStatisticsDTO stats = statisticsService.getGeneralStatistics();
        return ResponseEntity.ok(stats);
    }

    // Utilisateurs les plus actifs
    @GetMapping("/most-active-users")
    public ResponseEntity<List<ForumStatisticsDTO.UserActivityDTO>> getMostActiveUsers() {
        List<ForumStatisticsDTO.UserActivityDTO> users = statisticsService.getMostActiveUsers();
        return ResponseEntity.ok(users);
    }

    // Posts les plus populaires
    @GetMapping("/most-popular-posts")
    public ResponseEntity<List<ForumStatisticsDTO.PostPopularityDTO>> getMostPopularPosts() {
        List<ForumStatisticsDTO.PostPopularityDTO> posts = statisticsService.getMostPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // Sujets tendance
    @GetMapping("/trending-topics")
    public ResponseEntity<List<ForumStatisticsDTO.TopicTrendDTO>> getTrendingTopics() {
        List<ForumStatisticsDTO.TopicTrendDTO> topics = statisticsService.getTrendingTopics();
        return ResponseEntity.ok(topics);
    }

    // Statistiques d'activité par période
    @GetMapping("/activity")
    public ResponseEntity<ForumStatisticsDTO.ActivityStatisticsDTO> getActivityByPeriod() {
        ForumStatisticsDTO.ActivityStatisticsDTO stats = statisticsService.getActivityByPeriod();
        return ResponseEntity.ok(stats);
    }

    // Dashboard complet
    @GetMapping("/dashboard")
    public ResponseEntity<ForumStatisticsDTO> getDashboardStatistics() {
        ForumStatisticsDTO dashboard = statisticsService.getDashboardStatistics();
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

    // Exporter les statistiques en CSV
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportStatisticsToCsv() {
        // Implémentation à ajouter si nécessaire
        return ResponseEntity.ok("CSV export not implemented yet");
    }

    // Obtenir les statistiques en temps réel
    @GetMapping("/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeStatistics() {
        // Implémentation à ajouter si nécessaire
        Map<String, Object> realtimeStats = new java.util.HashMap<>();
        realtimeStats.put("message", "Real-time statistics not implemented yet");
        return ResponseEntity.ok(realtimeStats);
    }
}
