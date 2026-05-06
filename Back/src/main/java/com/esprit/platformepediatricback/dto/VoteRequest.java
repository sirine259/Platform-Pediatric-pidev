package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.VoteComment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    
    @NotNull(message = "L'ID du commentaire est requis")
    private Long commentId;
    
    @NotNull(message = "Le type de vote est requis")
    private VoteComment voteType;
    
    // Validation
    public boolean isValidVoteType() {
        return voteType != null && 
               (voteType == VoteComment.UpVote || voteType == VoteComment.DownVote);
    }
}
