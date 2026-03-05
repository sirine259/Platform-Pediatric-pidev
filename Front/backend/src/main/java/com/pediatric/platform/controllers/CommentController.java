package com.pediatric.platform.controllers;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.services.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {

    private final CommentService commentService;

    // Récupérer tous les commentaires
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAll();
        return ResponseEntity.ok(comments);
    }

    // Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return ResponseEntity.ok(comment);
    }

    // Créer un nouveau commentaire
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.save(comment);
        log.info("Created new comment with ID: {}", savedComment.getId());
        return ResponseEntity.ok(savedComment);
    }

    // Ajouter un commentaire à un post
    @PostMapping("/post/{postId}")
    public ResponseEntity<Comment> addCommentToPost(@PathVariable Long postId, @RequestBody Comment comment) {
        Comment savedComment = commentService.addCommentToPost(postId, comment);
        log.info("Added comment {} to post {}", savedComment.getId(), postId);
        return ResponseEntity.ok(savedComment);
    }

    // Répondre à un commentaire
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<Comment> addReplyToComment(@PathVariable Long parentId, @RequestBody Comment reply) {
        Comment savedReply = commentService.addReply(parentId, reply);
        log.info("Added reply {} to comment {}", savedReply.getId(), parentId);
        return ResponseEntity.ok(savedReply);
    }

    // Mettre à jour un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Comment comment = commentService.updateComment(id, updatedComment);
        log.info("Updated comment {}", id);
        return ResponseEntity.ok(comment);
    }

    // Mettre à jour la réaction d'un commentaire (système PIDEV)
    @PutMapping("/{commentId}/react")
    public ResponseEntity<Void> updateCommentReaction(
            @PathVariable Long commentId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            commentService.updateCommentReaction(commentId, reactionType);
            log.info("Updated reaction for comment {} to {}", commentId, reaction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Mettre à jour la réaction d'une réponse (système PIDEV)
    @PutMapping("/{replyId}/react")
    public ResponseEntity<Void> updateReplyReaction(
            @PathVariable Long replyId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            commentService.updateReplyReaction(replyId, reactionType);
            log.info("Updated reaction for reply {} to {}", replyId, reaction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Voter pour un commentaire (UpVote/DownVote) - NOUVEAU
    @PutMapping("/{commentId}/vote")
    public ResponseEntity<Void> voteComment(
            @PathVariable Long commentId, 
            @RequestParam String vote) {
        
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            commentService.voteComment(commentId, voteType);
            log.info("Voted comment {} with {}", commentId, vote);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    // Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        log.info("Deleted comment {}", id);
        return ResponseEntity.noContent().build();
    }

    // Récupérer les commentaires d'un post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // Compter les commentaires d'un post
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countCommentsByPostId(@PathVariable Long postId) {
        long count = commentService.countCommentsByPostId(postId);
        return ResponseEntity.ok(count);
    }

    // Rechercher des commentaires par contenu
    @GetMapping("/search")
    public ResponseEntity<List<Comment>> searchComments(@RequestParam String keyword) {
        List<Comment> comments = commentService.searchCommentsByContent(keyword);
        return ResponseEntity.ok(comments);
    }
}
