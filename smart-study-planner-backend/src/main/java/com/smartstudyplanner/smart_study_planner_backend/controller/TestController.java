package com.smartstudyplanner.smart_study_planner_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String testEndpoint() {
        return "Backend is running!";
    }
}
