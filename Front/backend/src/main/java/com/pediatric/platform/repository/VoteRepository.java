package com.pediatric.platform.repository;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.Post;
import com.pediatric.platform.entities.VoteComment;
import com.pediatric.platform.entities.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Object, Long> {
    
    // Compter les votes UpVote pour les commentaires
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.voteComment = :vote")
    long countCommentsByVote(@Param("vote") VoteComment vote);
    
    // Compter les likes pour les posts
    @Query("SELECT COUNT(p) FROM Post p WHERE p.likePost = :likeType")
    long countPostsByLikeType(@Param("likeType") LikePost likeType);
    
    // Compter les réactions pour les commentaires
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reaction = :reaction")
    long countCommentsByReaction(@Param("reaction") LikePost reaction);
    
    // Trouver les posts les plus likés
    @Query("SELECT p FROM Post p WHERE p.likePost = :likeType ORDER BY p.datePost DESC")
    List<Post> findPostsByLikeType(@Param("likeType") LikePost likeType);
    
    // Trouver les commentaires les plus votés
    @Query("SELECT c FROM Comment c WHERE c.voteComment = :vote ORDER BY c.dateComment DESC")
    List<Comment> findCommentsByVote(@Param("vote") VoteComment vote);
    
    // Statistiques des votes par post
    @Query("SELECT p.id, p.subject, COUNT(c) as commentCount, " +
           "SUM(CASE WHEN c.voteComment = 'UpVote' THEN 1 ELSE 0 END) as upVotes, " +
           "SUM(CASE WHEN c.voteComment = 'DownVote' THEN 1 ELSE 0 END) as downVotes " +
           "FROM Post p LEFT JOIN p.comments c " +
           "GROUP BY p.id, p.subject")
    List<Object[]> getPostVoteStatistics();
    
    // Statistiques des réactions par post
    @Query("SELECT p.id, p.subject, p.likePost, COUNT(c) as commentCount " +
           "FROM Post p LEFT JOIN p.comments c " +
           "GROUP BY p.id, p.subject, p.likePost")
    List<Object[]> getPostReactionStatistics();
}
