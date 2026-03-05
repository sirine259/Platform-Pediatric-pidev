package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find by type
    List<Post> findByPostType(Post.PostType postType);
    List<Post> findByPostTypeAndIsDeletedFalseOrderByCreatedAtDesc(Post.PostType postType);
    
    // Rechercher des posts par sujet (système PIDEV)
    @Query("SELECT p FROM Post p WHERE p.subject LIKE :subject")
    List<Post> searchPostsBySubject(@Param("subject") String subject);
    
    // Rechercher des posts par sujet (case insensitive)
    @Query("SELECT p FROM Post p WHERE LOWER(p.subject) LIKE LOWER(CONCAT('%', :subject, '%'))")
    List<Post> searchPostsBySubjectC(@Param("subject") String subject);
    
    // Trouver les posts par utilisateur
    List<Post> findByUserId(Long userId);
    
    // Trouver les posts par statut
    List<Post> findByStatus(String status);
    
    // Compter les posts par statut
    long countByStatus(String status);
    
    // Compter les posts par utilisateur
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Trouver les posts récents
    @Query("SELECT p FROM Post p ORDER BY p.datePost DESC")
    List<Post> findRecentPosts();
    
    // Rechercher dans le contenu et le sujet
    @Query("SELECT p FROM Post p WHERE p.subject LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
    
    // Compter les posts par type de like
    @Query("SELECT COUNT(p) FROM Post p WHERE p.likePost = :likeType")
    long countByLikeType(@Param("likeType") LikePost likeType);
    
    // Trouver les posts par type de like
    @Query("SELECT p FROM Post p WHERE p.likePost = :likeType ORDER BY p.datePost DESC")
    List<Post> findByLikeType(@Param("likeType") LikePost likeType);
    
    // Find posts by user and not deleted
    List<Post> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);
    
    // Find posts by type and user
    List<Post> findByPostTypeAndUserAndIsDeletedFalseOrderByCreatedAtDesc(Post.PostType postType, User user);
    
    // Find recent posts by type
    List<Post> findTop10ByPostTypeAndIsDeletedFalseOrderByCreatedAtDesc(Post.PostType postType);
    
    // Count posts by type
    @Query("SELECT COUNT(p) FROM Post p WHERE p.postType = :postType AND p.isDeleted = false")
    long countByPostType(@Param("postType") Post.PostType postType);
    
    // Search posts by content
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:content% AND p.isDeleted = false")
    List<Post> searchByContent(@Param("content") String content);
    
    // Find posts by date range
    @Query("SELECT p FROM Post p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.isDeleted = false")
    List<Post> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find by author
    List<Post> findByAuthor(User author);
    List<Post> findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(User author);
    List<Post> findByAuthorAndIsDeletedFalse(User author);
    
    // Find by title/content
    List<Post> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);
    List<Post> findByContentContainingIgnoreCaseAndIsDeletedFalse(String content);
    
    // Find by date range
    List<Post> findByCreatedAtBetweenAndIsDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find all non-deleted posts
    List<Post> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // Find by related entities
    List<Post> findByForum(com.esprit.platformepediatricback.entity.Forum forum);
    List<Post> findByTransplant(com.esprit.platformepediatricback.entity.Transplant transplant);
    
    // Find public posts
    List<Post> findByIsPublicTrueAndIsDeletedFalseOrderByCreatedAtDesc();
    
    // Search operations
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND (" +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) OR " +
           "LOWER(p.subject) LIKE LOWER(CONCAT('%', :title, '%'))" +
           ")")
    List<Post> searchPostsByTitleOrSubject(@Param("title") String title);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchPosts(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.postType = :type AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchPostsByType(@Param("type") Post.PostType type, @Param("keyword") String keyword);
    
    // Forum specific queries
    @Query("SELECT p FROM Post p WHERE p.postType = 'FORUM' AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findAllForumPostsOrderByDate();
    
    @Query("SELECT p FROM Post p WHERE p.postType = 'FORUM' AND p.isDeleted = false AND p.author = :author ORDER BY p.createdAt DESC")
    List<Post> findForumPostsByAuthor(@Param("author") User author);
    
    // Medical specific queries
    @Query("SELECT p FROM Post p WHERE p.postType IN ('MEDICAL_UPDATE', 'FOLLOW_UP', 'ANNOUNCEMENT') AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findAllMedicalPostsOrderByDate();
    
    @Query("SELECT p FROM Post p WHERE p.postType IN ('MEDICAL_UPDATE', 'FOLLOW_UP', 'ANNOUNCEMENT') AND p.isDeleted = false AND p.author = :author ORDER BY p.createdAt DESC")
    List<Post> findMedicalPostsByAuthor(@Param("author") User author);
    
    // Transplant related queries
    @Query("SELECT p FROM Post p WHERE p.transplant = :transplant AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findPostsByTransplant(@Param("transplant") com.esprit.platformepediatricback.entity.Transplant transplant);
    
    // Statistics
    @Query("SELECT COUNT(p) FROM Post p WHERE p.author = :author AND p.isDeleted = false")
    Long countByAuthor(@Param("author") User author);
    
    // Popular posts
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Post> findPopularPosts();
    
    @Query("SELECT p FROM Post p WHERE p.postType = 'FORUM' AND p.isDeleted = false ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Post> findPopularForumPosts();
    
    @Query("SELECT p FROM Post p WHERE p.postType IN ('MEDICAL_UPDATE', 'FOLLOW_UP', 'ANNOUNCEMENT') AND p.isDeleted = false ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Post> findPopularMedicalPosts();
    
    // Méthodes de statistiques pour remplacer VoteRepository
    @Query("SELECT COUNT(p) FROM Post p WHERE p.likePost = :likeType")
    long countPostsByLikeType(@Param("likeType") LikePost likeType);
    
    // Méthodes de statistiques supplémentaires
    @Query("SELECT COUNT(p) FROM Post p WHERE p.datePost >= :startDate AND p.datePost < :endDate")
    long countPostsToday(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.datePost >= :startDate AND p.datePost <= :endDate")
    long countPostsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT p.id, p.subject, COUNT(c) as commentCount, " +
            "p.likePost, " +
            "(CASE WHEN p.likePost IS NOT NULL THEN 5 ELSE 0 END + " +
            "(COUNT(c) * 2) + " +
            "(SELECT COUNT(cr) FROM Comment cr WHERE cr.post.id = p.id AND cr.reaction IS NOT NULL) " +
            ") as popularityScore " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.post.id " +
            "WHERE p.status = 'Approved' " +
            "GROUP BY p.id, p.subject, p.likePost " +
            "ORDER BY popularityScore DESC")
    List<Object[]> findMostPopularPosts();
    
    @Query("SELECT p.subject, COUNT(p) as count " +
           "FROM Post p " +
           "WHERE p.status = 'Approved' " +
           "GROUP BY p.subject " +
           "ORDER BY count DESC")
    List<Object[]> findTrendingTopics();
    
    // Recent activity
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.datePost >= :since ORDER BY p.datePost DESC")
    List<Post> findRecentPosts(@Param("since") LocalDateTime since);
    
    // Méthodes de statistiques de likes supplémentaires
    @Query("SELECT COUNT(p) FROM Post p WHERE p.likePost IS NOT NULL")
    long countTotalLikes();
    
    @Query("SELECT p FROM Post p WHERE p.likePost = :likeType ORDER BY p.datePost DESC")
    List<Post> findPostsByLikeType(@Param("likeType") LikePost likeType);
    
    @Query("SELECT p FROM Post p WHERE p.likePost IS NOT NULL ORDER BY p.datePost DESC")
    List<Post> findMostLikedPosts();
    
    @Query("SELECT p FROM Post p WHERE p.likePost IS NULL ORDER BY p.datePost DESC")
    List<Post> findPostsWithoutLikes();
}
