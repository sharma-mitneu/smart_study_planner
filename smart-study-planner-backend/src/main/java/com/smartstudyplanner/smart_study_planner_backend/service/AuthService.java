package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.auth.AuthResponse;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.LoginRequest;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.RegistrationRequest;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.UserRole;
import com.smartstudyplanner.smart_study_planner_backend.repository.UserRepository;
import com.smartstudyplanner.smart_study_planner_backend.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtil;

    @Value("${admin.setup.token}")
    private String adminSetupToken;

    @Transactional
    public AuthResponse registerInitialAdmin(RegistrationRequest request, String setupToken) {
        // Verify setup token
        if (!adminSetupToken.toString().trim().equals(setupToken.toString().trim())) {
            log.info("Configured setup token: '{}'", adminSetupToken.toString().trim());
            log.info("Received setup token: '{}'", setupToken.toString().trim());
            throw new StudyPlannerException("Invalid setup token");
        }

        // Check if any admin already exists
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            throw new StudyPlannerException("Admin already exists. Use regular admin registration.");
        }

        return registerUser(request, UserRole.ADMIN);
    }

    @Transactional
    public AuthResponse registerAdmin(RegistrationRequest request) {
        // Verify current user is admin
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new StudyPlannerException("Only administrators can register new administrators");
        }
        return registerUser(request, UserRole.ADMIN);
    }

    @Transactional
    public AuthResponse registerStudent(RegistrationRequest request) {
        return registerUser(request, UserRole.STUDENT);
    }

    private AuthResponse registerUser(RegistrationRequest request, UserRole role) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new StudyPlannerException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new StudyPlannerException("Email is already registered");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(role)
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new StudyPlannerException("No authenticated user found");
        }

        return (User) authentication.getPrincipal();
    }
}