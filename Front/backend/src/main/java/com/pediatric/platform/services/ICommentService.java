package com.pediatric.platform.services;

import com.pediatric.platform.entities.Comment;
import com.pediatric.platform.entities.LikePost;

import java.util.List;

public interface ICommentService {
    List<Comment> getAll();
    Comment getById(Long id);
    Comment save(Comment comment);
    void delete(Long id);
    Comment updateComment(Long commentId, String newDescription);
    void updateCommentReaction(Long commentId, LikePost reaction);
    void updateReplyReaction(Long replyId, LikePost reaction);
}
