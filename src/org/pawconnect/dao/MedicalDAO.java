package org.pawconnect.dao;

import org.pawconnect.model.MedicalRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Clinical and Vaccination Records (Pure GUI In-Memory Implementation).
 */
public class MedicalDAO {
    private final InMemoryDatabase db;

    public MedicalDAO() {
        this.db = InMemoryDatabase.getInstance();
    }

    public List<MedicalRecord> getRecordsForPet(int petId) {
        List<MedicalRecord> result = new ArrayList<>();
        for (MedicalRecord r : db.getMedicalRecords()) {
            if (r.getPetId() == petId) {
                result.add(r);
            }
        }
        return result;
    }

    public List<MedicalRecord> getAllRecords() {
        return new ArrayList<>(db.getMedicalRecords());
    }

    public boolean addRecord(MedicalRecord record) {
        record.setId(db.nextMedId());
        return db.getMedicalRecords().add(record);
    }

    public boolean deleteRecord(int id) {
        return db.getMedicalRecords().removeIf(r -> r.getId() == id);
    }
}
