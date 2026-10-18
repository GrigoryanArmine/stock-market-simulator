package com.armine.stockmarket.gui.trader;

import javax.swing.*;
import java.awt.*;
/**
 * Represents the trader navigation menu in the graphical
 * stock market simulator.
 *
 * Provides options for logging into an existing trader
 * account, creating a new account, or returning to
 * the application's main menu.
 *
 * Navigation is handled through Runnable callbacks,
 * keeping the panel independent of other GUI screens.
 */
public class TraderMenuPanel extends JPanel {
    /**
     * Creates the trader menu with its navigation buttons.
     *
     * Uses GridBagLayout to center the menu and BoxLayout
     * to arrange the components vertically.
     *
     * Buttons are configured with consistent dimensions,
     * and each button executes its corresponding callback
     * when clicked.
     *
     * @param onLogin the action to open the trader login screen
     * @param onCreateAccount the action to open account registration
     * @param onBack the action to return to the main menu
     * @throws IllegalArgumentException if any navigation
     *         callback is null
     */
    public TraderMenuPanel(Runnable onLogin, Runnable onCreateAccount, Runnable onBack) {
        if (onLogin == null || onCreateAccount == null || onBack == null)
            throw new IllegalArgumentException("Navigation callbacks cannot be null.");

        setLayout(new GridBagLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER");
        JLabel subtitleLabel = new JLabel("Manage your trading account");

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        // Consistent button sizes
        Dimension buttonSize = new Dimension(180, 35);

        loginButton.setPreferredSize(buttonSize);
        createAccountButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        loginButton.setMaximumSize(buttonSize);
        createAccountButton.setMaximumSize(buttonSize);
        backButton.setMaximumSize(buttonSize);

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Navigation
        loginButton.addActionListener(e -> onLogin.run());
        createAccountButton.addActionListener(e -> onCreateAccount.run());
        backButton.addActionListener(e -> onBack.run());

        // Build interface
        menuPanel.add(titleLabel);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(subtitleLabel);

        menuPanel.add(Box.createVerticalStrut(30));
        menuPanel.add(loginButton);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(createAccountButton);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(backButton);

        add(menuPanel);
    }
}
