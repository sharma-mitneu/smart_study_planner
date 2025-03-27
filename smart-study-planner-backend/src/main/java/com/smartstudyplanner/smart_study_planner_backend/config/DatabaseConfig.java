package com.smartstudyplanner.smart_study_planner_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.smartstudy.repository")
public class DatabaseConfig {
    // Spring Boot's auto-configuration will handle the DataSource and EntityManagerFactory
    // We only need this configuration class to enable JPA features
}