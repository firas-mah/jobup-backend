package com.example.jobup.config;

import com.example.jobup.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()  // Allow WebSocket connections
                        .requestMatchers("/topic/**").permitAll() // Allow WebSocket topics
                        .requestMatchers("/app/**").permitAll()   // Allow WebSocket app destinations
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/workers/**").permitAll() // Temporarily allow for testing
                        .requestMatchers("/api/chat/**").permitAll() // Allow chat endpoints
                        .requestMatchers("/api/proposals/**").permitAll() // Allow proposal endpoints
                        .requestMatchers("/api/deals/**").permitAll() // Allow deal endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/posts").hasRole("CLIENT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/posts/multipart").hasRole("CLIENT")
                        // File upload endpoints - require authentication
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/files/upload").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/files/**").authenticated()
                        // Public access for profile pictures and portfolios
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/owner/*/category/PROFILE_PICTURE").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/owner/*/category/WORKER_PORTFOLIO").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/owner/*/category/WORKER_CERTIFICATE").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/owner/**").authenticated()
                        // File viewing/downloading - public for now (access control in service layer)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/*/download").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/*/view").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/files/*").permitAll()
                        // Protected endpoints - will be configured later
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
