package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.LikeService;
import com.esprit.platformepediatricback.entity.LikePost;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:4200")
public class LikeController {

    private final LikeService likeService;

    // Like un post
    @PutMapping("/post/{postId}")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @RequestParam String likeType) {
        
        LikePost likePost = LikePost.valueOf(likeType.toUpperCase());
        likeService.likePost(postId, likePost);
        
        return ResponseEntity.ok().build();
    }

    // Réagir à un commentaire
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> reactToComment(
            @PathVariable Long commentId,
            @RequestParam String reaction) {
        
        LikePost likePost = LikePost.valueOf(reaction.toUpperCase());
        likeService.reactToComment(commentId, likePost);
        
        return ResponseEntity.ok().build();
    }

    // Obtenir les posts par type de like
    @GetMapping("/posts/type/{likeType}")
    public ResponseEntity<List<Post>> getPostsByLikeType(@PathVariable LikePost likeType) {
        List<Post> posts = likeService.getPostsByLikeType(likeType);
        return ResponseEntity.ok(posts);
    }

    // Obtenir les commentaires par type de réaction
    @GetMapping("/comments/type/{reaction}")
    public ResponseEntity<List<Comment>> getCommentsByReaction(@PathVariable LikePost reaction) {
        List<Comment> comments = likeService.getCommentsByReaction(reaction);
        return ResponseEntity.ok(comments);
    }

    // Obtenir les posts les plus likés
    @GetMapping("/posts/most-liked")
    public ResponseEntity<List<Post>> getMostLikedPosts() {
        List<Post> posts = likeService.getMostLikedPosts();
        return ResponseEntity.ok(posts);
    }

    // Obtenir les commentaires avec le plus de réactions
    @GetMapping("/comments/most-reacted")
    public ResponseEntity<List<Comment>> getMostReactedComments() {
        List<Comment> comments = likeService.getMostReactedComments();
        return ResponseEntity.ok(comments);
    }

    // Compter les likes par type
    @GetMapping("/count/type/{likeType}")
    public ResponseEntity<Long> countLikesByType(@PathVariable LikePost likeType) {
        long count = likeService.countLikesByType(likeType);
        return ResponseEntity.ok(count);
    }

    // Compter les réactions par type
    @GetMapping("/count/reactions/{reaction}")
    public ResponseEntity<Long> countReactionsByType(@PathVariable LikePost reaction) {
        long count = likeService.countReactionsByType(reaction);
        return ResponseEntity.ok(count);
    }

    // Obtenir les statistiques de likes
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getLikeStatistics() {
        Map<String, Object> stats = likeService.getLikeStatistics();
        return ResponseEntity.ok(stats);
    }
}
