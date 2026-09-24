package org.pawconnect.view.dialogs;

import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import org.pawconnect.model.Pet;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Modal JDialog for Adopters to fill out and submit an Adoption Application.
 * Incorporates JRadioButton, ButtonGroup, JComboBox, JCheckBox, and JTextArea within GridBagLayout.
 */
public class ApplicationDialog extends JDialog {
    private final Pet pet;
    private AdoptionApplication application;
    private boolean submitted = false;

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> housingCombo;
    private JRadioButton yardYesRadio;
    private JRadioButton yardNoRadio;
    private JRadioButton kidsYesRadio;
    private JRadioButton kidsNoRadio;
    private JTextField otherPetsField;
    private JCheckBox experienceCheck;
    private JTextArea reasonArea;

    public ApplicationDialog(Frame owner, Pet pet) {
        super(owner, "Adoption Petition - Target: " + (pet != null ? pet.getName() : "Pet"), true);
        this.pet = pet;

        initComponents();

        setSize(580, 680);
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

        String targetInfo = (pet != null) ? "Adoption Application for " + pet.getName() + " (" + pet.getBreed() + ")" : "General Adoption Application";
        JLabel titleLabel = new JLabel(targetInfo);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);

        housingCombo = new JComboBox<>(new String[]{"Apartment", "Independent House", "Villa with Garden", "Farm / Rural"});

        // Yard Radio Buttons
        yardYesRadio = new JRadioButton("Yes", true);
        yardNoRadio = new JRadioButton("No", false);
        yardYesRadio.setBackground(Color.WHITE);
        yardNoRadio.setBackground(Color.WHITE);
        ButtonGroup yardGroup = new ButtonGroup();
        yardGroup.add(yardYesRadio);
        yardGroup.add(yardNoRadio);
        JPanel yardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        yardPanel.setBackground(Color.WHITE);
        yardPanel.add(yardYesRadio);
        yardPanel.add(yardNoRadio);

        // Kids Radio Buttons
        kidsYesRadio = new JRadioButton("Yes", false);
        kidsNoRadio = new JRadioButton("No", true);
        kidsYesRadio.setBackground(Color.WHITE);
        kidsNoRadio.setBackground(Color.WHITE);
        ButtonGroup kidsGroup = new ButtonGroup();
        kidsGroup.add(kidsYesRadio);
        kidsGroup.add(kidsNoRadio);
        JPanel kidsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        kidsPanel.setBackground(Color.WHITE);
        kidsPanel.add(kidsYesRadio);
        kidsPanel.add(kidsNoRadio);

        otherPetsField = new JTextField("None", 15);
        experienceCheck = new JCheckBox("I have prior experience owning or fostering companion animals");
        experienceCheck.setBackground(Color.WHITE);

        reasonArea = new JTextArea(4, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);

        // Grid placements
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Full Legal Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("Primary Contact Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Email Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createLabel("Residence Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(housingCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createLabel("Fenced Yard Available:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(yardPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(createLabel("Children in Household:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(kidsPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(createLabel("Other Resident Pets:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(otherPetsField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(experienceCheck, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0;
        formPanel.add(createLabel("Statement of Care:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(reasonScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.COLOR_BG_LIGHT);

        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton submitButton = UITheme.createPrimaryButton("Submit Adoption Application");
        submitButton.addActionListener(e -> onSubmit());

        actionPanel.add(cancelButton);
        actionPanel.add(submitButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_REGULAR_BOLD);
        lbl.setForeground(UITheme.COLOR_TEXT_MAIN);
        return lbl;
    }

    private void onSubmit() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Applicant Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact Phone is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "A valid email address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a brief statement of why you want to adopt this pet.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            reasonArea.requestFocus();
            return;
        }

        this.application = new AdoptionApplication(
            0,
            pet != null ? pet.getId() : 1,
            pet != null ? pet.getName() : "Unknown",
            pet != null ? pet.getBreed() : "Unknown",
            name,
            phone,
            email,
            (String) housingCombo.getSelectedItem(),
            yardYesRadio.isSelected(),
            kidsYesRadio.isSelected(),
            otherPetsField.getText().trim(),
            experienceCheck.isSelected(),
            reason,
            ApplicationStatus.PENDING,
            LocalDate.now().toString(),
            "Pending administrative review"
        );

        submitted = true;
        dispose();
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public AdoptionApplication getApplication() {
        return application;
    }
}
