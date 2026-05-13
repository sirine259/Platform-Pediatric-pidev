package com.esprit.platformepediatricback.Repository;

import com.esprit.platformepediatricback.entity.PostRating;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRatingRepository extends JpaRepository<PostRating, Long> {
    
    Optional<PostRating> findByPostAndUser(Post post, User user);
    
    Optional<PostRating> findByPostIdAndUserId(Long postId, Long userId);
    
    boolean existsByPostAndUser(Post post, User user);
    
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    void deleteByPostAndUser(Post post, User user);
    
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    @Query("SELECT pr FROM PostRating pr WHERE pr.post.id = :postId")
    List<PostRating> findByPostId(@Param("postId") Long postId);
    
    @Query("SELECT AVG(pr.rating) FROM PostRating pr WHERE pr.post.id = :postId")
    Double getAverageRatingByPostId(@Param("postId") Long postId);
    
    @Query("SELECT COUNT(pr) FROM PostRating pr WHERE pr.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
    
    @Query("SELECT COUNT(pr) FROM PostRating pr WHERE pr.post.id = :postId AND pr.rating = :rating")
    Long countByPostIdAndRating(@Param("postId") Long postId, @Param("rating") Integer rating);
    
    @Query("SELECT pr.rating, COUNT(pr) FROM PostRating pr WHERE pr.post.id = :postId GROUP BY pr.rating")
    List<Object[]> getRatingDistribution(@Param("postId") Long postId);
    
    @Query("SELECT pr.post.id, AVG(pr.rating) FROM PostRating pr GROUP BY pr.post.id ORDER BY AVG(pr.rating) DESC")
    List<Object[]> findTopRatedPosts();
    
    @Query("SELECT pr.post.id, AVG(pr.rating), COUNT(pr) FROM PostRating pr GROUP BY pr.post.id ORDER BY AVG(pr.rating) DESC")
    List<Object[]> findTopRatedPostsWithCount();
}
