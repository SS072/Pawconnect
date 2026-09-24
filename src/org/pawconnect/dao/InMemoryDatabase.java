package org.pawconnect.dao;

import org.pawconnect.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory Data Store for Pure GUI Mode.
 * Eliminates all external database driver dependencies and connection hurdles
 * while maintaining the exact Model-View-Controller architecture.
 */
public class InMemoryDatabase {
    private static InMemoryDatabase instance;

    private final List<Pet> pets = new CopyOnWriteArrayList<>();
    private final List<AdoptionApplication> applications = new CopyOnWriteArrayList<>();
    private final List<MedicalRecord> medicalRecords = new CopyOnWriteArrayList<>();
    private final List<User> users = new CopyOnWriteArrayList<>();

    private final AtomicInteger petIdCounter = new AtomicInteger(0);
    private final AtomicInteger appIdCounter = new AtomicInteger(0);
    private final AtomicInteger medIdCounter = new AtomicInteger(0);

    private InMemoryDatabase() {
        seedInitialData();
    }

    public static synchronized InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    public List<Pet> getPets() { return pets; }
    public List<AdoptionApplication> getApplications() { return applications; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public List<User> getUsers() { return users; }

    public int nextPetId() { return petIdCounter.incrementAndGet(); }
    public int nextAppId() { return appIdCounter.incrementAndGet(); }
    public int nextMedId() { return medIdCounter.incrementAndGet(); }

    private void seedInitialData() {
        // Users
        users.add(new User(1, "admin", "Dr. Sarah Jenkins (Shelter Director)", "ADMIN"));
        users.add(new User(2, "adopter", "Rahul Sharma", "ADOPTER"));

        // Sample Pets
        pets.add(new Pet(nextPetId(), "Bruno", "Dog", "Labrador Retriever", 24, "Male", "Large", "Excellent", "AVAILABLE",
            true, true, false, true, 4, "Friendly, high energy, loves outdoor fetch, obedient with basic commands.", "2026-06-15"));

        pets.add(new Pet(nextPetId(), "Luna", "Cat", "Domestic Shorthair", 14, "Female", "Small", "Good", "AVAILABLE",
            true, false, true, true, 2, "Gentle, quiet companion, litter-trained, ideal for calm apartment living.", "2026-07-02"));

        pets.add(new Pet(nextPetId(), "Max", "Dog", "Beagle", 36, "Male", "Medium", "Good", "AVAILABLE",
            true, true, true, true, 3, "Affectionate scent hound, very social with other canines, loves walks.", "2026-07-20"));

        pets.add(new Pet(nextPetId(), "Bella", "Dog", "Golden Retriever", 48, "Female", "Large", "Excellent", "PENDING",
            true, true, true, true, 3, "Calm family dog, certified gentle demeanor, excellent around young children.", "2026-08-01"));

        pets.add(new Pet(nextPetId(), "Milo", "Cat", "Tabby", 8, "Male", "Small", "Excellent", "AVAILABLE",
            true, true, true, true, 3, "Playful kitten, curious explorer, fully vaccinated and litter trained.", "2026-08-10"));

        pets.add(new Pet(nextPetId(), "Rocky", "Dog", "German Shepherd", 30, "Male", "Large", "Special Needs", "AVAILABLE",
            false, true, false, true, 5, "Intelligent, protective, requires experienced handler and active yard. Hip dysplasia monitored.", "2026-08-15"));

        pets.add(new Pet(nextPetId(), "Daisy", "Rabbit", "Holland Lop", 12, "Female", "Small", "Good", "AVAILABLE",
            true, false, false, true, 1, "Gentle herbivore, indoor habitat required, accustomed to gentle handling.", "2026-09-01"));

        pets.add(new Pet(nextPetId(), "Charlie", "Dog", "Cocker Spaniel", 60, "Male", "Medium", "Good", "ADOPTED",
            true, true, true, true, 2, "Adopted into a quiet family home. Affectionate lap companion.", "2026-05-10"));

        // Sample Applications
        applications.add(new AdoptionApplication(
            nextAppId(), 4, "Bella", "Golden Retriever",
            "Rahul Sharma", "+91 98401 23456", "rahul.sharma@example.com",
            "Independent House", true, true, "None", true,
            "We have a large fenced garden and children aged 7 and 10 who love Golden Retrievers.",
            ApplicationStatus.PENDING, "2026-09-15", "Initial screening completed. Home visit recommended."
        ));

        applications.add(new AdoptionApplication(
            nextAppId(), 1, "Bruno", "Labrador Retriever",
            "Priya Sundaram", "+91 97890 54321", "priya.sundaram@example.com",
            "Apartment", false, false, "1 Cat", true,
            "Experienced dog lover looking for an active companion for weekend runs.",
            ApplicationStatus.UNDER_REVIEW, "2026-09-18", "Reviewing compatibility with existing cat."
        ));

        // Sample Medical Records
        medicalRecords.add(new MedicalRecord(
            nextMedId(), 1, "Bruno", "2026-06-18", "VACCINATION",
            "Annual Rabies & DHPP Booster",
            "Administered subcutaneous Rabvac 3 and Nobivac DHPP. No adverse reaction observed.",
            "Dr. K. Ramanathan, MVSc", "2027-06-18"
        ));

        medicalRecords.add(new MedicalRecord(
            nextMedId(), 1, "Bruno", "2026-06-20", "ROUTINE_CHECKUP",
            "Comprehensive Physical & Deworming",
            "Heart and lungs clear. Weight: 28.5 kg. Fenbendazole deworming protocol administered.",
            "Dr. K. Ramanathan, MVSc", "2026-12-20"
        ));

        medicalRecords.add(new MedicalRecord(
            nextMedId(), 6, "Rocky", "2026-08-20", "DIAGNOSTIC",
            "Orthopedic Evaluation - Hip Joint",
            "Radiographs confirm mild bilateral hip dysplasia. Prescribed joint supplement (Glucosamine/Chondroitin).",
            "Dr. Sarah Jenkins, DVM", "2026-11-20"
        ));
    }
}
