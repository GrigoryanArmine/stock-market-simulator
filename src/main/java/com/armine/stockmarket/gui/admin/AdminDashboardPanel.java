package com.armine.stockmarket.gui.admin;

import javax.swing.*;
import java.awt.*;
/**
 * Represents the administrator dashboard of the
 * graphical stock market simulator.
 *
 * Provides navigation options for managing market
 * stocks, viewing registered traders, and logging out.
 *
 * The dashboard uses Runnable callbacks to delegate
 * navigation to MainFrame instead of creating other
 * GUI screens directly.
 */
public class AdminDashboardPanel extends JPanel {
    /**
     * Creates the administrator dashboard with navigation buttons.
     *
     * Uses GridBagLayout to center the dashboard and
     * BoxLayout to arrange its components vertically.
     *
     * The buttons have consistent dimensions and execute
     * their corresponding navigation callbacks when clicked.
     *
     * @param onManageStocks the action to open stock management
     * @param onManageTraders the action to open trader management
     * @param onLogout the action to return to the main menu
     * @throws IllegalArgumentException if any navigation
     *         callback is null
     */
    public AdminDashboardPanel(Runnable onManageStocks, Runnable onManageTraders, Runnable onLogout) {
        if (onManageStocks == null || onManageTraders == null || onLogout == null)
            throw new IllegalArgumentException("Navigation callbacks cannot be null.");

        setLayout(new GridBagLayout());

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");

        JButton stocksButton = new JButton("Manage Stocks");
        JButton tradersButton = new JButton("Manage Traders");
        JButton logoutButton = new JButton("Logout");

        // Consistent button sizes
        Dimension buttonSize = new Dimension(180, 35);
        JButton[] buttons = {stocksButton, tradersButton, logoutButton};

        for (JButton button : buttons) {
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Navigation
        stocksButton.addActionListener(e -> onManageStocks.run());
        tradersButton.addActionListener(e -> onManageTraders.run());
        logoutButton.addActionListener(e -> onLogout.run());

        // Build dashboard
        dashboardPanel.add(titleLabel);

        dashboardPanel.add(Box.createVerticalStrut(30));
        dashboardPanel.add(stocksButton);

        dashboardPanel.add(Box.createVerticalStrut(15));
        dashboardPanel.add(tradersButton);

        dashboardPanel.add(Box.createVerticalStrut(15));
        dashboardPanel.add(logoutButton);

        add(dashboardPanel);
    }
}