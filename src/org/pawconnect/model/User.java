package org.pawconnect.model;

/**
 * Model class representing an authorized system user (Admin, Staff, or Adopter).
 */
public class User {
    private int id;
    private String username;
    private String fullName;
    private String role; // ADMIN, STAFF, ADOPTER

    public User(int id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role) || "STAFF".equalsIgnoreCase(role);
    }
}
