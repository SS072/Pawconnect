package org.pawconnect.view;

import org.pawconnect.controller.PetController;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Shelter Operations & Metrics Dashboard.
 * Demonstrates: GridLayout for KPI summary cards, high-contrast metric typography,
 * and real-time aggregation queries from the database.
 */
public class DashboardMetricsPanel extends JPanel {
    private final PetController petController;
    private final Runnable onNavigateToInventory;
    private final Runnable onNavigateToApplications;
    private final Runnable onNavigateToMatching;

    private JLabel totalPetsValue;
    private JLabel availablePetsValue;
    private JLabel adoptedPetsValue;
    private JLabel pendingAppsValue;
    private JLabel medicalHoldValue;

    public DashboardMetricsPanel(PetController petController,
                                 Runnable onNavigateToInventory,
                                 Runnable onNavigateToApplications,
                                 Runnable onNavigateToMatching) {
        this.petController = petController;
        this.onNavigateToInventory = onNavigateToInventory;
        this.onNavigateToApplications = onNavigateToApplications;
        this.onNavigateToMatching = onNavigateToMatching;

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.COLOR_BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initComponents();
        refreshMetrics();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createHeaderLabel("Shelter Operations & Census Overview");
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Metrics");
        refreshBtn.addActionListener(e -> refreshMetrics());
        header.add(title, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: Cards in GridLayout
        JPanel centerContainer = new JPanel(new BorderLayout(12, 12));
        centerContainer.setOpaque(false);

        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        cardsGrid.setOpaque(false);

        totalPetsValue = new JLabel("0");
        availablePetsValue = new JLabel("0");
        adoptedPetsValue = new JLabel("0");
        pendingAppsValue = new JLabel("0");
        medicalHoldValue = new JLabel("0");

        cardsGrid.add(createMetricCard("Total Shelter Registry", totalPetsValue, "Total animals tracked in system catalog", UITheme.COLOR_PRIMARY));
        cardsGrid.add(createMetricCard("Available for Placement", availablePetsValue, "Ready for immediate adoption", new Color(22, 101, 52)));
        cardsGrid.add(createMetricCard("Successful Adoptions", adoptedPetsValue, "Animals placed in permanent homes", new Color(30, 64, 175)));
        cardsGrid.add(createMetricCard("Pending Petitions", pendingAppsValue, "Awaiting administrative adjudication", new Color(180, 83, 9)));
        cardsGrid.add(createMetricCard("Clinical & Special Care", medicalHoldValue, "In active therapy or medical quarantine", new Color(153, 27, 27)));
        cardsGrid.add(createActionShortcutsCard());

        centerContainer.add(cardsGrid, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // Footer System Notice
        JPanel noticePanel = new JPanel(new BorderLayout());
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel noticeLabel = new JLabel("System Note: PawConnect operates under strict animal welfare regulations. All pet transfers and adoption approvals are legally binding and recorded in the audit registry.");
        noticeLabel.setFont(UITheme.FONT_SMALL);
        noticeLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
        noticePanel.add(noticeLabel, BorderLayout.CENTER);

        add(noticePanel, BorderLayout.SOUTH);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.COLOR_TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_METRIC);
        valueLabel.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.COLOR_TEXT_MUTED);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionShortcutsCard() {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(UITheme.COLOR_BORDER, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLbl = new JLabel("Direct Operational Navigation");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.COLOR_PRIMARY);

        JPanel btnGrid = new JPanel(new GridLayout(3, 1, 6, 6));
        btnGrid.setOpaque(false);

        JButton toInventory = UITheme.createSecondaryButton("Go to Pet Inventory");
        toInventory.addActionListener(e -> onNavigateToInventory.run());

        JButton toApps = UITheme.createSecondaryButton("Open Application Queue");
        toApps.addActionListener(e -> onNavigateToApplications.run());

        JButton toMatch = UITheme.createSecondaryButton("Launch Smart Match Engine");
        toMatch.addActionListener(e -> onNavigateToMatching.run());

        btnGrid.add(toInventory);
        btnGrid.add(toApps);
        btnGrid.add(toMatch);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(btnGrid, BorderLayout.CENTER);

        return card;
    }

    public void refreshMetrics() {
        Map<String, Integer> metrics = petController.getDashboardMetrics();
        totalPetsValue.setText(String.valueOf(metrics.getOrDefault("total", 0)));
        availablePetsValue.setText(String.valueOf(metrics.getOrDefault("available", 0)));
        adoptedPetsValue.setText(String.valueOf(metrics.getOrDefault("adopted", 0)));
        pendingAppsValue.setText(String.valueOf(metrics.getOrDefault("pending", 0)));
        medicalHoldValue.setText(String.valueOf(metrics.getOrDefault("medical", 0)));
    }
}
