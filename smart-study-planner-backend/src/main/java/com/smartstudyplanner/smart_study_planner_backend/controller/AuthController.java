package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.auth.AuthResponse;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.LoginRequest;
import com.smartstudyplanner.smart_study_planner_backend.dto.auth.RegistrationRequest;
import com.smartstudyplanner.smart_study_planner_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/setup")
    public ResponseEntity<AuthResponse> registerInitialAdmin(
            @Valid @RequestBody RegistrationRequest request,
            @RequestParam String setupToken) {
        return ResponseEntity.ok(authService.registerInitialAdmin(request, setupToken));
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/student/register")
    public ResponseEntity<AuthResponse> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.registerStudent(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}