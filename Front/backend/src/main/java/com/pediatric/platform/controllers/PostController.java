package com.pediatric.platform.controllers;

import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.services.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;

    // Récupérer tous les posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAll();
        return ResponseEntity.ok(posts);
    }

    // Récupérer un post par ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getById(id);
        return ResponseEntity.ok(post);
    }

    // Créer un nouveau post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postService.save(post);
        log.info("Created new post with ID: {}", savedPost.getId());
        return ResponseEntity.ok(savedPost);
    }

    // Mettre à jour un post
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post updatedPost = postService.updatePost(id, postDetails);
        log.info("Updated post {}", id);
        return ResponseEntity.ok(updatedPost);
    }

    // Supprimer un post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        log.info("Deleted post {}", id);
        return ResponseEntity.noContent().build();
    }

    // Like un post (système PIDEV) - NOUVEAU
    @PutMapping("/{postId}/like")
    public ResponseEntity<Post> likePost(
            @PathVariable Long postId, 
            @RequestParam String likeType) {
        
        try {
            LikePost reaction = LikePost.valueOf(likeType);
            Post updatedPost = postService.likePost(postId, reaction);
            log.info("Post {} liked with {}", postId, likeType);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            log.error("Invalid like type: {}", likeType);
            return ResponseEntity.badRequest().build();
        }
    }

    // Voter pour un post (extension)
    @PutMapping("/{postId}/vote")
    public ResponseEntity<Void> votePost(
            @PathVariable Long postId, 
            @RequestParam String voteType) {
        
        try {
            LikePost vote = LikePost.valueOf(voteType);
            postService.votePost(postId, vote);
            log.info("Post {} voted with {}", postId, voteType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType);
            return ResponseEntity.badRequest().build();
        }
    }

    // Rechercher des posts par sujet (système PIDEV)
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsBySubject(@RequestParam String subject) {
        List<Post> posts = postService.searchPostsBySubject(subject);
        return ResponseEntity.ok(posts);
    }

    // Rechercher des posts par sujet (case insensitive)
    @GetMapping("/search/case-insensitive")
    public ResponseEntity<List<Post>> searchPostsBySubjectC(@RequestParam String subject) {
        List<Post> posts = postService.searchPostsBySubjectC(subject);
        return ResponseEntity.ok(posts);
    }

    // Récupérer les posts par utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    // Récupérer les posts par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Post>> getPostsByStatus(@PathVariable String status) {
        List<Post> posts = postService.getPostsByStatus(status);
        return ResponseEntity.ok(posts);
    }

    // Compter les posts par utilisateur
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countPostsByUserId(@PathVariable Long userId) {
        long count = postService.countPostsByUserId(userId);
        return ResponseEntity.ok(count);
    }

    // Récupérer les posts récents
    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecentPosts() {
        List<Post> posts = postService.getRecentPosts();
        return ResponseEntity.ok(posts);
    }

    // Rechercher des posts par mot-clé
    @GetMapping("/search/keyword")
    public ResponseEntity<List<Post>> searchByKeyword(@RequestParam String keyword) {
        List<Post> posts = postService.searchByKeyword(keyword);
        return ResponseEntity.ok(posts);
    }
}
