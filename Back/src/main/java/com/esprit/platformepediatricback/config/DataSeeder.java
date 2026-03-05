package com.esprit.platformepediatricback.config;

import com.esprit.platformepediatricback.Repository.UserRepository;
import com.esprit.platformepediatricback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Crée des utilisateurs de test au démarrage (pour forgot-password, login, etc.)
 * Les emails ririhamzo12@gmail.com et sirine.hamzaoui@esprit.tn peuvent être utilisés
 * pour tester le flux mot de passe oublié avec Mailtrap.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUserIfNotExists("admin", "admin@pidev.com", "Admin", "User", User.Role.ADMIN);
        seedUserIfNotExists("doctor", "doctor@pidev.com", "Dr.", "Martin", User.Role.DOCTOR);
        seedUserIfNotExists("patient", "patient@pidev.com", "Jean", "Dupont", User.Role.USER);
        seedUserIfNotExists("ririhamzo", "ririhamzo12@gmail.com", "Ririhamzo", "Test", User.Role.USER);
        seedUserIfNotExists("sirine", "sirine.hamzaoui@esprit.tn", "Sirine", "Hamzaoui", User.Role.USER);
    }

    private void seedUserIfNotExists(String username, String email, String firstName, String lastName, User.Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
