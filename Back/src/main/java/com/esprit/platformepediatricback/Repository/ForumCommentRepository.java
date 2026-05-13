package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    
    List<ForumComment> findByPost(Post post);
    List<ForumComment> findByPostAndIsDeletedFalseOrderByCreatedAtDesc(Post post);
    
    List<ForumComment> findByAuthor(User author);
    
    List<ForumComment> findByPostIdAndIsDeletedFalse(Long postId);
    
    List<ForumComment> findByAuthorAndIsDeletedFalse(User author);
    
    List<ForumComment> findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);
    
    List<ForumComment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentCommentId);
    
    Long countByPostIdAndIsDeletedFalse(Long postId);
    Long countByPostAndIsDeletedFalse(Post post);
    
    Long countByParentCommentIdAndIsDeletedFalse(Long parentCommentId);
    
    @Query("SELECT c FROM ForumComment c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    List<ForumComment> findAllActiveCommentsOrderByDate();
    
    @Query("SELECT c FROM ForumComment c WHERE c.isDeleted = false AND c.post = :post ORDER BY c.createdAt ASC")
    List<ForumComment> findActiveCommentsByPostOrderByDate(@Param("post") Post post);
    
    @Query("SELECT c FROM ForumComment c WHERE c.isDeleted = false AND c.content LIKE %:content%")
    List<ForumComment> findByContentContaining(@Param("content") String content);
    
    @Query("SELECT c FROM ForumComment c WHERE c.isDeleted = false AND c.createdAt BETWEEN :startDate AND :endDate")
    List<ForumComment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM ForumComment c WHERE c.isDeleted = false AND c.post = :post")
    long countActiveCommentsByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(c) FROM ForumComment c WHERE c.isDeleted = false AND c.author = :author")
    long countActiveCommentsByAuthor(@Param("author") User author);
    
    @Query("SELECT c FROM ForumComment c WHERE " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "c.isDeleted = false")
    List<ForumComment> searchComments(@Param("searchTerm") String searchTerm);
    
    List<ForumComment> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();
}
