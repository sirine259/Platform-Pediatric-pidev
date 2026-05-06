package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LikeCommentService {

    private static final Logger log = LoggerFactory.getLogger(LikeCommentService.class);

    private final CommentRepository commentRepository;

    public LikeCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void likeComment(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setReaction(reaction);
        commentRepository.save(comment);
        log.info("Comment {} liked with {}", commentId, reaction);
    }

    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setReaction(null);
        commentRepository.save(comment);
        log.info("Comment {} unliked", commentId);
    }

    public boolean isCommentLiked(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return reaction.equals(comment.getReaction());
    }

    public long countLikesByReaction(LikePost reaction) {
        return commentRepository.findAll().stream()
                .filter(c -> reaction.equals(c.getReaction()))
                .count();
    }

    public java.util.List<Comment> getCommentsByReaction(LikePost reaction) {
        return commentRepository.findAll().stream()
                .filter(c -> reaction.equals(c.getReaction()))
                .collect(java.util.stream.Collectors.toList());
    }
}
