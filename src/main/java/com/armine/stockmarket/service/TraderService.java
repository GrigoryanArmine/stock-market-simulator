package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TraderService {
    private List<Trader> traders;
    private PasswordService passwordService;
    private Random random;

    public TraderService() {
        this(new PasswordService());
    }

    public TraderService(PasswordService passwordService) {
        if (passwordService == null) throw new IllegalArgumentException("PasswordService cannot be null.");

        this.traders = new ArrayList<>();
        this.passwordService = passwordService;
        this.random = new Random();
    }

    public TraderService(TraderService other) {
        if (passwordService == null) throw new IllegalArgumentException("PasswordService cannot be null.");

        this.traders = new ArrayList<>(other.traders);
        this.passwordService = other.passwordService;
        this.random = new Random();
    }

    public Trader createTrader(String firstName, String surname, String username, char[] password) {
        if (firstName == null || firstName.isBlank() || surname == null || surname.isBlank())
            throw new IllegalArgumentException("Please fill in all fields.");

        validateUsername(username);

        firstName = firstName.trim();
        surname = surname.trim();
        username = username.trim();

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
        if (username == null || username.isBlank() || password == null || password.length == 0)
            throw new IllegalArgumentException("Please enter your username and password.");

        Trader trader = findTraderByUsername(username);
        if (trader == null || !passwordService.verifyPassword(password, trader.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect username or password.");
        return trader;
    }

    public void addTrader(Trader trader) {
        if (trader == null)
            throw new IllegalArgumentException("Trader cannot be null.");
        validateUsername(trader.getUsername());
        if (findTraderByUsername(trader.getUsername()) != null)
            throw new IllegalArgumentException("Username already exists.");
        if (findTraderById(trader.getId()) != null)
            throw new IllegalArgumentException("Trader ID already exists.");

        traders.add(trader);
    }

    public List<Trader> getAllTraders() {
        return new ArrayList<>(traders);
    }

    public Trader findTraderByUsername(String username) {
        if (username == null || username.isBlank()) return null;
        String normalizedUsername = username.trim();
        for (Trader trader : traders) {
            if (trader.getUsername().equalsIgnoreCase(normalizedUsername)) {
                return trader;
            }
        }
        return null;
    }

    public Trader findTraderById(int id) {
        for (Trader trader : traders) {
            if (trader.getId() == id) return trader;
        }
        return null;
    }

    private int generateUniqueId() {
        int id;
        do {
            id = 100000 + random.nextInt(900000);
        } while (findTraderById(id) != null);
        return id;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (!username.trim().matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Username can contain only letters, numbers, and underscores.");

    }
}
