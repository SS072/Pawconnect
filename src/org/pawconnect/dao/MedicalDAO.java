package org.pawconnect.dao;

import org.pawconnect.model.MedicalRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Clinical and Vaccination Records using MySQL JDBC.
 * Implements CRUD operations using PreparedStatement and handles SQLExceptions.
 */
public class MedicalDAO {

    /**
     * READ: Retrieves all medical and vaccination records from MySQL.
     * @return List of medical records ordered by newest first
     */
    public List<MedicalRecord> getAllRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM medical_records ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractRecordFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[MedicalDAO.getAllRecords] DB Warning: " + e.getMessage() + ". Using fallback data.");
            return new ArrayList<>(InMemoryDatabase.getInstance().getMedicalRecords());
        }
    }

    /**
     * READ: Retrieves medical records for a specific pet.
     * @param petId Pet ID
     * @return List of matching medical records
     */
    public List<MedicalRecord> getRecordsForPet(int petId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM medical_records WHERE pet_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, petId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRecordFromResultSet(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[MedicalDAO.getRecordsForPet] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().getMedicalRecordsForPetMemory(petId);
        }
    }

    /**
     * CREATE / INSERT: Adds a new medical log or vaccination record into MySQL.
     * @param record MedicalRecord object to insert
     * @return true if successfully inserted
     */
    public boolean addRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (pet_id, pet_name, record_date, record_type, title, description, veterinarian, next_due_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, record.getPetId());
            ps.setString(2, record.getPetName());
            ps.setString(3, record.getRecordDate());
            ps.setString(4, record.getRecordType());
            ps.setString(5, record.getTitle());
            ps.setString(6, record.getDescription());
            ps.setString(7, record.getVeterinarian());
            ps.setString(8, record.getNextDueDate());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        record.setId(keys.getInt(1));
                    }
                }
                InMemoryDatabase.getInstance().getMedicalRecords().add(record);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MedicalDAO.addRecord] DB Warning: " + e.getMessage());
            record.setId(InMemoryDatabase.getInstance().nextMedId());
            return InMemoryDatabase.getInstance().getMedicalRecords().add(record);
        }
        return false;
    }

    /**
     * DELETE: Permanently deletes a medical record by primary key ID.
     * @param id Medical record ID to delete
     * @return true if deleted
     */
    public boolean deleteRecord(int id) {
        String sql = "DELETE FROM medical_records WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            InMemoryDatabase.getInstance().getMedicalRecords().removeIf(r -> r.getId() == id);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("[MedicalDAO.deleteRecord] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().getMedicalRecords().removeIf(r -> r.getId() == id);
        }
    }

    private MedicalRecord extractRecordFromResultSet(ResultSet rs) throws SQLException {
        return new MedicalRecord(
            rs.getInt("id"),
            rs.getInt("pet_id"),
            rs.getString("pet_name"),
            rs.getString("record_date"),
            rs.getString("record_type"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("veterinarian"),
            rs.getString("next_due_date")
        );
    }
}
