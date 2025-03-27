package com.smartstudyplanner.smart_study_planner_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * General application exception
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StudyPlannerException extends RuntimeException {

    public StudyPlannerException(String message) {
        super(message);
    }

    public StudyPlannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
