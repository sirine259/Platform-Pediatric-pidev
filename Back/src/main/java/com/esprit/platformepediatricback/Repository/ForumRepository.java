package com.esprit.platformepediatricback.repository;

import com.esprit.platformepediatricback.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    
    // Find by theme (unique)
    Optional<Forum> findByTheme(String theme);
    
    // Find all active forums
    List<Forum> findByIsActiveTrue();
    
    // Find all public forums
    List<Forum> findByIsPrivateFalse();
    
    // Find all private forums
    List<Forum> findByIsPrivateTrue();
    
    // Find forums by moderator
    List<Forum> findByModeratorId(Long moderatorId);
    
    // Search forums by theme or description
    @Query("SELECT f FROM Forum f WHERE " +
           "LOWER(f.theme) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Forum> searchForums(@Param("searchTerm") String searchTerm);
    
    // Count posts in a forum
    @Query("SELECT COUNT(p) FROM Post p WHERE p.forum.id = :forumId AND p.isDeleted = false")
    Long countPostsInForum(@Param("forumId") Long forumId);
    
    // Get forums with most posts
    @Query("SELECT f, COUNT(p) as postCount FROM Forum f " +
           "LEFT JOIN f.posts p " +
           "WHERE p.isDeleted = false OR p IS NULL " +
           "GROUP BY f.id " +
           "ORDER BY postCount DESC")
    List<Object[]> getForumsWithPostCount();
    
    // Check if user is moderator of forum
    boolean existsByModeratorIdAndId(Long moderatorId, Long forumId);
}
