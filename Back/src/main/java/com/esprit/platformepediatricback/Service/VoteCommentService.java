package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VoteCommentService {

    private static final Logger log = LoggerFactory.getLogger(VoteCommentService.class);

    private final CommentRepository commentRepository;

    public VoteCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void voteComment(Long commentId, VoteComment vote) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setVoteComment(vote);
        commentRepository.save(comment);
        log.info("Comment {} voted with {}", commentId, vote);
    }

    public void changeVote(Long commentId, VoteComment newVote) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (newVote.equals(comment.getVoteComment())) {
            comment.setVoteComment(null);
            log.info("Comment {} vote removed", commentId);
        } else {
            comment.setVoteComment(newVote);
            log.info("Comment {} vote changed to {}", commentId, newVote);
        }
        commentRepository.save(comment);
    }

    public void removeVote(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setVoteComment(null);
        commentRepository.save(comment);
        log.info("Comment {} vote removed", commentId);
    }

    public VoteComment getCommentVote(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return comment.getVoteComment();
    }

    public long countVotesByType(VoteComment voteType) {
        return commentRepository.findAll().stream()
                .filter(c -> voteType.equals(c.getVoteComment()))
                .count();
    }

    public java.util.List<Comment> getCommentsByVote(VoteComment vote) {
        return commentRepository.findAll().stream()
                .filter(c -> vote.equals(c.getVoteComment()))
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.Map<VoteComment, Long> getVoteStatistics() {
        java.util.Map<VoteComment, Long> stats = new java.util.HashMap<>();
        for (VoteComment v : VoteComment.values()) {
            stats.put(v, countVotesByType(v));
        }
        return stats;
    }

    public int getCommentScore(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (comment.getVoteComment() == VoteComment.UpVote) return 1;
        else if (comment.getVoteComment() == VoteComment.DownVote) return -1;
        return 0;
    }

    public java.util.List<Comment> getTopVotedComments() {
        return commentRepository.findAll().stream()
                .filter(c -> c.getVoteComment() != null)
                .sorted((a, b) -> Integer.compare(getCommentScore(b.getId()), getCommentScore(a.getId())))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
    }
}
