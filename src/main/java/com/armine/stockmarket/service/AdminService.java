
package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Admin;

public class AdminService {
    private Admin admin;
    private PasswordService passwordService;

    public AdminService(Admin admin, PasswordService passwordService) {
        if (admin == null || passwordService == null) {
            throw new IllegalArgumentException("Admin and PasswordService cannot be null.");
        }
        this.admin = admin;
        this.passwordService = passwordService;
    }

    public Admin login(String username, char[] password) {
        if (username == null || password == null) {
            return null;
        }
        if (!admin.getUsername().equals(username.trim())) {
            return null;
        }
        if (!passwordService.verifyPassword(password, admin.getPasswordHash())) {
            return null;
        }
        return admin;
    }
}
