package com.example.jobup.services;

import com.example.jobup.dto.AuthResponseDto;
import com.example.jobup.dto.LoginRequestDto;
import com.example.jobup.dto.RegisterRequestDto;
import com.example.jobup.entities.Role;
import com.example.jobup.entities.User;
import com.example.jobup.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user with CLIENT role by default
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.ROLE_CLIENT))
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getUsername());

        // Generate JWT token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", savedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));

        String token = jwtUtil.generateToken(savedUser, extraClaims);

        return AuthResponseDto.builder()
                .token(token)
                .roles(savedUser.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            log.info("User logged in: {}", user.getUsername());

            // Generate JWT token
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));

            String token = jwtUtil.generateToken(user, extraClaims);

            return AuthResponseDto.builder()
                    .token(token)
                    .roles(user.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList()))
                    .userId(user.getId()) // Add this line
                    .username(user.getUsername())
                    .build();

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password!");
        }
    }
}
