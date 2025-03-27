package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstudyplanner.smart_study_planner_backend.model.ApiResponse;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ApiResponse<List<User>> getUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = objectMapper.readValue(
                new ClassPathResource("data/users.json").getInputStream(),
                new TypeReference<List<User>>() {}
        );

        return new ApiResponse<>("success", "Users fetched successfully", users);
    }
}
