package org.pawconnect.view;

import org.pawconnect.controller.ApplicationController;
import org.pawconnect.controller.AuthController;
import org.pawconnect.controller.PetController;
import org.pawconnect.model.User;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Top-Level MainFrame for PawConnect.
 * Strictly segregates the workspace according to user role:
 * - Adopter Portal: Browse Pets, Smart Matcher, My Application Status Tracker, Pet Health Summary.
 * - Shelter Admin Portal: Census Operations Dashboard, Pet Inventory CRUD, Petition Adjudication, Clinical Logs.
 */
public class MainFrame extends JFrame {
    private final PetController petController;
    private final ApplicationController appController;
    private final User activeUser;
    private final Runnable onLogout;

    private JTabbedPane tabbedPane;
    private StatusBar statusBar;

    // Admin Panels
    private DashboardMetricsPanel dashboardPanel;
    private ApplicationQueuePanel applicationPanel;

    // Shared / Adopter Panels
    private PetInventoryPanel inventoryPanel;
    private SmartMatchPanel matchPanel;
    private MyApplicationsPanel myAppsPanel;
    private MedicalRecordsPanel medicalPanel;

    public MainFrame() {
        this(AuthController.getInstance().getCurrentUser(), null);
    }

    public MainFrame(User activeUser, Runnable onLogout) {
        super(activeUser.isAdmin() ?
            "PawConnect - Shelter Management System [Staff Portal: " + activeUser.getFullName() + "]" :
            "PawConnect - Pet Adoption Portal [Adopter: " + activeUser.getFullName() + "]");

        this.activeUser = activeUser;
        this.petController = new PetController();
        this.appController = new ApplicationController();
        this.onLogout = onLogout;

        initWindow();
        initMenuBar();
        initToolBar();
        initWorkspaceTabs();
        initStatusBar();

        setSize(1200, 800);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Are you sure you want to exit PawConnect Pet Adoption Management System?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.COLOR_BG_CANVAS);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.COLOR_CARD_BORDER));

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(UITheme.FONT_REGULAR);

        JMenuItem signOutItem = new JMenuItem("Sign Out / Switch Portal...");
        signOutItem.addActionListener(e -> performLogout());

        JMenuItem refreshItem = new JMenuItem("Refresh Datasets");
        refreshItem.addActionListener(e -> refreshAllData());

        JMenuItem exitItem = new JMenuItem("Exit Application");
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        fileMenu.add(signOutItem);
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 2. Role-Specific Navigation Menu
        JMenu navMenu = new JMenu(activeUser.isAdmin() ? "Shelter Operations" : "Adoption Navigation");
        navMenu.setFont(UITheme.FONT_REGULAR);

        if (activeUser.isAdmin()) {
            JMenuItem dashItem = new JMenuItem("Census & Operations Dashboard");
            dashItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));

            JMenuItem invItem = new JMenuItem("Manage Pet Inventory (CRUD)");
            invItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));

            JMenuItem appsItem = new JMenuItem("Adoption Petitions Queue (Review & Decide)");
            appsItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));

            JMenuItem medItem = new JMenuItem("Veterinary & Clinical Logs");
            medItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));

            navMenu.add(dashItem);
            navMenu.add(invItem);
            navMenu.add(appsItem);
            navMenu.add(medItem);
        } else {
            JMenuItem browseItem = new JMenuItem("Browse Adoptable Animals");
            browseItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));

            JMenuItem matchItem = new JMenuItem("Find My Match (Smart Compatibility)");
            matchItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));

            JMenuItem myAppsItem = new JMenuItem("My Applications Status Tracker");
            myAppsItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));

            JMenuItem healthItem = new JMenuItem("Pet Health & Clinical Records");
            healthItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));

            navMenu.add(browseItem);
            navMenu.add(matchItem);
            navMenu.add(myAppsItem);
            navMenu.add(healthItem);
        }

        // 3. Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(UITheme.FONT_REGULAR);

        JMenuItem aboutItem = new JMenuItem("About PawConnect System");
        aboutItem.addActionListener(e -> showAboutDialog());

        JMenuItem vivaItem = new JMenuItem("21CSC206P Concept Verification Details");
        vivaItem.addActionListener(e -> showVivaConceptDetails());

        helpMenu.add(aboutItem);
        helpMenu.add(vivaItem);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.WHITE);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.COLOR_CARD_BORDER));
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        if (activeUser.isAdmin()) {
            JButton dashBtn = UITheme.createSecondaryButton("Operations Dashboard");
            dashBtn.addActionListener(e -> tabbedPane.setSelectedIndex(0));

            JButton invBtn = UITheme.createSecondaryButton("Manage Pet Inventory");
            invBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));

            JButton appBtn = UITheme.createSecondaryButton("Review Petitions Queue");
            appBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));

            JButton medBtn = UITheme.createSecondaryButton("Veterinary Logs");
            medBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));

            toolBar.add(dashBtn);
            toolBar.add(invBtn);
            toolBar.add(appBtn);
            toolBar.add(medBtn);
        } else {
            JButton browseBtn = UITheme.createSecondaryButton("Browse Adoptable Pets");
            browseBtn.addActionListener(e -> tabbedPane.setSelectedIndex(0));

            JButton matchBtn = UITheme.createSecondaryButton("Find My Match");
            matchBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));

            JButton myAppsBtn = UITheme.createSecondaryButton("My Application Status");
            myAppsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));

            JButton healthBtn = UITheme.createSecondaryButton("Pet Health Cards");
            healthBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));

            toolBar.add(browseBtn);
            toolBar.add(matchBtn);
            toolBar.add(myAppsBtn);
            toolBar.add(healthBtn);
        }

        toolBar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.addActionListener(e -> performLogout());
        toolBar.add(logoutBtn);

        add(toolBar, BorderLayout.NORTH);
    }

    private void initWorkspaceTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_HEADER);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(new EmptyBorder(4, 6, 4, 6));

        statusBar = new StatusBar();

        if (activeUser.isAdmin()) {
            // ADMIN WORKSPACE
            dashboardPanel = new DashboardMetricsPanel(
                petController,
                () -> tabbedPane.setSelectedIndex(1),
                () -> tabbedPane.setSelectedIndex(2),
                () -> tabbedPane.setSelectedIndex(1)
            );
            inventoryPanel = new PetInventoryPanel(petController, statusBar, true);
            applicationPanel = new ApplicationQueuePanel(appController, petController, statusBar);
            medicalPanel = new MedicalRecordsPanel(petController, statusBar, true);

            tabbedPane.addTab("Operations Dashboard", dashboardPanel);
            tabbedPane.addTab("Manage Pet Inventory", inventoryPanel);
            tabbedPane.addTab("Adoption Petitions Queue", applicationPanel);
            tabbedPane.addTab("Veterinary & Medical Logs", medicalPanel);

            tabbedPane.addChangeListener(e -> {
                int selected = tabbedPane.getSelectedIndex();
                if (selected == 0) dashboardPanel.refreshMetrics();
                else if (selected == 1) inventoryPanel.loadPetData();
                else if (selected == 2) applicationPanel.loadApplications();
                else if (selected == 3) medicalPanel.loadRecords();
            });

        } else {
            // ADOPTER WORKSPACE
            inventoryPanel = new PetInventoryPanel(petController, statusBar, false);
            matchPanel = new SmartMatchPanel(petController, statusBar);
            myAppsPanel = new MyApplicationsPanel(appController, statusBar);
            medicalPanel = new MedicalRecordsPanel(petController, statusBar, false);

            tabbedPane.addTab("Browse Adoptable Pets", inventoryPanel);
            tabbedPane.addTab("Smart Compatibility Matcher", matchPanel);
            tabbedPane.addTab("My Application Status Tracker", myAppsPanel);
            tabbedPane.addTab("Pet Health & Clinical Cards", medicalPanel);

            tabbedPane.addChangeListener(e -> {
                int selected = tabbedPane.getSelectedIndex();
                if (selected == 0) inventoryPanel.loadPetData();
                else if (selected == 2) myAppsPanel.loadMyApplications();
                else if (selected == 3) medicalPanel.loadRecords();
            });
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initStatusBar() {
        add(statusBar, BorderLayout.SOUTH);
    }

    private void refreshAllData() {
        if (activeUser.isAdmin()) {
            if (dashboardPanel != null) dashboardPanel.refreshMetrics();
            if (inventoryPanel != null) inventoryPanel.loadPetData();
            if (applicationPanel != null) applicationPanel.loadApplications();
            if (medicalPanel != null) medicalPanel.loadRecords();
        } else {
            if (inventoryPanel != null) inventoryPanel.loadPetData();
            if (myAppsPanel != null) myAppsPanel.loadMyApplications();
            if (medicalPanel != null) medicalPanel.loadRecords();
        }
        statusBar.setStatusMessage("Active portal dataset refreshed.");
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to sign out and return to the portal login screen?",
            "Sign Out Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            if (onLogout != null) {
                onLogout.run();
            } else {
                new LoginFrame().setVisible(true);
            }
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
            this,
            "PawConnect - Pet Adoption Management System\n" +
            "Version 2.6.0 (Role-Segregated Enterprise Desktop Edition)\n\n" +
            "Active Role: " + (activeUser.isAdmin() ? "Shelter Staff / Administrator" : "Prospective Pet Adopter") + "\n" +
            "Developed using 100% Java Swing Architecture.\n" +
            "Engineered in accordance with 21CSC206P Object Oriented Programming Standards.\n\n" +
            "Operating Mode: Pure Standalone GUI (In-Memory Repository)\n" +
            "Interface Architecture: Model-View-Controller (MVC)",
            "About PawConnect System",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showVivaConceptDetails() {
        String msg = """
            21CSC206P Syllabus Concept Implementation Audit:
            
            1. Role-Segregated Portals:
               • Adopter: Browse Pets, Smart Matcher, Application Status Tracker, Health Cards.
               • Shelter Admin: Operations Dashboard, Pet Inventory CRUD, Petition Adjudication, Clinical Logs.
            2. Top-Level Windows: LoginFrame (Initial Auth Window), MainFrame (JFrame), PetProfileDialog, PetEntryDialog, ReviewApplicationDialog, MedicalEntryDialog (JDialog).
            3. Secondary Containers: JTabbedPane, JSplitPane (Master/Detail), JScrollPane, JPanel, PetCardGridPanel.
            4. Layout Managers: BorderLayout, GridBagLayout, GridLayout (Cards & KPIs), FlowLayout.
            5. Components: JTable, JProgressBar, JSlider, JSpinner, JComboBox, JRadioButton, ButtonGroup, JCheckBox, JTextField, JPasswordField, JTextArea.
            6. Event Delegation Model: ActionListener, DocumentListener (Live Table Search), ListSelectionListener, MouseAdapter, WindowAdapter.
            7. MVC Architecture: Model (Entities & MatchResult), View (Swing GUI & Cards), Controller (Pet, Application, Auth, MatchingEngine).
            8. Data Layer: In-Memory DAO repository (Modular & ready for JDBC plugging without altering GUI layer).
            """;
        JOptionPane.showMessageDialog(this, msg, "21CSC206P Concept Verification", JOptionPane.INFORMATION_MESSAGE);
    }
}
