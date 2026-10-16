package com.armine.stockmarket.model;

public class Admin extends User {
    public Admin(String firstName, String surname, String username, String passwordHash) {
        super(firstName, surname, username, passwordHash);
    }

    public Admin(Admin other) {
        super(other);
    }

    private static Admin checkAdmin(Admin other) {
        if (other == null)
            throw new IllegalArgumentException("Admin cannot be null.");
        return other;
    }

    @Override
    public String toString() {
        return "Admin: " + getFullName();
    }
}
