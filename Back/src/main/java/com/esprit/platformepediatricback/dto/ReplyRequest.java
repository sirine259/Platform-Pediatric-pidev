package com.esprit.platformepediatricback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {
    
    @NotBlank(message = "Le contenu de la réponse ne peut pas être vide")
    @Size(min = 3, max = 500, message = "La réponse doit contenir entre 3 et 500 caractères")
    private String replyText;
    
    @NotNull(message = "L'ID du commentaire parent est requis")
    private Long parentCommentId;
    
    @NotNull(message = "L'ID de l'utilisateur est requis")
    private Long userId;
    
    private Boolean isAnonymous = false;
    
    // Validation
    public boolean isValidReplyText() {
        return replyText != null && 
               !replyText.trim().isEmpty() && 
               replyText.length() >= 3 && 
               replyText.length() <= 500;
    }
    
    public String getDisplayName() {
        if (isAnonymous != null && isAnonymous) {
            return "Anonyme";
        }
        return "Utilisateur";
    }
}
