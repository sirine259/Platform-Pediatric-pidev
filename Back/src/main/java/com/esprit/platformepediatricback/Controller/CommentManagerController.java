package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.Service.CommentManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/comment-manager")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentManagerController {

    private final CommentManagerService commentManagerService;

    // Créer un nouveau commentaire
    @PostMapping("/post/{postId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestParam String description,
            @RequestParam Long userId) {
        
        Comment comment = commentManagerService.createComment(postId, description, userId);
        return ResponseEntity.ok(comment);
    }

    // Créer un nouveau commentaire avec body
    @PostMapping("/post/{postId}/create")
    public ResponseEntity<Comment> createCommentWithBody(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> request) {
        
        String description = (String) request.get("description");
        Long userId = Long.valueOf(request.get("userId").toString());
        
        Comment comment = commentManagerService.createComment(postId, description, userId);
        return ResponseEntity.ok(comment);
    }

    // Répondre à un commentaire
    @PostMapping("/{parentCommentId}/reply")
    public ResponseEntity<Comment> replyToComment(
            @PathVariable Long parentCommentId,
            @RequestParam String replyText,
            @RequestParam Long userId) {
        
        Comment reply = commentManagerService.replyToComment(parentCommentId, replyText, userId);
        return ResponseEntity.ok(reply);
    }

    // Répondre à un commentaire avec body
    @PostMapping("/{parentCommentId}/reply-body")
    public ResponseEntity<Comment> replyToCommentWithBody(
            @PathVariable Long parentCommentId,
            @RequestBody Map<String, Object> request) {
        
        String replyText = (String) request.get("replyText");
        Long userId = Long.valueOf(request.get("userId").toString());
        
        Comment reply = commentManagerService.replyToComment(parentCommentId, replyText, userId);
        return ResponseEntity.ok(reply);
    }

    // Mettre à jour un commentaire
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestParam String newDescription) {
        
        Comment updatedComment = commentManagerService.updateComment(commentId, newDescription);
        return ResponseEntity.ok(updatedComment);
    }

    // Mettre à jour un commentaire avec body
    @PutMapping("/{commentId}/update")
    public ResponseEntity<Comment> updateCommentWithBody(
            @PathVariable Long commentId,
            @RequestBody Map<String, Object> request) {
        
        String newDescription = (String) request.get("description");
        Comment updatedComment = commentManagerService.updateComment(commentId, newDescription);
        return ResponseEntity.ok(updatedComment);
    }

    // Supprimer un commentaire
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentManagerService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Voter pour un commentaire
    @PutMapping("/{commentId}/vote")
    public ResponseEntity<Void> voteOnComment(
            @PathVariable Long commentId,
            @RequestParam String vote) {
        
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            commentManagerService.voteOnComment(commentId, voteType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    // Like un commentaire
    @PutMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            commentManagerService.likeComment(commentId, reactionType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Unlike un commentaire
    @PutMapping("/{commentId}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        commentManagerService.unlikeComment(commentId);
        return ResponseEntity.ok().build();
    }

    // Obtenir tous les commentaires d'un post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentManagerService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    // Obtenir un commentaire par ID
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentManagerService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    // Obtenir les réponses d'un commentaire
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Set<Comment>> getRepliesToComment(@PathVariable Long commentId) {
        Set<Comment> replies = commentManagerService.getRepliesToComment(commentId);
        return ResponseEntity.ok(replies);
    }

    // Compter les commentaires d'un post
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countCommentsByPost(@PathVariable Long postId) {
        long count = commentManagerService.countCommentsByPost(postId);
        return ResponseEntity.ok(count);
    }

    // Obtenir les statistiques d'un commentaire
    @GetMapping("/{commentId}/statistics")
    public ResponseEntity<Map<String, Object>> getCommentStatistics(@PathVariable Long commentId) {
        Map<String, Object> stats = commentManagerService.getCommentStatistics(commentId);
        return ResponseEntity.ok(stats);
    }

    // Obtenir les commentaires les plus récents
    @GetMapping("/recent")
    public ResponseEntity<List<Comment>> getRecentComments(@RequestParam(defaultValue = "10") int limit) {
        List<Comment> comments = commentManagerService.getRecentComments(limit);
        return ResponseEntity.ok(comments);
    }

    // Rechercher des commentaires
    @GetMapping("/search")
    public ResponseEntity<List<Comment>> searchComments(@RequestParam String keyword) {
        List<Comment> comments = commentManagerService.searchComments(keyword);
        return ResponseEntity.ok(comments);
    }

    // Valider un commentaire
    @PutMapping("/{commentId}/validate")
    public ResponseEntity<Comment> validateComment(@PathVariable Long commentId) {
        Comment comment = commentManagerService.validateComment(commentId);
        return ResponseEntity.ok(comment);
    }

    // Signaler un commentaire
    @PutMapping("/{commentId}/report")
    public ResponseEntity<Comment> reportComment(
            @PathVariable Long commentId,
            @RequestParam String reason) {
        
        Comment comment = commentManagerService.reportComment(commentId, reason);
        return ResponseEntity.ok(comment);
    }
}
