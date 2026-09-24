package org.pawconnect.view.dialogs;

import org.pawconnect.model.MedicalRecord;
import org.pawconnect.model.Pet;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modal JDialog for logging veterinary examinations, treatments, and vaccinations.
 */
public class MedicalEntryDialog extends JDialog {
    private final List<Pet> availablePets;
    private final Integer presetPetId;
    private MedicalRecord record;
    private boolean saved = false;

    private JComboBox<Pet> petCombo;
    private JTextField dateField;
    private JComboBox<String> typeCombo;
    private JTextField titleField;
    private JTextField vetField;
    private JTextField nextDateField;
    private JTextArea descArea;

    public MedicalEntryDialog(Frame owner, List<Pet> pets, Integer presetPetId) {
        super(owner, "Veterinary Record Entry - Clinical & Vaccination Card", true);
        this.availablePets = pets;
        this.presetPetId = presetPetId;

        initComponents();

        setSize(540, 580);
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
        JLabel titleLabel = new JLabel("Log Clinical / Vaccination Record");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        petCombo = new JComboBox<>(availablePets.toArray(new Pet[0]));
        if (presetPetId != null) {
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                if (petCombo.getItemAt(i).getId() == presetPetId) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        dateField = new JTextField(LocalDate.now().toString(), 15);
        typeCombo = new JComboBox<>(new String[]{"VACCINATION", "ROUTINE_CHECKUP", "SURGERY", "MEDICATION", "DIAGNOSTIC"});
        titleField = new JTextField(15);
        vetField = new JTextField("Dr. Sarah Jenkins, DVM", 15);
        nextDateField = new JTextField(LocalDate.now().plusYears(1).toString(), 15);

        descArea = new JTextArea(4, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(makeLabel("Target Pet:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(petCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(makeLabel("Record Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(makeLabel("Record Classification:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(makeLabel("Treatment / Vaccine Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(makeLabel("Attending Veterinarian:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(vetField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(makeLabel("Next Due Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nextDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(makeLabel("Clinical Observations:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(descScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.COLOR_BG_LIGHT);

        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = UITheme.createPrimaryButton("Save Medical Entry");
        saveButton.addActionListener(e -> onSave());

        actionPanel.add(cancelButton);
        actionPanel.add(saveButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_MAIN);
        return l;
    }

    private void onSave() {
        Pet selectedPet = (Pet) petCombo.getSelectedItem();
        if (selectedPet == null) {
            JOptionPane.showMessageDialog(this, "A pet must be selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title/Treatment description cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return;
        }

        record = new MedicalRecord(
            0,
            selectedPet.getId(),
            selectedPet.getName(),
            dateField.getText().trim(),
            (String) typeCombo.getSelectedItem(),
            title,
            descArea.getText().trim(),
            vetField.getText().trim(),
            nextDateField.getText().trim()
        );

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public MedicalRecord getRecord() {
        return record;
    }
}
