package com.smartstudyplanner.smart_study_planner_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.smartstudyplanner.smart_study_planner_backend.repository")
public class SmartStudyPlannerBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmartStudyPlannerBackendApplication.class, args);
	}
}

