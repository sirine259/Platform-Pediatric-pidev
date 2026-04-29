package com.esprit.platformepediatricback.dto;

import com.esprit.platformepediatricback.entity.StatusComplaint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    
    @NotBlank(message = "Le sujet ne peut pas être vide")
    @Size(min = 5, max = 200, message = "Le sujet doit contenir entre 5 et 200 caractères")
    private String subject;
    
    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(min = 10, max = 2000, message = "Le contenu doit contenir entre 10 et 2000 caractères")
    private String content;
    
    private String picture;
    private Boolean isAnonymous;
    private StatusComplaint status;
    private String archivedReason;
    
    // Validation
    public boolean isValidContent() {
        return subject != null && !subject.trim().isEmpty() && 
               content != null && !content.trim().isEmpty() &&
               subject.length() >= 5 && subject.length() <= 200 &&
               content.length() >= 10 && content.length() <= 2000;
    }
    
    public boolean isArchived() {
        return status == StatusComplaint.Archived;
    }
    
    public boolean hasArchivedReason() {
        return isArchived() && 
               archivedReason != null && 
               !archivedReason.trim().isEmpty();
    }
    
    public String getDisplayName() {
        if (isAnonymous != null && isAnonymous) {
            return "Anonyme";
        }
        return "Utilisateur";
    }
}
