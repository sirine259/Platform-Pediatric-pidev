package com.esprit.platformepediatricback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCommentRequest {

    @NotBlank(message = "Le contenu du commentaire ne peut pas etre vide")
    @Size(min = 3, max = 1000, message = "Le commentaire doit contenir entre 3 et 1000 caracteres")
    private String description;

    private Boolean isAnonymous;

    public UpdateCommentRequest() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public boolean isValidContent() {
        return description != null && !description.trim().isEmpty() && description.length() >= 3 && description.length() <= 1000;
    }

    public String getDisplayName() {
        if (isAnonymous != null && isAnonymous) return "Anonyme";
        return "Utilisateur";
    }
}
