package com.smartstudyplanner.smart_study_planner_backend.util;

import com.smartstudyplanner.smart_study_planner_backend.exception.ValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class for importing data from CSV files
 */
public class CSVImporter {

    /**
     * Import entities from a CSV file
     *
     * @param <T> Type of entity implementing Exportable
     * @param filePath Path to the CSV file
     * @param supplier Supplier to create new instance of T
     * @return List of imported entities
     * @throws IOException If file operations fail
     */
    public static <T extends Exportable> List<T> importFromCSV(String filePath, Supplier<T> supplier)
            throws IOException {
        List<T> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    if (!line.trim().isEmpty()) {
                        T item = supplier.get();
                        item.fromCSV(line);
                        items.add(item);
                    }
                } catch (Exception e) {
                    throw new ValidationException("Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return items;
    }

    /**
     * Import entities from a CSV string
     *
     * @param <T> Type of entity implementing Exportable
     * @param csvContent CSV content as string
     * @param supplier Supplier to create new instance of T
     * @return List of imported entities
     * @throws IOException If reader operations fail
     */
    public static <T extends Exportable> List<T> importFromString(String csvContent, Supplier<T> supplier)
            throws IOException {
        List<T> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    if (!line.trim().isEmpty()) {
                        T item = supplier.get();
                        item.fromCSV(line);
                        items.add(item);
                    }
                } catch (Exception e) {
                    throw new ValidationException("Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return items;
    }
}

