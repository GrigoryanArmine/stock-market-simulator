package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TraderService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
/**
 * Provides the graphical login interface for trader accounts.
 *
 * Allows traders to enter their username and password
 * and authenticates their credentials using TraderService.
 *
 * The panel provides an option to show or hide the password,
 * displays authentication errors, and supports keyboard
 * navigation through the login form.
 *
 * After successful authentication, the authenticated Trader
 * is passed to a callback that handles navigation to the
 * trader dashboard.
 */
public class LoginPanel extends JPanel {
    /**
     * Creates the trader login panel.
     *
     * Initializes username and password fields, a password
     * visibility checkbox, error messages, and navigation buttons.
     *
     * Login requests are delegated to TraderService.
     * If authentication succeeds, the authenticated trader
     * is passed to the onLoginSuccess callback.
     *
     * If authentication fails, the error message is displayed
     * without leaving the current panel.
     *
     * The password character array is cleared after each
     * login attempt, and the password field is reset.
     *
     * @param traderService the service used to authenticate traders
     * @param onLoginSuccess the callback invoked with the
     *        authenticated trader after successful login
     * @param onBack the callback used to return to the trader menu
     * @throws IllegalArgumentException if traderService,
     *         onLoginSuccess, or onBack is null
     */
    public LoginPanel(TraderService traderService, Consumer<Trader> onLoginSuccess, Runnable onBack) {
        if (traderService == null || onLoginSuccess == null || onBack == null)
            throw new IllegalArgumentException("Login panel dependencies cannot be null.");

        setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER LOGIN");
        JLabel subtitleLabel = new JLabel("Welcome back! Sign in to your account.");

        // Username
        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField(15);
        usernameField.setMaximumSize(usernameField.getPreferredSize());

        // Password
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(passwordField.getPreferredSize());

        // Show password checkbox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show password");
        char defaultEchoChar = passwordField.getEchoChar();

        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        // Error message
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        // Login functionality
        loginButton.addActionListener(e -> {
            errorLabel.setText(" ");
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            Trader trader;
            try {
                trader = traderService.login(username, password);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            } finally {
                Arrays.fill(password, '\0');
                passwordField.setText("");
            }
            onLoginSuccess.accept(trader);
        });
        // Back navigation
        backButton.addActionListener(e -> onBack.run());

        // Press Enter to log in
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> loginButton.doClick());

        // Center components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build login panel
        loginPanel.add(titleLabel);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(subtitleLabel);

        loginPanel.add(Box.createVerticalStrut(30));

        loginPanel.add(usernameLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(usernameField);

        loginPanel.add(Box.createVerticalStrut(20));

        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordField);

        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(showPasswordCheckBox);

        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(errorLabel);

        loginPanel.add(Box.createVerticalStrut(25));
        loginPanel.add(buttonPanel);

        add(loginPanel);
    }
}
