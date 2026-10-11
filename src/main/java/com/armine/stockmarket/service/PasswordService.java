package com.armine.stockmarket.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordService {
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private final SecureRandom secureRandom = new SecureRandom();


    public String hashPassword(char[] password) {

        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = generateHash(password, salt, ITERATIONS);

        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // Check whether an entered password is correct
    public boolean verifyPassword(char[] password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            if (iterations < 100_000 || iterations > 2_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            if (salt.length != SALT_LENGTH || expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            byte[] actualHash = generateHash(password, salt, iterations);

            return MessageDigest.isEqual(actualHash, expectedHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Generate a PBKDF2 hash
    private byte[] generateHash(char[] password, byte[] salt, int iterations) {

        KeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();

        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing failed.", e);
        } finally {
            ((PBEKeySpec) spec).clearPassword();
        }
    }
}
