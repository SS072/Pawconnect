package org.pawconnect.view;

import org.pawconnect.controller.AuthController;
import org.pawconnect.model.User;
import org.pawconnect.view.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bottom status bar providing real-time system feedback, active user session,
 * database connectivity health, and current record counts.
 */
public class StatusBar extends JPanel {
    private final JLabel dbStatusLabel;
    private final JLabel userSessionLabel;
    private final JLabel recordCountLabel;
    private final JLabel timestampLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBackground(new Color(238, 242, 246));
        setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, UITheme.COLOR_BORDER),
            new EmptyBorder(4, 12, 4, 12)
        ));

        // Left Panel: DB & Session
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftPanel.setOpaque(false);

        dbStatusLabel = new JLabel("Mode: Pure GUI Standalone (In-Memory)");
        dbStatusLabel.setFont(UITheme.FONT_SMALL);
        dbStatusLabel.setForeground(UITheme.COLOR_PRIMARY);

        userSessionLabel = new JLabel("User: " + AuthController.getInstance().getCurrentUser().getFullName());
        userSessionLabel.setFont(UITheme.FONT_SMALL);
        userSessionLabel.setForeground(UITheme.COLOR_TEXT_MAIN);

        leftPanel.add(dbStatusLabel);
        leftPanel.add(new JSeparator(SwingConstants.VERTICAL));
        leftPanel.add(userSessionLabel);

        // Right Panel: Records & Clock
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);

        recordCountLabel = new JLabel("Ready");
        recordCountLabel.setFont(UITheme.FONT_SMALL);
        recordCountLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        timestampLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timestampLabel.setFont(UITheme.FONT_SMALL);
        timestampLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        rightPanel.add(recordCountLabel);
        rightPanel.add(new JSeparator(SwingConstants.VERTICAL));
        rightPanel.add(timestampLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Listen for user role switches
        AuthController.getInstance().addListener(user -> updateUser(user));

        // Timer for clock updates
        Timer timer = new Timer(60000, e -> {
            timestampLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        });
        timer.start();
    }

    public void setStatusMessage(String message) {
        recordCountLabel.setText(message);
    }

    public void updateUser(User user) {
        userSessionLabel.setText("User: " + user.getFullName() + " [" + user.getRole() + "]");
    }
}
