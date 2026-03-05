package com.pediatric.platform.services;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.entities.StatusComplaint;
import com.pediatric.platform.repository.CommentRepository;
import com.pediatric.platform.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class PostService implements IPostService {

    PostRepository postRepository;
    CommentRepository commentRepository;

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    // Ajouter un commentaire à un post (système PIDEV)
    public Post addCommentToPost(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.getComments().add(comment);
        return postRepository.save(post);
    }

    // Mettre à jour un post (système PIDEV)
    @Override
    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id " + id));

        post.setSubject(postDetails.getSubject());
        post.setContent(postDetails.getContent());
        post.setIsAnonymous(postDetails.getIsAnonymous());
        post.setStatus(postDetails.getStatus());
        post.setArchivedReason(postDetails.getArchivedReason());

        return postRepository.save(post);
    }

    // Rechercher des posts par sujet (système PIDEV)
    @Override
    public List<Post> searchPostsBySubject(String subject) {
        return postRepository.searchPostsBySubject(subject);
    }

    // Mettre à jour un commentaire (système PIDEV)
    @Override
    public Comment updateComment(Long commentId, Comment updatedComment) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        existingComment.setDescription(updatedComment.getDescription());
        existingComment.setDateComment(new Date());

        return commentRepository.save(existingComment);
    }

    // Mettre à jour le statut d'un post
    public Post updatePostStatus(Long id, StatusComplaint newStatus) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        post.setStatus(newStatus);
        return postRepository.save(post);
    }

    // Rechercher des posts par sujet (case insensitive)
    @Override
    public List<Post> searchPostsBySubjectC(String subject) {
        return postRepository.searchPostsBySubjectC(subject);
    }

    // Like un post (système PIDEV) - NOUVEAU
    @Override
    public Post likePost(Long postId, LikePost likeType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikePost(likeType);
        Post savedPost = postRepository.save(post);
        log.info("Post {} liked with {}", postId, likeType);
        return savedPost;
    }

    // Voter pour un post (extension du système)
    public void votePost(Long postId, LikePost voteType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikePost(voteType);
        postRepository.save(post);
        log.info("Post {} voted with {}", postId, voteType);
    }

    // Récupérer les posts par utilisateur
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // Récupérer les posts par statut
    public List<Post> getPostsByStatus(String status) {
        return postRepository.findByStatus(status);
    }

    // Compter les posts par utilisateur
    public long countPostsByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // Récupérer les posts récents
    public List<Post> getRecentPosts() {
        return postRepository.findRecentPosts();
    }

    // Rechercher des posts par mot-clé
    public List<Post> searchByKeyword(String keyword) {
        return postRepository.searchByKeyword(keyword);
    }
}
