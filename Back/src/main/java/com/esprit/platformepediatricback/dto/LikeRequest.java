package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.LikePost;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    
    @NotNull(message = "L'ID du commentaire est requis")
    private Long commentId;
    
    @NotNull(message = "Le type de réaction est requis")
    private LikePost reactionType;
    
    // Validation
    public boolean isValidReactionType() {
        return reactionType != null && 
               isValidLikePost(reactionType);
    }
    
    private boolean isValidLikePost(LikePost reaction) {
        return reaction == LikePost.Like || 
               reaction == LikePost.Love || 
               reaction == LikePost.Support || 
               reaction == LikePost.Celebrate || 
               reaction == LikePost.Insightful || 
               reaction == LikePost.Funny || 
               reaction == LikePost.Dislike;
    }
    
    public String getReactionDisplayName() {
        if (reactionType != null) {
            switch (reactionType) {
                case Like: return "J'aime";
                case Love: return "J'adore";
                case Support: return "Je soutiens";
                case Celebrate: return "Je célèbre";
                case Insightful: return "Je trouve ça pertinent";
                case Funny: return "Je trouve ça drôle";
                case Dislike: return "Je n'aime pas";
                default: return reactionType.name();
            }
        }
        return "";
    }
}
