package com.smartstudyplanner.smart_study_planner_backend.util;

/**
 * Interface for entities that can be exported to and imported from CSV
 */
public interface Exportable {

    /**
     * Convert entity to CSV representation
     *
     * @return CSV string representation of the entity
     */
    String toCSV();

    /**
     * Initialize entity from CSV representation
     *
     * @param csvLine CSV string to parse
     */
    void fromCSV(String csvLine);
}

