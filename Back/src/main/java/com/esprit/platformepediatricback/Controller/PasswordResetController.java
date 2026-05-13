package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Demander la réinitialisation du mot de passe
     */
    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "L'email est requis");
            return ResponseEntity.badRequest().body(response);
        }

        boolean emailSent = passwordResetService.initiatePasswordReset(email.trim());
        
        Map<String, String> response = new HashMap<>();
        if (emailSent) {
            response.put("message", "Un email de réinitialisation a été envoyé à votre adresse email");
            return ResponseEntity.ok(response);
        } else {
            // Pour des raisons de sécurité, on retourne toujours le même message
            response.put("message", "Si un compte existe avec cet email, un email de réinitialisation a été envoyé");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Valider le token de réinitialisation
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, String>> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateResetToken(token);
        
        Map<String, String> response = new HashMap<>();
        if (isValid) {
            response.put("valid", "true");
            response.put("message", "Token valide");
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", "false");
            response.put("message", "Token invalide ou expiré");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Réinitialiser le mot de passe avec le token
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (token == null || token.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Le token de réinitialisation est requis");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Le mot de passe doit contenir au moins 6 caractères");
            return ResponseEntity.badRequest().body(response);
        }

        boolean resetSuccess = passwordResetService.resetPassword(token.trim(), newPassword);
        
        Map<String, String> response = new HashMap<>();
        if (resetSuccess) {
            response.put("message", "Mot de passe réinitialisé avec succès");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token invalide ou expiré");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Changer le mot de passe (utilisateur connecté)
     */
    @PostMapping("/change")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        // Extraire le username du token JWT (simplifié pour l'exemple)
        String username = extractUsernameFromToken(authorizationHeader);
        
        if (username == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Non autorisé");
            return ResponseEntity.status(401).body(response);
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        
        if (currentPassword == null || newPassword == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Le mot de passe actuel et le nouveau mot de passe sont requis");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (newPassword.length() < 6) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Le nouveau mot de passe doit contenir au moins 6 caractères");
            return ResponseEntity.badRequest().body(response);
        }

        boolean changeSuccess = passwordResetService.changePassword(username, currentPassword, newPassword);
        
        Map<String, String> response = new HashMap<>();
        if (changeSuccess) {
            response.put("message", "Mot de passe changé avec succès");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Le mot de passe actuel est incorrect");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Nettoyer les tokens expirés (endpoint de maintenance)
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupExpiredTokens() {
        passwordResetService.cleanupExpiredTokens();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tokens expirés nettoyés avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Extraire le username du token JWT (méthode simplifiée)
     */
    private String extractUsernameFromToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Logique d'extraction du token JWT à implémenter
            // Pour l'instant, retourne null
            return null;
        }
        return null;
    }
}
