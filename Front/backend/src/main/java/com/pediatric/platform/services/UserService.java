package com.pediatric.platform.services;

import com.pediatric.platform.entities.User;
import com.pediatric.platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Récupérer un utilisateur par ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Créer un nouvel utilisateur
    public User createUser(User user) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        // Vérifier si le username existe déjà
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("Username already exists: " + user.getUserName());
        }
        
        User savedUser = userRepository.save(user);
        log.info("Created new user with ID: {}", savedUser.getId());
        return savedUser;
    }

    // Mettre à jour un utilisateur
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Updated user {}", id);
        return updatedUser;
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user {}", id);
    }

    // Trouver un utilisateur par email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Trouver un utilisateur par username
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    // Rechercher des utilisateurs par nom
    public List<User> searchUsersByName(String name) {
        return userRepository.searchByName(name);
    }

    // Compter les utilisateurs par rôle
    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    // Récupérer les utilisateurs par rôle
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // Vérifier si un email existe
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Vérifier si un username existe
    public boolean userNameExists(String userName) {
        return userRepository.existsByUserName(userName);
    }
}
