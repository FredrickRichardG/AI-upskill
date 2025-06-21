package com.school.attendance.validation;

/**
 * Validation groups for different types of operations.
 * These groups can be used to apply different validation rules
 * for different scenarios (e.g., creation vs update).
 */
public interface ValidationGroups {
    /**
     * Validation group for creation operations.
     */
    interface Create {}

    /**
     * Validation group for update operations.
     */
    interface Update {}

    /**
     * Validation group for bulk operations.
     */
    interface Bulk {}
} 