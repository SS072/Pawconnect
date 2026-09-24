package org.pawconnect.view;

import org.pawconnect.controller.ApplicationController;
import org.pawconnect.controller.AuthController;
import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import org.pawconnect.model.User;
import org.pawconnect.view.util.TableFormatters;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Adopter Portal - My Applications & Status Tracker (ApplicationStatusFrame concept).
 * Allows prospective adopters to monitor their submitted adoption petitions, review shelter feedback,
 * and withdraw active requests without accessing administrative controls.
 */
public class MyApplicationsPanel extends JPanel {
    private final ApplicationController appController;
    private final StatusBar statusBar;

    private JTable appTable;
    private DefaultTableModel tableModel;
    private JLabel statusHeadline;
    private JTextArea notesArea;
    private JButton withdrawBtn;

    public MyApplicationsPanel(ApplicationController appController, StatusBar statusBar) {
        this.appController = appController;
        this.statusBar = statusBar;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.COLOR_BG_CANVAS);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        initHeader();
        initContent();
        loadMyApplications();
    }

    private void initHeader() {
        JPanel headerPanel = UITheme.createCardPanel();
        headerPanel.setLayout(new BorderLayout(6, 4));

        JLabel title = new JLabel("My Adoption Applications & Status Tracker");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.COLOR_NAVY);

        JLabel subtitle = new JLabel("Track the real-time progress, shelter review notes, and approval status for your adoption petitions.");
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.COLOR_TEXT_MUTED);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initContent() {
        // Table Columns
        String[] columns = {"Petition ID", "Target Pet", "Breed", "Residence Type", "Submission Date", "Application Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appTable = new JTable(tableModel);
        appTable.setRowHeight(30);
        appTable.setFont(UITheme.FONT_REGULAR);
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appTable.getTableHeader().setFont(UITheme.FONT_HEADER);
        appTable.getTableHeader().setBackground(new Color(241, 245, 249));
        appTable.getTableHeader().setForeground(UITheme.COLOR_PRIMARY);

        appTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        appTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        appTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        appTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        appTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        appTable.getColumnModel().getColumn(5).setPreferredWidth(140);

        appTable.setDefaultRenderer(Object.class, new TableFormatters.StripedRowRenderer());
        appTable.getColumnModel().getColumn(5).setCellRenderer(new TableFormatters.StatusBadgeRenderer());

        appTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = appTable.getSelectedRow();
                if (row != -1) {
                    int appId = (Integer) tableModel.getValueAt(row, 0);
                    showApplicationDetails(appId);
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(appTable);
        tableScroll.setBorder(new LineBorder(UITheme.COLOR_CARD_BORDER, 1));

        // Bottom Detail & Shelter Feedback Card
        JPanel feedbackCard = UITheme.createCardPanel();
        feedbackCard.setLayout(new BorderLayout(8, 8));

        statusHeadline = new JLabel("Select an application above to view review notes and next steps.");
        statusHeadline.setFont(UITheme.FONT_HEADER);
        statusHeadline.setForeground(UITheme.COLOR_PRIMARY);

        notesArea = new JTextArea(4, 25);
        notesArea.setFont(UITheme.FONT_REGULAR);
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(new Color(248, 250, 252));
        notesArea.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_CARD_BORDER, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Status");
        refreshBtn.addActionListener(e -> loadMyApplications());

        withdrawBtn = UITheme.createSecondaryButton("Withdraw Selected Petition");
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(e -> onWithdrawApplication());

        actionRow.add(refreshBtn);
        actionRow.add(withdrawBtn);

        feedbackCard.add(statusHeadline, BorderLayout.NORTH);
        feedbackCard.add(new JScrollPane(notesArea), BorderLayout.CENTER);
        feedbackCard.add(actionRow, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, feedbackCard);
        splitPane.setResizeWeight(0.60);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadMyApplications() {
        tableModel.setRowCount(0);
        User user = AuthController.getInstance().getCurrentUser();
        List<AdoptionApplication> all = appController.getAllApplications();

        int count = 0;
        for (AdoptionApplication a : all) {
            // For adopter view: match current user name or display all adopter applications
            boolean isMine = a.getApplicantName().toLowerCase().contains(user.getUsername().toLowerCase()) ||
                             a.getApplicantName().toLowerCase().contains("rahul") ||
                             !user.isAdmin();

            if (isMine) {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getPetName(),
                    a.getPetBreed(),
                    a.getHousingType(),
                    a.getSubmissionDate(),
                    a.getStatus().name()
                });
                count++;
            }
        }

        if (tableModel.getRowCount() > 0) {
            appTable.setRowSelectionInterval(0, 0);
        }

        if (statusBar != null) {
            statusBar.setStatusMessage("Tracking " + count + " active petitions for " + user.getFullName());
        }
    }

    private void showApplicationDetails(int appId) {
        List<AdoptionApplication> all = appController.getAllApplications();
        for (AdoptionApplication a : all) {
            if (a.getId() == appId) {
                statusHeadline.setText("Petition #" + a.getId() + " - " + a.getPetName() + " [" + a.getStatus().getDisplayLabel() + "]");
                String feedback = "Shelter Adjudication Notes:\n" +
                                  (a.getReviewNotes() != null && !a.getReviewNotes().isEmpty() ? a.getReviewNotes() : "Your petition is currently pending initial review by the shelter director.") +
                                  "\n\nNext Steps:\n";

                if (a.getStatus() == ApplicationStatus.APPROVED) {
                    feedback += "CONGRATULATIONS! Your adoption petition has been approved. Please contact the shelter at +91 98400 11223 or visit during adoption hours (10:00 AM - 5:00 PM) to finalize transition paperwork and welcome your companion home!";
                } else if (a.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                    feedback += "A shelter counselor is reviewing your home context and compatibility. You may receive a follow-up call regarding garden access or existing pet routine.";
                } else if (a.getStatus() == ApplicationStatus.REJECTED) {
                    feedback += "This specific placement was not recommended based on housing or animal temperament requirements. We invite you to browse other available companions suited to your lifestyle.";
                } else {
                    feedback += "Applications are typically evaluated within 2-3 business days. Thank you for your patience.";
                }

                notesArea.setText(feedback);
                withdrawBtn.setEnabled(a.getStatus() == ApplicationStatus.PENDING || a.getStatus() == ApplicationStatus.UNDER_REVIEW);
                return;
            }
        }
    }

    private void onWithdrawApplication() {
        int row = appTable.getSelectedRow();
        if (row == -1) return;
        int appId = (Integer) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to withdraw petition #" + appId + "?",
            "Confirm Withdrawal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            List<AdoptionApplication> all = appController.getAllApplications();
            for (AdoptionApplication a : all) {
                if (a.getId() == appId) {
                    appController.processDecision(a.getId(), a.getPetId(), ApplicationStatus.WITHDRAWN, "Withdrawn by applicant.");
                    JOptionPane.showMessageDialog(this, "Application #" + appId + " has been withdrawn.", "Petition Withdrawn", JOptionPane.INFORMATION_MESSAGE);
                    loadMyApplications();
                    return;
                }
            }
        }
    }
}
