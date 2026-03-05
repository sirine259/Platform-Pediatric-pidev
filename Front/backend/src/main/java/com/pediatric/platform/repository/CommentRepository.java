package com.pediatric.platform.repository;

import com.pediatric.platform.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
