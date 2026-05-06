package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Service.LikeCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/like-comment")
@CrossOrigin(origins = "http://localhost:4200")
public class LikeCommentController {

    private static final Logger log = LoggerFactory.getLogger(LikeCommentController.class);

    private final LikeCommentService likeCommentService;
    private final CommentRepository commentRepository;

    public LikeCommentController(LikeCommentService likeCommentService, CommentRepository commentRepository) {
        this.likeCommentService = likeCommentService;
        this.commentRepository = commentRepository;
    }

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

    @PutMapping("/{commentId}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        likeCommentService.unlikeComment(commentId);
        log.info("Comment {} unliked", commentId);
        return ResponseEntity.ok().build();
    }

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
