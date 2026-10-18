package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Manages trader accounts in the stock market simulator.
 *
 * This service handles trader registration, authentication,
 * account lookup, and the collection of registered traders.
 *
 * New traders receive a unique six-digit ID, a starting
 * balance of $10,000, and a securely hashed password.
 *
 * Trader accounts are identified by unique IDs and usernames.
 * Username comparisons are case-insensitive.
 */
public class TraderService {
    private List<Trader> traders;
    private PasswordService passwordService;
    private Random random;
    /**
     * Creates a trader service with an empty trader list
     * and a new password service.
     */
    public TraderService() {
        this(new PasswordService());
    }
    /**
     * Creates a trader service using the provided password service.
     *
     * The service starts with no registered traders.
     *
     * @param passwordService the service used for password
     *        hashing and verification
     * @throws IllegalArgumentException if passwordService is null
     */
    public TraderService(PasswordService passwordService) {
        if (passwordService == null) throw new IllegalArgumentException("PasswordService cannot be null.");

        this.traders = new ArrayList<>();
        this.passwordService = passwordService;
        this.random = new Random();
    }
    /**
     * Attempts to create a trader service from another instance.
     *
     * The intended behavior is to copy the trader list,
     * share the password service, and create a new random
     * number generator.
     *
     * Note: The current implementation checks the uninitialized
     * passwordService field, so this constructor always throws
     * an IllegalArgumentException.
     *
     * @param other the trader service to copy
     * @throws IllegalArgumentException always in the
     *         current implementation
     */
    public TraderService(TraderService other) {
        if (passwordService == null) throw new IllegalArgumentException("PasswordService cannot be null.");

        this.traders = new ArrayList<>(other.traders);
        this.passwordService = other.passwordService;
        this.random = new Random();
    }
    /**
     * Registers a new trader account.
     *
     * Validates the provided information, checks username
     * availability, hashes the password, and generates a
     * unique six-digit trader ID.
     *
     * The new trader receives an initial simulated balance
     * of $10,000 and is added to the registered trader list.
     *
     * @param firstName the trader's first name
     * @param surname the trader's surname
     * @param username the desired account username
     * @param password the account password
     * @return the newly registered trader
     * @throws IllegalArgumentException if any required field
     *         is invalid, the username already exists, or
     *         the password contains fewer than eight characters
     * @throws IllegalStateException if password hashing fails
     */
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
    /**
     * Authenticates a registered trader.
     *
     * Finds the trader by username and verifies the provided
     * password against the stored password hash.
     *
     * Username comparison is case-insensitive and ignores
     * leading and trailing whitespace.
     *
     * @param username the trader's username
     * @param password the password entered during login
     * @return the authenticated trader
     * @throws IllegalArgumentException if credentials are
     *         missing or authentication fails
     * @throws IllegalStateException if password verification
     *         cannot be performed
     */
    public Trader login(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0)
            throw new IllegalArgumentException("Please enter your username and password.");

        Trader trader = findTraderByUsername(username);
        if (trader == null || !passwordService.verifyPassword(password, trader.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect username or password.");
        return trader;
    }
    /**
     * Adds an existing trader to the registered trader list.
     *
     * Validates the trader's username and ensures that both
     * the username and trader ID are unique.
     *
     * The trader object is added by reference, not copied.
     *
     * @param trader the trader account to register
     * @throws IllegalArgumentException if trader is null,
     *         the username is invalid or already used,
     *         or the trader ID already exists
     */
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
    /**
     * Returns a list of all registered traders.
     *
     * The returned list is a new collection, so modifying
     * its contents does not modify the internal trader list.
     *
     * However, the Trader objects themselves are shared.
     *
     * @return a list containing all registered traders
     */
    public List<Trader> getAllTraders() {
        return new ArrayList<>(traders);
    }
    /**
     * Searches for a trader using their username.
     *
     * Username matching is case-insensitive and ignores
     * leading and trailing whitespace.
     *
     * @param username the username to search for
     * @return the matching trader, or null if no trader
     *         exists or the username is null or blank
     */
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
    /**
     * Searches for a registered trader using their unique ID.
     *
     * @param id the trader ID to search for
     * @return the matching trader, or null if no trader
     *         has the specified ID
     */
    public Trader findTraderById(int id) {
        for (Trader trader : traders) {
            if (trader.getId() == id) return trader;
        }
        return null;
    }
    /**
     * Generates a unique six-digit trader ID.
     *
     * Random IDs are generated between 100000 and 999999.
     * Generation continues until an unused ID is found.
     *
     * @return an unused six-digit trader ID
     */
    private int generateUniqueId() {
        int id;
        do {
            id = 100000 + random.nextInt(900000);
        } while (findTraderById(id) != null);
        return id;
    }
    /**
     * Validates a trader's username.
     *
     * A username must not be null or blank and may contain
     * only English letters, digits, and underscores.
     *
     * Leading and trailing whitespace is ignored
     * during validation.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if the username
     *         is null, blank, or contains invalid characters
     */
    private void validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (!username.trim().matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Username can contain only letters, numbers, and underscores.");
    }
}
