package org.pawconnect.model;

/**
 * Model class representing an Adopter entity.
 */
public class Adopter {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String housingType; // Apartment, House, Farm, Villa
    private boolean hasYard;
    private String existingPets;
    private boolean previousExperience;

    public Adopter() {}

    public Adopter(int id, String fullName, String email, String phone, String address,
                   String housingType, boolean hasYard, String existingPets, boolean previousExperience) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.housingType = housingType;
        this.hasYard = hasYard;
        this.existingPets = existingPets;
        this.previousExperience = previousExperience;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getHousingType() { return housingType; }
    public void setHousingType(String housingType) { this.housingType = housingType; }

    public boolean isHasYard() { return hasYard; }
    public void setHasYard(boolean hasYard) { this.hasYard = hasYard; }

    public String getExistingPets() { return existingPets; }
    public void setExistingPets(String existingPets) { this.existingPets = existingPets; }

    public boolean isPreviousExperience() { return previousExperience; }
    public void setPreviousExperience(boolean previousExperience) { this.previousExperience = previousExperience; }
}
