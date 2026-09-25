package com.armine.stockmarket.model;

public class User {
    private String name;

    public User(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return "User: " + name;
    }
}