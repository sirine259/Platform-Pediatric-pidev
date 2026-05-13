package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.LikePost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumStatisticsDTO {
    private Long totalPosts;
    private Long totalComments;
    private Long totalUsers;
    private Long approvedPosts;
    private Long pendingPosts;
    private Double avgCommentsPerPost;
    private List<UserActivityDTO> mostActiveUsers;
    private List<PostPopularityDTO> mostPopularPosts;
    private List<TopicTrendDTO> trendingTopics;
    private VoteStatisticsDTO voteStatistics;
    private ActivityStatisticsDTO activityStats;
    
    // Getters/Setters manuels pour compatibilité
    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }
    
    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }
    
    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public void setAvgCommentsPerPost(double avgCommentsPerPost) {
        this.avgCommentsPerPost = avgCommentsPerPost;
    }
    
    public void setCommentVotes(java.util.Map<com.esprit.platformepediatricback.entity.VoteComment, java.lang.Long> commentVotes) {
        if (this.voteStatistics == null) {
            this.voteStatistics = new VoteStatisticsDTO();
        }
        this.voteStatistics.setCommentVotes(commentVotes);
    }
    
    public void setPostLikes(java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> postLikes) {
        if (this.voteStatistics == null) {
            this.voteStatistics = new VoteStatisticsDTO();
        }
        this.voteStatistics.setPostLikes(postLikes);
    }
    
    public void setCommentReactions(java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> commentReactions) {
        if (this.voteStatistics == null) {
            this.voteStatistics = new VoteStatisticsDTO();
        }
        this.voteStatistics.setCommentReactions(commentReactions);
    }
    
    public java.util.Map<com.esprit.platformepediatricback.entity.VoteComment, java.lang.Long> getCommentVotes() {
        return voteStatistics != null ? voteStatistics.getCommentVotes() : null;
    }
    
    public java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> getPostLikes() {
        return voteStatistics != null ? voteStatistics.getPostLikes() : null;
    }
    
    public java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> getCommentReactions() {
        return voteStatistics != null ? voteStatistics.getCommentReactions() : null;
    }
    
    public void setPostsToday(long postsToday) {
        if (this.activityStats == null) {
            this.activityStats = new ActivityStatisticsDTO();
        }
        this.activityStats.setPostsToday(postsToday);
    }
    
    public void setPostsThisWeek(long postsThisWeek) {
        if (this.activityStats == null) {
            this.activityStats = new ActivityStatisticsDTO();
        }
        this.activityStats.setPostsThisWeek(postsThisWeek);
    }
    
    public void setPostsThisMonth(long postsThisMonth) {
        if (this.activityStats == null) {
            this.activityStats = new ActivityStatisticsDTO();
        }
        this.activityStats.setPostsThisMonth(postsThisMonth);
    }
    
    public void setGeneral(java.util.Map<String, Object> generalStats) {
        // Stocker les statistiques générales si nécessaire
        // Pour l'instant, cette méthode est un placeholder
    }
    
    public void setMostActiveUsers(List<UserActivityDTO> users) {
        this.mostActiveUsers = users;
    }
    
    public void setMostPopularPosts(List<PostPopularityDTO> posts) {
        this.mostPopularPosts = posts;
    }
    
    public void setTrendingTopics(List<TopicTrendDTO> topics) {
        this.trendingTopics = topics;
    }
    
    public void setVoteStatistics(VoteStatisticsDTO voteStatistics) {
        this.voteStatistics = voteStatistics;
    }
    
    public void setActivityStats(ActivityStatisticsDTO activityStats) {
        this.activityStats = activityStats;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityDTO {
        private Long userId;
        private String userName;
        private String firstName;
        private String lastName;
        private Long postCount;
        private Long commentCount;
        private Long totalActivity;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPopularityDTO {
        private Long postId;
        private String subject;
        private Integer commentCount;
        private LikePost likeType;
        private Double popularityScore;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicTrendDTO {
        private String topic;
        private Integer count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityStatisticsDTO {
        private Long postsToday;
        private Long postsThisWeek;
        private Long postsThisMonth;
        private Long commentsToday;
        private Long commentsThisWeek;
        private Long commentsThisMonth;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteStatisticsDTO {
        private java.util.Map<com.esprit.platformepediatricback.entity.VoteComment, java.lang.Long> commentVotes;
        private java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> postLikes;
        private java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> commentReactions;
        private Long netScore;
        private Double upVotePercentage;
        private Double downVotePercentage;
        
        // Getters/Setters manuels
        public void setCommentVotes(java.util.Map<com.esprit.platformepediatricback.entity.VoteComment, java.lang.Long> commentVotes) {
            this.commentVotes = commentVotes;
        }
        
        public void setPostLikes(java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> postLikes) {
            this.postLikes = postLikes;
        }
        
        public void setCommentReactions(java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> commentReactions) {
            this.commentReactions = commentReactions;
        }
        
        public java.util.Map<com.esprit.platformepediatricback.entity.VoteComment, java.lang.Long> getCommentVotes() {
            return commentVotes;
        }
        
        public java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> getPostLikes() {
            return postLikes;
        }
        
        public java.util.Map<com.esprit.platformepediatricback.entity.LikePost, java.lang.Long> getCommentReactions() {
            return commentReactions;
        }
        
        public void setNetScore(long netScore) {
            this.netScore = netScore;
        }
        
        public void setUpVotePercentage(double upVotePercentage) {
            this.upVotePercentage = upVotePercentage;
        }
        
        public void setDownVotePercentage(double downVotePercentage) {
            this.downVotePercentage = downVotePercentage;
        }
        
        public Long getNetScore() {
            return netScore;
        }
        
        public Double getUpVotePercentage() {
            return upVotePercentage;
        }
        
        public Double getDownVotePercentage() {
            return downVotePercentage;
        }

        public void setTotalPostLikes(long sum) {
        }

        public void setTotalCommentReactions(long sum) {
        }

        public void setTotalCommentVotes(long sum) {
        }

        public long getTotalVotes() {
            return 0;
        }
    }
}
