package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRatingRepository;
import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.UserRepository;
import com.esprit.platformepediatricback.entity.PostRating;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PostRatingService {
    
    @Autowired
    private PostRatingRepository postRatingRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public PostRating ratePost(Long postId, Long userId, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        Optional<PostRating> existingRating = postRatingRepository.findByPostAndUser(post, user);
        
        if (existingRating.isPresent()) {
            PostRating postRating = existingRating.get();
            postRating.setRating(rating);
            return postRatingRepository.save(postRating);
        } else {
            PostRating newRating = new PostRating(post, user, rating);
            return postRatingRepository.save(newRating);
        }
    }

    public PostRating ratePostByAuthenticatedUser(Long postId, String username, Integer rating) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur authentifié introuvable"));
        return ratePost(postId, user.getId(), rating);
    }

    
    public Optional<PostRating> getUserRating(Long postId, Long userId) {
        return postRatingRepository.findByPostIdAndUserId(postId, userId);
    }

    public Optional<PostRating> getUserRatingByUsername(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur authentifié introuvable"));
        return getUserRating(postId, user.getId());
    }

    
    public Map<String, Object> getPostRatingSummary(Long postId) {
        Map<String, Object> summary = new HashMap<>();
        
        Double averageRating = postRatingRepository.getAverageRatingByPostId(postId);
        Long totalRatings = postRatingRepository.countByPostId(postId);
        
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, postRatingRepository.countByPostIdAndRating(postId, i));
        }
        
        summary.put("postId", postId);
        summary.put("averageRating", averageRating != null ? averageRating : 0.0);
        summary.put("totalRatings", totalRatings != null ? totalRatings : 0L);
        summary.put("ratingDistribution", distribution);
        
        return summary;
    }
    
    public void deleteRating(Long postId, Long userId) {
        postRatingRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public void deleteRatingByUsername(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur authentifié introuvable"));
        deleteRating(postId, user.getId());
    }

    
    public List<Map<String, Object>> getTopRatedPosts(int limit) {
        List<Object[]> results = postRatingRepository.findTopRatedPostsWithCount();
        return results.stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("postId", row[0]);
                    item.put("averageRating", row[1]);
                    item.put("totalRatings", row[2]);
                    return item;
                })
                .toList();
    }
    
    public boolean hasUserRated(Long postId, Long userId) {
        return postRatingRepository.existsByPostIdAndUserId(postId, userId);
    }

}