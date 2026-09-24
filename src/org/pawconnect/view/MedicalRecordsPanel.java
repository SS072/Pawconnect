package org.pawconnect.view;

import org.pawconnect.controller.PetController;
import org.pawconnect.dao.MedicalDAO;
import org.pawconnect.model.MedicalRecord;
import org.pawconnect.model.Pet;
import org.pawconnect.view.dialogs.MedicalEntryDialog;
import org.pawconnect.view.util.TableFormatters;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Veterinary and Clinical Records Management Panel.
 * Demonstrates: JTable, JComboBox pet selector filter, master-detail clinical notes,
 * and MedicalDAO persistence integration.
 */
public class MedicalRecordsPanel extends JPanel {
    private final PetController petController;
    private final MedicalDAO medicalDAO;
    private final StatusBar statusBar;

    private JComboBox<String> petFilterCombo;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextArea notesArea;
    private JButton deleteRecordBtn;
    private final boolean isAdmin;

    public MedicalRecordsPanel(PetController petController, StatusBar statusBar) {
        this(petController, statusBar, true);
    }

    public MedicalRecordsPanel(PetController petController, StatusBar statusBar, boolean isAdmin) {
        this.petController = petController;
        this.medicalDAO = new MedicalDAO();
        this.statusBar = statusBar;
        this.isAdmin = isAdmin;

        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.COLOR_BG_LIGHT);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        initToolbar();
        initTableAndDetail();
        loadRecords();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(new JLabel("Target Pet Clinical Filter:"));

        petFilterCombo = new JComboBox<>();
        petFilterCombo.addItem("All Animal Records");
        refreshPetFilterItems();
        petFilterCombo.addActionListener(e -> filterRecords());
        left.add(petFilterCombo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Logs");
        refreshBtn.addActionListener(e -> {
            refreshPetFilterItems();
            loadRecords();
        });

        right.add(refreshBtn);

        if (isAdmin) {
            deleteRecordBtn = UITheme.createSecondaryButton("Delete Selected Record");
            deleteRecordBtn.setEnabled(false);
            deleteRecordBtn.addActionListener(e -> onDeleteRecord());

            JButton addBtn = UITheme.createPrimaryButton("+ Log Veterinary Entry");
            addBtn.addActionListener(e -> openAddDialog());

            right.add(deleteRecordBtn);
            right.add(addBtn);
        }

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        add(toolbar, BorderLayout.NORTH);
    }

    private void refreshPetFilterItems() {
        Object selected = petFilterCombo.getSelectedItem();
        petFilterCombo.removeAllItems();
        petFilterCombo.addItem("All Animal Records");
        List<Pet> pets = petController.getAllPets();
        for (Pet p : pets) {
            petFilterCombo.addItem(p.getId() + " - " + p.getName() + " (" + p.getBreed() + ")");
        }
        if (selected != null) {
            petFilterCombo.setSelectedItem(selected);
        }
    }

    private void initTableAndDetail() {
        String[] columns = {"ID", "Pet ID", "Pet Name", "Record Date", "Type", "Clinical Procedure / Vaccine", "Attending Veterinarian", "Next Due Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recordsTable = new JTable(tableModel);
        recordsTable.setRowHeight(26);
        recordsTable.setFont(UITheme.FONT_REGULAR);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordsTable.getTableHeader().setFont(UITheme.FONT_HEADER);
        recordsTable.getTableHeader().setBackground(new Color(241, 245, 249));
        recordsTable.getTableHeader().setForeground(UITheme.COLOR_PRIMARY);

        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(55);
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(95);
        recordsTable.getColumnModel().getColumn(3).setPreferredWidth(95);
        recordsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        recordsTable.getColumnModel().getColumn(5).setPreferredWidth(210);
        recordsTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        recordsTable.getColumnModel().getColumn(7).setPreferredWidth(95);

        recordsTable.setDefaultRenderer(Object.class, new TableFormatters.StripedRowRenderer());
        recordsTable.getColumnModel().getColumn(4).setCellRenderer(new TableFormatters.StatusBadgeRenderer());

        rowSorter = new TableRowSorter<>(tableModel);
        recordsTable.setRowSorter(rowSorter);

        recordsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSel = recordsTable.getSelectedRow() != -1;
            if (deleteRecordBtn != null) {
                deleteRecordBtn.setEnabled(hasSel);
            }
            if (hasSel) {
                int modelRow = recordsTable.convertRowIndexToModel(recordsTable.getSelectedRow());
                int recId = (Integer) tableModel.getValueAt(modelRow, 0);
                showRecordNotes(recId);
            } else {
                notesArea.setText("Select a medical record row above to review clinical notes and dosage protocols.");
            }
        });

        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.setBorder(new LineBorder(UITheme.COLOR_BORDER, 1));

        // Bottom Detail Notes
        JPanel notesPanel = new JPanel(new BorderLayout(4, 4));
        notesPanel.setBackground(Color.WHITE);
        notesPanel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel notesHeader = new JLabel("Attending Veterinarian Observation & Treatment Description:");
        notesHeader.setFont(UITheme.FONT_REGULAR_BOLD);
        notesHeader.setForeground(UITheme.COLOR_PRIMARY);

        notesArea = new JTextArea(4, 25);
        notesArea.setFont(UITheme.FONT_REGULAR);
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(new Color(248, 250, 252));
        notesArea.setText("Select a medical record row above to review clinical notes and dosage protocols.");

        notesPanel.add(notesHeader, BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, notesPanel);
        splitPane.setResizeWeight(0.68);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadRecords() {
        tableModel.setRowCount(0);
        List<MedicalRecord> list = medicalDAO.getAllRecords();
        for (MedicalRecord r : list) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPetId(),
                r.getPetName(),
                r.getRecordDate(),
                r.getRecordType(),
                r.getTitle(),
                r.getVeterinarian(),
                r.getNextDueDate()
            });
        }
        if (statusBar != null) {
            statusBar.setStatusMessage("Loaded " + list.size() + " medical logs");
        }
    }

    private void filterRecords() {
        int idx = petFilterCombo.getSelectedIndex();
        if (idx <= 0) {
            loadRecords();
            return;
        }

        String item = (String) petFilterCombo.getSelectedItem();
        int petId = Integer.parseInt(item.split(" - ")[0]);

        tableModel.setRowCount(0);
        List<MedicalRecord> list = medicalDAO.getRecordsForPet(petId);
        for (MedicalRecord r : list) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPetId(),
                r.getPetName(),
                r.getRecordDate(),
                r.getRecordType(),
                r.getTitle(),
                r.getVeterinarian(),
                r.getNextDueDate()
            });
        }
    }

    private void showRecordNotes(int recordId) {
        List<MedicalRecord> list = medicalDAO.getAllRecords();
        for (MedicalRecord r : list) {
            if (r.getId() == recordId) {
                notesArea.setText(
                    "Procedure / Vaccine: " + r.getTitle() + "\n" +
                    "Record Date: " + r.getRecordDate() + " | Next Due: " + r.getNextDueDate() + "\n" +
                    "Attending Clinician: " + r.getVeterinarian() + "\n\n" +
                    "Clinical Observations:\n" + r.getDescription()
                );
                return;
            }
        }
    }

    private void openAddDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MedicalEntryDialog dialog = new MedicalEntryDialog(parent, petController.getAllPets(), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            boolean ok = medicalDAO.addRecord(dialog.getRecord());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Veterinary record saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPetFilterItems();
                loadRecords();
            }
        }
    }

    private void onDeleteRecord() {
        int row = recordsTable.getSelectedRow();
        if (row == -1) return;

        int modelRow = recordsTable.convertRowIndexToModel(row);
        int recId = (Integer) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to permanently delete clinical record ID " + recId + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = medicalDAO.deleteRecord(recId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Record deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadRecords();
            }
        }
    }
}
