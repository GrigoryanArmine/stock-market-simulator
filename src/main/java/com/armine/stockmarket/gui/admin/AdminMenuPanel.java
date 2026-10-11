package com.armine.stockmarket.gui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminMenuPanel extends JPanel {
    public AdminMenuPanel(Runnable onContinue, Runnable onBack) {
        setLayout(new GridBagLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ADMIN MENU");

        JButton continueButton = new JButton("Continue");
        JButton backButton = new JButton("Back");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton.addActionListener(e -> onContinue.run());
        backButton.addActionListener(e -> onBack.run());

        menuPanel.add(titleLabel);

        menuPanel.add(Box.createVerticalStrut(30));
        menuPanel.add(continueButton);

        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(backButton);

        add(menuPanel);
    }
}
