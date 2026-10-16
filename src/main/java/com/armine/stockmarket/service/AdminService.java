
package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Admin;

public class AdminService {
    private Admin admin;
    private PasswordService passwordService;

    public AdminService(Admin admin, PasswordService passwordService) {
        if (admin == null || passwordService == null)
            throw new IllegalArgumentException("Admin and PasswordService cannot be null.");
        this.admin = admin;
        this.passwordService = passwordService;
    }
    public AdminService(AdminService other) {
        if (other == null)
            throw new IllegalArgumentException("AdminService cannot be null.");
        this.admin = new Admin(other.admin);
        this.passwordService = new PasswordService(other.passwordService);
    }

    public Admin login(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0) return null;
        if (!admin.getUsername().equalsIgnoreCase(username.trim())) return null;
        if (!passwordService.verifyPassword(password, admin.getPasswordHash())) return null;
        return admin;
    }
}
