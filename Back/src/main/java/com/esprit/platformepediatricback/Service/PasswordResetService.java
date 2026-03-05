package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int TOKEN_EXPIRY_HOURS = 24;

    /**
     * Générer un token de réinitialisation et envoyer l'email
     */
    public boolean initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            // Pour des raisons de sécurité, on ne révèle pas si l'email existe ou non
            return false;
        }

        User user = userOpt.get();
        String resetToken = generateResetToken();
        
        // Mettre à jour l'utilisateur avec le token et sa date d'expiration
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
        userRepository.save(user);

        // Envoyer l'email de réinitialisation
        try {
            emailService.sendForgotPasswordEmail(email, resetToken, user.getFirstName() + " " + user.getLastName());
            return true;
        } catch (Exception e) {
            // Log l'erreur mais retourne false
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valider le token de réinitialisation
     */
    public boolean validateResetToken(String token) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Vérifier si le token n'a pas expiré
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    /**
     * Réinitialiser le mot de passe avec le token
     */
    public boolean resetPassword(String token, String newPassword) {
        if (!validateResetToken(token)) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByResetToken(token);
        
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Encoder le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Nettoyer le token de réinitialisation
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);

        // Envoyer l'email de confirmation
        try {
            emailService.sendPasswordResetConfirmationEmail(
                user.getEmail(), 
                user.getFirstName() + " " + user.getLastName()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation: " + e.getMessage());
            return true; // Le mot de passe a été changé même si l'email n'a pas été envoyé
        }
    }

    /**
     * Changer le mot de passe (utilisateur connecté)
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Vérifier le mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Encoder et mettre à jour le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    /**
     * Générer un token de réinitialisation sécurisé
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    /**
     * Nettoyer les tokens expirés (méthode de maintenance)
     */
    public void cleanupExpiredTokens() {
        userRepository.deleteExpiredResetTokens(LocalDateTime.now());
    }
}
