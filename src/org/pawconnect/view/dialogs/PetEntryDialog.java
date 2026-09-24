package org.pawconnect.view.dialogs;

import org.pawconnect.model.Pet;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Modal JDialog for Adding or Editing Pet records.
 * Demonstrates precision Form Layout using GridBagLayout, JComboBox, JSpinner, JSlider, JCheckBox, and JTextArea.
 */
public class PetEntryDialog extends JDialog {
    private final Pet pet;
    private boolean saved = false;

    private JTextField nameField;
    private JComboBox<String> speciesCombo;
    private JTextField breedField;
    private JSpinner ageSpinner;
    private JComboBox<String> genderCombo;
    private JComboBox<String> sizeCombo;
    private JComboBox<String> healthCombo;
    private JComboBox<String> statusCombo;
    private JCheckBox kidsCheck;
    private JCheckBox dogsCheck;
    private JCheckBox catsCheck;
    private JCheckBox trainedCheck;
    private JSlider activitySlider;
    private JTextArea descArea;

    public PetEntryDialog(Frame owner, Pet pet) {
        super(owner, pet == null ? "Add New Pet - PawConnect Shelter System" : "Edit Pet Record - ID: " + pet.getId(), true);
        this.pet = (pet != null) ? pet : new Pet();

        initComponents();
        populateFields();

        setSize(560, 680);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.COLOR_BG_LIGHT);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel titleLabel = new JLabel(pet.getId() == 0 ? "New Animal Intake Registration" : "Modify Pet Record [ID: " + pet.getId() + "]");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        speciesCombo = new JComboBox<>(new String[]{"Dog", "Cat", "Rabbit", "Bird", "Other"});
        breedField = new JTextField(15);
        ageSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 300, 1));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        sizeCombo = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        healthCombo = new JComboBox<>(new String[]{"Excellent", "Good", "Special Needs", "Medical Treatment"});
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "PENDING", "ADOPTED", "MEDICAL_HOLD"});

        kidsCheck = new JCheckBox("Good with Children");
        dogsCheck = new JCheckBox("Good with Dogs");
        catsCheck = new JCheckBox("Good with Cats");
        trainedCheck = new JCheckBox("House Trained");

        kidsCheck.setBackground(Color.WHITE);
        dogsCheck.setBackground(Color.WHITE);
        catsCheck.setBackground(Color.WHITE);
        trainedCheck.setBackground(Color.WHITE);

        activitySlider = new JSlider(1, 5, 3);
        activitySlider.setMajorTickSpacing(1);
        activitySlider.setPaintTicks(true);
        activitySlider.setPaintLabels(true);
        activitySlider.setBackground(Color.WHITE);

        descArea = new JTextArea(4, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        // Row 0: Pet Name & Species
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Pet Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Species:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(speciesCombo, gbc);

        // Row 2: Breed & Age
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Breed:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(breedField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Age (in Months):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(ageSpinner, gbc);

        // Row 4: Gender & Size
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Size Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(sizeCombo, gbc);

        // Row 6: Health & Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Health Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(healthCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Adoption Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(statusCombo, gbc);

        // Row 8: Temperament Checkboxes
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Behavior & Compatibility:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel checkPanel = new JPanel(new GridLayout(2, 2, 4, 4));
        checkPanel.setBackground(Color.WHITE);
        checkPanel.add(kidsCheck);
        checkPanel.add(dogsCheck);
        checkPanel.add(catsCheck);
        checkPanel.add(trainedCheck);
        formPanel.add(checkPanel, gbc);

        // Row 9: Activity Level Slider
        gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Activity Rating (1-5):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(activitySlider, gbc);

        // Row 10: Description
        gbc.gridx = 0; gbc.gridy = 10; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Behavioral Notes:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(descScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.COLOR_BG_LIGHT);

        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = UITheme.createPrimaryButton("Save Pet Record");
        saveButton.addActionListener(e -> onSave());

        actionPanel.add(cancelButton);
        actionPanel.add(saveButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_REGULAR_BOLD);
        lbl.setForeground(UITheme.COLOR_TEXT_MAIN);
        return lbl;
    }

    private void populateFields() {
        if (pet.getId() > 0) {
            nameField.setText(pet.getName());
            speciesCombo.setSelectedItem(pet.getSpecies());
            breedField.setText(pet.getBreed());
            ageSpinner.setValue(pet.getAgeMonths());
            genderCombo.setSelectedItem(pet.getGender());
            sizeCombo.setSelectedItem(pet.getSize());
            healthCombo.setSelectedItem(pet.getHealthStatus());
            statusCombo.setSelectedItem(pet.getAdoptionStatus());
            kidsCheck.setSelected(pet.isGoodWithChildren());
            dogsCheck.setSelected(pet.isGoodWithDogs());
            catsCheck.setSelected(pet.isGoodWithCats());
            trainedCheck.setSelected(pet.isHouseTrained());
            activitySlider.setValue(pet.getActivityLevel());
            descArea.setText(pet.getDescription());
        } else {
            statusCombo.setSelectedItem("AVAILABLE");
            kidsCheck.setSelected(true);
            dogsCheck.setSelected(true);
            trainedCheck.setSelected(true);
            activitySlider.setValue(3);
        }
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String breed = breedField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pet name cannot be blank.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (breed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Breed cannot be blank.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            breedField.requestFocus();
            return;
        }

        pet.setName(name);
        pet.setSpecies((String) speciesCombo.getSelectedItem());
        pet.setBreed(breed);
        pet.setAgeMonths((Integer) ageSpinner.getValue());
        pet.setGender((String) genderCombo.getSelectedItem());
        pet.setSize((String) sizeCombo.getSelectedItem());
        pet.setHealthStatus((String) healthCombo.getSelectedItem());
        pet.setAdoptionStatus((String) statusCombo.getSelectedItem());
        pet.setGoodWithChildren(kidsCheck.isSelected());
        pet.setGoodWithDogs(dogsCheck.isSelected());
        pet.setGoodWithCats(catsCheck.isSelected());
        pet.setHouseTrained(trainedCheck.isSelected());
        pet.setActivityLevel(activitySlider.getValue());
        pet.setDescription(descArea.getText().trim());

        if (pet.getIntakeDate() == null || pet.getIntakeDate().isEmpty()) {
            pet.setIntakeDate(LocalDate.now().toString());
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Pet getPet() {
        return pet;
    }
}
