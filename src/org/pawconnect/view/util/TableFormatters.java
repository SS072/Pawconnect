package org.pawconnect.view.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom Table Cell Renderers for high-density, professional desktop JTables.
 */
public class TableFormatters {

    /**
     * Renderer for pet and application status badges.
     */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(UITheme.FONT_REGULAR_BOLD);
            label.setBorder(new EmptyBorder(2, 6, 2, 6));

            String status = (value != null) ? value.toString().toUpperCase() : "";

            if (!isSelected) {
                if (status.contains("AVAILABLE") || status.contains("APPROVED")) {
                    label.setBackground(UITheme.STATUS_AVAILABLE_BG);
                    label.setForeground(UITheme.STATUS_AVAILABLE_FG);
                } else if (status.contains("PENDING") || status.contains("REVIEW")) {
                    label.setBackground(UITheme.STATUS_PENDING_BG);
                    label.setForeground(UITheme.STATUS_PENDING_FG);
                } else if (status.contains("ADOPTED")) {
                    label.setBackground(UITheme.STATUS_ADOPTED_BG);
                    label.setForeground(UITheme.STATUS_ADOPTED_FG);
                } else if (status.contains("HOLD") || status.contains("REJECTED") || status.contains("SPECIAL")) {
                    label.setBackground(UITheme.STATUS_HOLD_BG);
                    label.setForeground(UITheme.STATUS_HOLD_FG);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(UITheme.COLOR_TEXT_MAIN);
                }
            } else {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }

            label.setOpaque(true);
            return label;
        }
    }

    /**
     * Alternating row striping renderer for clean tabular readability.
     */
    public static class StripedRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                c.setForeground(UITheme.COLOR_TEXT_MAIN);
            }
            return c;
        }
    }
}
