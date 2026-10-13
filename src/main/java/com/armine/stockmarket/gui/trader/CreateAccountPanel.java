package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TraderPersistenceService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class CreateAccountPanel extends JPanel {

    public CreateAccountPanel(TraderService traderService, TraderPersistenceService traderPersistenceService, Consumer<Trader> onAccountCreated, Runnable onBack) {
        setLayout(new GridBagLayout());

        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));

        // Title and description
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        JLabel subtitleLabel = new JLabel("Start your trading journey");

        // First name
        JLabel firstNameLabel = new JLabel("First name");
        JTextField firstNameField = new JTextField(15);

        // Surname
        JLabel surnameLabel = new JLabel("Surname");
        JTextField surnameField = new JTextField(15);

        // Username
        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField(15);

        // Password
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(15);

        // Confirm password
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        JPasswordField confirmPasswordField = new JPasswordField(15);

        // Keep default field sizes
        firstNameField.setMaximumSize(firstNameField.getPreferredSize());
        surnameField.setMaximumSize(surnameField.getPreferredSize());
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        confirmPasswordField.setMaximumSize(confirmPasswordField.getPreferredSize());

        // Error message
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // Buttons
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        // Create account functionality
        createButton.addActionListener(e -> {
            errorLabel.setText(" ");

            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String username = usernameField.getText().trim();

            char[] password = passwordField.getPassword();
            char[] confirmPassword = confirmPasswordField.getPassword();

            try {
                if (!Arrays.equals(password, confirmPassword))
                    throw new IllegalArgumentException("Passwords do not match.");

                Trader trader = traderService.createTrader(firstName, surname, username, password);

                traderPersistenceService.saveTrader(trader);
                onAccountCreated.accept(trader);

            } catch (IllegalArgumentException | IllegalStateException ex) {
                errorLabel.setText(ex.getMessage());

            } finally {
                Arrays.fill(password, '\0');
                Arrays.fill(confirmPassword, '\0');
            }
        });

        // Back navigation
        backButton.addActionListener(e -> onBack.run());

        // Press Enter to create account
        confirmPasswordField.addActionListener(e -> createButton.doClick());

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        firstNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        surnameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build registration panel
        accountPanel.add(titleLabel);

        accountPanel.add(Box.createVerticalStrut(8));
        accountPanel.add(subtitleLabel);

        accountPanel.add(Box.createVerticalStrut(20));
        accountPanel.add(firstNameLabel);

        accountPanel.add(Box.createVerticalStrut(6));
        accountPanel.add(firstNameField);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(surnameLabel);

        accountPanel.add(Box.createVerticalStrut(6));
        accountPanel.add(surnameField);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(usernameLabel);

        accountPanel.add(Box.createVerticalStrut(6));
        accountPanel.add(usernameField);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(passwordLabel);

        accountPanel.add(Box.createVerticalStrut(6));
        accountPanel.add(passwordField);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(confirmPasswordLabel);

        accountPanel.add(Box.createVerticalStrut(6));
        accountPanel.add(confirmPasswordField);

        accountPanel.add(Box.createVerticalStrut(8));
        accountPanel.add(errorLabel);

        accountPanel.add(Box.createVerticalStrut(15));
        accountPanel.add(createButton);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(backButton);

        add(accountPanel);
    }
}
