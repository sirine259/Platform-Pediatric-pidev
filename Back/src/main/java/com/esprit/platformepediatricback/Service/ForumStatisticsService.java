package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.*;
import com.esprit.platformepediatricback.dto.*;
import com.esprit.platformepediatricback.entity.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumStatisticsService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;

    // Statistiques générales du forum
    public ForumStatisticsDTO getGeneralStatistics() {
        ForumStatisticsDTO stats = new ForumStatisticsDTO();
        
        stats.setTotalPosts(postRepository.count());
        stats.setTotalComments(commentRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setApprovedPosts(postRepository.countByStatus("Approved"));
        stats.setPendingPosts(postRepository.countByStatus("Pending"));
        
        // Moyenne de commentaires par post
        long totalPosts = stats.getTotalPosts();
        long totalComments = stats.getTotalComments();
        stats.setAvgCommentsPerPost(totalPosts > 0 ? (double) totalComments / totalPosts : 0);
        
        return stats;
    }

    // Utilisateurs les plus actifs
    public List<ForumStatisticsDTO.UserActivityDTO> getMostActiveUsers() {
        List<Object[]> activeUsers = userActivityRepository.findMostActiveUsers();
        
        return activeUsers.stream()
                .map(this::mapToUserActivityDTO)
                .collect(Collectors.toList());
    }

    // Posts les plus populaires
    public List<ForumStatisticsDTO.PostPopularityDTO> getMostPopularPosts() {
        List<Object[]> popularPosts = postRepository.findMostPopularPosts();
        
        return popularPosts.stream()
                .map(this::mapToPostPopularityDTO)
                .collect(Collectors.toList());
    }

    // Sujets tendance
    public List<ForumStatisticsDTO.TopicTrendDTO> getTrendingTopics() {
        List<Object[]> trendingTopics = postRepository.findTrendingTopics();
        
        return trendingTopics.stream()
                .map(this::mapToTopicTrendDTO)
                .collect(Collectors.toList());
    }

    // Statistiques des votes et réactions
    public ForumStatisticsDTO.VoteStatisticsDTO getVoteStatistics() {
        ForumStatisticsDTO.VoteStatisticsDTO voteStats = new ForumStatisticsDTO.VoteStatisticsDTO();
        
        // Statistiques des votes de commentaires
        Map<VoteComment, Long> commentVotes = new HashMap<>();
        commentVotes.put(VoteComment.UpVote, commentRepository.countCommentsByVote(VoteComment.UpVote));
        commentVotes.put(VoteComment.DownVote, commentRepository.countCommentsByVote(VoteComment.DownVote));
        voteStats.setCommentVotes(commentVotes);
        
        // Statistiques des likes de posts
        Map<LikePost, Long> postLikes = new HashMap<>();
        for (LikePost likeType : LikePost.values()) {
            postLikes.put(likeType, postRepository.countPostsByLikeType(likeType));
        }
        voteStats.setPostLikes(postLikes);
        
        // Statistiques des réactions de commentaires
        Map<LikePost, Long> commentReactions = new HashMap<>();
        for (LikePost reaction : LikePost.values()) {
            commentReactions.put(reaction, commentRepository.countCommentsByReaction(reaction));
        }
        voteStats.setCommentReactions(commentReactions);
        
        return voteStats;
    }

    // Statistiques d'activité par période
    public ForumStatisticsDTO.ActivityStatisticsDTO getActivityByPeriod() {
        ForumStatisticsDTO.ActivityStatisticsDTO activityStats = new ForumStatisticsDTO.ActivityStatisticsDTO();
        
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        activityStats.setPostsToday(postRepository.countPostsToday(todayStart, todayEnd));
        activityStats.setPostsThisWeek(postRepository.countPostsByDateRange(
            LocalDateTime.now().minusDays(7), 
            LocalDateTime.now()
        ));
        activityStats.setPostsThisMonth(postRepository.countPostsByDateRange(
            LocalDateTime.now().minusDays(30), 
            LocalDateTime.now()
        ));
        activityStats.setCommentsToday(commentRepository.countCommentsToday(todayStart, todayEnd));
        activityStats.setCommentsThisWeek(commentRepository.countCommentsByDateRange(
            LocalDateTime.now().minusDays(7), 
            LocalDateTime.now()
        ));
        activityStats.setCommentsThisMonth(commentRepository.countCommentsByDateRange(
            LocalDateTime.now().minusDays(30), 
            LocalDateTime.now()
        ));
        
        return activityStats;
    }

    // Dashboard complet
    public ForumStatisticsDTO getDashboardStatistics() {
        ForumStatisticsDTO dashboard = new ForumStatisticsDTO();
        
        // Copier les statistiques générales
        ForumStatisticsDTO generalStats = getGeneralStatistics();
        dashboard.setTotalPosts(generalStats.getTotalPosts());
        dashboard.setTotalComments(generalStats.getTotalComments());
        dashboard.setTotalUsers(generalStats.getTotalUsers());
        dashboard.setApprovedPosts(generalStats.getApprovedPosts());
        dashboard.setPendingPosts(generalStats.getPendingPosts());
        dashboard.setAvgCommentsPerPost(generalStats.getAvgCommentsPerPost());
        
        dashboard.setMostActiveUsers(getMostActiveUsers());
        dashboard.setMostPopularPosts(getMostPopularPosts());
        dashboard.setTrendingTopics(getTrendingTopics());
        dashboard.setVoteStatistics(getVoteStatistics());
        dashboard.setActivityStats(getActivityByPeriod());
        
        return dashboard;
    }

    // Méthodes de mapping privées
    private ForumStatisticsDTO.UserActivityDTO mapToUserActivityDTO(Object[] result) {
        ForumStatisticsDTO.UserActivityDTO userActivity = new ForumStatisticsDTO.UserActivityDTO();
        userActivity.setUserId((Long) result[0]);
        userActivity.setUserName((String) result[1]);
        userActivity.setFirstName((String) result[2]);
        userActivity.setLastName((String) result[3]);
        userActivity.setPostCount((Long) result[4]);
        userActivity.setCommentCount((Long) result[5]);
        userActivity.setTotalActivity((Long) result[6]);
        return userActivity;
    }

    private ForumStatisticsDTO.PostPopularityDTO mapToPostPopularityDTO(Object[] result) {
        ForumStatisticsDTO.PostPopularityDTO postPopularity = new ForumStatisticsDTO.PostPopularityDTO();
        postPopularity.setPostId((Long) result[0]);
        postPopularity.setSubject((String) result[1]);
        postPopularity.setCommentCount((Integer) result[2]);
        postPopularity.setLikeType((LikePost) result[3]);
        postPopularity.setPopularityScore((Double) result[4]);
        return postPopularity;
    }

    private ForumStatisticsDTO.TopicTrendDTO mapToTopicTrendDTO(Object[] result) {
        ForumStatisticsDTO.TopicTrendDTO topicTrend = new ForumStatisticsDTO.TopicTrendDTO();
        topicTrend.setTopic((String) result[0]);
        topicTrend.setCount((Integer) result[1]);
        return topicTrend;
    }
}
