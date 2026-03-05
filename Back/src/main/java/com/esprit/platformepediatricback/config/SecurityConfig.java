package com.esprit.platformepediatricback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints d'authentification (publics)
                        // Avec context-path=/api : chemin = /auth/... (sans préfixe /api)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/password/**").permitAll()
                        .requestMatchers("/auth/password/forgot").permitAll()
                        .requestMatchers("/auth/password/reset").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/verify-token/**").permitAll()
                        
                        // Endpoints de forum (publics pour lecture)
                        .requestMatchers("/api/forum/**").permitAll()
                        .requestMatchers("/api/posts/**").permitAll()
                        
                        // Endpoints de santé (publics)
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // Console H2 (développement uniquement)
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Documentation API
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        
                        // Endpoints protégés
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/kidney-transplant/**").authenticated()
                        .requestMatchers("/api/post-transplant-follow-up/**").authenticated()
                        .requestMatchers("/api/medical-records/**").authenticated()
                        .requestMatchers("/api/consultations/**").authenticated()
                        .requestMatchers("/api/dialyses/**").authenticated()
                        .requestMatchers("/api/growth-monitoring/**").authenticated()
                        
                        // Admin uniquement
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
