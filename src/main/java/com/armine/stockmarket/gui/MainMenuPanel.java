package com.armine.stockmarket.gui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

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
