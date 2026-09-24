package org.pawconnect.controller;

import org.pawconnect.dao.ApplicationDAO;
import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import java.util.List;

/**
 * Controller mediating adoption application operations between the UI and ApplicationDAO.
 */
public class ApplicationController {
    private final ApplicationDAO applicationDAO;

    public ApplicationController() {
        this.applicationDAO = new ApplicationDAO();
    }

    public List<AdoptionApplication> getAllApplications() {
        return applicationDAO.getAllApplications();
    }

    public List<AdoptionApplication> getApplicationsByStatus(String status) {
        return applicationDAO.getApplicationsByStatus(status);
    }

    public boolean submitApplication(AdoptionApplication app) {
        if (app.getApplicantName() == null || app.getApplicantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Applicant Name cannot be blank.");
        }
        if (app.getApplicantPhone() == null || app.getApplicantPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact Phone cannot be blank.");
        }
        if (app.getApplicantEmail() == null || !app.getApplicantEmail().contains("@")) {
            throw new IllegalArgumentException("Valid Email address is required.");
        }
        return applicationDAO.submitApplication(app);
    }

    public boolean processDecision(int applicationId, int petId, ApplicationStatus decision, String reviewNotes) {
        if (applicationId <= 0 || petId <= 0) {
            throw new IllegalArgumentException("Invalid application or pet ID.");
        }
        return applicationDAO.processDecision(applicationId, petId, decision, reviewNotes);
    }
}
