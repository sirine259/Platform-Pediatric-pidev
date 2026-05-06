package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.ForumService;
import com.esprit.platformepediatricback.Service.PostRatingService;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.PostRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {
    
    @Autowired
    private ForumService forumService;
    
    @Autowired
    private PostRatingService postRatingService;
    
    // Forum Post endpoints
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = forumService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }
    
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = forumService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = forumService.getPostsPaginated(pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/page")
    public ResponseEntity<Page<Post>> getPostsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, direction, sortBy);
        Page<Post> posts = forumService.getPostsPaginated(pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/forum/{forumId}")
    public ResponseEntity<Page<Post>> getPostsByForum(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, direction, sortBy);
        Page<Post> posts = forumService.getPostsByForumPaginated(forumId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, direction, sortBy);
        Page<Post> posts = forumService.searchPostsPaginated(keyword, category, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/author/{authorId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        List<Post> posts = forumService.getPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post updatedPost = forumService.updatePost(id, postDetails);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean deleted = forumService.deletePost(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/posts/search/title")
    public ResponseEntity<List<Post>> searchPostsByTitle(@RequestParam String title) {
        List<Post> posts = forumService.searchPostsByTitle(title);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/search/content")
    public ResponseEntity<List<Post>> searchPostsByContent(@RequestParam String content) {
        List<Post> posts = forumService.searchPostsByContent(content);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/posts/search/date-range")
    public ResponseEntity<List<Post>> getPostsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Post> posts = forumService.getPostsByDateRange(startDate, endDate);
        return ResponseEntity.ok(posts);
    }
    
    // Forum Comment endpoints
    @PostMapping("/comments")
    public ResponseEntity<ForumComment> createComment(@RequestBody ForumComment comment) {
        ForumComment createdComment = forumService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }
    
    @GetMapping("/comments/{id}")
    public ResponseEntity<ForumComment> getCommentById(@PathVariable Long id) {
        Optional<ForumComment> comment = forumService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/comments/post/{postId}")
    public ResponseEntity<List<ForumComment>> getCommentsByPost(@PathVariable Long postId) {
        Post post = new Post();
        post.setId(postId);
        List<ForumComment> comments = forumService.getCommentsByPost(post);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/comments/author/{authorId}")
    public ResponseEntity<List<ForumComment>> getCommentsByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        List<ForumComment> comments = forumService.getCommentsByAuthor(author);
        return ResponseEntity.ok(comments);
    }
    
    @PutMapping("/comments/{id}")
    public ResponseEntity<ForumComment> updateComment(@PathVariable Long id, @RequestBody ForumComment commentDetails) {
        ForumComment updatedComment = forumService.updateComment(id, commentDetails);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        boolean deleted = forumService.deleteComment(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/comments/search/content")
    public ResponseEntity<List<ForumComment>> searchCommentsByContent(@RequestParam String content) {
        List<ForumComment> comments = forumService.searchCommentsByContent(content);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/comments/search/date-range")
    public ResponseEntity<List<ForumComment>> getCommentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ForumComment> comments = forumService.getCommentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(comments);
    }
    
    // Statistics endpoints
    @GetMapping("/stats/posts/author/{authorId}")
    public ResponseEntity<Long> getPostCountByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        long count = forumService.getPostCountByAuthor(author);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/comments/post/{postId}")
    public ResponseEntity<Long> getCommentCountByPost(@PathVariable Long postId) {
        Post post = new Post();
        post.setId(postId);
        long count = forumService.getCommentCountByPost(post);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/comments/author/{authorId}")
    public ResponseEntity<Long> getCommentCountByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        long count = forumService.getCommentCountByAuthor(author);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/posts/{postId}/rating")
    public ResponseEntity<Map<String, Object>> ratePost(
            @PathVariable Long postId,
            @RequestParam Integer rating) {
        try {
            String username = resolveAuthenticatedUsername();
            PostRating postRating = postRatingService.ratePostByAuthenticatedUser(postId, username, rating);
            return ResponseEntity.ok(toRatingResponse(postRating));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/posts/{postId}/rating")
    public ResponseEntity<Map<String, Object>> getPostRatingSummary(@PathVariable Long postId) {
        Map<String, Object> summary = postRatingService.getPostRatingSummary(postId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/posts/{postId}/rating/me")
    public ResponseEntity<Map<String, Object>> getUserRating(
            @PathVariable Long postId) {
        String username = resolveAuthenticatedUsername();
        Optional<PostRating> rating = postRatingService.getUserRatingByUsername(postId, username);
        return rating.map(r -> ResponseEntity.ok(toRatingResponse(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/posts/{postId}/rating")
    public ResponseEntity<Void> deleteRating(@PathVariable Long postId) {
        String username = resolveAuthenticatedUsername();
        postRatingService.deleteRatingByUsername(postId, username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/posts/ratings/top")
    public ResponseEntity<List<Map<String, Object>>> getTopRatedPosts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> topPosts = postRatingService.getTopRatedPosts(limit);
        return ResponseEntity.ok(topPosts);
    }

    private Map<String, Object> toRatingResponse(PostRating rating) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", rating.getId());
        response.put("postId", rating.getPost() != null ? rating.getPost().getId() : null);
        response.put("userId", rating.getUser() != null ? rating.getUser().getId() : null);
        response.put("rating", rating.getRating());
        response.put("createdAt", rating.getCreatedAt());
        response.put("updatedAt", rating.getUpdatedAt());
        return response;
    }

    private String resolveAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            if (username != null && !"anonymousUser".equalsIgnoreCase(username)) {
                return username;
            }
        }
        throw new IllegalArgumentException("Utilisateur non authentifié");
    }
}
