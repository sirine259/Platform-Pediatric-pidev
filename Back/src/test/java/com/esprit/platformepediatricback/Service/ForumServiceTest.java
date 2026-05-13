package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.ForumCommentRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.User;
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
class ForumServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ForumCommentRepository forumCommentRepository;

    @InjectMocks
    private ForumService forumService;

    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.USER);

        testPost = new Post("Test Subject", "Test Content", false, testUser);
    }

    @Test
    void createPost_ShouldSaveAndReturnPost() {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = forumService.createPost(testPost);

        assertNotNull(result);
        assertEquals("Test Subject", result.getSubject());
        assertEquals(Post.PostType.FORUM, result.getPostType());
        assertFalse(result.getIsDeleted());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_ShouldSetTitleFromSubject_WhenTitleIsNull() {
        Post postWithoutTitle = new Post("Test Subject", "Test Content", false, testUser);
        postWithoutTitle.setTitle(null);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = forumService.createPost(postWithoutTitle);

        assertNotNull(result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPostById_ShouldReturnPost_WhenExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        Optional<Post> result = forumService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Subject", result.get().getSubject());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostById_ShouldReturnEmpty_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Post> result = forumService.getPostById(999L);

        assertFalse(result.isPresent());
        verify(postRepository, times(1)).findById(999L);
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByIsDeletedFalseOrderByCreatedAtDesc()).thenReturn(posts);

        List<Post> result = forumService.getAllPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByIsDeletedFalseOrderByCreatedAtDesc();
    }

    @Test
    void getAllPosts_ShouldReturnEmptyList_WhenNoPosts() {
        when(postRepository.findByIsDeletedFalseOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        List<Post> result = forumService.getAllPosts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updatePost_ShouldUpdateAndReturnPost_WhenExists() {
        Post updatedDetails = new Post("Updated Subject", "Updated Content", false, testUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = forumService.updatePost(1L, updatedDetails);

        assertNotNull(result);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_ShouldReturnNull_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Post result = forumService.updatePost(999L, testPost);

        assertNull(result);
        verify(postRepository, times(1)).findById(999L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldReturnTrue_WhenExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = forumService.deletePost(1L);

        assertTrue(result);
        assertTrue(testPost.getIsDeleted());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldReturnFalse_WhenNotExists() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = forumService.deletePost(999L);

        assertFalse(result);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void searchPostsByTitle_ShouldReturnMatchingPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.searchPostsByTitleOrSubject("Test")).thenReturn(posts);

        List<Post> result = forumService.searchPostsByTitle("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).searchPostsByTitleOrSubject("Test");
    }

    @Test
    void searchPostsByContent_ShouldReturnMatchingPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByContentContainingIgnoreCaseAndIsDeletedFalse("Test")).thenReturn(posts);

        List<Post> result = forumService.searchPostsByContent("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByContentContainingIgnoreCaseAndIsDeletedFalse("Test");
    }

    @Test
    void getPostCountByAuthor_ShouldReturnCount() {
        when(postRepository.countByAuthor(testUser)).thenReturn(5L);

        long result = forumService.getPostCountByAuthor(testUser);

        assertEquals(5L, result);
        verify(postRepository, times(1)).countByAuthor(testUser);
    }

    @Test
    void getCommentCountByPost_ShouldReturnCount() {
        when(forumCommentRepository.countByPostAndIsDeletedFalse(testPost)).thenReturn(3L);

        long result = forumService.getCommentCountByPost(testPost);

        assertEquals(3L, result);
        verify(forumCommentRepository, times(1)).countByPostAndIsDeletedFalse(testPost);
    }

    @Test
    void getCommentCountByAuthor_ShouldReturnCount() {
        when(forumCommentRepository.countActiveCommentsByAuthor(testUser)).thenReturn(10L);

        long result = forumService.getCommentCountByAuthor(testUser);

        assertEquals(10L, result);
        verify(forumCommentRepository, times(1)).countActiveCommentsByAuthor(testUser);
    }

    @Test
    void getPostsByDateRange_ShouldReturnPosts() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Post> posts = Arrays.asList(testPost);

        when(postRepository.findByCreatedAtBetweenAndIsDeletedFalse(startDate, endDate)).thenReturn(posts);

        List<Post> result = forumService.getPostsByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByCreatedAtBetweenAndIsDeletedFalse(startDate, endDate);
    }
}
