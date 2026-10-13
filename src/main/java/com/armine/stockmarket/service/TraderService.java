package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TraderService {
    private List<Trader> traders;
    private PasswordService passwordService;

    public TraderService() {
        this.traders = new ArrayList<>();
        this.passwordService = new PasswordService();
    }

    public Trader createTrader(String firstName, String surname, String username, char[] password) {
        if (firstName == null || firstName.isBlank() || surname == null || surname.isBlank() || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Please fill in all fields.");
        }

        firstName = firstName.trim();
        surname = surname.trim();
        username = username.trim();

        if (!username.matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Username can contain only letters, numbers, and underscores.");
        if (findTraderByUsername(username) != null)
            throw new IllegalArgumentException("Username already exists.");
        if (password == null || password.length == 0)
            throw new IllegalArgumentException("Please enter a password.");
        if (password.length < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters.");

        String passwordHash = passwordService.hashPassword(password);
        Trader trader = new Trader(generateUniqueId(), username, firstName, surname, 10000, passwordHash);
        addTrader(trader);
        return trader;
    }

    public Trader login(String username, char[] password) {
        if (username == null || username.isBlank()
                || password == null || password.length == 0) {
            throw new IllegalArgumentException(
                    "Please enter your username and password.");
        }

        Trader trader = findTraderByUsername(username.trim());

        if (trader == null || !passwordService.verifyPassword(
                password, trader.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Incorrect username or password.");
        }

        return trader;
    }
    public void addTrader(Trader trader) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        if (findTraderByUsername(trader.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (findTraderById(trader.getId()) != null) {
            throw new IllegalArgumentException("Trader ID already exists.");
        }
        traders.add(trader);
    }

    public List<Trader> getAllTraders() {
        return new ArrayList<>(traders);
    }

    public int generateUniqueId() {
        Random random = new Random();
        int id;
        do {
            id = 100000 + random.nextInt(900000);
        } while (findTraderById(id) != null);
        return id;
    }

    public Trader findTraderByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        for (Trader trader : traders) {
            if (trader.getUsername().equalsIgnoreCase(username)) {
                return trader;
            }
        }
        return null;
    }

    public Trader findTraderById(int id) {
        for (Trader trader : traders) {
            if (trader.getId() == id) {
                return trader;
            }
        }
        return null;
    }
}
