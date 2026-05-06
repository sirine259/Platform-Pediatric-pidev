package com.esprit.platformepediatricback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyRequest {

    @NotBlank(message = "Le contenu de la reponse ne peut pas etre vide")
    @Size(min = 3, max = 500, message = "La reponse doit contenir entre 3 et 500 caracteres")
    private String replyText;

    @NotNull(message = "L'ID du commentaire parent est requis")
    private Long parentCommentId;

    @NotNull(message = "L'ID de l'utilisateur est requis")
    private Long userId;

    private Boolean isAnonymous = false;

    public ReplyRequest() {}

    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }
    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public boolean isValidReplyText() {
        return replyText != null && !replyText.trim().isEmpty() && replyText.length() >= 3 && replyText.length() <= 500;
    }

    public String getDisplayName() {
        if (isAnonymous != null && isAnonymous) return "Anonyme";
        return "Utilisateur";
    }
}
