
package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TraderPersistenceService;

import javax.swing.*;
import java.awt.*;
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

        // Keep default text field sizes
        firstNameField.setMaximumSize(firstNameField.getPreferredSize());
        surnameField.setMaximumSize(surnameField.getPreferredSize());
        usernameField.setMaximumSize(usernameField.getPreferredSize());

        // Error message
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // Buttons
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        // Create account functionality
        createButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String username = usernameField.getText().trim();

            try {
                Trader trader = traderService.createTrader(firstName, surname, username);

                traderPersistenceService.saveTrader(trader);
                onAccountCreated.accept(trader);

            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        // Back navigation
        backButton.addActionListener(e -> onBack.run());

        // Press Enter to create account
        usernameField.addActionListener(e -> createButton.doClick());

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        firstNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        surnameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        accountPanel.add(Box.createVerticalStrut(8));
        accountPanel.add(errorLabel);

        accountPanel.add(Box.createVerticalStrut(15));
        accountPanel.add(createButton);

        accountPanel.add(Box.createVerticalStrut(12));
        accountPanel.add(backButton);

        add(accountPanel);
    }
}
