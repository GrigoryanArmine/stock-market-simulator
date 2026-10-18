package com.armine.stockmarket.gui;

import javax.swing.*;
import java.awt.*;
/**
 * Represents the main menu panel of the graphical
 * stock market simulator.
 *
 * Displays the application title and provides buttons
 * for accessing the trader interface, administrator
 * interface, or exiting the application.
 *
 * Navigation actions are supplied through Runnable
 * callbacks, allowing MainFrame to control which
 * screen is displayed next.
 */
public class MainMenuPanel extends JPanel {
    /**
     * Creates the main menu panel with navigation buttons.
     *
     * Uses GridBagLayout to center the menu and BoxLayout
     * to arrange its components vertically.
     *
     * Each button executes its corresponding callback
     * when clicked.
     *
     * @param onTrader the action to open the trader menu
     * @param onAdmin the action to open administrator login
     * @param onExit the action to exit the application
     */
    public MainMenuPanel(Runnable onTrader, Runnable onAdmin, Runnable onExit) {
        setLayout(new GridBagLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("STOCK MARKET");
        JLabel subtitleLabel = new JLabel("SIMULATOR");
        JLabel descriptionLabel = new JLabel("Choose how you want to continue");

        JButton traderButton = new JButton("Trader");
        JButton adminButton = new JButton("Admin");
        JButton exitButton = new JButton("Exit");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        traderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        traderButton.addActionListener(e -> onTrader.run());
        adminButton.addActionListener(e -> onAdmin.run());
        exitButton.addActionListener(e -> onExit.run());

        menuPanel.add(titleLabel);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(subtitleLabel);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(descriptionLabel);

        menuPanel.add(Box.createVerticalStrut(30));
        menuPanel.add(traderButton);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(adminButton);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(exitButton);

        add(menuPanel);
    }
}
