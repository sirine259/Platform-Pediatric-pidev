package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.ForumCommentRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ForumService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ForumCommentRepository forumCommentRepository;
    
    // Forum Post CRUD operations
    public Post createPost(Post post) {
        // compatibilité: si title est vide mais subject est renseigné, recopier
        if ((post.getTitle() == null || post.getTitle().isBlank()) && post.getSubject() != null) {
            post.setTitle(post.getSubject());
        }
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setDeleted(false);
        return postRepository.save(post);
    }
    
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id)
                .filter(post -> !post.getIsDeleted());
    }
    
    public List<Post> getAllPosts() {
        return postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
    }
    
    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorAndIsDeletedFalse(author);
    }
    
    public Post updatePost(Long id, Post postDetails) {
        Optional<Post> optionalPost = postRepository.findById(id)
                .filter(post -> !post.getIsDeleted());
        
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        }
        return null;
    }
    
    public boolean deletePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setIsDeleted(true);
            postRepository.save(post);
            return true;
        }
        return false;
    }
    
    public List<Post> searchPostsByTitle(String title) {
        return postRepository.searchPostsByTitleOrSubject(title);
    }
    
    public List<Post> searchPostsByContent(String content) {
        return postRepository.findByContentContainingIgnoreCaseAndIsDeletedFalse(content);
    }
    
    public List<Post> getPostsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.findByCreatedAtBetweenAndIsDeletedFalse(startDate, endDate);
    }
    
    // Forum Comment CRUD operations
    public ForumComment createComment(ForumComment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setIsDeleted(false);
        return forumCommentRepository.save(comment);
    }
    
    public Optional<ForumComment> getCommentById(Long id) {
        return forumCommentRepository.findById(id)
                .filter(comment -> !comment.getIsDeleted());
    }
    
    public List<ForumComment> getCommentsByPost(Post post) {
        return forumCommentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtDesc(post);
    }
    
    public List<ForumComment> getCommentsByAuthor(User author) {
        return forumCommentRepository.findByAuthorAndIsDeletedFalse(author);
    }
    
    public ForumComment updateComment(Long id, ForumComment commentDetails) {
        Optional<ForumComment> optionalComment = forumCommentRepository.findById(id)
                .filter(comment -> !comment.getIsDeleted());
        
        if (optionalComment.isPresent()) {
            ForumComment comment = optionalComment.get();
            comment.setContent(commentDetails.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            return forumCommentRepository.save(comment);
        }
        return null;
    }
    
    public boolean deleteComment(Long id) {
        Optional<ForumComment> optionalComment = forumCommentRepository.findById(id);
        if (optionalComment.isPresent()) {
            ForumComment comment = optionalComment.get();
            comment.setIsDeleted(true);
            forumCommentRepository.save(comment);
            return true;
        }
        return false;
    }
    
    public List<ForumComment> searchCommentsByContent(String content) {
        return forumCommentRepository.findByContentContaining(content);
    }
    
    public List<ForumComment> getCommentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return forumCommentRepository.findByDateRange(startDate, endDate);
    }
    
    // Statistics
    public long getPostCountByAuthor(User author) {
        return postRepository.countByAuthor(author);
    }
    
    public long getCommentCountByPost(Post post) {
        return forumCommentRepository.countByPostAndIsDeletedFalse(post);
    }
    
    public long getCommentCountByAuthor(User author) {
        return forumCommentRepository.countActiveCommentsByAuthor(author);
    }
}
