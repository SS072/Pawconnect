package org.pawconnect.view.dialogs;

import org.pawconnect.model.AdoptionApplication;
import org.pawconnect.model.ApplicationStatus;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal JDialog for Shelter Administrators to review and adjudicate adoption applications.
 * Directly demonstrates administrative decision-making and transaction triggers.
 */
public class ReviewApplicationDialog extends JDialog {
    private final AdoptionApplication application;
    private ApplicationStatus decision;
    private String reviewNotes;
    private boolean processed = false;

    private JComboBox<ApplicationStatus> statusCombo;
    private JTextArea notesArea;

    public ReviewApplicationDialog(Frame owner, AdoptionApplication application) {
        super(owner, "Application Review - Reference #" + application.getId() + " (" + application.getPetName() + ")", true);
        this.application = application;

        initComponents();

        setSize(560, 520);
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
        JLabel titleLabel = new JLabel("Adoption Petition Adjudication");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout(8, 8));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top info summary
        JPanel summaryPanel = new JPanel(new GridLayout(6, 2, 6, 6));
        summaryPanel.setBackground(Color.WHITE);

        summaryPanel.add(makeBoldLabel("Applicant:"));
        summaryPanel.add(new JLabel(application.getApplicantName() + " (" + application.getApplicantPhone() + ")"));

        summaryPanel.add(makeBoldLabel("Email:"));
        summaryPanel.add(new JLabel(application.getApplicantEmail()));

        summaryPanel.add(makeBoldLabel("Target Pet:"));
        summaryPanel.add(new JLabel(application.getPetName() + " [" + application.getPetBreed() + "]"));

        summaryPanel.add(makeBoldLabel("Housing & Yard:"));
        summaryPanel.add(new JLabel(application.getHousingType() + (application.isHasYard() ? " (Fenced Yard)" : " (No Yard)")));

        summaryPanel.add(makeBoldLabel("Household Context:"));
        summaryPanel.add(new JLabel((application.isHasChildren() ? "Has Children | " : "No Children | ") + "Other pets: " + application.getOtherPets()));

        summaryPanel.add(makeBoldLabel("Applicant Statement:"));
        summaryPanel.add(new JLabel("<html><i>\"" + application.getReasonForAdoption() + "\"</i></html>"));

        contentPanel.add(summaryPanel, BorderLayout.NORTH);

        // Adjudication inputs
        JPanel decisionPanel = new JPanel(new GridBagLayout());
        decisionPanel.setBackground(Color.WHITE);
        decisionPanel.setBorder(BorderFactory.createTitledBorder("Administrative Decision & Audit Log"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        statusCombo = new JComboBox<>(new ApplicationStatus[]{
            ApplicationStatus.UNDER_REVIEW,
            ApplicationStatus.APPROVED,
            ApplicationStatus.REJECTED,
            ApplicationStatus.WITHDRAWN
        });
        statusCombo.setSelectedItem(application.getStatus());

        notesArea = new JTextArea(4, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(application.getReviewNotes() != null ? application.getReviewNotes() : "");
        JScrollPane notesScroll = new JScrollPane(notesArea);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        decisionPanel.add(makeBoldLabel("Decision Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        decisionPanel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        decisionPanel.add(makeBoldLabel("Reviewer Notes / Conditions:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        decisionPanel.add(notesScroll, gbc);

        contentPanel.add(decisionPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.COLOR_BG_LIGHT);

        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = UITheme.createPrimaryButton("Confirm Decision");
        confirmButton.addActionListener(e -> onConfirm());

        actionPanel.add(cancelButton);
        actionPanel.add(confirmButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel makeBoldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_MAIN);
        return l;
    }

    private void onConfirm() {
        this.decision = (ApplicationStatus) statusCombo.getSelectedItem();
        this.reviewNotes = notesArea.getText().trim();

        if (decision == ApplicationStatus.APPROVED) {
            int resp = JOptionPane.showConfirmDialog(
                this,
                "Approving this application will execute an atomic transaction:\n" +
                "1. Update application status to APPROVED\n" +
                "2. Mark Pet '" + application.getPetName() + "' as ADOPTED\n\n" +
                "Do you wish to proceed?",
                "Confirm Adoption Finalization",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (resp != JOptionPane.YES_OPTION) {
                return;
            }
        }

        processed = true;
        dispose();
    }

    public boolean isProcessed() {
        return processed;
    }

    public ApplicationStatus getDecision() {
        return decision;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }
}
