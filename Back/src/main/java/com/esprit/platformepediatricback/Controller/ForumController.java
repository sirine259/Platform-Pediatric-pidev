package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.ForumService;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {
    
    @Autowired
    private ForumService forumService;
    
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
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = forumService.getAllPosts();
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
}
