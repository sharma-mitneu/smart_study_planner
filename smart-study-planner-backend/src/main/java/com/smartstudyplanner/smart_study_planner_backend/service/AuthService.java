package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.AuthenticationRequest;
import com.smartstudyplanner.smart_study_planner_backend.dto.AuthenticationResponse;
import com.smartstudyplanner.smart_study_planner_backend.dto.RegisterRequest;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Role;
import com.smartstudyplanner.smart_study_planner_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Validate unique email and username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already taken");
        }

        // Create new user
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // Generate JWT token
        var token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .user(userMapper.toDto(user))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user and generate token
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid credentials"));

        var token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .user(userMapper.toDto(user))
                .build();
    }
}

