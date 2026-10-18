package com.armine.stockmarket.model;
/**
 * Represents an administrator account in the stock market simulator.
 *
 * Admin extends User and inherits common account information,
 * including the administrator's name, username, and password hash.
 *
 * Administrator authentication and market management operations
 * are handled by the corresponding service classes.
 */
public class Admin extends User {
    /**
     * Creates an administrator with the specified account information.
     *
     * The inherited User constructor validates the provided values.
     *
     * @param firstName the administrator's first name
     * @param surname the administrator's surname
     * @param username the administrator's username
     * @param passwordHash the administrator's stored password hash
     * @throws IllegalArgumentException if any argument
     *         is null, empty, or contains only whitespace
     */
    public Admin(String firstName, String surname, String username, String passwordHash) {
        super(firstName, surname, username, passwordHash);
    }
    /**
     * Creates a copy of an existing administrator.
     *
     * The new administrator receives the same account
     * information and stored password hash.
     *
     * @param other the administrator to copy
     * @throws IllegalArgumentException if other is null
     */
    public Admin(Admin other) {
        super(other);
    }
    /**
     * Returns a readable representation of the administrator.
     *
     * @return the administrator's full name prefixed with "Admin:"
     */
    @Override
    public String toString() {
        return "Admin: " + getFullName();
    }
}
