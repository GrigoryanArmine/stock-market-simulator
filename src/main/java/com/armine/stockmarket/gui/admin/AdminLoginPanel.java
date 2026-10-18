package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
/**
 * Provides the graphical login interface for administrators.
 *
 * Allows administrators to enter their username and password
 * and authenticates their credentials using AdminService.
 *
 * The panel includes a password visibility option,
 * displays an error message for invalid credentials,
 * and supports keyboard-based form submission.
 *
 * After successful authentication, the authenticated Admin
 * object is passed to a callback that handles navigation
 * to the administrator dashboard.
 */
public class AdminLoginPanel extends JPanel {
    /**
     * Creates the administrator login panel.
     *
     * Initializes the username and password fields,
     * password visibility checkbox, error message,
     * and navigation buttons.
     *
     * Login requests are delegated to AdminService.
     * If authentication returns null, an invalid credentials
     * message is displayed without leaving the panel.
     *
     * If authentication succeeds, the authenticated administrator
     * is passed to the onLoginSuccess callback.
     *
     * After each login attempt, the password character array
     * is cleared and the password field is reset.
     *
     * @param adminService the service used to authenticate administrators
     * @param onLoginSuccess the callback invoked with the
     *        authenticated administrator after successful login
     * @param onBack the callback used to return to the main menu
     * @throws IllegalArgumentException if adminService,
     *         onLoginSuccess, or onBack is null
     */
    public AdminLoginPanel(AdminService adminService, Consumer<Admin> onLoginSuccess, Runnable onBack) {
        if (adminService == null || onLoginSuccess == null || onBack == null)
            throw new IllegalArgumentException("Admin login dependencies cannot be null.");

        setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        // Title and subtitle
        JLabel titleLabel = new JLabel("ADMIN LOGIN");
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
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            Admin admin;
            try {
                admin = adminService.login(username, password);
            } finally {
                Arrays.fill(password, '\0');
                passwordField.setText("");
            }
            if (admin == null) {
                errorLabel.setText("Invalid username or password.");
                return;
            }
            onLoginSuccess.accept(admin);
        });

        // Back navigation
        backButton.addActionListener(e -> onBack.run());

        // Press Enter to log in
        usernameField.addActionListener(e -> loginButton.doClick());
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