package org.pawconnect.model;

/**
 * Model class representing a Clinical or Vaccination Record for a Pet.
 */
public class MedicalRecord {
    private int id;
    private int petId;
    private String petName;
    private String recordDate; // YYYY-MM-DD
    private String recordType; // VACCINATION, SURGERY, ROUTINE_CHECKUP, MEDICATION, ALLERGY
    private String title;
    private String description;
    private String veterinarian;
    private String nextDueDate; // YYYY-MM-DD or "N/A"

    public MedicalRecord() {}

    public MedicalRecord(int id, int petId, String petName, String recordDate, String recordType,
                         String title, String description, String veterinarian, String nextDueDate) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.recordDate = recordDate;
        this.recordType = recordType;
        this.title = title;
        this.description = description;
        this.veterinarian = veterinarian;
        this.nextDueDate = nextDueDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVeterinarian() { return veterinarian; }
    public void setVeterinarian(String veterinarian) { this.veterinarian = veterinarian; }

    public String getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(String nextDueDate) { this.nextDueDate = nextDueDate; }
}
