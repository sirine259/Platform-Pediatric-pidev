package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.dto.CommentDTO;
import com.esprit.platformepediatricback.dto.CreateCommentRequest;
import com.esprit.platformepediatricback.dto.UpdateCommentRequest;
import com.esprit.platformepediatricback.dto.ReplyRequest;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentDTOService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeCommentService likeCommentService;
    private final VoteCommentService voteCommentService;

    // Créer un nouveau commentaire
    public CommentDTO createComment(CreateCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        Comment comment = new Comment();
        comment.setDescription(request.getDescription());
        comment.setDateComment(LocalDateTime.now());
        comment.setPost(post);
        comment.setVoteComment(VoteComment.UpVote);
        comment.setReponse(new java.util.HashSet<>());
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Created new comment {} for post {}", savedComment.getId(), request.getPostId());
        
        return new CommentDTO(savedComment);
    }

    // Répondre à un commentaire
    public CommentDTO replyToComment(ReplyRequest request) {
        Comment parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        
        Comment reply = new Comment();
        reply.setDescription(request.getReplyText());
        reply.setDateComment(LocalDateTime.now());
        reply.setPost(parentComment.getPost());
        reply.setVoteComment(VoteComment.UpVote);
        reply.setReponse(new java.util.HashSet<>());
        
        Comment savedReply = commentRepository.save(reply);
        
        // Ajouter la réponse au commentaire parent
        if (parentComment.getReponse() == null) {
            parentComment.setReponse(new java.util.HashSet<>());
        }
        parentComment.getReponse().add(savedReply);
        commentRepository.save(parentComment);
        
        log.info("Created reply {} to comment {}", savedReply.getId(), request.getParentCommentId());
        
        return new CommentDTO(savedReply);
    }

    // Mettre à jour un commentaire
    public CommentDTO updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setDescription(request.getDescription());
        comment.setDateComment(LocalDateTime.now());
        
        if (request.getIsAnonymous() != null) {
            comment.setUser(null); // Rendre anonyme si demandé
        }
        
        Comment updatedComment = commentRepository.save(comment);
        log.info("Updated comment {}", commentId);
        
        return new CommentDTO(updatedComment);
    }

    // Supprimer un commentaire
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        commentRepository.delete(comment);
        log.info("Deleted comment {}", commentId);
    }

    // Voter pour un commentaire
    public void voteOnComment(Long commentId, VoteComment vote) {
        voteCommentService.voteComment(commentId, vote);
        log.info("Voted on comment {} with {}", commentId, vote);
    }

    // Like un commentaire
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
    public List<CommentDTO> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir un commentaire par ID
    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return new CommentDTO(comment);
    }

    // Obtenir les réponses d'un commentaire
    public List<CommentDTO> getRepliesToComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (comment.getReponse() != null) {
            return comment.getReponse().stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // Compter les commentaires d'un post
    public long countCommentsByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // Obtenir les commentaires récents
    public List<CommentDTO> getRecentComments(int limit) {
        List<Comment> comments = commentRepository.findRecentComments();
        return comments.stream()
                .limit(limit)
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Rechercher des commentaires
    public List<CommentDTO> searchComments(String keyword) {
        List<Comment> comments = commentRepository.searchByContent(keyword);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les commentaires par type de vote
    public List<CommentDTO> getCommentsByVote(VoteComment vote) {
        List<Comment> comments = commentRepository.findByVoteType(vote);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les commentaires par type de réaction
    public List<CommentDTO> getCommentsByReaction(LikePost reaction) {
        List<Comment> comments = commentRepository.findByReactionType(reaction);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les commentaires les plus votés
    public List<CommentDTO> getTopVotedComments() {
        List<Comment> comments = commentRepository.findMostVotedComments();
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les statistiques d'un commentaire
    public Map<String, Object> getCommentStatistics(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("comment", comment);
        stats.put("vote", comment.getVoteComment());
        stats.put("reaction", comment.getReaction());
        stats.put("replyCount", comment.getReponse() != null ? comment.getReponse().size() : 0);
        stats.put("dateComment", comment.getDateComment());
        stats.put("score", comment.getVoteScore());
        
        return stats;
    }

    // Valider un commentaire
    public CommentDTO validateComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        // Logique de validation ici
        log.info("Validated comment {}", commentId);
        return new CommentDTO(comment);
    }

    // Signaler un commentaire
    public CommentDTO reportComment(Long commentId, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        // Logique de signalement ici
        log.info("Reported comment {} for reason: {}", commentId, reason);
        return new CommentDTO(comment);
    }
}
