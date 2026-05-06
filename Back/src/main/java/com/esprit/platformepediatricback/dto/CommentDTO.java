package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Long id;
    private String description;
    private LocalDateTime dateComment;
    private VoteComment voteComment;
    private LikePost reaction;
    private Long postId;
    private Long userId;
    private String userName;
    private String userFirstName;
    private String userLastName;
    private Integer replyCount;
    private List<CommentDTO> replies;

    public CommentDTO() {}

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.description = comment.getDescription();
        this.dateComment = comment.getDateComment();
        this.voteComment = comment.getVoteComment();
        this.reaction = comment.getReaction();
        this.postId = comment.getPost() != null ? comment.getPost().getId() : null;
        this.userId = comment.getUser() != null ? comment.getUser().getId() : null;
        this.userName = comment.getUser() != null ? comment.getUser().getUsername() : null;
        this.userFirstName = comment.getUser() != null ? comment.getUser().getFirstName() : null;
        this.userLastName = comment.getUser() != null ? comment.getUser().getLastName() : null;
        this.replyCount = comment.getReponse() != null ? comment.getReponse().size() : 0;

        if (comment.getReponse() != null) {
            this.replies = comment.getReponse().stream()
                    .map(CommentDTO::new)
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateComment() { return dateComment; }
    public void setDateComment(LocalDateTime dateComment) { this.dateComment = dateComment; }
    public VoteComment getVoteComment() { return voteComment; }
    public void setVoteComment(VoteComment voteComment) { this.voteComment = voteComment; }
    public LikePost getReaction() { return reaction; }
    public void setReaction(LikePost reaction) { this.reaction = reaction; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }

    public int getVoteScore() {
        if (voteComment == VoteComment.UpVote) return 1;
        if (voteComment == VoteComment.DownVote) return -1;
        return 0;
    }

    public boolean hasReaction() { return reaction != null; }
    public boolean hasVote() { return voteComment != null; }
    public boolean hasReplies() { return replies != null && !replies.isEmpty(); }
}
