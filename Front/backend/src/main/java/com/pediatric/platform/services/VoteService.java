package com.pediatric.platform.services;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final CommentService commentService;
    private final PostService postService;

    // Voter pour un commentaire (UpVote/DownVote)
    public void voteComment(Long commentId, VoteComment vote) {
        commentService.voteComment(commentId, vote);
        log.info("Comment {} voted with {}", commentId, vote);
    }

    // Voter pour un post (Like/Dislike/etc)
    public void votePost(Long postId, LikePost voteType) {
        postService.votePost(postId, voteType);
        log.info("Post {} voted with {}", postId, voteType);
    }

    // Like un post
    public Post likePost(Long postId, LikePost likeType) {
        return postService.likePost(postId, likeType);
    }

    // Réagir à un commentaire
    public void reactToComment(Long commentId, LikePost reaction) {
        commentService.updateCommentReaction(commentId, reaction);
        log.info("Comment {} reacted with {}", commentId, reaction);
    }

    // Réagir à une réponse
    public void reactToReply(Long replyId, LikePost reaction) {
        commentService.updateReplyReaction(replyId, reaction);
        log.info("Reply {} reacted with {}", replyId, reaction);
    }

    // Obtenir les statistiques des votes
    public Map<String, Object> getVoteStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques des votes de commentaires
        long upVotes = voteRepository.countCommentsByVote(VoteComment.UpVote);
        long downVotes = voteRepository.countCommentsByVote(VoteComment.DownVote);
        
        stats.put("commentUpVotes", upVotes);
        stats.put("commentDownVotes", downVotes);
        stats.put("totalCommentVotes", upVotes + downVotes);
        
        // Statistiques des likes de posts
        Map<String, Long> postLikes = new HashMap<>();
        for (LikePost likeType : LikePost.values()) {
            long count = voteRepository.countPostsByLikeType(likeType);
            postLikes.put(likeType.name(), count);
        }
        stats.put("postLikes", postLikes);
        
        // Statistiques des réactions de commentaires
        Map<String, Long> commentReactions = new HashMap<>();
        for (LikePost reaction : LikePost.values()) {
            long count = voteRepository.countCommentsByReaction(reaction);
            commentReactions.put(reaction.name(), count);
        }
        stats.put("commentReactions", commentReactions);
        
        return stats;
    }

    // Obtenir les posts les plus likés
    public List<Post> getMostLikedPosts(LikePost likeType) {
        return voteRepository.findPostsByLikeType(likeType);
    }

    // Obtenir les commentaires les plus votés
    public List<Comment> getMostVotedComments(VoteComment vote) {
        return voteRepository.findCommentsByVote(vote);
    }

    // Obtenir les statistiques détaillées des posts
    public List<Object[]> getPostVoteStatistics() {
        return voteRepository.getPostVoteStatistics();
    }

    // Obtenir les statistiques détaillées des réactions
    public List<Object[]> getPostReactionStatistics() {
        return voteRepository.getPostReactionStatistics();
    }

    // Calculer le score d'un post (basé sur les likes et les commentaires)
    public double calculatePostScore(Long postId) {
        Post post = postService.getById(postId);
        double score = 0.0;
        
        // Points pour le like du post
        if (post.getLikePost() != null) {
            switch (post.getLikePost()) {
                case Like: score += 5; break;
                case Love: score += 10; break;
                case Support: score += 7; break;
                case Celebrate: score += 8; break;
                case Insightful: score += 6; break;
                case Funny: score += 4; break;
                case Dislike: score -= 2; break;
            }
        }
        
        // Points pour les commentaires et leurs votes
        if (post.getComments() != null) {
            for (Comment comment : post.getComments()) {
                score += 2; // 2 points par commentaire
                
                // Points pour les votes du commentaire
                if (comment.getVoteComment() != null) {
                    switch (comment.getVoteComment()) {
                        case UpVote: score += 1; break;
                        case DownVote: score -= 0.5; break;
                    }
                }
                
                // Points pour les réactions du commentaire
                if (comment.getReaction() != null) {
                    score += 1; // 1 point pour toute réaction
                }
            }
        }
        
        return score;
    }

    // Obtenir les posts tendance (basés sur le score)
    public List<Post> getTrendingPosts() {
        // Implémentation simple : retourner les posts les plus likés récemment
        List<Post> likedPosts = getMostLikedPosts(LikePost.Love);
        likedPosts.addAll(getMostLikedPosts(LikePost.Support));
        likedPosts.addAll(getMostLikedPosts(LikePost.Celebrate));
        
        return likedPosts.stream()
                .distinct()
                .limit(10)
                .toList();
    }
}
