package com.esprit.platformepediatricback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.core.Ordered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import com.esprit.platformepediatricback.Service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Important: allowCredentials=true cannot be used with a wildcard origin in browsers.
        // Limit to the Angular dev origin to avoid CORS failures (status 0 in frontend).
        // In Kubernetes / production, on autorise aussi le domaine front via une variable d'environnement.
        // Exemple: CORS_ALLOWED_ORIGINS="https://pediatric.com,http://pediatric.com"
        String originsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (originsEnv == null || originsEnv.isBlank()) {
            configuration.setAllowedOrigins(Arrays.asList(
                    "http://localhost:4200",
                    "http://localhost:59772",
                    "http://localhost:64182",
                    "http://127.0.0.1:4200",
                    "http://127.0.0.1:59772",
                    "http://127.0.0.1:64182"
            ));
        } else {
            configuration.setAllowedOrigins(
                    Arrays.stream(originsEnv.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList()
            );
        }
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
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
                        
                        // Endpoints de forum + vidéos (publics)
                        // NB: server.servlet.context-path=/api, donc chemins = /forum/**, /videos/** au niveau app
                        .requestMatchers(HttpMethod.POST, "/forum/posts/*/rating").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/forum/posts/*/rating").authenticated()
                        .requestMatchers(HttpMethod.GET, "/forum/posts/*/rating/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/forum/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/videos/**").permitAll()
                        .requestMatchers("/forum/**").permitAll()
                        .requestMatchers("/videos/**").permitAll()
                        // Certains setups (gateway/proxy) gardent le préfixe /api dans le matcher
                        .requestMatchers(HttpMethod.POST, "/api/forum/posts/*/rating").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/forum/posts/*/rating").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/forum/posts/*/rating/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/forum/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/videos/**").permitAll()
                        .requestMatchers("/api/forum/**").permitAll()
                        .requestMatchers("/api/videos/**").permitAll()
                        .requestMatchers("/posts/**").permitAll()
                        
                        // Endpoints de santé (publics)
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/actuator/**").permitAll()
                        
                        // Console H2 (développement uniquement)
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/h2-console/**").permitAll()
                        
                        // Documentation API
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v3/api-docs/**").permitAll()
                        
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
                        
                        // Développement: ne bloque pas les endpoints non listés
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
