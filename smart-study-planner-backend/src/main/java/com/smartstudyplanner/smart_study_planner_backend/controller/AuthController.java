package com.smartstudyplanner.smart_study_planner_backend.controller;


import com.smartstudyplanner.smart_study_planner_backend.dto.auth.AuthResponse;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.LoginRequest;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.RegistrationRequest;
import com.smartstudyplanner.smart_study_planner_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Login an existing user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

