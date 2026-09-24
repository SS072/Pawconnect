package org.pawconnect.dao;

import org.pawconnect.model.User;

/**
 * Data Access Object for User Authentication (Pure GUI In-Memory Implementation).
 * Forgiving authentication supporting usernames, demo aliases, and case-insensitivity.
 */
public class UserDAO {
    private final InMemoryDatabase db;

    public UserDAO() {
        this.db = InMemoryDatabase.getInstance();
    }

    public User authenticate(String username, String password) {
        if (username == null) return null;
        String uClean = username.trim().toLowerCase();
        String pClean = (password != null) ? password.trim() : "";

        // Adopter aliases: "adopter", "demo adopter", "rahul", "demo"
        if (uClean.equals("adopter") || uClean.equals("demo adopter") || uClean.equals("rahul") || uClean.equals("user")) {
            if (pClean.isEmpty() || pClean.equals("user123") || pClean.equals("adopter") || pClean.equals("password")) {
                return new User(2, "adopter", "Rahul Sharma", "ADOPTER");
            }
        }

        // Admin aliases: "admin", "demo admin", "director", "staff"
        if (uClean.equals("admin") || uClean.equals("demo admin") || uClean.equals("director") || uClean.equals("staff")) {
            if (pClean.isEmpty() || pClean.equals("admin123") || pClean.equals("admin") || pClean.equals("password")) {
                return new User(1, "admin", "Dr. Sarah Jenkins (Shelter Director)", "ADMIN");
            }
        }

        // Generic check across database users
        for (User u : db.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(uClean)) {
                return u;
            }
        }

        // Fallback: If username contains "adopt", treat as Adopter
        if (uClean.contains("adopt")) {
            return new User(2, "adopter", "Rahul Sharma", "ADOPTER");
        }

        return null;
    }
}
