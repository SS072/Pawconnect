package org.pawconnect.view;

import org.pawconnect.controller.MatchingEngine;
import org.pawconnect.controller.PetController;
import org.pawconnect.model.MatchResult;
import org.pawconnect.model.Pet;
import org.pawconnect.view.dialogs.ApplicationDialog;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Smart Compatibility Engine Panel.
 * Demonstrates: Algorithmic heuristic matching, JSlider, JProgressBar, JComboBox,
 * JRadioButton, ButtonGroup, JSpinner, JCheckBox, and Master-Detail result inspection.
 */
public class SmartMatchPanel extends JPanel {
    private final PetController petController;
    private final MatchingEngine matchingEngine;
    private final StatusBar statusBar;

    // Inputs
    private JComboBox<String> speciesPrefCombo;
    private JComboBox<String> housingCombo;
    private JRadioButton yardYesRadio;
    private JRadioButton yardNoRadio;
    private JSlider activitySlider;
    private JRadioButton kidsYesRadio;
    private JRadioButton kidsNoRadio;
    private JCheckBox dogsCheck;
    private JCheckBox catsCheck;
    private JSpinner hoursAwaySpinner;

    // Results
    private DefaultListModel<MatchResult> resultsListModel;
    private JList<MatchResult> resultsList;
    private JProgressBar scoreBar;
    private JLabel matchTitleLabel;
    private DefaultListModel<String> factorsListModel;
    private DefaultListModel<String> concernsListModel;
    private JButton initiateApplyBtn;

    private MatchResult currentlySelectedResult;

    public SmartMatchPanel(PetController petController, StatusBar statusBar) {
        this.petController = petController;
        this.matchingEngine = new MatchingEngine();
        this.statusBar = statusBar;

        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.COLOR_BG_LIGHT);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        initContent();
    }

    private void initContent() {
        // Left side: Assessment Criteria Form
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel formTitle = UITheme.createHeaderLabel("Adopter Living Context & Preferences");
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        speciesPrefCombo = new JComboBox<>(new String[]{"Any", "Dog", "Cat", "Rabbit"});
        housingCombo = new JComboBox<>(new String[]{"Apartment", "Independent House", "Villa with Garden", "Farm / Rural"});

        yardYesRadio = new JRadioButton("Yes", false);
        yardNoRadio = new JRadioButton("No", true);
        yardYesRadio.setOpaque(false);
        yardNoRadio.setOpaque(false);
        ButtonGroup yardGroup = new ButtonGroup();
        yardGroup.add(yardYesRadio);
        yardGroup.add(yardNoRadio);
        JPanel yardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        yardPanel.setOpaque(false);
        yardPanel.add(yardYesRadio);
        yardPanel.add(yardNoRadio);

        activitySlider = new JSlider(1, 5, 3);
        activitySlider.setMajorTickSpacing(1);
        activitySlider.setPaintTicks(true);
        activitySlider.setPaintLabels(true);
        activitySlider.setOpaque(false);

        kidsYesRadio = new JRadioButton("Yes", false);
        kidsNoRadio = new JRadioButton("No", true);
        kidsYesRadio.setOpaque(false);
        kidsNoRadio.setOpaque(false);
        ButtonGroup kidsGroup = new ButtonGroup();
        kidsGroup.add(kidsYesRadio);
        kidsGroup.add(kidsNoRadio);
        JPanel kidsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        kidsPanel.setOpaque(false);
        kidsPanel.add(kidsYesRadio);
        kidsPanel.add(kidsNoRadio);

        dogsCheck = new JCheckBox("Canine companions currently in home");
        catsCheck = new JCheckBox("Feline companions currently in home");
        dogsCheck.setOpaque(false);
        catsCheck.setOpaque(false);

        hoursAwaySpinner = new JSpinner(new SpinnerNumberModel(4, 0, 16, 1));

        // Placements
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Target Animal Species:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(speciesPrefCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Dwelling Classification:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(housingCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Enclosed Yard Access:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(yardPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Household Energy Profile (1-5):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(activitySlider, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Resident Minors / Children:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(kidsPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formGrid.add(dogsCheck, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formGrid.add(catsCheck, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        formGrid.add(makeBoldLabel("Daily Workday Absence (Hours):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formGrid.add(hoursAwaySpinner, gbc);

        formContainer.add(new JScrollPane(formGrid), BorderLayout.CENTER);

        JButton runMatchBtn = UITheme.createPrimaryButton("Run Compatibility Matcher");
        runMatchBtn.addActionListener(e -> executeMatching());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(runMatchBtn);
        formContainer.add(btnPanel, BorderLayout.SOUTH);

        // Right side: Ranked Compatibility Results
        JPanel resultsContainer = new JPanel(new BorderLayout(8, 8));
        resultsContainer.setBackground(Color.WHITE);
        resultsContainer.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel resultsTitle = UITheme.createHeaderLabel("Ranked Compatibility Assessment");
        resultsContainer.add(resultsTitle, BorderLayout.NORTH);

        resultsListModel = new DefaultListModel<>();
        resultsList = new JList<>(resultsListModel);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setCellRenderer(new MatchListRenderer());
        resultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showMatchBreakdown(resultsList.getSelectedValue());
            }
        });

        JScrollPane listScroll = new JScrollPane(resultsList);
        listScroll.setPreferredSize(new Dimension(280, 200));

        // Details Breakdown subpanel
        JPanel breakdownPanel = new JPanel(new BorderLayout(6, 6));
        breakdownPanel.setOpaque(false);
        breakdownPanel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));

        matchTitleLabel = new JLabel("Select an animal from results to inspect breakdown");
        matchTitleLabel.setFont(UITheme.FONT_HEADER);
        matchTitleLabel.setForeground(UITheme.COLOR_PRIMARY);

        scoreBar = new JProgressBar(0, 100);
        scoreBar.setStringPainted(true);
        scoreBar.setFont(UITheme.FONT_REGULAR_BOLD);
        scoreBar.setForeground(new Color(22, 101, 52));
        scoreBar.setPreferredSize(new Dimension(200, 22));

        JPanel scoreHeader = new JPanel(new BorderLayout(6, 6));
        scoreHeader.setOpaque(false);
        scoreHeader.add(matchTitleLabel, BorderLayout.NORTH);
        scoreHeader.add(scoreBar, BorderLayout.CENTER);

        // Factors & Concerns
        JPanel listsGrid = new JPanel(new GridLayout(2, 1, 6, 6));
        listsGrid.setOpaque(false);

        factorsListModel = new DefaultListModel<>();
        JList<String> factorsList = new JList<>(factorsListModel);
        factorsList.setFont(UITheme.FONT_REGULAR);
        JScrollPane factorsScroll = new JScrollPane(factorsList);
        factorsScroll.setBorder(BorderFactory.createTitledBorder("Positive Match Criteria"));

        concernsListModel = new DefaultListModel<>();
        JList<String> concernsList = new JList<>(concernsListModel);
        concernsList.setFont(UITheme.FONT_REGULAR);
        JScrollPane concernsScroll = new JScrollPane(concernsList);
        concernsScroll.setBorder(BorderFactory.createTitledBorder("Advisory Notes & Cautions"));

        listsGrid.add(factorsScroll);
        listsGrid.add(concernsScroll);

        breakdownPanel.add(scoreHeader, BorderLayout.NORTH);
        breakdownPanel.add(listsGrid, BorderLayout.CENTER);

        initiateApplyBtn = UITheme.createPrimaryButton("Initiate Application for Selected Pet");
        initiateApplyBtn.setEnabled(false);
        initiateApplyBtn.addActionListener(e -> onInitiateApplication());
        breakdownPanel.add(initiateApplyBtn, BorderLayout.SOUTH);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, breakdownPanel);
        rightSplit.setResizeWeight(0.35);
        rightSplit.setDividerSize(6);
        rightSplit.setBorder(null);

        resultsContainer.add(rightSplit, BorderLayout.CENTER);

        // Main outer split
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formContainer, resultsContainer);
        mainSplit.setResizeWeight(0.42);
        mainSplit.setDividerSize(6);
        mainSplit.setBorder(null);

        add(mainSplit, BorderLayout.CENTER);
    }

    private JLabel makeBoldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_MAIN);
        return l;
    }

    private void executeMatching() {
        MatchingEngine.Criteria criteria = new MatchingEngine.Criteria();
        criteria.preferredSpecies = (String) speciesPrefCombo.getSelectedItem();
        criteria.housingType = (String) housingCombo.getSelectedItem();
        criteria.hasYard = yardYesRadio.isSelected();
        criteria.activityLevel = activitySlider.getValue();
        criteria.hasChildren = kidsYesRadio.isSelected();
        criteria.hasExistingDogs = dogsCheck.isSelected();
        criteria.hasExistingCats = catsCheck.isSelected();
        criteria.hoursAwayPerDay = (Integer) hoursAwaySpinner.getValue();

        List<Pet> allPets = petController.getAllPets();
        List<MatchResult> results = matchingEngine.calculateMatches(allPets, criteria);

        resultsListModel.clear();
        for (MatchResult r : results) {
            resultsListModel.addElement(r);
        }

        if (!results.isEmpty()) {
            resultsList.setSelectedIndex(0);
        }

        if (statusBar != null) {
            statusBar.setStatusMessage("Calculated compatibility rankings for " + results.size() + " pets");
        }
    }

    private void showMatchBreakdown(MatchResult result) {
        this.currentlySelectedResult = result;
        if (result == null) {
            matchTitleLabel.setText("Select an animal from results to inspect breakdown");
            scoreBar.setValue(0);
            scoreBar.setString("0%");
            factorsListModel.clear();
            concernsListModel.clear();
            initiateApplyBtn.setEnabled(false);
            return;
        }

        Pet p = result.getPet();
        matchTitleLabel.setText(p.getName() + " - " + p.getBreed() + " (" + p.getAgeFormatted() + ")");
        scoreBar.setValue(result.getScore());
        scoreBar.setString(result.getScore() + "% Compatibility Score");

        if (result.getScore() >= 80) {
            scoreBar.setForeground(new Color(22, 101, 52)); // Green
        } else if (result.getScore() >= 60) {
            scoreBar.setForeground(new Color(180, 83, 9)); // Amber
        } else {
            scoreBar.setForeground(new Color(185, 28, 28)); // Crimson
        }

        factorsListModel.clear();
        if (result.getMatchingFactors().isEmpty()) {
            factorsListModel.addElement("No prominent positive factors recorded.");
        } else {
            for (String f : result.getMatchingFactors()) {
                factorsListModel.addElement("+ " + f);
            }
        }

        concernsListModel.clear();
        if (result.getPotentialConcerns().isEmpty()) {
            concernsListModel.addElement("No risk factors or living discrepancies identified.");
        } else {
            for (String c : result.getPotentialConcerns()) {
                concernsListModel.addElement("- " + c);
            }
        }

        initiateApplyBtn.setEnabled(true);
    }

    private void onInitiateApplication() {
        if (currentlySelectedResult == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ApplicationDialog dialog = new ApplicationDialog(parent, currentlySelectedResult.getPet());
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            JOptionPane.showMessageDialog(this, "Application successfully dispatched for " + currentlySelectedResult.getPet().getName() + "!", "Application Filed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Custom ListCellRenderer for Match Results.
     */
    private static class MatchListRenderer extends JPanel implements ListCellRenderer<MatchResult> {
        private final JLabel nameLabel;
        private final JLabel scoreBadge;
        private final JLabel specLabel;

        public MatchListRenderer() {
            setLayout(new BorderLayout(6, 2));
            setBorder(new EmptyBorder(6, 8, 6, 8));

            nameLabel = new JLabel();
            nameLabel.setFont(UITheme.FONT_REGULAR_BOLD);

            scoreBadge = new JLabel();
            scoreBadge.setFont(UITheme.FONT_REGULAR_BOLD);

            specLabel = new JLabel();
            specLabel.setFont(UITheme.FONT_SMALL);
            specLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

            add(nameLabel, BorderLayout.WEST);
            add(scoreBadge, BorderLayout.EAST);
            add(specLabel, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends MatchResult> list, MatchResult value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                Pet p = value.getPet();
                nameLabel.setText(p.getName() + " (" + p.getBreed() + ")");
                scoreBadge.setText(value.getScore() + "%");
                specLabel.setText(p.getSpecies() + " | " + p.getAgeFormatted() + " | Energy: " + p.getActivityLevel() + "/5");

                if (value.getScore() >= 80) {
                    scoreBadge.setForeground(new Color(22, 101, 52));
                } else if (value.getScore() >= 60) {
                    scoreBadge.setForeground(new Color(180, 83, 9));
                } else {
                    scoreBadge.setForeground(new Color(185, 28, 28));
                }
            }

            if (isSelected) {
                setBackground(new Color(224, 231, 255));
                setOpaque(true);
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setOpaque(true);
            }

            return this;
        }
    }
}
