package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.LikePost;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // Like un post
    public void likePost(Long postId, LikePost likeType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setLikePost(likeType);
        postRepository.save(post);
        log.info("Post {} liked with {}", postId, likeType);
    }

    // Unlike un post
    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setLikePost(null);
        postRepository.save(post);
        log.info("Post {} unliked", postId);
    }

    // Réagir à un commentaire
    public void reactToComment(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setReaction(reaction);
        commentRepository.save(comment);
        log.info("Comment {} reacted with {}", commentId, reaction);
    }

    // Obtenir les posts par type de like
    public List<Post> getPostsByLikeType(LikePost likeType) {
        return postRepository.findPostsByLikeType(likeType);
    }

    // Obtenir les commentaires par type de réaction
    public List<Comment> getCommentsByReaction(LikePost reaction) {
        return commentRepository.findCommentsByReaction(reaction);
    }

    // Obtenir les posts les plus likés
    public List<Post> getMostLikedPosts() {
        return postRepository.findMostLikedPosts();
    }

    // Obtenir les commentaires avec le plus de réactions
    public List<Comment> getMostReactedComments() {
        return commentRepository.findMostReactedComments();
    }

    // Compter les likes par type
    public long countLikesByType(LikePost likeType) {
        return postRepository.countPostsByLikeType(likeType);
    }

    // Compter les réactions par type
    public long countReactionsByType(LikePost reaction) {
        return commentRepository.countCommentsByReaction(reaction);
    }

    // Obtenir les statistiques de likes simples
    public Map<String, Object> getLikeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Totaux
        long totalLikes = postRepository.countTotalLikes();
        long totalReactions = commentRepository.countTotalReactions();
        stats.put("totalLikes", totalLikes);
        stats.put("totalReactions", totalReactions);
        stats.put("totalInteractions", totalLikes + totalReactions);
        
        return stats;
    }

    // Calculer le score d'engagement
    public double calculateEngagementScore(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        long commentCount = commentRepository.countByPostId(postId);
        boolean hasLike = post.getLikePost() != null;
        
        double score = commentCount * 2; // 2 points par commentaire
        if (hasLike) score += 5; // 5 points pour le like
        
        return score;
    }

    // Obtenir les posts avec le meilleur engagement
    public List<Post> getTopEngagementPosts() {
        List<Post> allPosts = postRepository.findAll();
        
        return allPosts.stream()
                .sorted((a, b) -> Double.compare(
                    calculateEngagementScore(b.getId()),
                    calculateEngagementScore(a.getId())
                ))
                .limit(10)
                .toList();
    }
}
