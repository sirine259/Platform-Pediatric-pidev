package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.CommentDTOService;
import com.esprit.platformepediatricback.dto.CommentDTO;
import com.esprit.platformepediatricback.dto.CreateCommentRequest;
import com.esprit.platformepediatricback.dto.UpdateCommentRequest;
import com.esprit.platformepediatricback.dto.ReplyRequest;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentDTOController {

    private static final Logger log = LoggerFactory.getLogger(CommentDTOController.class);

    private final CommentDTOService commentDTOService;

    public CommentDTOController(CommentDTOService commentDTOService) {
        this.commentDTOService = commentDTOService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CreateCommentRequest request) {
        if (!request.isValidContent()) {
            return ResponseEntity.badRequest().build();
        }
        CommentDTO comment = commentDTOService.createComment(request);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/reply")
    public ResponseEntity<CommentDTO> replyToComment(@Valid @RequestBody ReplyRequest request) {
        if (!request.isValidReplyText()) {
            return ResponseEntity.badRequest().build();
        }
        CommentDTO reply = commentDTOService.replyToComment(request);
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        if (!request.isValidContent()) {
            return ResponseEntity.badRequest().build();
        }
        CommentDTO updatedComment = commentDTOService.updateComment(commentId, request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentDTOService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}/vote")
    public ResponseEntity<Void> voteOnComment(
            @PathVariable Long commentId,
            @RequestParam String vote) {
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            commentDTOService.voteOnComment(commentId, voteType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @RequestParam String reaction) {
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            commentDTOService.likeComment(commentId, reactionType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{commentId}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        commentDTOService.unlikeComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDTO> comments = commentDTOService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long commentId) {
        CommentDTO comment = commentDTOService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDTO>> getRepliesToComment(@PathVariable Long commentId) {
        List<CommentDTO> replies = commentDTOService.getRepliesToComment(commentId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countCommentsByPost(@PathVariable Long postId) {
        long count = commentDTOService.countCommentsByPost(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{commentId}/statistics")
    public ResponseEntity<Map<String, Object>> getCommentStatistics(@PathVariable Long commentId) {
        Map<String, Object> stats = commentDTOService.getCommentStatistics(commentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CommentDTO>> getRecentComments(@RequestParam(defaultValue = "10") int limit) {
        List<CommentDTO> comments = commentDTOService.getRecentComments(limit);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommentDTO>> searchComments(@RequestParam String keyword) {
        List<CommentDTO> comments = commentDTOService.searchComments(keyword);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/vote/{voteType}")
    public ResponseEntity<List<CommentDTO>> getCommentsByVote(@PathVariable String voteType) {
        try {
            VoteComment vote = VoteComment.valueOf(voteType);
            List<CommentDTO> comments = commentDTOService.getCommentsByVote(vote);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reaction/{reactionType}")
    public ResponseEntity<List<CommentDTO>> getCommentsByReaction(@PathVariable String reactionType) {
        try {
            LikePost reaction = LikePost.valueOf(reactionType);
            List<CommentDTO> comments = commentDTOService.getCommentsByReaction(reaction);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reactionType);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/top-voted")
    public ResponseEntity<List<CommentDTO>> getTopVotedComments() {
        List<CommentDTO> comments = commentDTOService.getTopVotedComments();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}/validate")
    public ResponseEntity<CommentDTO> validateComment(@PathVariable Long commentId) {
        CommentDTO comment = commentDTOService.validateComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}/report")
    public ResponseEntity<CommentDTO> reportComment(
            @PathVariable Long commentId,
            @RequestParam String reason) {
        CommentDTO comment = commentDTOService.reportComment(commentId, reason);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long postId) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("message", "Pagination not implemented yet");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<CommentDTO>> getFilteredComments(
            @RequestParam(required = false) String voteType,
            @RequestParam(required = false) String reactionType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        List<CommentDTO> comments = commentDTOService.getRecentComments(20);
        return ResponseEntity.ok(comments);
    }
}
