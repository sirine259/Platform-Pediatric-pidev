package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.TransplantRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.Post.PostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private TransplantRepository transplantRepository;
    
    @Autowired
    private UserService userService;

    // CRUD Operations
    public Post createPost(Post post) {
        // Validate author exists
        if (post.getAuthor() == null || post.getAuthor().getId() == null) {
            throw new RuntimeException("Author is required");
        }
        
        User author = userService.getUserById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        
        post.setAuthor(author);
        
        // Validate related entities based on post type
        if (post.getPostType() == PostType.FORUM && post.getForumPost() != null) {
            // Forum post logic
            post.setTransplant(null);
            post.setFollowUp(null);
        } else if (post.getPostType() == PostType.FOLLOW_UP && post.getFollowUp() != null) {
            // Follow-up post logic
            post.setForumPost(null);
            post.setTransplant(((Transplant) post.getFollowUp()).getTransplant());
        } else if (post.getPostType() == PostType.MEDICAL_UPDATE && post.getTransplant() != null) {
            // Medical update post logic
            post.setForumPost(null);
            post.setFollowUp(null);
            post.setTransplant(post.getTransplant());
        }
        
        return postRepository.save(post);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Update fields
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setPostType(postDetails.getPostType());
        post.setIsPublic(postDetails.getIsPublic());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    // Forum Operations
    public List<Post> getAllForumPosts() {
        return postRepository.findAllForumPostsOrderByDate();
    }

    public List<Post> getForumPostsByAuthor(Long authorId) {
        User author = new User();
        author.setId(authorId);
        return postRepository.findForumPostsByAuthor(author);
    }

    public Post createForumPost(Post post, User author) {
        post.setPostType(PostType.FORUM);
        post.setAuthor(author);
        post.setIsPublic(true);
        return createPost(post);
    }

    // Medical Operations
    public List<Post> getAllMedicalPosts() {
        return postRepository.findAllMedicalPostsOrderByDate();
    }

    public List<Post> getMedicalPostsByAuthor(Long authorId) {
        User author = new User();
        author.setId(authorId);
        return postRepository.findMedicalPostsByAuthor(author);
    }

    public Post createMedicalUpdate(Post post, User author, Transplant transplant) {
        post.setPostType(PostType.MEDICAL_UPDATE);
        post.setAuthor(author);
        post.setTransplant(transplant);
        post.setIsPublic(false); // Medical posts are private by default
        return createPost(post);
    }

    public Post createFollowUpPost(Post post, User author, Transplant transplant) {
        post.setPostType(PostType.FOLLOW_UP);
        post.setAuthor(author);
        post.setTransplant(transplant);
        post.setIsPublic(false);
        return createPost(post);
    }

    // Search Operations
    public List<Post> searchAllPosts(String keyword) {
        return postRepository.searchPosts(keyword);
    }

    public List<Post> searchForumPosts(String keyword) {
        return postRepository.searchPostsByType(PostType.FORUM, keyword);
    }

    public List<Post> searchMedicalPosts(String keyword) {
        return postRepository.searchPostsByType(PostType.MEDICAL_UPDATE, keyword);
    }

    // Transplant Related Operations
    public List<Post> getPostsByTransplant(Long transplantId) {
        Transplant transplant = new Transplant();
        transplant.setId(transplantId);
        return postRepository.findPostsByTransplant(transplant);
    }

    // Statistics Operations
    public Long countPostsByType(PostType postType) {
        return postRepository.countByPostType(postType);
    }

    public Long countPostsByAuthor(Long authorId) {
        User author = new User();
        author.setId(authorId);
        return postRepository.countByAuthor(author);
    }

    // Popular Posts
    public List<Post> getPopularPosts() {
        return postRepository.findPopularPosts();
    }

    public List<Post> getPopularForumPosts() {
        return postRepository.findPopularForumPosts();
    }

    public List<Post> getPopularMedicalPosts() {
        return postRepository.findPopularMedicalPosts();
    }

    // Recent Activity
    public List<Post> getRecentPosts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return postRepository.findRecentPosts(since);
    }

    // Engagement Operations
    public Post incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    public Post incrementLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }

    public Post decrementLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            return postRepository.save(post);
        }
        return post;
    }

    // Helper Methods
    public boolean isPostAccessible(Long postId, User user) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return false;
        }
        
        Post p = post.get();
        
        // Public posts are accessible to everyone
        if (p.getIsPublic()) {
            return true;
        }
        
        // Private posts are accessible to author and admins
        return p.getAuthor().getId().equals(user.getId()) || 
               user.getRole() == User.Role.ADMIN;
    }

    public boolean canEditPost(Long postId, User user) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return false;
        }
        
        Post p = post.get();
        
        // Authors and admins can edit
        return p.getAuthor().getId().equals(user.getId()) || 
               user.getRole() == User.Role.ADMIN;
    }

    public boolean canDeletePost(Long postId, User user) {
        return canEditPost(postId, user);
    }
}
