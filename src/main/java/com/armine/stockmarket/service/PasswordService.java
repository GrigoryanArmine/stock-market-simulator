package com.armine.stockmarket.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * Provides password hashing and verification functionality.
 *
 * Passwords are hashed using PBKDF2WithHmacSHA256 with a
 * randomly generated salt.
 *
 * Each stored password hash contains the iteration count,
 * Base64-encoded salt, and Base64-encoded derived key.
 *
 * The service verifies passwords without storing or comparing
 * their plaintext representations.
 */
public class PasswordService {
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private SecureRandom secureRandom;
    /**
     * Creates a password service with a secure random
     * number generator for producing salts.
     */
    public PasswordService() {
        this.secureRandom = new SecureRandom();
    }
    /**
     * Creates a new password service using another instance
     * as the source for construction.
     *
     * The provided instance is validated, and a new
     * SecureRandom generator is initialized. The original
     * generator's internal state is not copied.
     *
     * @param other the password service to copy from
     * @throws IllegalArgumentException if other is null
     */
    public PasswordService(PasswordService other) {
        if (other == null) throw new IllegalArgumentException("PasswordService cannot be null.");
        this.secureRandom = new SecureRandom();
    }
    /**
     * Generates a salted password hash.
     *
     * Uses PBKDF2WithHmacSHA256 with 600,000 iterations,
     * a randomly generated 16-byte salt, and a 256-bit
     * derived key.
     *
     * The resulting value is formatted as:
     * iterationCount:Base64Salt:Base64Hash
     *
     * @param password the password to hash
     * @return the formatted password hash containing
     *         the iteration count, salt, and derived key
     * @throws IllegalArgumentException if password is
     *         null or empty
     * @throws IllegalStateException if password hashing fails
     */
    public String hashPassword(char[] password) {
        if (password == null || password.length == 0)
            throw new IllegalArgumentException("Password cannot be empty.");

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = generateHash(password, salt, ITERATIONS);

        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }
    /**
     * Verifies a password against a previously stored hash.
     *
     * Extracts the iteration count, salt, and expected hash
     * from the stored value. The password is hashed again
     * using the extracted parameters.
     *
     * The calculated hash is compared with the stored hash
     * using MessageDigest.isEqual().
     *
     * The method rejects malformed hashes, invalid iteration
     * counts, and incorrectly sized salts or derived keys.
     *
     * @param password the password to verify
     * @param storedHash the previously stored password hash
     * @return true if the password matches the stored hash,
     *         false if the password or stored hash is invalid
     *         or the hashes do not match
     * @throws IllegalStateException if password hashing
     *         cannot be performed
     */
    public boolean verifyPassword(char[] password, String storedHash) {
        if (password == null || password.length == 0 || storedHash == null || storedHash.isBlank()) return false;
        try {
            String[] parts = storedHash.split(":", -1);
            if (parts.length != 3) return false;
            int iterations = Integer.parseInt(parts[0]);
            if (iterations < 100_000 || iterations > 2_000_000) return false;

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            if (salt.length != SALT_LENGTH || expectedHash.length != KEY_LENGTH / 8) return false;

            byte[] actualHash = generateHash(password, salt, iterations);
            return MessageDigest.isEqual(actualHash, expectedHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    /**
     * Derives a password hash using PBKDF2WithHmacSHA256.
     *
     * Creates a PBEKeySpec from the provided parameters
     * and uses SecretKeyFactory to generate the derived key.
     *
     * The password stored inside PBEKeySpec is cleared
     * after the operation, including when hashing fails.
     *
     * @param password the password to hash
     * @param salt the salt used for key derivation
     * @param iterations the number of PBKDF2 iterations
     * @return the generated password hash as a byte array
     * @throws IllegalStateException if the cryptographic
     *         operation fails
     */
    private byte[] generateHash(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {throw new IllegalStateException("Password hashing failed.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
