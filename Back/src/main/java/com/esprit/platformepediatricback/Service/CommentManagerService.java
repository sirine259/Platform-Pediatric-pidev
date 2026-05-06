package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CommentManagerService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeCommentService likeCommentService;
    private final VoteCommentService voteCommentService;

    // Créer un nouveau commentaire
    public Comment createComment(Long postId, String description, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setDateComment(LocalDateTime.now());
        comment.setPost(post);
        comment.setVoteComment(VoteComment.UpVote); // Vote par défaut
        comment.setReponse(new HashSet<>());
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Created new comment {} for post {}", savedComment.getId(), postId);
        
        return savedComment;
    }

    // Répondre à un commentaire
    public Comment replyToComment(Long parentCommentId, String replyText, Long userId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        
        Comment reply = new Comment();
        reply.setDescription(replyText);
        reply.setDateComment(LocalDateTime.now());
        reply.setPost(parentComment.getPost());
        reply.setVoteComment(VoteComment.UpVote);
        reply.setReponse(new HashSet<>());
        
        Comment savedReply = commentRepository.save(reply);
        
        // Ajouter la réponse au commentaire parent
        if (parentComment.getReponse() == null) {
            parentComment.setReponse(new HashSet<>());
        }
        parentComment.getReponse().add(savedReply);
        commentRepository.save(parentComment);
        
        log.info("Created reply {} to comment {}", savedReply.getId(), parentCommentId);
        
        return savedReply;
    }

    // Mettre à jour un commentaire
    public Comment updateComment(Long commentId, String newDescription) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setDescription(newDescription);
        comment.setDateComment(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        log.info("Updated comment {}", commentId);
        
        return updatedComment;
    }

    // Supprimer un commentaire
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        commentRepository.delete(comment);
        log.info("Deleted comment {}", commentId);
    }

    // Voter pour un commentaire (UpVote/DownVote)
    public void voteOnComment(Long commentId, VoteComment vote) {
        voteCommentService.voteComment(commentId, vote);
        log.info("Voted on comment {} with {}", commentId, vote);
    }

    // Like un commentaire (réaction)
    public void likeComment(Long commentId, LikePost reaction) {
        likeCommentService.likeComment(commentId, reaction);
        log.info("Liked comment {} with {}", commentId, reaction);
    }

    // Unlike un commentaire
    public void unlikeComment(Long commentId) {
        likeCommentService.unlikeComment(commentId);
        log.info("Unliked comment {}", commentId);
    }

    // Obtenir tous les commentaires d'un post
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Obtenir un commentaire par ID
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    // Obtenir les réponses d'un commentaire
    public Set<Comment> getRepliesToComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        return comment.getReponse() != null ? comment.getReponse() : new HashSet<>();
    }

    // Compter les commentaires d'un post
    public long countCommentsByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // Obtenir les statistiques d'un commentaire
    public java.util.Map<String, Object> getCommentStatistics(Long commentId) {
        Comment comment = getCommentById(commentId);
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("comment", comment);
        stats.put("vote", comment.getVoteComment());
        stats.put("reaction", comment.getReaction());
        stats.put("replyCount", comment.getReponse() != null ? comment.getReponse().size() : 0);
        stats.put("dateComment", comment.getDateComment());
        
        return stats;
    }

    // Obtenir les commentaires les plus récents
    public List<Comment> getRecentComments(int limit) {
        return commentRepository.findAll().stream()
                .sorted((a, b) -> b.getDateComment().compareTo(a.getDateComment()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    // Rechercher des commentaires par contenu
    public List<Comment> searchComments(String keyword) {
        return commentRepository.searchByContent(keyword);
    }

    // Valider un commentaire (pour la modération)
    public Comment validateComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        // Logique de validation ici
        log.info("Validated comment {}", commentId);
        return comment;
    }

    // Signaler un commentaire
    public Comment reportComment(Long commentId, String reason) {
        Comment comment = getCommentById(commentId);
        // Logique de signalement ici
        log.info("Reported comment {} for reason: {}", commentId, reason);
        return comment;
    }
}
