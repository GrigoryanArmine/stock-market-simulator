package com.armine.stockmarket.model;

public class User {
    private String firstName;
    private String surname;
    private String username;
    private String passwordHash;

    public User(String firstName, String surname, String username) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }

        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be empty.");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
    }

    public User(String firstName, String surname, String username, String passwordHash) {
        this(firstName, surname, username);
        setPasswordHash(passwordHash);
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

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
