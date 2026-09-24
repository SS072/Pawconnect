package org.pawconnect.model;

/**
 * Enumeration representing the lifecycle states of an adoption application.
 * Used across Model, View (table filters, badges), and Controller layers.
 */
public enum ApplicationStatus {
    PENDING("Pending Review"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    private final String displayLabel;

    ApplicationStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    @Override
    public String toString() {
        return displayLabel;
    }

    public static ApplicationStatus fromString(String text) {
        if (text == null) return PENDING;
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.name().equalsIgnoreCase(text) || status.displayLabel.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING;
    }
}
