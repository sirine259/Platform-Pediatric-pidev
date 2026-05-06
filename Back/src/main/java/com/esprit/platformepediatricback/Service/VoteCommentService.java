package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class VoteCommentService {

    private final CommentRepository commentRepository;

    // Voter pour un commentaire (UpVote/DownVote) - système PIDEV
    public void voteComment(Long commentId, VoteComment vote) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setVoteComment(vote);
        commentRepository.save(comment);
        log.info("Comment {} voted with {}", commentId, vote);
    }

    // Changer le vote d'un commentaire
    public void changeVote(Long commentId, VoteComment newVote) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Si le vote est le même, on l'annule
        if (newVote.equals(comment.getVoteComment())) {
            comment.setVoteComment(null);
            log.info("Comment {} vote removed", commentId);
        } else {
            comment.setVoteComment(newVote);
            log.info("Comment {} vote changed to {}", commentId, newVote);
        }
        
        commentRepository.save(comment);
    }

    // Annuler le vote d'un commentaire
    public void removeVote(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setVoteComment(null);
        commentRepository.save(comment);
        log.info("Comment {} vote removed", commentId);
    }

    // Obtenir le vote d'un commentaire
    public VoteComment getCommentVote(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        return comment.getVoteComment();
    }

    // Compter les votes par type
    public long countVotesByType(VoteComment voteType) {
        return commentRepository.findAll().stream()
                .filter(c -> voteType.equals(c.getVoteComment()))
                .count();
    }

    // Obtenir tous les commentaires votés avec un type spécifique
    public java.util.List<Comment> getCommentsByVote(VoteComment vote) {
        return commentRepository.findAll().stream()
                .filter(c -> vote.equals(c.getVoteComment()))
                .collect(java.util.stream.Collectors.toList());
    }

    // Obtenir les statistiques de votes
    public java.util.Map<VoteComment, Long> getVoteStatistics() {
        java.util.Map<VoteComment, Long> stats = new java.util.HashMap<>();
        
        for (VoteComment vote : VoteComment.values()) {
            long count = countVotesByType(vote);
            stats.put(vote, count);
        }
        
        return stats;
    }

    // Calculer le score net d'un commentaire (UpVotes - DownVotes)
    public int getCommentScore(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (comment.getVoteComment() == VoteComment.UpVote) {
            return 1;
        } else if (comment.getVoteComment() == VoteComment.DownVote) {
            return -1;
        }
        
        return 0;
    }

    // Obtenir les commentaires les mieux votés
    public java.util.List<Comment> getTopVotedComments() {
        return commentRepository.findAll().stream()
                .filter(c -> c.getVoteComment() != null)
                .sorted((a, b) -> Integer.compare(getCommentScore(b.getId()), getCommentScore(a.getId())))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
    }
}
