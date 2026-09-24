package org.pawconnect.model;

/**
 * Model class representing a Pet entity in the Pet Adoption Management System.
 * Encapsulates core identity, physical attributes, behavioral traits, and status.
 */
public class Pet {
    private int id;
    private String name;
    private String species;
    private String breed;
    private int ageMonths;
    private String gender;
    private String size;
    private String healthStatus;
    private String adoptionStatus; // AVAILABLE, PENDING, ADOPTED, MEDICAL_HOLD
    private boolean goodWithChildren;
    private boolean goodWithDogs;
    private boolean goodWithCats;
    private boolean houseTrained;
    private int activityLevel; // 1 (Low) to 5 (Very High)
    private String description;
    private String intakeDate;

    public Pet() {
        this.adoptionStatus = "AVAILABLE";
        this.healthStatus = "Good";
        this.activityLevel = 3;
    }

    public Pet(int id, String name, String species, String breed, int ageMonths, 
               String gender, String size, String healthStatus, String adoptionStatus,
               boolean goodWithChildren, boolean goodWithDogs, boolean goodWithCats,
               boolean houseTrained, int activityLevel, String description, String intakeDate) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ageMonths = ageMonths;
        this.gender = gender;
        this.size = size;
        this.healthStatus = healthStatus;
        this.adoptionStatus = adoptionStatus;
        this.goodWithChildren = goodWithChildren;
        this.goodWithDogs = goodWithDogs;
        this.goodWithCats = goodWithCats;
        this.houseTrained = houseTrained;
        this.activityLevel = activityLevel;
        this.description = description;
        this.intakeDate = intakeDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAgeMonths() { return ageMonths; }
    public void setAgeMonths(int ageMonths) { this.ageMonths = ageMonths; }

    public String getAgeFormatted() {
        if (ageMonths < 12) {
            return ageMonths + (ageMonths == 1 ? " month" : " months");
        } else {
            int years = ageMonths / 12;
            int rem = ageMonths % 12;
            if (rem == 0) {
                return years + (years == 1 ? " year" : " years");
            } else {
                return years + " yr " + rem + " mo";
            }
        }
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getAdoptionStatus() { return adoptionStatus; }
    public void setAdoptionStatus(String adoptionStatus) { this.adoptionStatus = adoptionStatus; }

    public boolean isGoodWithChildren() { return goodWithChildren; }
    public void setGoodWithChildren(boolean goodWithChildren) { this.goodWithChildren = goodWithChildren; }

    public boolean isGoodWithDogs() { return goodWithDogs; }
    public void setGoodWithDogs(boolean goodWithDogs) { this.goodWithDogs = goodWithDogs; }

    public boolean isGoodWithCats() { return goodWithCats; }
    public void setGoodWithCats(boolean goodWithCats) { this.goodWithCats = goodWithCats; }

    public boolean isHouseTrained() { return houseTrained; }
    public void setHouseTrained(boolean houseTrained) { this.houseTrained = houseTrained; }

    public int getActivityLevel() { return activityLevel; }
    public void setActivityLevel(int activityLevel) { this.activityLevel = activityLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIntakeDate() { return intakeDate; }
    public void setIntakeDate(String intakeDate) { this.intakeDate = intakeDate; }

    @Override
    public String toString() {
        return name + " (" + breed + ", " + getAgeFormatted() + ")";
    }
}
