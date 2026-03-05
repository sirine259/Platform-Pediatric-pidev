package com.pediatric.platform.repository;

import com.pediatric.platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);
    
    // Trouver un utilisateur par username
    Optional<User> findByUserName(String userName);
    
    // Vérifier si un email existe
    boolean existsByEmail(String email);
    
    // Vérifier si un username existe
    boolean existsByUserName(String userName);
    
    // Rechercher des utilisateurs par nom
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> searchByName(@Param("name") String name);
    
    // Compter les utilisateurs par rôle
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
    
    // Trouver les utilisateurs par rôle
    List<User> findByRole(String role);
}
