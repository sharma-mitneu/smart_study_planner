package com.smartstudyplanner.smart_study_planner_backend.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting data to CSV files
 */
public class CSVExporter {

    /**
     * Export a list of exportable entities to a CSV file
     *
     * @param <T> Type of entity implementing Exportable
     * @param items List of items to export
     * @param filePath Path to export CSV file
     * @throws IOException If file operations fail
     */
    public static <T extends Exportable> void exportToCSV(List<T> items, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T item : items) {
                writer.write(item.toCSV());
                writer.newLine();
            }
        }
    }

    /**
     * Export a list of exportable entities to a CSV string
     *
     * @param <T> Type of entity implementing Exportable
     * @param items List of items to export
     * @return CSV content as string
     */
    public static <T extends Exportable> String exportToString(List<T> items) {
        StringBuilder sb = new StringBuilder();
        for (T item : items) {
            sb.append(item.toCSV()).append("\n");
        }
        return sb.toString();
    }
}

