package org.pawconnect.view;

import org.pawconnect.controller.ApplicationController;
import org.pawconnect.controller.PetController;
import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.view.dialogs.ReviewApplicationDialog;
import org.pawconnect.view.util.TableFormatters;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Administrative Queue for evaluating incoming Adoption Applications.
 * Demonstrates: JTable, TableRowSorter, MouseAdapter (double-click triggers),
 * Modal JDialog review workflow, and atomic database transaction updates.
 */
public class ApplicationQueuePanel extends JPanel {
    private final ApplicationController appController;
    private final PetController petController;
    private final StatusBar statusBar;

    private JTable appTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JComboBox<String> statusFilter;
    private JButton reviewButton;

    public ApplicationQueuePanel(ApplicationController appController, PetController petController, StatusBar statusBar) {
        this.appController = appController;
        this.petController = petController;
        this.statusBar = statusBar;

        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.COLOR_BG_LIGHT);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        initToolbar();
        initTable();
        loadApplications();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Filters
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(new JLabel("Application Status Filter:"));

        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "WITHDRAWN"});
        statusFilter.addActionListener(e -> filterData());
        left.add(statusFilter);

        // Actions
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Queue");
        refreshBtn.addActionListener(e -> loadApplications());

        reviewButton = UITheme.createPrimaryButton("Review & Adjudicate");
        reviewButton.setEnabled(false);
        reviewButton.addActionListener(e -> openReviewSelected());

        right.add(refreshBtn);
        right.add(reviewButton);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        add(toolbar, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"App ID", "Pet ID", "Pet Name", "Applicant Name", "Contact Phone", "Email", "Housing Type", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appTable = new JTable(tableModel);
        appTable.setRowHeight(26);
        appTable.setFont(UITheme.FONT_REGULAR);
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appTable.getTableHeader().setFont(UITheme.FONT_HEADER);
        appTable.getTableHeader().setBackground(new Color(241, 245, 249));
        appTable.getTableHeader().setForeground(UITheme.COLOR_PRIMARY);

        appTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        appTable.getColumnModel().getColumn(1).setPreferredWidth(55);
        appTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        appTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        appTable.getColumnModel().getColumn(4).setPreferredWidth(105);
        appTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        appTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        appTable.getColumnModel().getColumn(7).setPreferredWidth(85);
        appTable.getColumnModel().getColumn(8).setPreferredWidth(110);

        appTable.setDefaultRenderer(Object.class, new TableFormatters.StripedRowRenderer());
        appTable.getColumnModel().getColumn(8).setCellRenderer(new TableFormatters.StatusBadgeRenderer());

        rowSorter = new TableRowSorter<>(tableModel);
        appTable.setRowSorter(rowSorter);

        appTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = appTable.getSelectedRow() != -1;
            reviewButton.setEnabled(hasSelection);
        });

        // Double click to open review
        appTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && appTable.getSelectedRow() != -1) {
                    openReviewSelected();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appTable);
        scrollPane.setBorder(new LineBorder(UITheme.COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadApplications() {
        tableModel.setRowCount(0);
        List<AdoptionApplication> list = appController.getAllApplications();
        for (AdoptionApplication a : list) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getPetId(),
                a.getPetName(),
                a.getApplicantName(),
                a.getApplicantPhone(),
                a.getApplicantEmail(),
                a.getHousingType(),
                a.getSubmissionDate(),
                a.getStatus().name()
            });
        }
        if (statusBar != null) {
            statusBar.setStatusMessage("Adoption queue: " + list.size() + " petitions recorded");
        }
    }

    private void filterData() {
        String filter = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        List<AdoptionApplication> list = appController.getApplicationsByStatus(filter);
        for (AdoptionApplication a : list) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getPetId(),
                a.getPetName(),
                a.getApplicantName(),
                a.getApplicantPhone(),
                a.getApplicantEmail(),
                a.getHousingType(),
                a.getSubmissionDate(),
                a.getStatus().name()
            });
        }
    }

    private void openReviewSelected() {
        int selectedRow = appTable.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = appTable.convertRowIndexToModel(selectedRow);
        int appId = (Integer) tableModel.getValueAt(modelRow, 0);

        List<AdoptionApplication> all = appController.getAllApplications();
        AdoptionApplication target = null;
        for (AdoptionApplication a : all) {
            if (a.getId() == appId) {
                target = a;
                break;
            }
        }
        if (target == null) return;

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ReviewApplicationDialog dialog = new ReviewApplicationDialog(parent, target);
        dialog.setVisible(true);

        if (dialog.isProcessed()) {
            boolean success = appController.processDecision(target.getId(), target.getPetId(), dialog.getDecision(), dialog.getReviewNotes());
            if (success) {
                JOptionPane.showMessageDialog(this, "Application status successfully updated to: " + dialog.getDecision(), "Adjudication Complete", JOptionPane.INFORMATION_MESSAGE);
                loadApplications();
            } else {
                JOptionPane.showMessageDialog(this, "Database transaction error occurred during adjudication.", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
