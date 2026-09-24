package org.pawconnect.controller;

import org.pawconnect.dao.PetDAO;
import org.pawconnect.model.Pet;
import java.util.List;
import java.util.Map;

/**
 * Controller mediating pet inventory operations between the View layer and PetDAO.
 */
public class PetController {
    private final PetDAO petDAO;

    public PetController() {
        this.petDAO = new PetDAO();
    }

    public List<Pet> getAllPets() {
        return petDAO.getAllPets();
    }

    public Pet getPetById(int id) {
        return petDAO.getPetById(id);
    }

    public List<Pet> filterPets(String query, String species, String status) {
        return petDAO.searchPets(query, species, status);
    }

    public boolean createPet(Pet pet) {
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name is mandatory.");
        }
        if (pet.getBreed() == null || pet.getBreed().trim().isEmpty()) {
            throw new IllegalArgumentException("Pet breed is mandatory.");
        }
        if (pet.getAgeMonths() < 0) {
            throw new IllegalArgumentException("Age in months must be positive.");
        }
        return petDAO.addPet(pet);
    }

    public boolean updatePet(Pet pet) {
        if (pet.getId() <= 0) {
            throw new IllegalArgumentException("Invalid Pet ID for update.");
        }
        return petDAO.updatePet(pet);
    }

    public boolean removePet(int id) {
        return petDAO.deletePet(id);
    }

    public boolean updatePetStatus(int id, String status) {
        return petDAO.updatePetStatus(id, status);
    }

    public Map<String, Integer> getDashboardMetrics() {
        return petDAO.getDashboardMetrics();
    }
}
