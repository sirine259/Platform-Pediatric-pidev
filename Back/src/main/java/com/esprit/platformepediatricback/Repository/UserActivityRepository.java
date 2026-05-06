package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<User, Long> {
    
    // Compter les posts par utilisateur
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countPostsByUserId(@Param("userId") Long userId);
    
    // Compter les commentaires par utilisateur
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);
    
    // Obtenir les utilisateurs les plus actifs
    @Query("SELECT u.id, u.username, u.firstName, u.lastName, " +
           "(SELECT COUNT(p) FROM Post p WHERE p.user.id = u.id) as postCount, " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.user.id = u.id) as commentCount " +
           "FROM User u " +
           "ORDER BY ((SELECT COUNT(p) FROM Post p WHERE p.user.id = u.id) + " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.user.id = u.id)) DESC")
    List<Object[]> findMostActiveUsers();
    
    // Obtenir les utilisateurs par rôle
    List<User> findByRole(String role);
    
    // Obtenir les utilisateurs par nom
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);
    
    // Compter les utilisateurs par rôle
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
    
    // Obtenir les utilisateurs récemment actifs
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since")
    List<User> findRecentlyActive(@Param("since") LocalDateTime since);
    
    // Obtenir les statistiques d'activité pour un utilisateur
    @Query("SELECT " +
           "(SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId) as postCount, " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId) as commentCount, " +
           "(SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId AND p.datePost >= :startDate) as recentPosts, " +
           "(SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId AND c.dateComment >= :startDate) as recentComments " +
           "FROM User u WHERE u.id = :userId")
    Object[] getUserActivityStats(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
