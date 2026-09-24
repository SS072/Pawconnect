package org.pawconnect.view.cards;

import org.pawconnect.model.Pet;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Visual Pet Card component.
 * Implements the card layout concept from the syllabus with clean typography,
 * temperament chips, status tags, and interactive action buttons.
 */
public class PetCard extends JPanel {
    private final Pet pet;
    private final Consumer<Pet> onViewProfile;
    private boolean isHovered = false;

    public PetCard(Pet pet, Consumer<Pet> onViewProfile) {
        this.pet = pet;
        this.onViewProfile = onViewProfile;

        setLayout(new BorderLayout(8, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(280, 200));

        initComponents();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    private void initComponents() {
        // Top Header: Name and Status Badge
        JPanel topRow = new JPanel(new BorderLayout(6, 0));
        topRow.setOpaque(false);

        JLabel nameLabel = new JLabel(pet.getName());
        nameLabel.setFont(UITheme.FONT_TITLE);
        nameLabel.setForeground(UITheme.COLOR_NAVY);

        Color bg = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_AVAILABLE_BG :
                   "PENDING".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_PENDING_BG : UITheme.STATUS_ADOPTED_BG;
        Color fg = "AVAILABLE".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_AVAILABLE_FG :
                   "PENDING".equalsIgnoreCase(pet.getAdoptionStatus()) ? UITheme.STATUS_PENDING_FG : UITheme.STATUS_ADOPTED_FG;
        JLabel statusBadge = UITheme.createChipBadge(pet.getAdoptionStatus(), bg, fg);

        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(statusBadge, BorderLayout.EAST);

        // Subtitle: Breed, Age, Gender
        JPanel metaPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        metaPanel.setOpaque(false);

        JLabel breedLabel = new JLabel(pet.getBreed() + " (" + pet.getSpecies() + ")");
        breedLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        breedLabel.setForeground(UITheme.COLOR_PRIMARY);

        JLabel ageGenderLabel = new JLabel(pet.getAgeFormatted() + " • " + pet.getGender() + " • Size: " + pet.getSize());
        ageGenderLabel.setFont(UITheme.FONT_SMALL);
        ageGenderLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        metaPanel.add(breedLabel);
        metaPanel.add(ageGenderLabel);

        JPanel headerBlock = new JPanel(new BorderLayout(0, 4));
        headerBlock.setOpaque(false);
        headerBlock.add(topRow, BorderLayout.NORTH);
        headerBlock.add(metaPanel, BorderLayout.CENTER);
        add(headerBlock, BorderLayout.NORTH);

        // Middle: Behavioral Tags Row
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        tagsPanel.setOpaque(false);

        if (pet.isGoodWithChildren()) {
            tagsPanel.add(UITheme.createChipBadge("Kids OK", new Color(241, 245, 249), UITheme.COLOR_TEXT_MAIN));
        }
        if (pet.isGoodWithDogs()) {
            tagsPanel.add(UITheme.createChipBadge("Dogs OK", new Color(241, 245, 249), UITheme.COLOR_TEXT_MAIN));
        }
        if (pet.isHouseTrained()) {
            tagsPanel.add(UITheme.createChipBadge("House Trained", new Color(241, 245, 249), UITheme.COLOR_TEXT_MAIN));
        }

        JLabel activityLabel = new JLabel("Energy: " + pet.getActivityLevel() + "/5");
        activityLabel.setFont(UITheme.FONT_SMALL);
        activityLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
        tagsPanel.add(activityLabel);

        add(tagsPanel, BorderLayout.CENTER);

        // Bottom: Action Button
        JButton viewBtn = UITheme.createPrimaryButton("View Profile");
        viewBtn.addActionListener(e -> {
            if (onViewProfile != null) {
                onViewProfile.accept(pet);
            }
        });
        add(viewBtn, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card Background
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

        // Card Border with hover feedback
        if (isHovered) {
            g2.setColor(UITheme.COLOR_PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(UITheme.COLOR_CARD_BORDER);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

        g2.dispose();
        super.paintComponent(g);
    }
}
