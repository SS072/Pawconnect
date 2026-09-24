package org.pawconnect.model;

/**
 * Model class representing an Adoption Application submitted by a prospective adopter.
 */
public class AdoptionApplication {
    private int id;
    private int petId;
    private String petName;
    private String petBreed;
    private String applicantName;
    private String applicantPhone;
    private String applicantEmail;
    private String housingType; // Apartment, House, Farm, Villa
    private boolean hasYard;
    private boolean hasChildren;
    private String otherPets; // e.g. "1 Dog", "2 Cats", "None"
    private boolean petExperience;
    private String reasonForAdoption;
    private ApplicationStatus status;
    private String submissionDate; // YYYY-MM-DD
    private String reviewNotes;

    public AdoptionApplication() {
        this.status = ApplicationStatus.PENDING;
    }

    public AdoptionApplication(int id, int petId, String petName, String petBreed,
                               String applicantName, String applicantPhone, String applicantEmail,
                               String housingType, boolean hasYard, boolean hasChildren,
                               String otherPets, boolean petExperience, String reasonForAdoption,
                               ApplicationStatus status, String submissionDate, String reviewNotes) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.petBreed = petBreed;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.applicantEmail = applicantEmail;
        this.housingType = housingType;
        this.hasYard = hasYard;
        this.hasChildren = hasChildren;
        this.otherPets = otherPets;
        this.petExperience = petExperience;
        this.reasonForAdoption = reasonForAdoption;
        this.status = status;
        this.submissionDate = submissionDate;
        this.reviewNotes = reviewNotes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPetBreed() { return petBreed; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getHousingType() { return housingType; }
    public void setHousingType(String housingType) { this.housingType = housingType; }

    public boolean isHasYard() { return hasYard; }
    public void setHasYard(boolean hasYard) { this.hasYard = hasYard; }

    public boolean isHasChildren() { return hasChildren; }
    public void setHasChildren(boolean hasChildren) { this.hasChildren = hasChildren; }

    public String getOtherPets() { return otherPets; }
    public void setOtherPets(String otherPets) { this.otherPets = otherPets; }

    public boolean isPetExperience() { return petExperience; }
    public void setPetExperience(boolean petExperience) { this.petExperience = petExperience; }

    public String getReasonForAdoption() { return reasonForAdoption; }
    public void setReasonForAdoption(String reasonForAdoption) { this.reasonForAdoption = reasonForAdoption; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }

    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
}
