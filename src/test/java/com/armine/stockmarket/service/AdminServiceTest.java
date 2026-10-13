package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    private AdminService adminService;
    private Admin admin;

    @BeforeEach
    void setUp() {
        PasswordService passwordService = new PasswordService();

        String passwordHash = passwordService.hashPassword(
                "AdminPassword123!".toCharArray()
        );

        admin = new Admin(
                "System",
                "Administrator",
                "demo_admin",
                passwordHash
        );

        adminService = new AdminService(admin, passwordService);
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        Admin loggedInAdmin = adminService.login(
                "demo_admin",
                "AdminPassword123!".toCharArray()
        );

        assertNotNull(loggedInAdmin);
        assertSame(admin, loggedInAdmin);
    }

    @Test
    void shouldRejectIncorrectPassword() {
        Admin loggedInAdmin = adminService.login(
                "demo_admin",
                "WrongPassword123!".toCharArray()
        );

        assertNull(loggedInAdmin);
    }

    @Test
    void shouldRejectUnknownUsername() {
        Admin loggedInAdmin = adminService.login(
                "unknown_admin",
                "AdminPassword123!".toCharArray()
        );

        assertNull(loggedInAdmin);
    }

    @Test
    void shouldRejectNullUsername() {
        Admin loggedInAdmin = adminService.login(
                null,
                "AdminPassword123!".toCharArray()
        );

        assertNull(loggedInAdmin);
    }

    @Test
    void shouldRejectNullPassword() {
        Admin loggedInAdmin = adminService.login(
                "demo_admin",
                null
        );

        assertNull(loggedInAdmin);
    }

    @Test
    void shouldAllowUsernameWithSurroundingSpaces() {
        Admin loggedInAdmin = adminService.login(
                "  demo_admin  ",
                "AdminPassword123!".toCharArray()
        );

        assertSame(admin, loggedInAdmin);
    }
}
