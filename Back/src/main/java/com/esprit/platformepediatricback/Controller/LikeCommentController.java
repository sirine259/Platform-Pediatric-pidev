package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Service.LikeCommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/like-comment")
@CrossOrigin(origins = "http://localhost:4200")
public class LikeCommentController {

    private final LikeCommentService likeCommentService;
    private final CommentRepository commentRepository;

    // Like un commentaire
    @PutMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            likeCommentService.likeComment(commentId, reactionType);
            log.info("Comment {} liked with {}", commentId, reaction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Unlike un commentaire
    @PutMapping("/{commentId}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        likeCommentService.unlikeComment(commentId);
        log.info("Comment {} unliked", commentId);
        return ResponseEntity.ok().build();
    }

    // Vérifier si un commentaire est liké
    @GetMapping("/{commentId}/is-liked")
    public ResponseEntity<Boolean> isCommentLiked(
            @PathVariable Long commentId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            boolean isLiked = likeCommentService.isCommentLiked(commentId, reactionType);
            return ResponseEntity.ok(isLiked);
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Compter les likes par type de réaction
    @GetMapping("/count/{reaction}")
    public ResponseEntity<Long> countLikesByReaction(@PathVariable String reaction) {
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            long count = likeCommentService.countLikesByReaction(reactionType);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir tous les commentaires likés avec une réaction spécifique
    @GetMapping("/reaction/{reaction}")
    public ResponseEntity<List<Comment>> getCommentsByReaction(@PathVariable String reaction) {
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            List<Comment> comments = likeCommentService.getCommentsByReaction(reactionType);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les statistiques de likes pour tous les types
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getLikeStatistics() {
        Map<String, Long> stats = new java.util.HashMap<>();
        
        for (LikePost reaction : LikePost.values()) {
            long count = likeCommentService.countLikesByReaction(reaction);
            stats.put(reaction.name(), count);
        }
        
        return ResponseEntity.ok(stats);
    }
}
