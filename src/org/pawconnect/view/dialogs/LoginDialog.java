package org.pawconnect.view.dialogs;

import org.pawconnect.controller.AuthController;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal JDialog for Role-Based Authentication (Adopter vs Shelter Staff/Admin).
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean authenticated = false;

    public LoginDialog(Frame owner) {
        super(owner, "Authentication - PawConnect System Security", true);

        initComponents();

        setSize(420, 320);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(UITheme.COLOR_BG_LIGHT);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel titleLabel = new JLabel("System Access Verification");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField("admin", 15);
        passwordField = new JPasswordField("admin123", 15);

        // Quick profile loader
        JComboBox<String> presetCombo = new JComboBox<>(new String[]{
            "Shelter Director (admin / admin123)",
            "Registered Adopter (adopter / user123)"
        });
        presetCombo.addActionListener(e -> {
            int idx = presetCombo.getSelectedIndex();
            if (idx == 0) {
                usernameField.setText("admin");
                passwordField.setText("admin123");
            } else {
                usernameField.setText("adopter");
                passwordField.setText("user123");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Quick Profile:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(presetCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.COLOR_BG_LIGHT);

        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton loginButton = UITheme.createPrimaryButton("Sign In");
        loginButton.addActionListener(e -> onLogin());

        actionPanel.add(cancelButton);
        actionPanel.add(loginButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        boolean ok = AuthController.getInstance().login(username, password);
        if (ok) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please re-enter.", "Authentication Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
