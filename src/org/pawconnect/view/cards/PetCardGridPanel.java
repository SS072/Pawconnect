package org.pawconnect.view.cards;

import org.pawconnect.model.Pet;
import org.pawconnect.view.dialogs.ApplicationDialog;
import org.pawconnect.view.dialogs.PetProfileDialog;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Scrollable Grid of Pet Cards.
 * Implements GridLayout card gallery with dynamic filtering and detail modal triggers.
 */
public class PetCardGridPanel extends JPanel {
    private final JPanel cardsContainer;
    private final Runnable onDataChanged;

    public PetCardGridPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_BG_CANVAS);

        cardsContainer = new JPanel();
        cardsContainer.setLayout(new GridLayout(0, 3, 14, 14));
        cardsContainer.setBackground(UITheme.COLOR_BG_CANVAS);
        cardsContainer.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.COLOR_BG_CANVAS);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updatePets(List<Pet> pets) {
        cardsContainer.removeAll();

        if (pets.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("No matching animals found for the current search criteria.");
            emptyLabel.setFont(UITheme.FONT_HEADER);
            emptyLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            cardsContainer.setLayout(new BorderLayout());
            cardsContainer.add(emptyPanel, BorderLayout.CENTER);
        } else {
            cardsContainer.setLayout(new GridLayout(0, 3, 14, 14));
            for (Pet p : pets) {
                PetCard card = new PetCard(p, this::onViewPetProfile);
                cardsContainer.add(card);
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void onViewPetProfile(Pet pet) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PetProfileDialog dialog = new PetProfileDialog(parent, pet, () -> {
            // Open Application dialog when "Apply for Adoption" is clicked inside the profile
            ApplicationDialog appDialog = new ApplicationDialog(parent, pet);
            appDialog.setVisible(true);
            if (appDialog.isSubmitted()) {
                JOptionPane.showMessageDialog(parent, "Adoption application submitted for " + pet.getName() + "!", "Application Received", JOptionPane.INFORMATION_MESSAGE);
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            }
        });
        dialog.setVisible(true);
    }
}
