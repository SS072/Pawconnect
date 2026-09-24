package org.pawconnect.dao;

import org.pawconnect.model.Pet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Pet records (Pure GUI In-Memory Implementation).
 * Operates on fast in-memory structures without external database drivers.
 */
public class PetDAO {
    private final InMemoryDatabase db;

    public PetDAO() {
        this.db = InMemoryDatabase.getInstance();
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(db.getPets());
    }

    public Pet getPetById(int id) {
        for (Pet p : db.getPets()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Pet> searchPets(String query, String speciesFilter, String statusFilter) {
        List<Pet> result = new ArrayList<>();
        String q = (query != null) ? query.trim().toLowerCase() : "";

        for (Pet p : db.getPets()) {
            // Text query match
            boolean matchesQuery = q.isEmpty() ||
                p.getName().toLowerCase().contains(q) ||
                p.getBreed().toLowerCase().contains(q) ||
                (p.getDescription() != null && p.getDescription().toLowerCase().contains(q));

            if (!matchesQuery) continue;

            // Species filter match
            if (speciesFilter != null && !speciesFilter.equalsIgnoreCase("All")) {
                if (!p.getSpecies().equalsIgnoreCase(speciesFilter.trim())) {
                    continue;
                }
            }

            // Status filter match
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
                if (!p.getAdoptionStatus().equalsIgnoreCase(statusFilter.trim())) {
                    continue;
                }
            }

            result.add(p);
        }
        return result;
    }

    public boolean addPet(Pet pet) {
        pet.setId(db.nextPetId());
        return db.getPets().add(pet);
    }

    public boolean updatePet(Pet pet) {
        for (int i = 0; i < db.getPets().size(); i++) {
            if (db.getPets().get(i).getId() == pet.getId()) {
                db.getPets().set(i, pet);
                return true;
            }
        }
        return false;
    }

    public boolean deletePet(int id) {
        return db.getPets().removeIf(p -> p.getId() == id);
    }

    public boolean updatePetStatus(int id, String status) {
        Pet p = getPetById(id);
        if (p != null) {
            p.setAdoptionStatus(status);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getDashboardMetrics() {
        Map<String, Integer> map = new HashMap<>();
        int total = db.getPets().size();
        int available = 0;
        int adopted = 0;
        int pending = 0;
        int medical = 0;

        for (Pet p : db.getPets()) {
            String s = p.getAdoptionStatus().toUpperCase();
            if (s.equals("AVAILABLE")) available++;
            else if (s.equals("ADOPTED")) adopted++;
            else if (s.equals("PENDING")) pending++;

            String h = p.getHealthStatus().toUpperCase();
            if (h.contains("SPECIAL") || s.equals("MEDICAL_HOLD")) {
                medical++;
            }
        }

        map.put("total", total);
        map.put("available", available);
        map.put("adopted", adopted);
        map.put("pending", pending);
        map.put("medical", medical);
        return map;
    }
}
