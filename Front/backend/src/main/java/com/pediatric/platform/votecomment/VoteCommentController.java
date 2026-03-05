package com.pediatric.platform.votecomment;

import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.repository.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/vote-comment")
@CrossOrigin(origins = "http://localhost:4200")
public class VoteCommentController {

    private final VoteCommentService voteCommentService;
    private final CommentRepository commentRepository;

    // Voter pour un commentaire (UpVote/DownVote)
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

    // Changer le vote d'un commentaire
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

    // Annuler le vote d'un commentaire
    @PutMapping("/{commentId}/remove-vote")
    public ResponseEntity<Void> removeVote(@PathVariable Long commentId) {
        voteCommentService.removeVote(commentId);
        log.info("Comment {} vote removed", commentId);
        return ResponseEntity.ok().build();
    }

    // Obtenir le vote d'un commentaire
    @GetMapping("/{commentId}/vote")
    public ResponseEntity<VoteComment> getCommentVote(@PathVariable Long commentId) {
        VoteComment vote = voteCommentService.getCommentVote(commentId);
        return ResponseEntity.ok(vote);
    }

    // Obtenir le score d'un commentaire
    @GetMapping("/{commentId}/score")
    public ResponseEntity<Integer> getCommentScore(@PathVariable Long commentId) {
        int score = voteCommentService.getCommentScore(commentId);
        return ResponseEntity.ok(score);
    }

    // Compter les votes par type
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

    // Obtenir tous les commentaires votés avec un type spécifique
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

    // Obtenir les statistiques de votes
    @GetMapping("/statistics")
    public ResponseEntity<Map<VoteComment, Long>> getVoteStatistics() {
        Map<VoteComment, Long> stats = voteCommentService.getVoteStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtenir les commentaires les mieux votés
    @GetMapping("/top-voted")
    public ResponseEntity<List<Comment>> getTopVotedComments() {
        List<Comment> comments = voteCommentService.getTopVotedComments();
        return ResponseEntity.ok(comments);
    }

    // Obtenir les statistiques détaillées
    @GetMapping("/detailed-statistics")
    public ResponseEntity<Map<String, Object>> getDetailedStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Statistiques de base
        Map<VoteComment, Long> voteStats = voteCommentService.getVoteStatistics();
        stats.put("voteStatistics", voteStats);
        
        // Totaux
        long totalVotes = voteStats.values().stream().mapToLong(Long::longValue).sum();
        stats.put("totalVotes", totalVotes);
        
        // Scores nets
        long upVotes = voteStats.getOrDefault(VoteComment.UpVote, 0L);
        long downVotes = voteStats.getOrDefault(VoteComment.DownVote, 0L);
        long netScore = upVotes - downVotes;
        stats.put("netScore", netScore);
        
        // Pourcentages
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
