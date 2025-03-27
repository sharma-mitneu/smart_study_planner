package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstudyplanner.smart_study_planner_backend.model.ApiResponse;
import com.smartstudyplanner.smart_study_planner_backend.model.Course;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @GetMapping
    public ApiResponse<List<Course>> getCourses() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Course> courses = objectMapper.readValue(
                new ClassPathResource("data/courses.json").getInputStream(),
                new TypeReference<List<Course>>() {}
        );

        return new ApiResponse<>("success", "Courses fetched successfully", courses);
    }

}
