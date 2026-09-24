package org.pawconnect.view;

import org.pawconnect.controller.AuthController;
import org.pawconnect.model.User;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dedicated Login Window for the PawConnect Pet Adoption Management System.
 * Features clean role switching (Adopter Portal vs Shelter Admin Portal)
 * and 1-click Quick Demo logins for instant viva evaluation.
 */
public class LoginFrame extends JFrame {
    private String selectedPortal = "ADOPTER"; // ADOPTER or ADMIN

    private JButton adopterPortalBtn;
    private JButton adminPortalBtn;
    private JLabel portalDescLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super("PawConnect - Pet Adoption Management System | Sign In");

        initComponents();

        setSize(480, 460);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.COLOR_BG_CANVAS);

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
        headerPanel.setBackground(UITheme.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel brandLabel = new JLabel("PAWCONNECT", SwingConstants.CENTER);
        brandLabel.setFont(UITheme.FONT_BRAND);
        brandLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Pet Adoption Management System", SwingConstants.CENTER);
        subLabel.setFont(UITheme.FONT_REGULAR);
        subLabel.setForeground(new Color(204, 251, 241));

        headerPanel.add(brandLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Container
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel cardPanel = UITheme.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(410, 310));

        // 1. Role / Portal Toggle Row
        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        togglePanel.setOpaque(false);

        adopterPortalBtn = UITheme.createPrimaryButton("Adopter Portal");
        adminPortalBtn = UITheme.createSecondaryButton("Shelter Admin Portal");

        adopterPortalBtn.addActionListener(e -> selectPortal("ADOPTER"));
        adminPortalBtn.addActionListener(e -> selectPortal("ADMIN"));

        togglePanel.add(adopterPortalBtn);
        togglePanel.add(adminPortalBtn);

        portalDescLabel = new JLabel("<html><center>Browse adoptable animals, run compatibility matching, and submit adoption petitions.</center></html>", SwingConstants.CENTER);
        portalDescLabel.setFont(UITheme.FONT_SMALL);
        portalDescLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
        portalDescLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        portalDescLabel.setBorder(new EmptyBorder(8, 4, 12, 4));

        // 2. Input Fields
        JPanel formGrid = new JPanel(new GridLayout(4, 1, 4, 4));
        formGrid.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        userLabel.setForeground(UITheme.COLOR_NAVY);
        usernameField = UITheme.createPaddedTextField(15);
        usernameField.setText("adopter");

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        passLabel.setForeground(UITheme.COLOR_NAVY);
        passwordField = UITheme.createPaddedPasswordField(15);
        passwordField.setText("user123");
        passwordField.addActionListener(e -> onLogin());

        formGrid.add(userLabel);
        formGrid.add(usernameField);
        formGrid.add(passLabel);
        formGrid.add(passwordField);

        // 3. Login Action Button
        JButton loginBtn = UITheme.createPrimaryButton("Sign In to PawConnect");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(380, 38));
        loginBtn.addActionListener(e -> onLogin());

        // Assemble into Card
        cardPanel.add(togglePanel);
        cardPanel.add(portalDescLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(formGrid);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(loginBtn);

        centerContainer.add(cardPanel);
        add(centerContainer, BorderLayout.CENTER);
    }

    private void selectPortal(String portal) {
        this.selectedPortal = portal;
        if ("ADOPTER".equals(portal)) {
            adopterPortalBtn.setBackground(UITheme.COLOR_PRIMARY);
            adopterPortalBtn.setForeground(Color.WHITE);
            adminPortalBtn.setBackground(Color.WHITE);
            adminPortalBtn.setForeground(UITheme.COLOR_TEXT_MAIN);
            portalDescLabel.setText("<html><center>Browse adoptable animals, run compatibility matching, and submit adoption petitions.</center></html>");
            usernameField.setText("adopter");
            passwordField.setText("user123");
        } else {
            adminPortalBtn.setBackground(UITheme.COLOR_PRIMARY);
            adminPortalBtn.setForeground(Color.WHITE);
            adopterPortalBtn.setBackground(Color.WHITE);
            adopterPortalBtn.setForeground(UITheme.COLOR_TEXT_MAIN);
            portalDescLabel.setText("<html><center>Access shelter operations, animal intake, application reviews, and medical logs.</center></html>");
            usernameField.setText("admin");
            passwordField.setText("admin123");
        }
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        boolean ok = AuthController.getInstance().login(username, password);
        if (ok) {
            launchPortal(AuthController.getInstance().getCurrentUser());
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Invalid username or password.\n\nAccounts:\n• Adopter: adopter / user123\n• Admin: admin / admin123",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void launchPortal(User user) {
        AuthController.getInstance().setSession(user);
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(user, () -> {
                LoginFrame newLogin = new LoginFrame();
                newLogin.setVisible(true);
            });
            mainFrame.setVisible(true);
        });
    }
}
