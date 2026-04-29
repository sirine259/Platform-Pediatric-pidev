package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.dto.PostDTO;
import com.esprit.platformepediatricback.dto.CreatePostRequest;
import com.esprit.platformepediatricback.dto.UpdatePostRequest;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.entity.StatusComplaint;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
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
public class PostDTOService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VoteCommentService voteCommentService;

    // Créer un nouveau post
    public PostDTO createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setSubject(request.getSubject());
        post.setContent(request.getContent());
        post.setPicture(request.getPicture());
        post.setIsAnonymous(request.getIsAnonymous());
        post.setStatus(StatusComplaint.Pending);
        post.setDatePost(LocalDateTime.now());
        
        Post savedPost = postRepository.save(post);
        log.info("Created new post {}", savedPost.getId());
        
        return new PostDTO(savedPost);
    }

    // Mettre à jour un post
    public PostDTO updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setSubject(request.getSubject());
        post.setContent(request.getContent());
        post.setPicture(request.getPicture());
        post.setIsAnonymous(request.getIsAnonymous());
        post.setStatus(request.getStatus());
        post.setArchivedReason(request.getArchivedReason());
        
        Post updatedPost = postRepository.save(post);
        log.info("Updated post {}", postId);
        
        return new PostDTO(updatedPost);
    }

    // Supprimer un post
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        postRepository.delete(post);
        log.info("Deleted post {}", postId);
    }

    // Like un post
    public void likePost(Long postId, LikePost likeType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setLikePost(likeType);
        postRepository.save(post);
        log.info("Post {} liked with {}", postId, likeType);
    }

    // Obtenir un post par ID
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return new PostDTO(post);
    }

    // Obtenir tous les posts
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts par statut
    public List<PostDTO> getPostsByStatus(String status) {
        List<Post> posts = postRepository.findByStatus(status);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts par utilisateur
    public List<PostDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts par type de like
    public List<PostDTO> getPostsByLikeType(LikePost likeType) {
        List<Post> posts = postRepository.findByLikeType(likeType);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts récents
    public List<PostDTO> getRecentPosts(int limit) {
        List<Post> posts = postRepository.findRecentPosts();
        return posts.stream()
                .limit(limit)
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Rechercher des posts par sujet
    public List<PostDTO> searchPostsBySubject(String subject) {
        List<Post> posts = postRepository.searchPostsBySubject(subject);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Rechercher des posts par mot-clé
    public List<PostDTO> searchByKeyword(String keyword) {
        List<Post> posts = postRepository.searchByKeyword(keyword);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts tendance
    public List<PostDTO> getTrendingPosts() {
        List<Post> likedPosts = postRepository.findByLikeType(LikePost.Love);
        likedPosts.addAll(postRepository.findByLikeType(LikePost.Support));
        likedPosts.addAll(postRepository.findByLikeType(LikePost.Celebrate));
        
        return likedPosts.stream()
                .distinct()
                .map(PostDTO::new)
                .filter(PostDTO::isTrending)
                .limit(10)
                .collect(Collectors.toList());
    }

    // Obtenir les posts les plus populaires
    public List<PostDTO> getMostPopularPosts() {
        List<Post> posts = postRepository.findByStatus("Approved");
        return posts.stream()
                .map(PostDTO::new)
                .sorted((a, b) -> Double.compare(b.getPopularityScore(), a.getPopularityScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Compter les posts par utilisateur
    public long countPostsByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // Compter les posts par statut
    public long countPostsByStatus(String status) {
        return postRepository.countByStatus(status);
    }

    // Mettre à jour le statut d'un post
    public PostDTO updatePostStatus(Long postId, StatusComplaint newStatus) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setStatus(newStatus);
        Post updatedPost = postRepository.save(post);
        log.info("Updated post {} status to {}", postId, newStatus);
        
        return new PostDTO(updatedPost);
    }

    // Obtenir les statistiques d'un post
    public Map<String, Object> getPostStatistics(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("post", post);
        stats.put("likeType", post.getLikePost());
        stats.put("commentCount", post.getCommentCount());
        stats.put("upVotes", post.getUpVotes());
        stats.put("downVotes", post.getDownVotes());
        stats.put("totalReactions", post.getTotalReactions());
        stats.put("popularityScore", post.getPopularityScore());
        stats.put("isTrending", post.isTrending());
        
        return stats;
    }

    // Obtenir les posts par plage de dates
    public List<PostDTO> getPostsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Post> posts = postRepository.findByCreatedAtBetween(startDate, endDate);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts par type
    public List<PostDTO> getPostsByType(Post.PostType postType) {
        List<Post> posts = postRepository.findByPostType(postType);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts par type et non supprimés
    public List<PostDTO> getPostsByTypeAndNotDeleted(Post.PostType postType) {
        List<Post> posts = postRepository.findByPostTypeAndIsDeletedFalseOrderByCreatedAtDesc(postType);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Obtenir les posts récents par type
    public List<PostDTO> getTop10PostsByType(Post.PostType postType) {
        List<Post> posts = postRepository.findTop10ByPostTypeAndIsDeletedFalseOrderByCreatedAtDesc(postType);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // Compter les posts par type
    public long countPostsByType(Post.PostType postType) {
        return postRepository.countByPostType(postType);
    }

    // Rechercher des posts par contenu
    public List<PostDTO> searchPostsByContent(String content) {
        List<Post> posts = postRepository.searchByContent(content);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }
}
