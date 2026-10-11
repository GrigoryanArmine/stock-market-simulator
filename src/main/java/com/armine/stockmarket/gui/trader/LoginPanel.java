
package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TraderService;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {

    public LoginPanel(TraderService traderService, Consumer<Trader> onLoginSuccess, Runnable onBack) {

        setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER LOGIN");
        JLabel subtitleLabel = new JLabel("Welcome back! Sign in to your account.");
        JLabel usernameLabel = new JLabel("Username");

        JTextField usernameField = new JTextField(15);
        usernameField.setMaximumSize(usernameField.getPreferredSize());

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Login functionality
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                errorLabel.setText("Please enter your username.");
                return;
            }

            Trader trader = traderService.findTraderByUsername(username);

            if (trader == null) {
                errorLabel.setText("Trader not found.");
            } else {
                errorLabel.setText(" ");
                onLoginSuccess.accept(trader);
            }
        });

        // Back navigation
        backButton.addActionListener(e -> onBack.run());

        // Press Enter to log in
        usernameField.addActionListener(e -> loginButton.doClick());

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build login panel
        loginPanel.add(titleLabel);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(subtitleLabel);

        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(usernameLabel);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(usernameField);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(errorLabel);

        loginPanel.add(Box.createVerticalStrut(25));
        loginPanel.add(loginButton);

        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(backButton);

        add(loginPanel);
    }
}
