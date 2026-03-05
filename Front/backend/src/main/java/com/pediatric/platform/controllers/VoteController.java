package com.pediatric.platform.controllers;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.services.VoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/votes")
@CrossOrigin(origins = "http://localhost:4200")
public class VoteController {

    private final VoteService voteService;

    // Voter pour un commentaire (UpVote/DownVote)
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> voteComment(
            @PathVariable Long commentId, 
            @RequestParam String vote) {
        
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            voteService.voteComment(commentId, voteType);
            log.info("Comment {} voted with {}", commentId, vote);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    // Voter pour un post (Like/Dislike/etc)
    @PutMapping("/post/{postId}")
    public ResponseEntity<Void> votePost(
            @PathVariable Long postId, 
            @RequestParam String voteType) {
        
        try {
            LikePost vote = LikePost.valueOf(voteType);
            voteService.votePost(postId, vote);
            log.info("Post {} voted with {}", postId, voteType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", voteType);
            return ResponseEntity.badRequest().build();
        }
    }

    // Like un post
    @PutMapping("/post/{postId}/like")
    public ResponseEntity<Post> likePost(
            @PathVariable Long postId, 
            @RequestParam String likeType) {
        
        try {
            LikePost like = LikePost.valueOf(likeType);
            Post updatedPost = voteService.likePost(postId, like);
            log.info("Post {} liked with {}", postId, likeType);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            log.error("Invalid like type: {}", likeType);
            return ResponseEntity.badRequest().build();
        }
    }

    // Réagir à un commentaire
    @PutMapping("/comment/{commentId}/react")
    public ResponseEntity<Void> reactToComment(
            @PathVariable Long commentId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            voteService.reactToComment(commentId, reactionType);
            log.info("Comment {} reacted with {}", commentId, reaction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Réagir à une réponse
    @PutMapping("/reply/{replyId}/react")
    public ResponseEntity<Void> reactToReply(
            @PathVariable Long replyId, 
            @RequestParam String reaction) {
        
        try {
            LikePost reactionType = LikePost.valueOf(reaction);
            voteService.reactToReply(replyId, reactionType);
            log.info("Reply {} reacted with {}", replyId, reaction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid reaction type: {}", reaction);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les statistiques des votes
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getVoteStatistics() {
        Map<String, Object> stats = voteService.getVoteStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtenir les posts les plus likés
    @GetMapping("/posts/most-liked")
    public ResponseEntity<List<Post>> getMostLikedPosts(@RequestParam String likeType) {
        try {
            LikePost like = LikePost.valueOf(likeType);
            List<Post> posts = voteService.getMostLikedPosts(like);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            log.error("Invalid like type: {}", likeType);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les commentaires les plus votés
    @GetMapping("/comments/most-voted")
    public ResponseEntity<List<Comment>> getMostVotedComments(@RequestParam String vote) {
        try {
            VoteComment voteType = VoteComment.valueOf(vote);
            List<Comment> comments = voteService.getMostVotedComments(voteType);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.error("Invalid vote type: {}", vote);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les statistiques détaillées des posts
    @GetMapping("/posts/statistics")
    public ResponseEntity<List<Object[]>> getPostVoteStatistics() {
        List<Object[]> stats = voteService.getPostVoteStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtenir les statistiques détaillées des réactions
    @GetMapping("/reactions/statistics")
    public ResponseEntity<List<Object[]>> getPostReactionStatistics() {
        List<Object[]> stats = voteService.getPostReactionStatistics();
        return ResponseEntity.ok(stats);
    }

    // Calculer le score d'un post
    @GetMapping("/posts/{postId}/score")
    public ResponseEntity<Double> calculatePostScore(@PathVariable Long postId) {
        double score = voteService.calculatePostScore(postId);
        return ResponseEntity.ok(score);
    }

    // Obtenir les posts tendance
    @GetMapping("/posts/trending")
    public ResponseEntity<List<Post>> getTrendingPosts() {
        List<Post> trendingPosts = voteService.getTrendingPosts();
        return ResponseEntity.ok(trendingPosts);
    }
}
