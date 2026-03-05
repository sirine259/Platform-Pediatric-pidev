package com.pediatric.platform.services;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.repository.CommentRepository;
import com.pediatric.platform.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService implements ICommentService {

    CommentRepository commentRepository;
    PostRepository postRepository;

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment updateComment(Long commentId, String newDescription) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.setDescription(newDescription);
        return commentRepository.save(comment);
    }

    // Ajouter une réponse à un commentaire (système PIDEV)
    public Comment addReply(Long parentId, Comment reply) {
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        if (parentComment.getReponse() == null) {
            parentComment.setReponse(new HashSet<>());
        }

        parentComment.getReponse().add(reply);
        commentRepository.save(parentComment);
        return reply;
    }

    // Supprimer un commentaire par ID
    public boolean deleteCommentById(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    // Mettre à jour la réaction d'un commentaire (système PIDEV)
    @Override
    public void updateCommentReaction(Long commentId, LikePost reaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setReaction(reaction);
        commentRepository.save(comment);
    }

    // Mettre à jour la réaction d'une réponse (système PIDEV)
    @Override
    public void updateReplyReaction(Long replyId, LikePost reaction) {
        Comment reply = commentRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));
        reply.setReaction(reaction);
        commentRepository.save(reply);
    }

    // Voter pour un commentaire (UpVote/DownVote) - NOUVEAU
    public void voteComment(Long commentId, VoteComment vote) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setVoteComment(vote);
        commentRepository.save(comment);
        log.info("Comment {} voted with {}", commentId, vote);
    }

    // Ajouter un commentaire à un post
    public Comment addCommentToPost(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        comment.setPost(post);
        comment.setDateComment(new java.util.Date());
        comment.setVoteComment(VoteComment.UpVote); // Vote par défaut

        Comment savedComment = commentRepository.save(comment);
        
        // Ajouter le commentaire au post
        if (post.getComments() == null) {
            post.setComments(new HashSet<>());
        }
        post.getComments().add(savedComment);
        postRepository.save(post);

        return savedComment;
    }

    // Récupérer les commentaires d'un post
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Compter les commentaires d'un post
    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // Rechercher des commentaires par contenu
    public List<Comment> searchCommentsByContent(String keyword) {
        return commentRepository.searchByContent(keyword);
    }
}
