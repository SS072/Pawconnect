package org.pawconnect.dao;

import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import org.pawconnect.model.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Adoption Applications (Pure GUI In-Memory Implementation).
 */
public class ApplicationDAO {
    private final InMemoryDatabase db;

    public ApplicationDAO() {
        this.db = InMemoryDatabase.getInstance();
    }

    public List<AdoptionApplication> getAllApplications() {
        return new ArrayList<>(db.getApplications());
    }

    public List<AdoptionApplication> getApplicationsByStatus(String statusFilter) {
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            return getAllApplications();
        }

        List<AdoptionApplication> result = new ArrayList<>();
        for (AdoptionApplication a : db.getApplications()) {
            if (a.getStatus().name().equalsIgnoreCase(statusFilter.trim()) ||
                a.getStatus().getDisplayLabel().equalsIgnoreCase(statusFilter.trim())) {
                result.add(a);
            }
        }
        return result;
    }

    public boolean submitApplication(AdoptionApplication app) {
        app.setId(db.nextAppId());
        return db.getApplications().add(app);
    }

    public boolean processDecision(int applicationId, int petId, ApplicationStatus decision, String notes) {
        AdoptionApplication targetApp = null;
        for (AdoptionApplication a : db.getApplications()) {
            if (a.getId() == applicationId) {
                targetApp = a;
                break;
            }
        }

        if (targetApp == null) return false;

        // 1. Update application status and notes
        targetApp.setStatus(decision);
        targetApp.setReviewNotes(notes);

        // 2. Synchronize target pet status
        PetDAO petDAO = new PetDAO();
        Pet pet = petDAO.getPetById(petId);
        if (pet != null) {
            if (decision == ApplicationStatus.APPROVED) {
                pet.setAdoptionStatus("ADOPTED");
            } else if (decision == ApplicationStatus.REJECTED || decision == ApplicationStatus.WITHDRAWN) {
                if (!"ADOPTED".equalsIgnoreCase(pet.getAdoptionStatus())) {
                    pet.setAdoptionStatus("AVAILABLE");
                }
            }
        }

        return true;
    }
}
