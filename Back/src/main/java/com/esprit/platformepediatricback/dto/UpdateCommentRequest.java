package com.esprit.platformepediatricback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {
    
    @NotBlank(message = "Le contenu du commentaire ne peut pas être vide")
    @Size(min = 3, max = 1000, message = "Le commentaire doit contenir entre 3 et 1000 caractères")
    private String description;
    
    private Boolean isAnonymous;
    
    // Validation personnalisée
    public boolean isValidContent() {
        return description != null && 
               !description.trim().isEmpty() && 
               description.length() >= 3 && 
               description.length() <= 1000;
    }
    
    public String getDisplayName() {
        if (isAnonymous != null && isAnonymous) {
            return "Anonyme";
        }
        return "Utilisateur";
    }
}
