package com.armine.stockmarket.gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    public AdminDashboardPanel(Runnable onManageStocks, Runnable onManageTraders, Runnable onBack) {
        setLayout(new GridBagLayout());

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");

        JButton stocksButton = new JButton("Manage Stocks");
        JButton tradersButton = new JButton("Manage Traders");
        JButton backButton = new JButton("Back");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stocksButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tradersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        stocksButton.setPreferredSize(new Dimension(180, 35));
        tradersButton.setPreferredSize(new Dimension(180, 35));
        backButton.setPreferredSize(new Dimension(100, 35));

        stocksButton.setMaximumSize(new Dimension(180, 35));
        tradersButton.setMaximumSize(new Dimension(180, 35));
        backButton.setMaximumSize(new Dimension(100, 35));

        stocksButton.addActionListener(e -> onManageStocks.run());
        tradersButton.addActionListener(e -> onManageTraders.run());
        backButton.addActionListener(e -> onBack.run());

        dashboardPanel.add(titleLabel);

        dashboardPanel.add(Box.createVerticalStrut(30));
        dashboardPanel.add(stocksButton);

        dashboardPanel.add(Box.createVerticalStrut(15));
        dashboardPanel.add(tradersButton);

        dashboardPanel.add(Box.createVerticalStrut(15));
        dashboardPanel.add(backButton);

        add(dashboardPanel);
    }
}
