package com.armine.stockmarket.model;

public class User {
    private String firstName;
    private String surname;

    public User(String firstName, String surname) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }

        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be empty.");
        }

        this.firstName = firstName;
        this.surname = surname;
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

    @Override
    public String toString() {
        return getFullName();
    }
}