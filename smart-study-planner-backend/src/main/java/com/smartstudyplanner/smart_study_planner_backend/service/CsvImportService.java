package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.ProgressDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.RecurrenceFrequency;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for importing data from CSV files
 * Demonstrates CSV file handling requirement
 */
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final SubjectService subjectService;
    private final TaskService taskService;
    private final ProgressService progressService;

    // Date and time formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Import subjects from CSV file
     */
    @Transactional
    public List<SubjectDTO> importSubjects(MultipartFile file) throws IOException {
        validateCsvFile(file);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<SubjectDTO> importedSubjects = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                try {
                    SubjectDTO subjectDto = SubjectDTO.builder()
                            .name(record.get("Name"))
                            .description(record.get("Description"))
                            //.priority(getPriority(record.get("Priority")))
                            .build();

                    SubjectDTO savedSubject = subjectService.createSubject(subjectDto);
                    importedSubjects.add(savedSubject);
                } catch (Exception e) {
                    throw new StudyPlannerException("Error importing subject at line " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }

            return importedSubjects;
        }
    }

    /**
     * Import tasks from CSV file
     */
    @Transactional
    public List<TaskDto> importTasks(MultipartFile file) throws IOException {
        validateCsvFile(file);

        // Get all subjects to map by name
        List<SubjectDTO> allSubjects = subjectService.getAllSubjects();
        Map<String, Integer> subjectMap = allSubjects.stream()
                .collect(Collectors.toMap(SubjectDTO::getName, SubjectDTO::getId));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<TaskDto> importedTasks = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                try {
                    String subjectName = record.get("Subject");
                    Integer subjectId = subjectMap.get(subjectName);

                    if (subjectId == null) {
                        throw new StudyPlannerException("Subject not found: " + subjectName);
                    }

                    TaskDto taskDto = TaskDto.builder()
                            .title(record.get("Title"))
                            .description(record.get("Description"))
                            .dueDate(parseDateTime(record.get("Due Date")))
                            .completed("Yes".equalsIgnoreCase(record.get("Completed")))
                            .priority(getPriority(record.get("Priority")))
                            .subjectId(subjectId)
                            .recurring("Yes".equalsIgnoreCase(record.get("Recurring")))
                            .build();

                    // Handle optional recurrence fields
                    if (taskDto.isRecurring()) {
                        taskDto.setRecurrenceFrequency(getRecurrenceFrequency(record.get("Recurrence Frequency")));

                        String endDateStr = record.get("Recurrence End Date");
                        if (endDateStr != null && !endDateStr.isEmpty()) {
                            taskDto.setRecurrenceEndDate(parseDateTime(endDateStr));
                        }
                    }

                    TaskDto savedTask = taskService.createTask(taskDto);
                    importedTasks.add(savedTask);
                } catch (Exception e) {
                    throw new StudyPlannerException("Error importing task at line " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }

            return importedTasks;
        }
    }

    /**
     * Import progress entries from CSV file
     */
    @Transactional
    public List<ProgressDto> importProgress(MultipartFile file) throws IOException {
        validateCsvFile(file);

        // Get all tasks to map by title
        List<TaskDto> allTasks = taskService.getAllTasks(null);
        Map<String, Integer> taskMap = allTasks.stream()
                .collect(Collectors.toMap(TaskDto::getTitle, TaskDto::getId));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<ProgressDto> importedProgress = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                try {
                    String taskTitle = record.get("Task");
                    Integer taskId = taskMap.get(taskTitle);

                    if (taskId == null) {
                        throw new StudyPlannerException("Task not found: " + taskTitle);
                    }

                    ProgressDto progressDto = ProgressDto.builder()
                            .date(parseDate(record.get("Date")))
                            .minutesSpent(Integer.parseInt(record.get("Minutes Spent")))
                            .notes(record.get("Notes"))
                            .taskId(taskId)
                            .build();

                    ProgressDto savedProgress = progressService.createProgress(progressDto);
                    importedProgress.add(savedProgress);
                } catch (Exception e) {
                    throw new StudyPlannerException("Error importing progress at line " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }

            return importedProgress;
        }
    }

    /**
     * Validate file is a CSV
     */
    private void validateCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StudyPlannerException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new StudyPlannerException("File must be a CSV file");
        }
    }

    /**
     * Parse Priority from string
     */
    private Priority getPriority(String value) {
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Priority.MEDIUM; // Default priority
        }
    }

    /**
     * Parse RecurrenceFrequency from string
     */
    private RecurrenceFrequency getRecurrenceFrequency(String value) {
        try {
            return RecurrenceFrequency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RecurrenceFrequency.WEEKLY; // Default frequency
        }
    }

    /**
     * Parse LocalDate from string
     */
    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new StudyPlannerException("Invalid date format. Expected: yyyy-MM-dd");
        }
    }

    /**
     * Parse LocalDateTime from string
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new StudyPlannerException("Invalid date-time format. Expected: yyyy-MM-dd HH:mm:ss");
        }
    }
}
