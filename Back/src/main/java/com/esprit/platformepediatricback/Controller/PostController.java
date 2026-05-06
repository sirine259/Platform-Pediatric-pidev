package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.PostService;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.Post.PostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    // CRUD Endpoints
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        try {
            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        try {
            Post updatedPost = postService.updatePost(id, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Forum Specific Endpoints
    @PostMapping("/forum")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> createForumPost(@RequestBody Post post) {
        try {
            // Set post type to FORUM
            post.setPostType(PostType.FORUM);
            Post createdPost = postService.createForumPost(post, post.getAuthor());
            return ResponseEntity.ok(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/forum")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getAllForumPosts() {
        List<Post> posts = postService.getAllForumPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/forum/author/{authorId}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getForumPostsByAuthor(@PathVariable Long authorId) {
        List<Post> posts = postService.getForumPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    // Medical Specific Endpoints
    @PostMapping("/medical-update")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Post> createMedicalUpdate(@RequestBody Post post) {
        try {
            post.setPostType(PostType.MEDICAL_UPDATE);
            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/follow-up")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SURGEON', 'ADMIN')")
    public ResponseEntity<Post> createFollowUpPost(@RequestBody Post post) {
        try {
            post.setPostType(PostType.FOLLOW_UP);
            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/medical")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getAllMedicalPosts() {
        List<Post> posts = postService.getAllMedicalPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/medical/author/{authorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getMedicalPostsByAuthor(@PathVariable Long authorId) {
        List<Post> posts = postService.getMedicalPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    // Search Endpoints
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchAllPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search/forum")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> searchForumPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchForumPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search/medical")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> searchMedicalPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchMedicalPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    // Transplant Related Endpoints
    @GetMapping("/transplant/{transplantId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getPostsByTransplant(@PathVariable Long transplantId) {
        List<Post> posts = postService.getPostsByTransplant(transplantId);
        return ResponseEntity.ok(posts);
    }

    // Statistics Endpoints
    @GetMapping("/stats/type/{postType}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Long> countPostsByType(@PathVariable PostType postType) {
        Long count = postService.countPostsByType(postType);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/author/{authorId}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Long> countPostsByAuthor(@PathVariable Long authorId) {
        Long count = postService.countPostsByAuthor(authorId);
        return ResponseEntity.ok(count);
    }

    // Popular Posts Endpoints
    @GetMapping("/popular")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getPopularPosts() {
        List<Post> posts = postService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/popular/forum")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getPopularForumPosts() {
        List<Post> posts = postService.getPopularForumPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/popular/medical")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getPopularMedicalPosts() {
        List<Post> posts = postService.getPopularMedicalPosts();
        return ResponseEntity.ok(posts);
    }

    // Recent Activity Endpoints
    @GetMapping("/recent/{hours}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<List<Post>> getRecentPosts(@PathVariable int hours) {
        List<Post> posts = postService.getRecentPosts(hours);
        return ResponseEntity.ok(posts);
    }

    // Engagement Endpoints
    @PostMapping("/{id}/view")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> incrementViewCount(@PathVariable Long id) {
        try {
            Post post = postService.incrementViewCount(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> incrementLikeCount(@PathVariable Long id) {
        try {
            Post post = postService.incrementLikeCount(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/unlike")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Post> decrementLikeCount(@PathVariable Long id) {
        try {
            Post post = postService.decrementLikeCount(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Permission Check Endpoints
    @GetMapping("/{id}/can-access")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Boolean> canAccessPost(@PathVariable Long id) {
        // This would need the current user from security context
        // For now, return true as a placeholder
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{id}/can-edit")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'NURSE', 'ADMIN', 'SURGEON')")
    public ResponseEntity<Boolean> canEditPost(@PathVariable Long id) {
        // This would need the current user from security context
        // For now, return true as a placeholder
        return ResponseEntity.ok(true);
    }
}
