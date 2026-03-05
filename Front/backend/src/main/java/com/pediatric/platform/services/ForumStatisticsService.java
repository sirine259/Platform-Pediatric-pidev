package com.pediatric.platform.services;

import com.pediatric.platform.entities.*;
import com.pediatric.platform.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumStatisticsService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    // Statistiques générales du forum
    public Map<String, Object> getGeneralStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre total de posts
        long totalPosts = postRepository.count();
        stats.put("totalPosts", totalPosts);
        
        // Nombre total de commentaires
        long totalComments = commentRepository.count();
        stats.put("totalComments", totalComments);
        
        // Nombre total d'utilisateurs
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Posts approuvés
        List<Post> approvedPosts = postRepository.findByStatus("Approved");
        stats.put("approvedPosts", approvedPosts.size());
        
        // Posts en attente
        List<Post> pendingPosts = postRepository.findByStatus("Pending");
        stats.put("pendingPosts", pendingPosts.size());
        
        // Moyenne de commentaires par post
        double avgCommentsPerPost = totalPosts > 0 ? (double) totalComments / totalPosts : 0;
        stats.put("avgCommentsPerPost", Math.round(avgCommentsPerPost * 100) / 100.0);
        
        return stats;
    }

    // Statistiques des utilisateurs les plus actifs
    public List<Map<String, Object>> getMostActiveUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> activeUsers = new ArrayList<>();
        
        for (User user : users) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("user", user);
            userData.put("postCount", postRepository.countByUserId(user.getId()));
            userData.put("commentCount", commentRepository.findByUserId(user.getId()).size());
            
            long totalActivity = (Long) userData.get("postCount") + (Long) userData.get("commentCount");
            userData.put("totalActivity", totalActivity);
            
            activeUsers.add(userData);
        }
        
        // Trier par activité totale décroissante
        return activeUsers.stream()
                .sorted((a, b) -> Long.compare((Long) b.get("totalActivity"), (Long) a.get("totalActivity")))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Statistiques des posts les plus populaires
    public List<Map<String, Object>> getMostPopularPosts() {
        List<Post> posts = postRepository.findByStatus("Approved");
        List<Map<String, Object>> popularPosts = new ArrayList<>();
        
        for (Post post : posts) {
            Map<String, Object> postData = new HashMap<>();
            postData.put("post", post);
            postData.put("commentCount", commentRepository.countByPostId(post.getId()));
            postData.put("likeType", post.getLikePost());
            
            // Calculer un score de popularité
            double score = calculatePopularityScore(post);
            postData.put("popularityScore", score);
            
            popularPosts.add(postData);
        }
        
        // Trier par score de popularité décroissant
        return popularPosts.stream()
                .sorted((a, b) -> Double.compare((Double) b.get("popularityScore"), (Double) a.get("popularityScore")))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Calculer le score de popularité d'un post
    private double calculatePopularityScore(Post post) {
        double score = 0.0;
        
        // Points pour les commentaires
        long commentCount = commentRepository.countByPostId(post.getId());
        score += commentCount * 2;
        
        // Points pour le like du post
        if (post.getLikePost() != null) {
            switch (post.getLikePost()) {
                case Love: score += 10; break;
                case Support: score += 8; break;
                case Celebrate: score += 7; break;
                case Insightful: score += 6; break;
                case Like: score += 5; break;
                case Funny: score += 3; break;
                case Dislike: score -= 1; break;
            }
        }
        
        // Bonus pour les posts récents
        if (post.getDatePost() != null) {
            long daysSinceCreation = ChronoUnit.DAYS.between(
                post.getDatePost().toInstant(), 
                LocalDateTime.now().toInstant()
            );
            if (daysSinceCreation < 7) {
                score += 2; // Bonus pour les posts de moins d'une semaine
            }
        }
        
        return score;
    }

    // Statistiques des tendances (sujets les plus discutés)
    public List<Map<String, Object>> getTrendingTopics() {
        List<Post> posts = postRepository.findByStatus("Approved");
        Map<String, Integer> topicCounts = new HashMap<>();
        
        // Extraire les mots-clés des sujets
        for (Post post : posts) {
            if (post.getSubject() != null) {
                String[] words = post.getSubject().toLowerCase().split("\\s+");
                for (String word : words) {
                    if (word.length() > 3) { // Ignorer les mots courts
                        topicCounts.put(word, topicCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
        
        // Convertir en liste et trier
        return topicCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(20)
                .map(entry -> {
                    Map<String, Object> topicData = new HashMap<>();
                    topicData.put("topic", entry.getKey());
                    topicData.put("count", entry.getValue());
                    return topicData;
                })
                .collect(Collectors.toList());
    }

    // Statistiques des votes et réactions
    public Map<String, Object> getVoteStatistics() {
        Map<String, Object> voteStats = new HashMap<>();
        
        // Votes des commentaires
        long upVotes = voteRepository.countCommentsByVote(VoteComment.UpVote);
        long downVotes = voteRepository.countCommentsByVote(VoteComment.DownVote);
        voteStats.put("commentUpVotes", upVotes);
        voteStats.put("commentDownVotes", downVotes);
        voteStats.put("totalCommentVotes", upVotes + downVotes);
        
        // Likes des posts
        Map<String, Long> postLikes = new HashMap<>();
        for (LikePost likeType : LikePost.values()) {
            long count = voteRepository.countPostsByLikeType(likeType);
            postLikes.put(likeType.name(), count);
        }
        voteStats.put("postLikes", postLikes);
        
        // Réactions des commentaires
        Map<String, Long> commentReactions = new HashMap<>();
        for (LikePost reaction : LikePost.values()) {
            long count = voteRepository.countCommentsByReaction(reaction);
            commentReactions.put(reaction.name(), count);
        }
        voteStats.put("commentReactions", commentReactions);
        
        return voteStats;
    }

    // Statistiques d'activité par période
    public Map<String, Object> getActivityByPeriod() {
        Map<String, Object> activityStats = new HashMap<>();
        
        // Posts créés aujourd'hui
        List<Post> todayPosts = postRepository.findAll().stream()
                .filter(p -> p.getDatePost() != null)
                .filter(p -> {
                    LocalDateTime postDate = p.getDatePost().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
                    return postDate.toLocalDate().equals(LocalDateTime.now().toLocalDate());
                })
                .collect(Collectors.toList());
        activityStats.put("postsToday", todayPosts.size());
        
        // Posts créés cette semaine
        List<Post> weekPosts = postRepository.findAll().stream()
                .filter(p -> p.getDatePost() != null)
                .filter(p -> {
                    LocalDateTime postDate = p.getDatePost().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
                    return ChronoUnit.DAYS.between(postDate, LocalDateTime.now()) <= 7;
                })
                .collect(Collectors.toList());
        activityStats.put("postsThisWeek", weekPosts.size());
        
        // Posts créés ce mois
        List<Post> monthPosts = postRepository.findAll().stream()
                .filter(p -> p.getDatePost() != null)
                .filter(p -> {
                    LocalDateTime postDate = p.getDatePost().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
                    return ChronoUnit.DAYS.between(postDate, LocalDateTime.now()) <= 30;
                })
                .collect(Collectors.toList());
        activityStats.put("postsThisMonth", monthPosts.size());
        
        return activityStats;
    }

    // Statistiques détaillées pour le dashboard admin
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> dashboardStats = new HashMap<>();
        
        dashboardStats.put("general", getGeneralStatistics());
        dashboardStats.put("mostActiveUsers", getMostActiveUsers());
        dashboardStats.put("mostPopularPosts", getMostPopularPosts());
        dashboardStats.put("trendingTopics", getTrendingTopics());
        dashboardStats.put("votes", getVoteStatistics());
        dashboardStats.put("activity", getActivityByPeriod());
        
        return dashboardStats;
    }
}
