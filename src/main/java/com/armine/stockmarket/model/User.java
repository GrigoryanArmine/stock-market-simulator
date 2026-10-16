package com.armine.stockmarket.model;

public abstract class User {
    private String firstName;
    private String surname;
    private String username;
    private String passwordHash;

    protected User(String firstName, String surname, String username, String passwordHash) {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be empty.");
        if (surname == null || surname.isBlank())
            throw new IllegalArgumentException("Surname cannot be empty.");
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (passwordHash == null || passwordHash.isBlank())
            throw new IllegalArgumentException("Password hash cannot be empty.");

        this.firstName = firstName.trim();
        this.surname = surname.trim();
        this.username = username.trim();
        this.passwordHash = passwordHash;
    }

    protected User(User other) {
        if (other == null) throw new IllegalArgumentException("User cannot be null.");

        this.firstName = other.firstName;
        this.surname = other.surname;
        this.username = other.username;
        this.passwordHash = other.passwordHash;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName() {
        return firstName + " " + surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
