package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Service.VoteCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vote-comment")
@CrossOrigin(origins = "http://localhost:4200")
public class VoteCommentController {

    private static final Logger log = LoggerFactory.getLogger(VoteCommentController.class);

    private final VoteCommentService voteCommentService;
    private final CommentRepository commentRepository;

    public VoteCommentController(VoteCommentService voteCommentService, CommentRepository commentRepository) {
        this.voteCommentService = voteCommentService;
        this.commentRepository = commentRepository;
    }

    @PutMapping("/{commentId}/vote")
    public ResponseEntity<Void> voteComment(
            @PathVariable Long commentId,
            @RequestParam String vote) {
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            voteCommentService.voteComment(commentId, voteType);
            log.info("Comment {} voted with {}", commentId, vote);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{commentId}/change-vote")
    public ResponseEntity<Void> changeVote(
            @PathVariable Long commentId,
            @RequestParam String newVote) {
        try {
            VoteComment voteType = VoteComment.valueOf(newVote);
            voteCommentService.changeVote(commentId, voteType);
            log.info("Comment {} vote changed to {}", commentId, newVote);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", newVote);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{commentId}/remove-vote")
    public ResponseEntity<Void> removeVote(@PathVariable Long commentId) {
        voteCommentService.removeVote(commentId);
        log.info("Comment {} vote removed", commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}/vote")
    public ResponseEntity<VoteComment> getCommentVote(@PathVariable Long commentId) {
        VoteComment vote = voteCommentService.getCommentVote(commentId);
        return ResponseEntity.ok(vote);
    }

    @GetMapping("/{commentId}/score")
    public ResponseEntity<Integer> getCommentScore(@PathVariable Long commentId) {
        int score = voteCommentService.getCommentScore(commentId);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/count/{voteType}")
    public ResponseEntity<Long> countVotesByType(@PathVariable String voteType) {
        try {
            VoteComment vote = VoteComment.valueOf(voteType);
            long count = voteCommentService.countVotesByType(vote);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{voteType}")
    public ResponseEntity<List<Comment>> getCommentsByVote(@PathVariable String voteType) {
        try {
            VoteComment vote = VoteComment.valueOf(voteType);
            List<Comment> comments = voteCommentService.getCommentsByVote(vote);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<VoteComment, Long>> getVoteStatistics() {
        Map<VoteComment, Long> stats = voteCommentService.getVoteStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-voted")
    public ResponseEntity<List<Comment>> getTopVotedComments() {
        List<Comment> comments = voteCommentService.getTopVotedComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/detailed-statistics")
    public ResponseEntity<Map<String, Object>> getDetailedStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        Map<VoteComment, Long> voteStats = voteCommentService.getVoteStatistics();
        stats.put("voteStatistics", voteStats);
        long totalVotes = voteStats.values().stream().mapToLong(Long::longValue).sum();
        stats.put("totalVotes", totalVotes);
        long upVotes = voteStats.getOrDefault(VoteComment.UpVote, 0L);
        long downVotes = voteStats.getOrDefault(VoteComment.DownVote, 0L);
        stats.put("netScore", upVotes - downVotes);
        if (totalVotes > 0) {
            stats.put("upVotePercentage", (upVotes * 100.0) / totalVotes);
            stats.put("downVotePercentage", (downVotes * 100.0) / totalVotes);
        } else {
            stats.put("upVotePercentage", 0.0);
            stats.put("downVotePercentage", 0.0);
        }
        return ResponseEntity.ok(stats);
    }
}
