package org.pawconnect.view.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Enhanced Desktop Design System for PawConnect.
 * Delivers a clean, attractive, modern desktop aesthetic with antialiased cards,
 * padded form controls, rounded buttons, and harmonious corporate color palettes.
 */
public class UITheme {

    // Palette - Modern Slate Canvas & Deep Teal/Navy Accents
    public static final Color COLOR_PRIMARY = new Color(15, 118, 110);       // Rich Deep Teal #0F766E
    public static final Color COLOR_PRIMARY_HOVER = new Color(13, 148, 136); // Lighter Teal #0D9488
    public static final Color COLOR_PRIMARY_DARK = new Color(17, 94, 89);    // Darker Teal #115E59
    public static final Color COLOR_NAVY = new Color(15, 23, 42);            // Slate Navy #0F172A

    public static final Color COLOR_BG_CANVAS = new Color(248, 250, 252);    // Ultra-clean Slate #F8FAFC
    public static final Color COLOR_BG_LIGHT = COLOR_BG_CANVAS;              // Compatibility alias
    public static final Color COLOR_BG_SIDEBAR = new Color(241, 245, 249);   // Sidebar Slate #F1F5F9
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_CARD_BORDER = new Color(226, 232, 240);  // Crisp subtle border #E2E8F0
    public static final Color COLOR_BORDER = COLOR_CARD_BORDER;               // Compatibility alias
    public static final Color COLOR_INPUT_BORDER = new Color(203, 213, 225); // #CBD5E1

    public static final Color COLOR_TEXT_MAIN = new Color(30, 41, 59);       // Charcoal #1E293B
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);   // Slate Gray #64748B
    public static final Color COLOR_TEXT_LIGHT = new Color(148, 163, 184);

    // Status Badges Colors
    public static final Color STATUS_AVAILABLE_BG = new Color(220, 252, 231);
    public static final Color STATUS_AVAILABLE_FG = new Color(22, 101, 52);

    public static final Color STATUS_PENDING_BG = new Color(254, 243, 199);
    public static final Color STATUS_PENDING_FG = new Color(146, 64, 14);

    public static final Color STATUS_ADOPTED_BG = new Color(224, 231, 255);
    public static final Color STATUS_ADOPTED_FG = new Color(55, 48, 163);

    public static final Color STATUS_HOLD_BG = new Color(254, 226, 226);
    public static final Color STATUS_HOLD_FG = new Color(153, 27, 27);

    // Typography
    public static final Font FONT_BRAND = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_REGULAR_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 26);

    // Borders
    public static final Border BORDER_PANEL = new CompoundBorder(
        new LineBorder(COLOR_CARD_BORDER, 1),
        new EmptyBorder(12, 14, 12, 14)
    );

    public static final Border BORDER_INPUT_PADDED = new CompoundBorder(
        new LineBorder(COLOR_INPUT_BORDER, 1, true),
        new EmptyBorder(6, 10, 6, 10)
    );

    /**
     * Modern Flat Button with subtle rounded corners and hover effect.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_PRIMARY_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_PRIMARY_HOVER);
                } else {
                    g2.setColor(COLOR_PRIMARY);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_REGULAR_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Secondary outlined button with rounded corners and hover feedback.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(241, 245, 249));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(248, 250, 252));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setColor(COLOR_INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_REGULAR);
        btn.setForeground(COLOR_TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Padded JTextField with modern rounded border.
     */
    public static JTextField createPaddedTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_REGULAR);
        field.setBorder(BORDER_INPUT_PADDED);
        field.setBackground(Color.WHITE);
        field.setForeground(COLOR_TEXT_MAIN);
        return field;
    }

    /**
     * Padded JPasswordField with modern rounded border.
     */
    public static JPasswordField createPaddedPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_REGULAR);
        field.setBorder(BORDER_INPUT_PADDED);
        field.setBackground(Color.WHITE);
        field.setForeground(COLOR_TEXT_MAIN);
        return field;
    }

    /**
     * Section title label with proper typographic scale.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(COLOR_NAVY);
        lbl.setBorder(new EmptyBorder(4, 0, 8, 0));
        return lbl;
    }

    /**
     * Creates an antialiased rounded card panel.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.setColor(COLOR_CARD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        return card;
    }

    /**
     * Pill-shaped Chip / Tag badge.
     */
    public static JLabel createChipBadge(String text, Color bg, Color fg) {
        JLabel chip = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(FONT_SMALL);
        chip.setForeground(fg);
        chip.setHorizontalAlignment(SwingConstants.CENTER);
        chip.setBorder(new EmptyBorder(3, 8, 3, 8));
        chip.setOpaque(false);
        return chip;
    }
}
