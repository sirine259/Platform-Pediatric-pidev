package com.pediatric.platform.likecomment;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.repository.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LikeCommentService {

    private final CommentRepository commentRepository;

    // Like un commentaire (système PIDEV)
    public void likeComment(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setReaction(reaction);
        commentRepository.save(comment);
        log.info("Comment {} liked with {}", commentId, reaction);
    }

    // Unlike un commentaire
    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setReaction(null);
        commentRepository.save(comment);
        log.info("Comment {} unliked", commentId);
    }

    // Vérifier si un commentaire est liké
    public boolean isCommentLiked(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        return reaction.equals(comment.getReaction());
    }

    // Compter les likes par type de réaction
    public long countLikesByReaction(LikePost reaction) {
        return commentRepository.findAll().stream()
                .filter(c -> reaction.equals(c.getReaction()))
                .count();
    }

    // Obtenir tous les commentaires likés avec une réaction spécifique
    public java.util.List<Comment> getCommentsByReaction(LikePost reaction) {
        return commentRepository.findAll().stream()
                .filter(c -> reaction.equals(c.getReaction()))
                .collect(java.util.stream.Collectors.toList());
    }
}
