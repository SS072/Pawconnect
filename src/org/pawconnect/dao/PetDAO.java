package org.pawconnect.dao;

import org.pawconnect.model.Pet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for Pet records using MySQL JDBC.
 * Implements basic CRUD operations using PreparedStatement and handles SQLExceptions.
 */
public class PetDAO {

    /**
     * READ: Retrieves all pets from the MySQL database.
     * @return List of all pet records
     */
    public List<Pet> getAllPets() {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractPetFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[PetDAO.getAllPets] DB Warning: " + e.getMessage() + ". Using fallback data.");
            return new ArrayList<>(InMemoryDatabase.getInstance().getPets());
        }
    }

    /**
     * READ: Retrieves a single pet by its primary key ID.
     * @param id Pet ID
     * @return Pet object or null if not found
     */
    public Pet getPetById(int id) {
        String sql = "SELECT * FROM pets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPetFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PetDAO.getPetById] DB Warning: " + e.getMessage());
            for (Pet p : InMemoryDatabase.getInstance().getPets()) {
                if (p.getId() == id) return p;
            }
        }
        return null;
    }

    /**
     * READ: Filters pets by search keyword, species, and adoption status using dynamic SQL.
     */
    public List<Pet> searchPets(String query, String speciesFilter, String statusFilter) {
        List<Pet> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pets WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE ? OR LOWER(breed) LIKE ? OR LOWER(description) LIKE ?) ");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (speciesFilter != null && !speciesFilter.equalsIgnoreCase("All")) {
            sql.append("AND LOWER(species) = ? ");
            params.add(speciesFilter.trim().toLowerCase());
        }

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
            sql.append("AND LOWER(adoption_status) = ? ");
            params.add(statusFilter.trim().toLowerCase());
        }

        sql.append("ORDER BY id ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(extractPetFromResultSet(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            System.err.println("[PetDAO.searchPets] DB Warning: " + e.getMessage() + ". Using fallback search.");
            return InMemoryDatabase.getInstance().searchPetsMemory(query, speciesFilter, statusFilter);
        }
    }

    /**
     * CREATE / INSERT: Inserts a new pet record into the MySQL database.
     * @param pet Pet object with data to insert
     * @return true if record was successfully created
     */
    public boolean addPet(Pet pet) {
        String sql = "INSERT INTO pets (name, species, breed, age_months, gender, size, description, " +
                     "adoption_status, intake_date, good_with_children, good_with_dogs, good_with_cats, " +
                     "house_trained, activity_level, health_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPetParameters(ps, pet);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        pet.setId(keys.getInt(1));
                    }
                }
                InMemoryDatabase.getInstance().getPets().add(pet);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PetDAO.addPet] DB Warning: " + e.getMessage());
            pet.setId(InMemoryDatabase.getInstance().nextPetId());
            return InMemoryDatabase.getInstance().getPets().add(pet);
        }
        return false;
    }

    /**
     * UPDATE: Updates an existing pet record in the MySQL database.
     * @param pet Pet object with updated values
     * @return true if record was updated
     */
    public boolean updatePet(Pet pet) {
        String sql = "UPDATE pets SET name=?, species=?, breed=?, age_months=?, gender=?, size=?, description=?, " +
                     "adoption_status=?, intake_date=?, good_with_children=?, good_with_dogs=?, good_with_cats=?, " +
                     "house_trained=?, activity_level=?, health_status=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setPetParameters(ps, pet);
            ps.setInt(16, pet.getId());
            int affected = ps.executeUpdate();

            InMemoryDatabase.getInstance().updatePetMemory(pet);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("[PetDAO.updatePet] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().updatePetMemory(pet);
        }
    }

    /**
     * DELETE: Permanently deletes a pet record by primary key ID.
     * @param id Pet ID to delete
     * @return true if deleted
     */
    public boolean deletePet(int id) {
        String sql = "DELETE FROM pets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();

            InMemoryDatabase.getInstance().getPets().removeIf(p -> p.getId() == id);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("[PetDAO.deletePet] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().getPets().removeIf(p -> p.getId() == id);
        }
    }

    /**
     * UPDATE: Updates only the adoption status of a pet.
     */
    public boolean updatePetStatus(int id, String status) {
        String sql = "UPDATE pets SET adoption_status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();

            Pet p = InMemoryDatabase.getInstance().findPetMemory(id);
            if (p != null) p.setAdoptionStatus(status);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("[PetDAO.updatePetStatus] DB Warning: " + e.getMessage());
            Pet p = InMemoryDatabase.getInstance().findPetMemory(id);
            if (p != null) {
                p.setAdoptionStatus(status);
                return true;
            }
            return false;
        }
    }

    /**
     * READ: Aggregates inventory counts for dashboard KPI cards.
     */
    public Map<String, Integer> getDashboardMetrics() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT adoption_status, COUNT(*) AS cnt FROM pets GROUP BY adoption_status";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int total = 0;
            int available = 0;
            int adopted = 0;
            int pending = 0;
            int medical = 0;

            while (rs.next()) {
                String status = rs.getString("adoption_status");
                int count = rs.getInt("cnt");
                total += count;

                if ("AVAILABLE".equalsIgnoreCase(status)) available += count;
                else if ("ADOPTED".equalsIgnoreCase(status)) adopted += count;
                else if ("PENDING".equalsIgnoreCase(status)) pending += count;
                else if ("MEDICAL_HOLD".equalsIgnoreCase(status)) medical += count;
            }

            map.put("TOTAL", total);
            map.put("AVAILABLE", available);
            map.put("ADOPTED", adopted);
            map.put("PENDING", pending);
            map.put("MEDICAL_HOLD", medical);
            return map;
        } catch (SQLException e) {
            System.err.println("[PetDAO.getDashboardMetrics] DB Warning: " + e.getMessage());
            return InMemoryDatabase.getInstance().getDashboardMetricsMemory();
        }
    }

    private void setPetParameters(PreparedStatement ps, Pet pet) throws SQLException {
        ps.setString(1, pet.getName());
        ps.setString(2, pet.getSpecies());
        ps.setString(3, pet.getBreed());
        ps.setInt(4, pet.getAgeMonths());
        ps.setString(5, pet.getGender());
        ps.setString(6, pet.getSize());
        ps.setString(7, pet.getDescription());
        ps.setString(8, pet.getAdoptionStatus());
        ps.setString(9, pet.getIntakeDate());
        ps.setBoolean(10, pet.isGoodWithChildren());
        ps.setBoolean(11, pet.isGoodWithDogs());
        ps.setBoolean(12, pet.isGoodWithCats());
        ps.setBoolean(13, pet.isHouseTrained());
        ps.setInt(14, pet.getActivityLevel());
        ps.setString(15, pet.getHealthStatus());
    }

    private Pet extractPetFromResultSet(ResultSet rs) throws SQLException {
        return new Pet(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("species"),
            rs.getString("breed"),
            rs.getInt("age_months"),
            rs.getString("gender"),
            rs.getString("size"),
            rs.getString("health_status"),
            rs.getString("adoption_status"),
            rs.getBoolean("good_with_children"),
            rs.getBoolean("good_with_dogs"),
            rs.getBoolean("good_with_cats"),
            rs.getBoolean("house_trained"),
            rs.getInt("activity_level"),
            rs.getString("description"),
            rs.getString("intake_date")
        );
    }
}
