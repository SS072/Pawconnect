package org.pawconnect.view.dialogs;

import org.pawconnect.model.Pet;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dedicated Pet Profile Inspector Dialog (PetProfileFrame concept).
 * Renders complete physical, clinical, behavioral, and lifestyle compatibility specifications.
 */
public class PetProfileDialog extends JDialog {
    private final Pet pet;
    private final Runnable onApplyAction;

    public PetProfileDialog(Frame owner, Pet pet, Runnable onApplyAction) {
        super(owner, "Pet Profile - " + pet.getName() + " [ID: " + pet.getId() + "]", true);
        this.pet = pet;
        this.onApplyAction = onApplyAction;

        initComponents();

        setSize(580, 640);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.COLOR_BG_CANVAS);

        // Header Panel with deep teal banner
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBlock.setOpaque(false);

        JLabel nameLabel = new JLabel(pet.getName());
        nameLabel.setFont(UITheme.FONT_BRAND);
        nameLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(pet.getBreed() + " • " + pet.getSpecies() + " • " + pet.getAgeFormatted());
        subLabel.setFont(UITheme.FONT_REGULAR);
        subLabel.setForeground(new Color(204, 251, 241)); // Soft light teal

        titleBlock.add(nameLabel);
        titleBlock.add(subLabel);

        // Status Badge in header
        Color bg = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_AVAILABLE_BG :
                   "PENDING".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_PENDING_BG : UITheme.STATUS_ADOPTED_BG;
        Color fg = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_AVAILABLE_FG :
                   "PENDING".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_PENDING_FG : UITheme.STATUS_ADOPTED_FG;
        JLabel statusBadge = UITheme.createChipBadge(pet.getAdoptionStatus(), bg, fg);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(statusBadge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Content Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Vital Statistics Card
        JPanel statsCard = UITheme.createCardPanel();
        statsCard.setLayout(new GridLayout(3, 2, 8, 8));

        statsCard.add(createAttribute("Gender:", pet.getGender()));
        statsCard.add(createAttribute("Size Class:", pet.getSize()));
        statsCard.add(createAttribute("Health Classification:", pet.getHealthStatus()));
        statsCard.add(createAttribute("Intake Date:", pet.getIntakeDate()));
        statsCard.add(createAttribute("House Trained:", pet.isHouseTrained() ? "Yes" : "In Progress"));
        statsCard.add(createAttribute("Activity Requirement:", pet.getActivityLevel() + " / 5 (" + (pet.getActivityLevel() >= 4 ? "High" : (pet.getActivityLevel() >= 2 ? "Moderate" : "Low")) + ")"));

        // 2. Behavioral Compatibility Chips
        JPanel behaviorCard = UITheme.createCardPanel();
        behaviorCard.setLayout(new BorderLayout(8, 8));
        JLabel behTitle = new JLabel("Household & Living Compatibility");
        behTitle.setFont(UITheme.FONT_HEADER);
        behTitle.setForeground(UITheme.COLOR_NAVY);
        behaviorCard.add(behTitle, BorderLayout.NORTH);

        JPanel chipsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chipsRow.setOpaque(false);

        if (pet.isGoodWithChildren()) {
            chipsRow.add(UITheme.createChipBadge("Good with Children", new Color(220, 252, 231), new Color(22, 101, 52)));
        } else {
            chipsRow.add(UITheme.createChipBadge("Adult Household Preferred", new Color(254, 226, 226), new Color(153, 27, 27)));
        }

        if (pet.isGoodWithDogs()) {
            chipsRow.add(UITheme.createChipBadge("Dog Friendly", new Color(224, 231, 255), new Color(55, 48, 163)));
        }

        if (pet.isGoodWithCats()) {
            chipsRow.add(UITheme.createChipBadge("Cat Friendly", new Color(254, 243, 199), new Color(146, 64, 14)));
        }

        behaviorCard.add(chipsRow, BorderLayout.CENTER);

        // 3. Biography & Background Notes Card
        JPanel descCard = UITheme.createCardPanel();
        descCard.setLayout(new BorderLayout(6, 6));
        JLabel descTitle = new JLabel("Personality & Care Requirements");
        descTitle.setFont(UITheme.FONT_HEADER);
        descTitle.setForeground(UITheme.COLOR_NAVY);

        JTextArea descArea = new JTextArea(pet.getDescription());
        descArea.setFont(UITheme.FONT_REGULAR);
        descArea.setForeground(UITheme.COLOR_TEXT_MAIN);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);

        descCard.add(descTitle, BorderLayout.NORTH);
        descCard.add(descArea, BorderLayout.CENTER);

        body.add(statsCard);
        body.add(Box.createVerticalStrut(10));
        body.add(behaviorCard);
        body.add(Box.createVerticalStrut(10));
        body.add(descCard);

        add(new JScrollPane(body), BorderLayout.CENTER);

        // Footer Action Row
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.COLOR_CARD_BORDER));

        JButton closeBtn = UITheme.createSecondaryButton("Close Profile");
        closeBtn.addActionListener(e -> dispose());

        JButton applyBtn = UITheme.createPrimaryButton("Apply for Adoption");
        boolean isAvailable = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus());
        applyBtn.setEnabled(isAvailable);
        applyBtn.addActionListener(e -> {
            dispose();
            if (onApplyAction != null) {
                onApplyAction.run();
            }
        });

        footer.add(closeBtn);
        footer.add(applyBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createAttribute(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(UITheme.FONT_REGULAR);
        v.setForeground(UITheme.COLOR_TEXT_MAIN);

        p.add(l);
        p.add(v);
        return p;
    }
}
