package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.entity.StatusComplaint;
import com.esprit.platformepediatricback.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String subject;
    private String content;
    private String picture;
    private Boolean isAnonymous;
    private String archivedReason;
    private LocalDateTime datePost;
    private LikePost likePost;
    private StatusComplaint status;
    private Long userId;
    private String userName;
    private String userFirstName;
    private String userLastName;
    private Integer commentCount;
    private List<CommentDTO> comments;
    private Integer totalVotes;
    private Integer totalReactions;
    private Double popularityScore;
    
    // Constructeur à partir de l'entité
    public PostDTO(Post post) {
        this.id = post.getId();
        this.subject = post.getSubject();
        this.content = post.getContent();
        this.picture = post.getPicture();
        this.isAnonymous = post.getIsAnonymous();
        this.archivedReason = post.getArchivedReason();
        this.datePost = post.getDatePost();
        this.likePost = post.getLikePost();
        this.status = post.getStatus();
        this.userId = post.getUser() != null ? post.getUser().getId() : null;
        this.userName = post.getUser() != null ? post.getUser().getUsername() : null;
        this.userFirstName = post.getUser() != null ? post.getUser().getFirstName() : null;
        this.userLastName = post.getUser() != null ? post.getUser().getLastName() : null;
        
        // Convertir les commentaires en DTOs
        if (post.getComments() != null) {
            this.comments = post.getComments().stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
            this.commentCount = this.comments.size();
        } else {
            this.commentCount = 0;
            this.comments = List.of();
        }
        
        // Calculer les statistiques
        calculateStatistics();
    }
    
    // Méthodes utilitaires
    private void calculateStatistics() {
        // Calculer les votes
        if (comments != null) {
            this.totalVotes = comments.stream()
                    .mapToInt(CommentDTO::getVoteScore)
                    .sum();
            this.totalReactions = (int) comments.stream()
                    .filter(CommentDTO::hasReaction)
                    .count();
        } else {
            this.totalVotes = 0;
            this.totalReactions = 0;
        }
        
        // Calculer le score de popularité
        this.popularityScore = calculatePopularityScore();
    }
    
    private Double calculatePopularityScore() {
        double score = 0.0;
        
        // Points pour les commentaires
        score += commentCount * 2;
        
        // Points pour le like du post
        if (likePost != null) {
            switch (likePost) {
                case Love: score += 10; break;
                case Support: score += 8; break;
                case Celebrate: score += 7; break;
                case Insightful: score += 6; break;
                case Like: score += 5; break;
                case Funny: score += 3; break;
                case Dislike: score -= 1; break;
            }
        }
        
        // Points pour les réactions des commentaires
        if (totalReactions > 0) {
            score += totalReactions * 1;
        }
        
        // Bonus pour les posts récents
        if (datePost != null) {
            long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                datePost, 
                LocalDateTime.now()
            );
            if (daysSinceCreation < 7) {
                score += 2;
            }
        }
        
        return score;
    }
    
    // Getters utilitaires
    public boolean hasLike() {
        return likePost != null;
    }
    
    public boolean hasComments() {
        return comments != null && !comments.isEmpty();
    }
    
    public Integer getUpVotes() {
        if (comments == null) return 0;
        return (int) comments.stream()
                .filter(c -> c.getVoteComment() == com.esprit.platformepediatricback.entity.VoteComment.UpVote)
                .count();
    }
    
    public Integer getDownVotes() {
        if (comments == null) return 0;
        return (int) comments.stream()
                .filter(c -> c.getVoteComment() == com.esprit.platformepediatricback.entity.VoteComment.DownVote)
                .count();
    }
    
    public boolean isTrending() {
        return popularityScore != null && popularityScore > 5.0;
    }
}
