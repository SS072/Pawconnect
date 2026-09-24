package org.pawconnect;

import org.pawconnect.dao.InMemoryDatabase;
import org.pawconnect.dao.PetDAO;
import org.pawconnect.model.Pet;
import org.pawconnect.model.User;
import org.pawconnect.view.LoginFrame;
import org.pawconnect.view.MainFrame;

import javax.swing.*;
import java.util.List;

/**
 * Main application entrypoint for PawConnect Pet Adoption Management System.
 * Configures Desktop Native Look & Feel and launches the pure Swing GUI on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Headless diagnostic flag for automated test suites
        if (args.length > 0 && "--headless-check".equalsIgnoreCase(args[0])) {
            runHeadlessSanityCheck();
            return;
        }

        // Configure Native System Desktop Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Note: Using default Swing Look and Feel (" + e.getMessage() + ")");
        }

        final String targetMode = (args.length > 0) ? args[0].toLowerCase() : "";

        // Launch GUI on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize in-memory seed records
                InMemoryDatabase.getInstance();

                if (targetMode.contains("adopt") || targetMode.contains("user")) {
                    // Direct launch into Adopter Portal
                    User adopter = new User(2, "adopter", "Rahul Sharma", "ADOPTER");
                    org.pawconnect.controller.AuthController.getInstance().setSession(adopter);
                    MainFrame frame = new MainFrame(adopter, () -> {
                        new LoginFrame().setVisible(true);
                    });
                    frame.setVisible(true);
                    System.out.println("PawConnect Adopter Portal launched directly.");
                } else if (targetMode.contains("admin") || targetMode.contains("staff")) {
                    // Direct launch into Shelter Admin Portal
                    User admin = new User(1, "admin", "Dr. Sarah Jenkins (Shelter Director)", "ADMIN");
                    org.pawconnect.controller.AuthController.getInstance().setSession(admin);
                    MainFrame frame = new MainFrame(admin, () -> {
                        new LoginFrame().setVisible(true);
                    });
                    frame.setVisible(true);
                    System.out.println("PawConnect Shelter Admin Portal launched directly.");
                } else {
                    // Open initial dedicated LoginFrame
                    LoginFrame loginFrame = new org.pawconnect.view.LoginFrame();
                    loginFrame.setVisible(true);
                    System.out.println("PawConnect LoginFrame initialized and running.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error occurred during application startup:\n" + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static void runHeadlessSanityCheck() {
        System.out.println("=== PAWCONNECT PURE GUI SANITY CHECK ===");
        try {
            InMemoryDatabase db = InMemoryDatabase.getInstance();
            System.out.println("[PASS] InMemoryDatabase initialized with sample records.");

            PetDAO petDAO = new PetDAO();
            List<Pet> pets = petDAO.getAllPets();
            System.out.println("[PASS] Retrieved " + pets.size() + " pets from in-memory catalog.");
            for (Pet p : pets) {
                System.out.println("       - ID: " + p.getId() + " | " + p.getName() + " (" + p.getBreed() + ") -> Status: " + p.getAdoptionStatus());
            }

            System.out.println("=== ALL SANITY CHECKS PASSED SUCCESSFULLY ===");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("[FAIL] Sanity check failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
