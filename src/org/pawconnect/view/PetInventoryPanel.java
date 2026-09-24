package org.pawconnect.view;

import org.pawconnect.controller.PetController;
import org.pawconnect.model.Pet;
import org.pawconnect.view.cards.PetCardGridPanel;
import org.pawconnect.view.dialogs.ApplicationDialog;
import org.pawconnect.view.dialogs.MedicalEntryDialog;
import org.pawconnect.view.dialogs.PetEntryDialog;
import org.pawconnect.view.dialogs.PetProfileDialog;
import org.pawconnect.view.util.TableFormatters;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Enhanced Pet Inventory Workspace.
 * Supports Dual View Switching: Card Gallery View (GridLayout) vs Data Table Master-Detail View.
 */
public class PetInventoryPanel extends JPanel {
    private final PetController petController;
    private final StatusBar statusBar;

    // View Switching
    private CardLayout viewCardLayout;
    private JPanel viewCardsContainer;
    private JButton cardsViewBtn;
    private JButton tableViewBtn;

    // Components
    private PetCardGridPanel cardGridPanel;
    private JTable petTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> speciesFilter;
    private JComboBox<String> statusFilter;

    // Table Detail Components
    private JLabel detailTitleLabel;
    private JLabel detailBreedLabel;
    private JLabel detailAgeLabel;
    private JLabel detailGenderLabel;
    private JLabel detailSizeLabel;
    private JLabel detailHealthLabel;
    private JLabel detailStatusLabel;
    private JLabel detailKidsLabel;
    private JLabel detailDogsLabel;
    private JLabel detailTrainedLabel;
    private JLabel detailActivityLabel;
    private JTextArea detailNotesArea;
    private JButton applyButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton addMedicalButton;

    private Pet currentSelectedPet;
    private final boolean isAdmin;

    public PetInventoryPanel(PetController petController, StatusBar statusBar) {
        this(petController, statusBar, true);
    }

    public PetInventoryPanel(PetController petController, StatusBar statusBar, boolean isAdmin) {
        this.petController = petController;
        this.statusBar = statusBar;
        this.isAdmin = isAdmin;

        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.COLOR_BG_CANVAS);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        initViewContainer();
        initTopToolbar();
        loadPetData();
    }

    private void initTopToolbar() {
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_CARD_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Filters Panel
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtersPanel.setOpaque(false);

        filtersPanel.add(new JLabel("Search:"));
        searchField = UITheme.createPaddedTextField(13);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filtersPanel.add(searchField);

        filtersPanel.add(new JLabel("Species:"));
        speciesFilter = new JComboBox<>(new String[]{"All", "Dog", "Cat", "Rabbit", "Bird"});
        speciesFilter.setFont(UITheme.FONT_REGULAR);
        speciesFilter.addActionListener(e -> applyFilters());
        filtersPanel.add(speciesFilter);

        filtersPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "AVAILABLE", "PENDING", "ADOPTED", "MEDICAL_HOLD"});
        statusFilter.setFont(UITheme.FONT_REGULAR);
        statusFilter.addActionListener(e -> applyFilters());
        filtersPanel.add(statusFilter);

        // Actions & View Switcher
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        // View toggle buttons
        cardsViewBtn = UITheme.createPrimaryButton("Cards View");
        tableViewBtn = UITheme.createSecondaryButton("Table View");

        cardsViewBtn.addActionListener(e -> switchToCardsView());
        tableViewBtn.addActionListener(e -> switchToTableView());

        rightPanel.add(cardsViewBtn);
        rightPanel.add(tableViewBtn);

        if (isAdmin) {
            JButton addBtn = UITheme.createPrimaryButton("+ Add Pet Intake");
            addBtn.addActionListener(e -> openAddPetDialog());

            deleteButton = UITheme.createSecondaryButton("Delete Selected");
            deleteButton.addActionListener(e -> onDeleteSelected());

            rightPanel.add(new JSeparator(SwingConstants.VERTICAL));
            rightPanel.add(deleteButton);
            rightPanel.add(addBtn);
        } else {
            // For adopters: default filter to available pets
            statusFilter.setSelectedItem("AVAILABLE");
        }

        toolbarPanel.add(filtersPanel, BorderLayout.WEST);
        toolbarPanel.add(rightPanel, BorderLayout.EAST);

        add(toolbarPanel, BorderLayout.NORTH);
    }

    private void initViewContainer() {
        viewCardLayout = new CardLayout();
        viewCardsContainer = new JPanel(viewCardLayout);
        viewCardsContainer.setOpaque(false);

        // 1. Cards Grid View
        cardGridPanel = new PetCardGridPanel(this::loadPetData);
        viewCardsContainer.add(cardGridPanel, "CARDS");

        // 2. Table Master-Detail View
        JPanel tableView = initMasterDetailTableView();
        viewCardsContainer.add(tableView, "TABLE");

        add(viewCardsContainer, BorderLayout.CENTER);
    }

    private void switchToCardsView() {
        viewCardLayout.show(viewCardsContainer, "CARDS");
        cardsViewBtn.setBackground(UITheme.COLOR_PRIMARY);
        cardsViewBtn.setForeground(Color.WHITE);
        tableViewBtn.setBackground(Color.WHITE);
        tableViewBtn.setForeground(UITheme.COLOR_TEXT_MAIN);
    }

    private void switchToTableView() {
        viewCardLayout.show(viewCardsContainer, "TABLE");
        tableViewBtn.setBackground(UITheme.COLOR_PRIMARY);
        tableViewBtn.setForeground(Color.WHITE);
        cardsViewBtn.setBackground(Color.WHITE);
        cardsViewBtn.setForeground(UITheme.COLOR_TEXT_MAIN);
    }

    private JPanel initMasterDetailTableView() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        String[] columnNames = {"ID", "Name", "Species", "Breed", "Age", "Gender", "Size", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        petTable = new JTable(tableModel);
        petTable.setRowHeight(28);
        petTable.setFont(UITheme.FONT_REGULAR);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petTable.getTableHeader().setFont(UITheme.FONT_HEADER);
        petTable.getTableHeader().setBackground(new Color(241, 245, 249));
        petTable.getTableHeader().setForeground(UITheme.COLOR_PRIMARY);

        petTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        petTable.getColumnModel().getColumn(1).setPreferredWidth(95);
        petTable.getColumnModel().getColumn(2).setPreferredWidth(75);
        petTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        petTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        petTable.getColumnModel().getColumn(5).setPreferredWidth(65);
        petTable.getColumnModel().getColumn(6).setPreferredWidth(65);
        petTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        petTable.setDefaultRenderer(Object.class, new TableFormatters.StripedRowRenderer());
        petTable.getColumnModel().getColumn(7).setCellRenderer(new TableFormatters.StatusBadgeRenderer());

        rowSorter = new TableRowSorter<>(tableModel);
        petTable.setRowSorter(rowSorter);

        petTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = petTable.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = petTable.convertRowIndexToModel(selectedRow);
                    int petId = (Integer) tableModel.getValueAt(modelRow, 0);
                    showPetDetails(petController.getPetById(petId));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(petTable);
        tableScroll.setBorder(new LineBorder(UITheme.COLOR_CARD_BORDER, 1));

        JPanel detailContainer = createDetailPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailContainer);
        splitPane.setResizeWeight(0.62);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        container.add(splitPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_CARD_BORDER, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        detailTitleLabel = new JLabel("Select an animal to inspect record");
        detailTitleLabel.setFont(UITheme.FONT_TITLE);
        detailTitleLabel.setForeground(UITheme.COLOR_PRIMARY);

        detailStatusLabel = new JLabel("");
        detailStatusLabel.setFont(UITheme.FONT_REGULAR_BOLD);

        headerPanel.add(detailTitleLabel, BorderLayout.WEST);
        headerPanel.add(detailStatusLabel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        detailBreedLabel = new JLabel("-");
        detailAgeLabel = new JLabel("-");
        detailGenderLabel = new JLabel("-");
        detailSizeLabel = new JLabel("-");
        detailHealthLabel = new JLabel("-");
        detailKidsLabel = new JLabel("-");
        detailDogsLabel = new JLabel("-");
        detailTrainedLabel = new JLabel("-");
        detailActivityLabel = new JLabel("-");

        detailNotesArea = new JTextArea(4, 20);
        detailNotesArea.setFont(UITheme.FONT_REGULAR);
        detailNotesArea.setEditable(false);
        detailNotesArea.setLineWrap(true);
        detailNotesArea.setWrapStyleWord(true);
        detailNotesArea.setBackground(new Color(248, 250, 252));
        detailNotesArea.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_CARD_BORDER, 1),
            new EmptyBorder(6, 6, 6, 6)
        ));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Breed:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailBreedLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Age:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailAgeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Gender & Size:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailGenderLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Health Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailHealthLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Good with Kids:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailKidsLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Other Animals:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailDogsLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("House Trained:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailTrainedLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Activity Rating:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailActivityLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0;
        fieldsPanel.add(makeFieldHeader("Notes:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(detailNotesArea, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        applyButton = UITheme.createPrimaryButton("Submit Adoption Petition");
        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> onApplyForAdoption());

        actionPanel.add(applyButton);

        if (isAdmin) {
            editButton = UITheme.createSecondaryButton("Edit Pet Record");
            editButton.setEnabled(false);
            editButton.addActionListener(e -> onEditPet());

            addMedicalButton = UITheme.createSecondaryButton("Log Medical Entry");
            addMedicalButton.setEnabled(false);
            addMedicalButton.addActionListener(e -> onAddMedicalRecord());

            actionPanel.add(editButton);
            actionPanel.add(addMedicalButton);
        }

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel makeFieldHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_MUTED);
        return l;
    }

    public void loadPetData() {
        tableModel.setRowCount(0);
        List<Pet> pets = petController.getAllPets();
        for (Pet p : pets) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getSpecies(),
                p.getBreed(),
                p.getAgeFormatted(),
                p.getGender(),
                p.getSize(),
                p.getAdoptionStatus()
            });
        }

        // Update Card Gallery View
        cardGridPanel.updatePets(pets);

        if (statusBar != null) {
            statusBar.setStatusMessage("Showing " + pets.size() + " total pet records");
        }
        if (petTable.getRowCount() > 0) {
            petTable.setRowSelectionInterval(0, 0);
        }
    }

    private void applyFilters() {
        if (tableModel == null || cardGridPanel == null || searchField == null || speciesFilter == null || statusFilter == null) {
            return;
        }
        String query = searchField.getText().trim();
        String species = (String) speciesFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();

        tableModel.setRowCount(0);
        List<Pet> filtered = petController.filterPets(query, species, status);
        for (Pet p : filtered) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getSpecies(),
                p.getBreed(),
                p.getAgeFormatted(),
                p.getGender(),
                p.getSize(),
                p.getAdoptionStatus()
            });
        }

        cardGridPanel.updatePets(filtered);

        if (statusBar != null) {
            statusBar.setStatusMessage("Displaying " + filtered.size() + " matching records");
        }
    }

    private void showPetDetails(Pet pet) {
        this.currentSelectedPet = pet;
        if (pet == null) {
            detailTitleLabel.setText("No Pet Selected");
            if (applyButton != null) applyButton.setEnabled(false);
            if (editButton != null) editButton.setEnabled(false);
            if (deleteButton != null) deleteButton.setEnabled(false);
            if (addMedicalButton != null) addMedicalButton.setEnabled(false);
            return;
        }

        detailTitleLabel.setText(pet.getName() + " [ID: " + pet.getId() + "]");
        detailStatusLabel.setText(pet.getAdoptionStatus());
        detailBreedLabel.setText(pet.getBreed() + " (" + pet.getSpecies() + ")");
        detailAgeLabel.setText(pet.getAgeFormatted());
        detailGenderLabel.setText(pet.getGender() + " | Size: " + pet.getSize());
        detailHealthLabel.setText(pet.getHealthStatus());
        detailKidsLabel.setText(pet.isGoodWithChildren() ? "Yes - Certified Gentle" : "No - Adult-Only Home");
        detailDogsLabel.setText((pet.isGoodWithDogs() ? "Dogs: Yes | " : "Dogs: No | ") + (pet.isGoodWithCats() ? "Cats: Yes" : "Cats: No"));
        detailTrainedLabel.setText(pet.isHouseTrained() ? "Yes" : "In Training");
        detailActivityLabel.setText(pet.getActivityLevel() + " / 5 (" + (pet.getActivityLevel() >= 4 ? "High" : (pet.getActivityLevel() >= 2 ? "Moderate" : "Low")) + ")");
        detailNotesArea.setText(pet.getDescription());

        boolean isAvailable = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus());
        if (applyButton != null) applyButton.setEnabled(isAvailable);
        if (editButton != null) editButton.setEnabled(true);
        if (deleteButton != null) deleteButton.setEnabled(true);
        if (addMedicalButton != null) addMedicalButton.setEnabled(true);
    }

    private void openAddPetDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PetEntryDialog dialog = new PetEntryDialog(parent, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            boolean ok = petController.createPet(dialog.getPet());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pet record successfully created.", "Record Added", JOptionPane.INFORMATION_MESSAGE);
                loadPetData();
            }
        }
    }

    private void onEditPet() {
        if (currentSelectedPet == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PetEntryDialog dialog = new PetEntryDialog(parent, currentSelectedPet);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            boolean ok = petController.updatePet(dialog.getPet());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pet record updated.", "Record Updated", JOptionPane.INFORMATION_MESSAGE);
                loadPetData();
            }
        }
    }

    private void onDeleteSelected() {
        if (currentSelectedPet == null) {
            JOptionPane.showMessageDialog(this, "Please select a pet to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete pet record '" + currentSelectedPet.getName() + "' (ID: " + currentSelectedPet.getId() + ")?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = petController.removePet(currentSelectedPet.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pet removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPetData();
            }
        }
    }

    private void onApplyForAdoption() {
        if (currentSelectedPet == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ApplicationDialog dialog = new ApplicationDialog(parent, currentSelectedPet);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            JOptionPane.showMessageDialog(this, "Adoption application submitted successfully!\nOur shelter team will review your petition.", "Application Received", JOptionPane.INFORMATION_MESSAGE);
            loadPetData();
        }
    }

    private void onAddMedicalRecord() {
        if (currentSelectedPet == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MedicalEntryDialog dialog = new MedicalEntryDialog(parent, petController.getAllPets(), currentSelectedPet.getId());
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            JOptionPane.showMessageDialog(this, "Medical entry recorded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
