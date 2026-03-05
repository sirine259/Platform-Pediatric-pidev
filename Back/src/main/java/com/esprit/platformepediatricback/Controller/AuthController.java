package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.UserService;
import com.esprit.platformepediatricback.dto.AuthRequestSimple;
import com.esprit.platformepediatricback.dto.RegisterRequestSimple;
import com.esprit.platformepediatricback.dto.ForgotPasswordRequestSimple;
import com.esprit.platformepediatricback.dto.ResetPasswordRequestSimple;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Endpoint de connexion
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequestSimple authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            
            // Générer un token JWT simple (mock pour l'instant)
            String token = "mock-jwt-token-" + UUID.randomUUID().toString();
            
            // Mettre à jour la dernière connexion
            userService.updateLastLogin(user.getId());
            
            AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isEnabled()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint d'inscription
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequestSimple registerRequest) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userService.getUserByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Créer le nouvel utilisateur
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setFirstName(registerRequest.getFirstName());
            newUser.setLastName(registerRequest.getLastName());
            newUser.setPhoneNumber(registerRequest.getPhoneNumber());
            newUser.setAddress(registerRequest.getAddress());
            newUser.setRole(User.Role.valueOf(registerRequest.getRole()));
            newUser.setEnabled(true);
            
            User createdUser = userService.createUser(newUser);
            
            // Générer un token JWT simple (mock pour l'instant)
            String token = "mock-jwt-token-" + UUID.randomUUID().toString();
            
            AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getFirstName(),
                createdUser.getLastName(),
                createdUser.getRole().name(),
                createdUser.isEnabled()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint de mot de passe oublié
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestSimple request) {
        try {
            // Vérifier si l'utilisateur existe avec cet email
            java.util.Optional<User> userOptional = userService.getUserByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                // Pour des raisons de sécurité, ne pas révéler si l'email existe ou non
                return ResponseEntity.ok("Un email de réinitialisation a été envoyé si l'adresse email est valide.");
            }
            
            User user = userOptional.get();
            
            // Générer un token de réinitialisation (mock pour l'instant)
            String resetToken = UUID.randomUUID().toString();
            
            // TODO: Sauvegarder le token et envoyer un email
            // Pour l'instant, on simule l'envoi d'email
            System.out.println("Token de réinitialisation pour " + user.getEmail() + ": " + resetToken);
            
            return ResponseEntity.ok("Un email de réinitialisation a été envoyé.");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint de réinitialisation du mot de passe
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestSimple request) {
        try {
            // TODO: Valider le token et trouver l'utilisateur
            // Pour l'instant, on simule la réinitialisation
            
            // Mock: trouver l'utilisateur par token (à implémenter avec une vraie base de données)
            // User user = userService.findByResetToken(request.getToken());
            
            // if (user == null) {
            //     return ResponseEntity.badRequest().body("Token invalide ou expiré.");
            // }
            
            // user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            // userService.updateUser(user.getId(), user);
            
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint pour vérifier si le token est valide
    @GetMapping("/verify-token/{token}")
    public ResponseEntity<Boolean> verifyToken(@PathVariable String token) {
        try {
            // TODO: Implémenter la vérification du token
            // Pour l'instant, on retourne true pour les tests
            return ResponseEntity.ok(true);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint pour rafraîchir le token
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        try {
            // TODO: Implémenter le rafraîchissement du token
            // Pour l'instant, on retourne une erreur
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoint de déconnexion
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("Déconnexion réussie");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
