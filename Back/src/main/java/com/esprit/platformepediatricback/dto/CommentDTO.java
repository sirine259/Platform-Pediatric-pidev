package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    // Constructeur à partir de l'entité
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
        
        // Convertir les réponses en DTOs
        if (comment.getReponse() != null) {
            this.replies = comment.getReponse().stream()
                    .map(CommentDTO::new)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
    
    // Méthodes utilitaires
    public int getVoteScore() {
        if (voteComment == VoteComment.UpVote) return 1;
        if (voteComment == VoteComment.DownVote) return -1;
        return 0;
    }
    
    public boolean hasReaction() {
        return reaction != null;
    }
    
    public boolean hasVote() {
        return voteComment != null;
    }
    
    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }
}
