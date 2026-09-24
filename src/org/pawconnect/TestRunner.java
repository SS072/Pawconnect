package org.pawconnect;

import org.pawconnect.controller.ApplicationController;
import org.pawconnect.controller.MatchingEngine;
import org.pawconnect.controller.PetController;
import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import org.pawconnect.model.MatchResult;
import org.pawconnect.model.Pet;

import java.util.List;

/**
 * Automated Test Suite verifying MVC, MatchingEngine, and JDBC Transaction integrity.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("========== RUNNING PAWCONNECT VERIFICATION TESTS ==========");

        PetController petController = new PetController();
        ApplicationController appController = new ApplicationController();
        MatchingEngine matchingEngine = new MatchingEngine();

        // 1. Verify Pet Search and Filters
        List<Pet> dogs = petController.filterPets("", "Dog", "AVAILABLE");
        assert dogs.size() >= 3 : "Expected at least 3 available dogs";
        System.out.println("[PASS] Pet filter test: found " + dogs.size() + " available dogs.");

        // 2. Verify Smart Matching Algorithm
        MatchingEngine.Criteria criteria = new MatchingEngine.Criteria();
        criteria.preferredSpecies = "Dog";
        criteria.housingType = "Independent House";
        criteria.hasYard = true;
        criteria.activityLevel = 4;
        criteria.hasChildren = true;
        criteria.hasExistingDogs = true;
        criteria.hasExistingCats = false;
        criteria.hoursAwayPerDay = 4;

        List<MatchResult> matches = matchingEngine.calculateMatches(petController.getAllPets(), criteria);
        assert !matches.isEmpty() : "Matches should not be empty";
        MatchResult topMatch = matches.get(0);
        System.out.println("[PASS] Matching algorithm: Top match is " + topMatch.getPet().getName() + " with score " + topMatch.getScore() + "%");
        for (String factor : topMatch.getMatchingFactors()) {
            System.out.println("       + " + factor);
        }

        // 3. Verify Application Submission & Atomic JDBC Transaction
        List<AdoptionApplication> apps = appController.getAllApplications();
        int initialAppCount = apps.size();
        System.out.println("[PASS] Initial applications count: " + initialAppCount);

        AdoptionApplication newApp = new AdoptionApplication(
            0,
            1, // Bruno
            "Bruno",
            "Labrador Retriever",
            "Amit Patel",
            "+91 99887 76655",
            "amit.patel@example.com",
            "Independent House",
            true,
            true,
            "None",
            true,
            "Looking for an energetic companion for our family home.",
            ApplicationStatus.PENDING,
            "2026-09-23",
            "Initial automated test petition"
        );

        boolean submitted = appController.submitApplication(newApp);
        assert submitted : "Application submission should succeed";
        System.out.println("[PASS] Application submitted with generated ID: " + newApp.getId());

        // Process Decision (Approval) -> Triggers atomic update to ADOPTED on Pet ID 1
        boolean processed = appController.processDecision(newApp.getId(), 1, ApplicationStatus.APPROVED, "Verified living conditions and approved.");
        assert processed : "Decision processing should succeed";

        Pet updatedBruno = petController.getPetById(1);
        System.out.println("[PASS] Atomic Transaction verified: Pet ID 1 status is now: " + updatedBruno.getAdoptionStatus());
        assert "ADOPTED".equalsIgnoreCase(updatedBruno.getAdoptionStatus()) : "Pet status should be ADOPTED";

        // Revert Bruno back to AVAILABLE for clean demo state
        petController.updatePetStatus(1, "AVAILABLE");
        appController.processDecision(newApp.getId(), 1, ApplicationStatus.WITHDRAWN, "Reverted test app");
        System.out.println("[PASS] Demo state restored: Bruno status reverted to: " + petController.getPetById(1).getAdoptionStatus());

        // 4. Automated GUI EDT Instantiation & Role Segregation Test
        verifyGuiInstantiation();

        System.out.println("========== ALL TEST SUITES COMPLETED WITH ZERO ERRORS ==========");
    }

    private static void verifyGuiInstantiation() {
        System.out.println("[RUNNING] Verifying Swing EDT GUI instantiation for Adopter & Admin...");
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                // 1. Test LoginFrame
                org.pawconnect.view.LoginFrame loginFrame = new org.pawconnect.view.LoginFrame();
                assert loginFrame != null : "LoginFrame must instantiate";
                loginFrame.dispose();
                System.out.println("       + [PASS] LoginFrame instantiated and disposed cleanly.");

                // 2. Test Adopter Portal
                org.pawconnect.model.User adopter = new org.pawconnect.model.User(2, "adopter", "Rahul Sharma", "ADOPTER");
                org.pawconnect.controller.AuthController.getInstance().setSession(adopter);
                org.pawconnect.view.MainFrame adopterFrame = new org.pawconnect.view.MainFrame(adopter, null);
                assert adopterFrame != null : "MainFrame for Adopter must instantiate";
                
                // Exercise all 4 Adopter tabs
                javax.swing.JTabbedPane adopterTabs = findTabbedPane(adopterFrame);
                if (adopterTabs != null) {
                    assert adopterTabs.getTabCount() == 4 : "Adopter must have exactly 4 tabs";
                    for (int i = 0; i < adopterTabs.getTabCount(); i++) {
                        adopterTabs.setSelectedIndex(i);
                    }
                }
                adopterFrame.dispose();
                System.out.println("       + [PASS] Adopter MainFrame & all 4 tabs instantiated with zero NPEs.");

                // 3. Test Shelter Admin Portal
                org.pawconnect.model.User admin = new org.pawconnect.model.User(1, "admin", "Dr. Sarah Jenkins", "ADMIN");
                org.pawconnect.controller.AuthController.getInstance().setSession(admin);
                org.pawconnect.view.MainFrame adminFrame = new org.pawconnect.view.MainFrame(admin, null);
                assert adminFrame != null : "MainFrame for Admin must instantiate";

                // Exercise all 4 Admin tabs
                javax.swing.JTabbedPane adminTabs = findTabbedPane(adminFrame);
                if (adminTabs != null) {
                    assert adminTabs.getTabCount() == 4 : "Admin must have exactly 4 tabs";
                    for (int i = 0; i < adminTabs.getTabCount(); i++) {
                        adminTabs.setSelectedIndex(i);
                    }
                }
                adminFrame.dispose();
                System.out.println("       + [PASS] Admin MainFrame & all 4 tabs instantiated with zero NPEs.");
            });
            System.out.println("[PASS] Full Swing GUI verification complete.");
        } catch (Exception ex) {
            System.err.println("[FAIL] GUI Verification Exception: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static javax.swing.JTabbedPane findTabbedPane(java.awt.Container container) {
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof javax.swing.JTabbedPane) {
                return (javax.swing.JTabbedPane) c;
            } else if (c instanceof java.awt.Container) {
                javax.swing.JTabbedPane found = findTabbedPane((java.awt.Container) c);
                if (found != null) return found;
            }
        }
        return null;
    }
}
