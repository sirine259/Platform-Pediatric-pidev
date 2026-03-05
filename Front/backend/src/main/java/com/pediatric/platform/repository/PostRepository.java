package com.pediatric.platform.repository;

import com.pediatric.platform.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Rechercher des posts par sujet
    @Query("SELECT p FROM Post p WHERE p.subject LIKE :subject")
    List<Post> searchPostsBySubject(@Param("subject") String subject);
    
    // Rechercher des posts par sujet (case insensitive)
    @Query("SELECT p FROM Post p WHERE LOWER(p.subject) LIKE LOWER(CONCAT('%', :subject, '%'))")
    List<Post> searchPostsBySubjectC(@Param("subject") String subject);
    
    // Trouver les posts par utilisateur
    List<Post> findByUserId(Long userId);
    
    // Trouver les posts par statut
    List<Post> findByStatus(String status);
    
    // Compter les posts par utilisateur
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Trouver les posts récents
    @Query("SELECT p FROM Post p ORDER BY p.datePost DESC")
    List<Post> findRecentPosts();
    
    // Rechercher dans le contenu et le sujet
    @Query("SELECT p FROM Post p WHERE p.subject LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
}
