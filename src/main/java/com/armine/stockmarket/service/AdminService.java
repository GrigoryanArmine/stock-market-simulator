package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Admin;
/**
 * Provides administrator authentication for the stock market simulator.
 *
 * This service manages an administrator account and verifies login
 * credentials using PasswordService.
 *
 * Username comparisons are case-insensitive, while passwords
 * are verified against the administrator's stored password hash.
 *
 * Invalid login credentials result in a null return value
 * rather than an exception.
 */
public class AdminService {
    private Admin admin;
    private PasswordService passwordService;
    /**
     * Creates an administrator service with the specified
     * administrator and password service.
     *
     * The provided objects are stored by reference.
     *
     * @param admin the administrator account used for authentication
     * @param passwordService the service used to verify passwords
     * @throws IllegalArgumentException if admin or
     *         passwordService is null
     */
    public AdminService(Admin admin, PasswordService passwordService) {
        if (admin == null || passwordService == null)
            throw new IllegalArgumentException("Admin and PasswordService cannot be null.");
        this.admin = admin;
        this.passwordService = passwordService;
    }
    /**
     * Creates a copy of an existing administrator service.
     *
     * The administrator account is copied using the Admin
     * copy constructor, and a new PasswordService instance
     * is created using its copy constructor.
     *
     * @param other the administrator service to copy
     * @throws IllegalArgumentException if other is null
     */
    public AdminService(AdminService other) {
        if (other == null)
            throw new IllegalArgumentException("AdminService cannot be null.");
        this.admin = new Admin(other.admin);
        this.passwordService = new PasswordService(other.passwordService);
    }
    /**
     * Authenticates an administrator using a username and password.
     *
     * The username is compared without regard to letter case,
     * and leading and trailing whitespace is ignored.
     *
     * The provided password is verified against the stored
     * password hash using PasswordService.
     *
     * @param username the administrator's username
     * @param password the password entered during login
     * @return the authenticated administrator if the credentials
     *         are valid, or null if authentication fails
     */
    public Admin login(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0) return null;
        if (!admin.getUsername().equalsIgnoreCase(username.trim())) return null;
        if (!passwordService.verifyPassword(password, admin.getPasswordHash())) return null;
        return admin;
    }
}
