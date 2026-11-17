package org.pawconnect.util;

import org.pawconnect.controller.AuthController;
import org.pawconnect.dao.InMemoryDatabase;
import org.pawconnect.model.User;
import org.pawconnect.view.LoginFrame;
import org.pawconnect.view.MainFrame;
import org.pawconnect.view.dialogs.PetEntryDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility to capture high-resolution screenshots of all PawConnect Swing GUI frames
 * and panels for academic documentation and project reports.
 */
public class CaptureScreenshots {

    private static final String OUTPUT_DIR = "docs/screenshots";

    public static void main(String[] args) {
        System.out.println("Starting automated UI screenshot capture...");

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        // Initialize in-memory mock data so screens are populated
        InMemoryDatabase.getInstance();

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Capture Login Screen
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setSize(480, 520);
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setVisible(true);
                Thread.sleep(400);
                captureWindow(loginFrame, "01_login_screen.png", 480, 520);
                loginFrame.dispose();

                // 2. Capture Adopter Portal - Available Pets
                User adopter = new User(2, "adopter", "Rahul Sharma", "ADOPTER");
                AuthController.getInstance().setSession(adopter);
                MainFrame adopterFrame = new MainFrame(adopter, null);
                adopterFrame.setSize(1100, 750);
                adopterFrame.setLocationRelativeTo(null);
                adopterFrame.setVisible(true);
                Thread.sleep(400);

                JTabbedPane adopterTabs = findTabbedPane(adopterFrame);
                if (adopterTabs != null) {
                    adopterTabs.setSelectedIndex(0); // Available Pets
                    Thread.sleep(300);
                    captureWindow(adopterFrame, "02_adopter_available_pets.png", 1100, 750);

                    adopterTabs.setSelectedIndex(1); // Smart Match
                    Thread.sleep(300);
                    captureWindow(adopterFrame, "03_adopter_smart_match.png", 1100, 750);

                    adopterTabs.setSelectedIndex(2); // My Applications
                    Thread.sleep(300);
                    captureWindow(adopterFrame, "04_adopter_my_applications.png", 1100, 750);
                }
                adopterFrame.dispose();

                // 3. Capture Admin Portal - Dashboard & Queues
                User admin = new User(1, "admin", "Shelter Manager", "ADMIN");
                AuthController.getInstance().setSession(admin);
                MainFrame adminFrame = new MainFrame(admin, null);
                adminFrame.setSize(1150, 780);
                adminFrame.setLocationRelativeTo(null);
                adminFrame.setVisible(true);
                Thread.sleep(400);

                JTabbedPane adminTabs = findTabbedPane(adminFrame);
                if (adminTabs != null) {
                    adminTabs.setSelectedIndex(0); // Dashboard Metrics
                    Thread.sleep(300);
                    captureWindow(adminFrame, "05_admin_dashboard.png", 1150, 780);

                    adminTabs.setSelectedIndex(1); // Pet Inventory
                    Thread.sleep(300);
                    captureWindow(adminFrame, "06_admin_pet_inventory.png", 1150, 780);

                    adminTabs.setSelectedIndex(2); // Application Queue / Pending Requests
                    Thread.sleep(300);
                    captureWindow(adminFrame, "07_admin_application_queue.png", 1150, 780);

                    adminTabs.setSelectedIndex(3); // Medical Records
                    Thread.sleep(300);
                    captureWindow(adminFrame, "08_admin_medical_records.png", 1150, 780);
                }
                adminFrame.dispose();

                // 4. Capture Add Pet Dialog
                JFrame dummyParent = new JFrame();
                dummyParent.setSize(800, 600);
                dummyParent.setLocationRelativeTo(null);
                dummyParent.setVisible(true);

                PetEntryDialog petDialog = new PetEntryDialog(dummyParent, null);
                petDialog.setSize(560, 680);
                petDialog.setLocationRelativeTo(dummyParent);
                // Non-modal show for screenshot
                petDialog.setModal(false);
                petDialog.setVisible(true);
                Thread.sleep(400);
                captureWindow(petDialog, "09_worker_add_pet_dialog.png", 560, 680);
                petDialog.dispose();
                dummyParent.dispose();

                System.out.println("All screenshots successfully captured into: " + dir.getAbsolutePath());
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureWindow(Window window, String filename, int width, int height) throws Exception {
        window.validate();
        window.repaint();
        Thread.sleep(150);

        Container content = (window instanceof JFrame) ? ((JFrame) window).getContentPane() : ((JDialog) window).getContentPane();
        int w = content.getWidth() > 0 ? content.getWidth() : width;
        int h = content.getHeight() > 0 ? content.getHeight() : height;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        content.printAll(g2);
        g2.dispose();

        File outputFile = new File(OUTPUT_DIR, filename);
        ImageIO.write(img, "png", outputFile);
        System.out.println("Saved screenshot: " + outputFile.getName() + " (" + w + "x" + h + ") - File size: " + outputFile.length() + " bytes");
    }

    private static JTabbedPane findTabbedPane(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTabbedPane) {
                return (JTabbedPane) c;
            } else if (c instanceof Container) {
                JTabbedPane found = findTabbedPane((Container) c);
                if (found != null) return found;
            }
        }
        return null;
    }
}
