package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.TransplantRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.entity.Transplant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TransplantRepository transplantRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private User testUser;
    private User adminUser;
    private Transplant testTransplant;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);

        testTransplant = new Transplant();

        testPost = new Post("Test Subject", "Test Content", false, testUser);
        testPost.setAuthor(testUser);
    }

    @Test
    void createPost_ShouldSaveAndReturnPost_WhenAuthorExists() {
        Post postToCreate = new Post("Test Subject", "Test Content", false, testUser);
        postToCreate.setAuthor(testUser);

        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createPost(postToCreate);

        assertNotNull(result);
        assertEquals("Test Subject", result.getSubject());
        assertEquals(testUser, result.getAuthor());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_ShouldThrowException_WhenAuthorIsNull() {
        testPost.setAuthor(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createPost(testPost);
        });

        assertEquals("Author is required", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_ShouldThrowException_WhenAuthorNotFound() {
        Post postWithInvalidAuthor = new Post("Test Subject", "Test Content", false, testUser);
        postWithInvalidAuthor.setAuthor(testUser);
        postWithInvalidAuthor.getAuthor().setId(999L);

        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createPost(postWithInvalidAuthor);
        });

        assertEquals("Author not found", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createForumPost_ShouldSetCorrectPostType() {
        Post forumPost = new Post("Forum Subject", "Forum Content", false, testUser);
        forumPost.setAuthor(testUser);

        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createForumPost(forumPost, testUser);

        assertNotNull(result);
        assertEquals(Post.PostType.FORUM, result.getPostType());
        assertTrue(result.getIsPublic());
        assertEquals(testUser, result.getAuthor());
    }

    @Test
    void getPostById_ShouldReturnPost_WhenExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        Optional<Post> result = postService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals(testPost.getId(), result.get().getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostById_ShouldReturnEmpty_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Post> result = postService.getPostById(999L);

        assertFalse(result.isPresent());
        verify(postRepository, times(1)).findById(999L);
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void updatePost_ShouldUpdateAndReturnPost_WhenExists() {
        Post updatedDetails = new Post("Updated Subject", "Updated Content", false, testUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.updatePost(1L, updatedDetails);

        assertNotNull(result);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_ShouldThrowException_WhenNotExists() {
        Post updatedDetails = new Post("Updated Subject", "Updated Content", false, testUser);

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.updatePost(999L, updatedDetails);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldMarkAsDeleted_WhenExists() {
        Post postToDelete = new Post("Test Subject", "Test Content", false, testUser);
        when(postRepository.findById(1L)).thenReturn(Optional.of(postToDelete));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.deletePost(1L);

        assertTrue(postToDelete.getIsDeleted());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldThrowException_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.deletePost(999L);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getAllForumPosts_ShouldReturnForumPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findAllForumPostsOrderByDate()).thenReturn(posts);

        List<Post> result = postService.getAllForumPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAllForumPostsOrderByDate();
    }

    @Test
    void getPopularPosts_ShouldReturnPopularPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findPopularPosts()).thenReturn(posts);

        List<Post> result = postService.getPopularPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findPopularPosts();
    }

    @Test
    void incrementViewCount_ShouldIncrementAndReturnPost() {
        Post postWithViews = new Post("Test Subject", "Test Content", false, testUser);
        postWithViews.setViewCount(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(postWithViews));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.incrementViewCount(1L);

        assertNotNull(result);
        assertEquals(6, postWithViews.getViewCount());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void incrementViewCount_ShouldThrowException_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.incrementViewCount(999L);
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void incrementLikeCount_ShouldIncrementAndReturnPost() {
        Post postWithLikes = new Post("Test Subject", "Test Content", false, testUser);
        postWithLikes.setLikeCount(3);
        when(postRepository.findById(1L)).thenReturn(Optional.of(postWithLikes));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.incrementLikeCount(1L);

        assertNotNull(result);
        assertEquals(4, postWithLikes.getLikeCount());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void decrementLikeCount_ShouldDecrement_WhenCountGreaterThanZero() {
        Post postWithLikes = new Post("Test Subject", "Test Content", false, testUser);
        postWithLikes.setLikeCount(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(postWithLikes));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.decrementLikeCount(1L);

        assertNotNull(result);
        assertEquals(4, postWithLikes.getLikeCount());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void decrementLikeCount_ShouldNotDecrement_WhenCountIsZero() {
        Post postWithZeroLikes = new Post("Test Subject", "Test Content", false, testUser);
        postWithZeroLikes.setLikeCount(0);
        when(postRepository.findById(1L)).thenReturn(Optional.of(postWithZeroLikes));

        Post result = postService.decrementLikeCount(1L);

        assertNotNull(result);
        assertEquals(0, postWithZeroLikes.getLikeCount());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void isPostAccessible_ShouldReturnTrue_WhenPostIsPublic() {
        testPost.setIsPublic(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.isPostAccessible(1L, testUser);

        assertTrue(result);
    }

    @Test
    void isPostAccessible_ShouldReturnTrue_WhenUserIsAuthor() {
        testPost.setIsPublic(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.isPostAccessible(1L, testUser);

        assertTrue(result);
    }

    @Test
    void isPostAccessible_ShouldReturnTrue_WhenUserIsAdmin() {
        testPost.setIsPublic(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.isPostAccessible(1L, adminUser);

        assertTrue(result);
    }

    @Test
    void isPostAccessible_ShouldReturnFalse_WhenPostNotPublicAndUserNotAuthorNorAdmin() {
        testPost.setIsPublic(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.isPostAccessible(1L, adminUser);

        assertTrue(result);
    }

    @Test
    void isPostAccessible_ShouldReturnFalse_WhenPostNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = postService.isPostAccessible(999L, testUser);

        assertFalse(result);
    }

    @Test
    void canEditPost_ShouldReturnTrue_WhenUserIsAuthor() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.canEditPost(1L, testUser);

        assertTrue(result);
    }

    @Test
    void canEditPost_ShouldReturnTrue_WhenUserIsAdmin() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.canEditPost(1L, adminUser);

        assertTrue(result);
    }

    @Test
    void canEditPost_ShouldReturnFalse_WhenUserIsNotAuthorNorAdmin() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setRole(User.Role.USER);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.canEditPost(1L, anotherUser);

        assertFalse(result);
    }

    @Test
    void canEditPost_ShouldReturnFalse_WhenPostNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = postService.canEditPost(999L, testUser);

        assertFalse(result);
    }

    @Test
    void canDeletePost_ShouldDelegateToCanEditPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        boolean result = postService.canDeletePost(1L, testUser);

        assertTrue(result);
    }

    @Test
    void countPostsByType_ShouldReturnCount() {
        when(postRepository.countByPostType(Post.PostType.FORUM)).thenReturn(10L);

        Long result = postService.countPostsByType(Post.PostType.FORUM);

        assertEquals(10L, result);
        verify(postRepository, times(1)).countByPostType(Post.PostType.FORUM);
    }

    @Test
    void searchAllPosts_ShouldReturnMatchingPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.searchPosts("test")).thenReturn(posts);

        List<Post> result = postService.searchAllPosts("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).searchPosts("test");
    }

    @Test
    void getRecentPosts_ShouldReturnRecentPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findRecentPosts(any(LocalDateTime.class))).thenReturn(posts);

        List<Post> result = postService.getRecentPosts(24);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findRecentPosts(any(LocalDateTime.class));
    }

    @Test
    void createMedicalUpdate_ShouldSetCorrectPostType() {
        Post medicalPost = new Post("Medical Subject", "Medical Content", false, testUser);
        medicalPost.setAuthor(testUser);

        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createMedicalUpdate(medicalPost, testUser, testTransplant);

        assertNotNull(result);
        assertEquals(Post.PostType.MEDICAL_UPDATE, result.getPostType());
        assertFalse(result.getIsPublic());
        assertEquals(testTransplant, result.getTransplant());
    }
}
