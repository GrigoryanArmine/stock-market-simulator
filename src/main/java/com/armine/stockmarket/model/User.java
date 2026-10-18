package com.armine.stockmarket.model;
/**
 * Represents a general user of the stock market simulator.
 *
 * This abstract class stores information shared by different
 * types of users, including traders and administrators.
 *
 * Each user has a first name, surname, username, and
 * stored password hash.
 *
 * User serves as the superclass for Trader and Admin,
 * allowing common user information and behavior to
 * be inherited by both classes.
 */
public abstract class User {
    private String firstName;
    private String surname;
    private String username;
    private String passwordHash;
    /**
     * Creates a user with the specified personal and
     * authentication information.
     *
     * Leading and trailing whitespace is removed from
     * the first name, surname, and username.
     *
     * @param firstName the user's first name
     * @param surname the user's surname
     * @param username the user's account username
     * @param passwordHash the user's stored password hash
     * @throws IllegalArgumentException if any argument
     *         is null, empty, or contains only whitespace
     */
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
    /**
     * Creates a copy of another user.
     *
     * Copies the personal information, username,
     * and stored password hash from the given user.
     *
     * @param other the user to copy
     * @throws IllegalArgumentException if other is null
     */
    protected User(User other) {
        if (other == null) throw new IllegalArgumentException("User cannot be null.");

        this.firstName = other.firstName;
        this.surname = other.surname;
        this.username = other.username;
        this.passwordHash = other.passwordHash;
    }
    /**
     * Returns the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Returns the user's surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }
    /**
     * Returns the user's full name.
     *
     * The full name consists of the first name
     * followed by a space and the surname.
     *
     * @return the user's full name
     */
    public String getFullName() {
        return firstName + " " + surname;
    }
    /**
     * Returns the user's account username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Returns the user's stored password hash.
     *
     * The returned value is used by the authentication
     * service to verify user credentials.
     *
     * @return the stored password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    /**
     * Returns a readable representation of the user.
     *
     * @return the user's full name
     */
    @Override
    public String toString() {
        return getFullName();
    }
}
