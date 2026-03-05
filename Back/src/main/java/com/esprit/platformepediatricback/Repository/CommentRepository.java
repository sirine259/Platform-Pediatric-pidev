package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Trouver les commentaires par post
    List<Comment> findByPostId(Long postId);
    
    // Trouver les commentaires par utilisateur
    List<Comment> findByUserId(Long userId);
    
    // Compter les commentaires par post
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);
    
    // Rechercher des commentaires par contenu
    @Query("SELECT c FROM Comment c WHERE c.description LIKE %:keyword%")
    List<Comment> searchByContent(@Param("keyword") String keyword);
    
    // Trouver les réponses d'un commentaire
    List<Comment> findByPostIdAndIdIn(Long postId, List<Long> commentIds);
    
    // Compter les commentaires par type de vote
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.voteComment = :vote")
    long countByVoteType(@Param("vote") VoteComment vote);
    
    // Compter les commentaires par type de réaction
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reaction = :reaction")
    long countByReactionType(@Param("reaction") LikePost reaction);
    
    // Trouver les commentaires par type de vote
    @Query("SELECT c FROM Comment c WHERE c.voteComment = :vote ORDER BY c.dateComment DESC")
    List<Comment> findByVoteType(@Param("vote") VoteComment vote);
    
    // Trouver les commentaires par type de réaction
    @Query("SELECT c FROM Comment c WHERE c.reaction = :reaction ORDER BY c.dateComment DESC")
    List<Comment> findByReactionType(@Param("reaction") LikePost reaction);
    
    // Trouver les commentaires récents
    @Query("SELECT c FROM Comment c ORDER BY c.dateComment DESC")
    List<Comment> findRecentComments();
    
    // Trouver les commentaires par plage de dates
    @Query("SELECT c FROM Comment c WHERE c.dateComment BETWEEN :startDate AND :endDate")
    List<Comment> findByDateCommentBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Compter les commentaires par utilisateur
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Obtenir les commentaires avec leurs réponses
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.reponse r WHERE c.post.id = :postId")
    List<Comment> findCommentsWithRepliesByPostId(@Param("postId") Long postId);
    
    // Obtenir les commentaires les plus votés
    @Query("SELECT c FROM Comment c WHERE c.voteComment IS NOT NULL ORDER BY c.dateComment DESC")
    List<Comment> findMostVotedComments();
    
    // Méthodes de statistiques pour remplacer VoteRepository
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.voteComment = :vote")
    long countCommentsByVote(@Param("vote") VoteComment vote);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reaction = :reaction")
    long countCommentsByReaction(@Param("reaction") LikePost reaction);
    
    // Méthodes de statistiques supplémentaires
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.dateComment >= :startDate AND c.dateComment < :endDate")
    long countCommentsToday(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.dateComment >= :startDate AND c.dateComment <= :endDate")
    long countCommentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);
    
    // Méthodes de statistiques de réactions supplémentaires
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reaction IS NOT NULL")
    long countTotalReactions();
    
    @Query("SELECT c FROM Comment c WHERE c.reaction = :reaction ORDER BY c.dateComment DESC")
    List<Comment> findCommentsByReaction(@Param("reaction") LikePost reaction);
    
    @Query("SELECT c FROM Comment c WHERE c.reaction IS NOT NULL ORDER BY c.dateComment DESC")
    List<Comment> findMostReactedComments();
    
    @Query("SELECT c FROM Comment c WHERE c.reaction IS NULL ORDER BY c.dateComment DESC")
    List<Comment> findCommentsWithoutReactions();
}
