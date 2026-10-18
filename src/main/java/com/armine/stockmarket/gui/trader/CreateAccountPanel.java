package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TraderPersistenceService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
/**
 * Provides the graphical interface for registering new trader accounts.
 *
 * Collects a trader's personal information, username, password,
 * and password confirmation.
 *
 * Account validation and creation are delegated to TraderService,
 * while account data is saved using TraderPersistenceService.
 *
 * If saving fails after an account has been created in memory,
 * the panel allows the user to retry saving without creating
 * a duplicate account.
 *
 * After successful registration and persistence, the created
 * trader is passed to a callback for further navigation.
 */
public class CreateAccountPanel extends JPanel {
    /**
     * Holds an account that has been created but has not yet
     * been successfully saved.
     *
     * This prevents repeated registration attempts from
     * creating duplicate trader accounts after a saving failure.
     */
    private Trader pendingTrader;
    /**
     * Creates the trader registration panel.
     *
     * Initializes fields for the trader's first name, surname,
     * username, password, and password confirmation.
     *
     * When registration is requested, the panel verifies that
     * both passwords match and delegates account creation
     * to TraderService.
     *
     * After account creation, it attempts to save the trader
     * using TraderPersistenceService.
     *
     * If saving fails, the created account is retained in
     * pendingTrader, the registration fields become read-only,
     * and the create button changes to "Retry Save".
     * Back navigation is disabled until saving succeeds.
     *
     * After successful saving, the created trader is passed
     * to the onAccountCreated callback.
     *
     * Password character arrays are cleared after the
     * registration attempt.
     *
     * @param traderService the service responsible for
     *        creating and validating trader accounts
     * @param traderPersistenceService the service responsible
     *        for saving trader account data
     * @param onAccountCreated the callback invoked with the
     *        newly registered trader after successful saving
     * @param onBack the callback used to return to the trader menu
     * @throws IllegalArgumentException if any required service
     *         or callback is null
     */
    public CreateAccountPanel(TraderService traderService, TraderPersistenceService traderPersistenceService, Consumer<Trader> onAccountCreated, Runnable onBack) {
        if (traderService == null || traderPersistenceService == null || onAccountCreated == null || onBack == null)
            throw new IllegalArgumentException("Account panel dependencies cannot be null.");

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
            if (pendingTrader == null) {
                String firstName = firstNameField.getText().trim();
                String surname = surnameField.getText().trim();
                String username = usernameField.getText().trim();

                char[] password = passwordField.getPassword();
                char[] confirmPassword = confirmPasswordField.getPassword();

                try {
                    if (!Arrays.equals(password, confirmPassword))
                        throw new IllegalArgumentException("Passwords do not match.");
                    pendingTrader = traderService.createTrader(firstName, surname, username, password);
                } catch (IllegalArgumentException ex) {
                    errorLabel.setText(ex.getMessage());
                    return;
                } finally {
                    Arrays.fill(password, '\0');
                    Arrays.fill(confirmPassword, '\0');
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                }
            }
            try {
                traderPersistenceService.saveTrader(pendingTrader);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                errorLabel.setText(ex.getMessage());
                createButton.setText("Retry Save");

                // Prevent changes to an account already created
                firstNameField.setEditable(false);
                surnameField.setEditable(false);
                usernameField.setEditable(false);
                passwordField.setEditable(false);
                confirmPasswordField.setEditable(false);

                // Avoid leaving the unsaved account behind
                backButton.setEnabled(false);
                return;
            }
            // Account successfully saved
            Trader createdTrader = pendingTrader;
            pendingTrader = null;

            onAccountCreated.accept(createdTrader);
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
