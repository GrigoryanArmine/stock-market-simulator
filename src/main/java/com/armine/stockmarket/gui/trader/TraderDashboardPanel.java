package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class TraderDashboardPanel extends JPanel {

    public TraderDashboardPanel(
            Trader trader,
            boolean newAccount,
            Runnable onMarket,
            Runnable onPortfolio,
            Runnable onTransactions,
            Runnable onLogout) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Dashboard title
        JLabel titleLabel = new JLabel("TRADER DASHBOARD");

        // Account creation message
        JLabel successLabel = new JLabel(" ");

        if (newAccount) {
            successLabel.setText("Account created successfully!");
            successLabel.setForeground(new Color(0, 128, 0));
        }

        // Trader information
        JLabel nameLabel = new JLabel("Welcome, " + trader.getFullName() + "!");
        JLabel usernameLabel = new JLabel("Username: @" + trader.getUsername());
        JLabel balanceLabel = new JLabel("Balance: $" + String.format(Locale.US, "%.2f", trader.getBalance())
        );

        // Navigation buttons
        JButton marketButton = new JButton("View Market");
        JButton portfolioButton = new JButton("Portfolio");
        JButton transactionsButton = new JButton("Transactions");
        JButton logoutButton = new JButton("Logout");

        // Button actions
        marketButton.addActionListener(e -> onMarket.run());
        portfolioButton.addActionListener(e -> onPortfolio.run());
        transactionsButton.addActionListener(e -> onTransactions.run());
        logoutButton.addActionListener(e -> onLogout.run());

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        marketButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        portfolioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build dashboard
        add(Box.createVerticalStrut(40));
        add(titleLabel);

        if (newAccount) {
            add(Box.createVerticalStrut(10));
            add(successLabel);
        }

        add(Box.createVerticalStrut(20));
        add(nameLabel);

        add(Box.createVerticalStrut(5));
        add(usernameLabel);

        add(Box.createVerticalStrut(10));
        add(balanceLabel);

        add(Box.createVerticalStrut(30));
        add(marketButton);

        add(Box.createVerticalStrut(10));
        add(portfolioButton);

        add(Box.createVerticalStrut(10));
        add(transactionsButton);

        add(Box.createVerticalStrut(20));
        add(logoutButton);
    }
}
