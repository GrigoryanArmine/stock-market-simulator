
package com.armine.stockmarket.gui.trader;

import javax.swing.*;
import java.awt.*;

public class TraderMenuPanel extends JPanel {

    public TraderMenuPanel(Runnable onLogin, Runnable onCreateAccount, Runnable onBack) {
        setLayout(new GridBagLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER");
        JLabel subtitleLabel = new JLabel("Manage your trading account");

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> onLogin.run());
        createAccountButton.addActionListener(
                e -> onCreateAccount.run()
        );
        backButton.addActionListener(e -> onBack.run());

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
