package com.pediatric.platform.services;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;
import com.pediatric.platform.entities.Post;

import java.util.List;

public interface IPostService {
    List<Post> getAll();
    Post getById(Long id);
    Post save(Post post);
    void delete(Long id);
    Post updatePost(Long id, Post postDetails);
    List<Post> searchPostsBySubject(String subject);
    Comment updateComment(Long commentId, Comment updatedComment);
    List<Post> searchPostsBySubjectC(String subject);
    Post likePost(Long postId, LikePost likeType);
}
