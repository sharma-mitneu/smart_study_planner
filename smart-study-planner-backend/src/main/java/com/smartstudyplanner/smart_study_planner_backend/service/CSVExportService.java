package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.repository.ProgressRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting data to CSV format
 * Demonstrates CSV file handling requirement
 */
@Service
@RequiredArgsConstructor
public class CSVExportService {

    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final ProgressRepository progressRepository;
    private final AuthService authService;

    // Date and time formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Export all subjects to CSV
     */
    @Transactional(readOnly = true)
    public String exportSubjects() throws IOException {
        User currentUser = authService.getCurrentUser();
        List<Subject> subjects = subjectRepository.findByUser(currentUser);

        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Name", "Description", "Priority", "Created At", "Updated At")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Subject subject : subjects) {
                csvPrinter.printRecord(
                        subject.getId(),
                        subject.getName(),
                        subject.getDescription(),
                        subject.getPriority().name(),
                        formatDateTime(subject.getCreatedAt()),
                        formatDateTime(subject.getUpdatedAt())
                );
            }
        }

        return writer.toString();
    }

    /**
     * Export all tasks to CSV
     */
    @Transactional(readOnly = true)
    public String exportTasks() throws IOException {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks = taskRepository.findByUser(currentUser);

        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Title", "Description", "Due Date", "Completed", "Priority",
                        "Subject", "Recurring", "Recurrence Frequency", "Recurrence End Date")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Task task : tasks) {
                csvPrinter.printRecord(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        formatDateTime(task.getDueDate()),
                        task.isCompleted() ? "Yes" : "No",
                        task.getPriority().name(),
                        task.getSubject().getName(),
                        task.isRecurring() ? "Yes" : "No",
                        task.getRecurrenceFrequency() != null ? task.getRecurrenceFrequency().name() : "",
                        task.getRecurrenceEndDate() != null ? formatDateTime(task.getRecurrenceEndDate()) : ""
                );
            }
        }

        return writer.toString();
    }

    /**
     * Export all progress entries to CSV
     */
    @Transactional(readOnly = true)
    public String exportProgress() throws IOException {
        User currentUser = authService.getCurrentUser();
        List<Progress> progressEntries = progressRepository.findByUser(currentUser);

        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Date", "Minutes Spent", "Notes", "Task", "Subject", "Created At")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Progress progress : progressEntries) {
                csvPrinter.printRecord(
                        progress.getId(),
                        formatDate(progress.getDate()),
                        progress.getMinutesSpent(),
                        progress.getNotes(),
                        progress.getTask().getTitle(),
                        progress.getTask().getSubject().getName(),
                        formatDateTime(progress.getCreatedAt())
                );
            }
        }

        return writer.toString();
    }

    /**
     * Export study summary to CSV
     */
    @Transactional(readOnly = true)
    public String exportStudySummary() throws IOException {
        User currentUser = authService.getCurrentUser();

        // Get study time summary by subject
        List<Object[]> subjectSummary = progressRepository.getStudyTimeSummaryBySubject(currentUser.getId());

        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Subject", "Total Study Time (Minutes)", "Total Study Time (Hours)")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Object[] row : subjectSummary) {
                String subjectName = (String) row[0];
                Long minutes = (Long) row[1];
                double hours = minutes / 60.0;

                csvPrinter.printRecord(
                        subjectName,
                        minutes,
                        String.format("%.2f", hours)
                );
            }
        }

        return writer.toString();
    }

    /**
     * Helper method to format LocalDate
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Helper method to format LocalDateTime
     */
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
