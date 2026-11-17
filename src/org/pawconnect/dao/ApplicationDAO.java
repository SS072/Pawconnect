package org.pawconnect.dao;

import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Adoption Applications using MySQL JDBC.
 * Implements CRUD operations, status filtering, and atomic multi-table state updates.
 */
public class ApplicationDAO {

    /**
     * READ: Retrieves all submitted adoption applications from MySQL database.
     * @return List of applications ordered by newest first
     */
    public List<AdoptionApplication> getAllApplications() {
        List<AdoptionApplication> list = new ArrayList<>();
        String sql = "SELECT * FROM adoption_applications ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractAppFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[ApplicationDAO.getAllApplications] DB Warning: " + e.getMessage() + ". Using fallback data.");
            return new ArrayList<>(InMemoryDatabase.getInstance().getApplications());
        }
    }

    /**
     * READ: Filters applications by application status.
     * @param statusFilter e.g., "PENDING", "APPROVED", or "All"
     */
    public List<AdoptionApplication> getApplicationsByStatus(String statusFilter) {
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            return getAllApplications();
        }

        List<AdoptionApplication> list = new ArrayList<>();
        String sql = "SELECT * FROM adoption_applications WHERE LOWER(status) = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusFilter.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAppFromResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[ApplicationDAO.getApplicationsByStatus] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().getApplicationsByStatusMemory(statusFilter);
        }
    }

    /**
     * CREATE / INSERT: Inserts a newly submitted adoption petition into MySQL.
     * @param app Application object
     * @return true if successfully inserted
     */
    public boolean submitApplication(AdoptionApplication app) {
        String sql = "INSERT INTO adoption_applications (pet_id, pet_name, pet_breed, applicant_name, " +
                     "contact_phone, contact_email, housing_type, has_fenced_yard, has_children, other_pets, " +
                     "household_agreement, notes, status, submission_date, review_notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setAppParameters(ps, app);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        app.setId(keys.getInt(1));
                    }
                }
                InMemoryDatabase.getInstance().getApplications().add(app);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ApplicationDAO.submitApplication] DB Warning: " + e.getMessage());
            app.setId(InMemoryDatabase.getInstance().nextAppId());
            return InMemoryDatabase.getInstance().getApplications().add(app);
        }
        return false;
    }

    /**
     * UPDATE: Adjudicates an application and synchronizes the target pet's status atomically.
     * Uses JDBC Transaction management (setAutoCommit, commit, rollback).
     */
    public boolean processDecision(int applicationId, int petId, ApplicationStatus decision, String notes) {
        String updateAppSql = "UPDATE adoption_applications SET status = ?, review_notes = ? WHERE id = ?";
        String updatePetSql = (decision == ApplicationStatus.APPROVED) ?
            "UPDATE pets SET adoption_status = 'ADOPTED' WHERE id = ?" :
            "UPDATE pets SET adoption_status = 'AVAILABLE' WHERE id = ? AND adoption_status != 'ADOPTED'";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Update application status and notes
            try (PreparedStatement psApp = conn.prepareStatement(updateAppSql)) {
                psApp.setString(1, decision.name());
                psApp.setString(2, notes);
                psApp.setInt(3, applicationId);
                psApp.executeUpdate();
            }

            // 2. Synchronize pet adoption status
            if (petId > 0 && (decision == ApplicationStatus.APPROVED || decision == ApplicationStatus.REJECTED || decision == ApplicationStatus.WITHDRAWN)) {
                try (PreparedStatement psPet = conn.prepareStatement(updatePetSql)) {
                    psPet.setInt(1, petId);
                    psPet.executeUpdate();
                }
            }

            conn.commit(); // Commit Transaction

            // Sync fallback memory cache
            InMemoryDatabase.getInstance().processDecisionMemory(applicationId, petId, decision, notes);
            return true;
        } catch (SQLException e) {
            System.err.println("[ApplicationDAO.processDecision] DB Warning: " + e.getMessage() + ". Rolling back.");
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            return InMemoryDatabase.getInstance().processDecisionMemory(applicationId, petId, decision, notes);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignored */ }
            }
        }
    }

    private void setAppParameters(PreparedStatement ps, AdoptionApplication app) throws SQLException {
        ps.setInt(1, app.getPetId());
        ps.setString(2, app.getPetName());
        ps.setString(3, app.getPetBreed());
        ps.setString(4, app.getApplicantName());
        ps.setString(5, app.getApplicantPhone());
        ps.setString(6, app.getApplicantEmail());
        ps.setString(7, app.getHousingType());
        ps.setBoolean(8, app.isHasYard());
        ps.setBoolean(9, app.isHasChildren());
        ps.setString(10, app.getOtherPets());
        ps.setBoolean(11, app.isPetExperience());
        ps.setString(12, app.getReasonForAdoption());
        ps.setString(13, app.getStatus().name());
        ps.setString(14, app.getSubmissionDate());
        ps.setString(15, app.getReviewNotes());
    }

    private AdoptionApplication extractAppFromResultSet(ResultSet rs) throws SQLException {
        String statusStr = rs.getString("status");
        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        } catch (Exception e) {
            status = ApplicationStatus.PENDING;
        }

        return new AdoptionApplication(
            rs.getInt("id"),
            rs.getInt("pet_id"),
            rs.getString("pet_name"),
            rs.getString("pet_breed"),
            rs.getString("applicant_name"),
            rs.getString("contact_phone"),
            rs.getString("contact_email"),
            rs.getString("housing_type"),
            rs.getBoolean("has_fenced_yard"),
            rs.getBoolean("has_children"),
            rs.getString("other_pets"),
            rs.getBoolean("household_agreement"),
            rs.getString("notes"),
            status,
            rs.getString("submission_date"),
            rs.getString("review_notes")
        );
    }
}
