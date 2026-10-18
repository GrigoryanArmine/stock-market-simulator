package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
/**
 * Handles file-based persistence of trader accounts.
 *
 * Each trader is stored in a separate UTF-8 text file
 * inside the configured directory. The file contains
 * the trader's account information, balance, password hash,
 * and portfolio holdings.
 *
 * Saving uses a temporary file and atomic replacement
 * to reduce the risk of losing existing trader data.
 *
 * When loading a portfolio, stock holdings are matched
 * to stocks in the current market using their symbols.
 *
 * Transaction history is managed separately by
 * TransactionService.
 */
public class TraderPersistenceService {
    private String directoryName;
    /**
     * Creates a persistence service using the specified directory.
     *
     * @param directoryName the directory used to store trader files
     * @throws IllegalArgumentException if directoryName
     *         is null or blank
     */
    public TraderPersistenceService(String directoryName) {
        if (directoryName == null || directoryName.isBlank())
            throw new IllegalArgumentException("Directory name cannot be empty.");
        this.directoryName = directoryName;
    }
    /**
     * Creates a copy of an existing trader persistence service.
     *
     * The new service uses the same storage directory
     * as the original service.
     *
     * @param other the persistence service to copy
     * @throws IllegalArgumentException if other is null
     */
    public TraderPersistenceService(TraderPersistenceService other) {
        if (other == null)
            throw new IllegalArgumentException("TraderPersistenceService cannot be null.");
        this.directoryName = other.directoryName;
    }
    /**
     * Saves a trader's account information and portfolio holdings.
     *
     * Creates the storage directory if it does not already exist.
     * The trader's data is written to a UTF-8 text file named
     * after the username.
     *
     * The file stores the trader ID, username, first name,
     * surname, cash balance, and password hash, followed
     * by the portfolio holdings.
     *
     * Each portfolio record contains the stock symbol,
     * company name, current market price, and share quantity.
     *
     * Data is first written to a temporary file in the same
     * directory. The temporary file then atomically replaces
     * the destination file.
     *
     * If saving fails, the method attempts to delete the
     * temporary file without hiding the original exception.
     *
     * @param trader the trader whose account data will be saved
     * @throws IllegalArgumentException if trader is null
     *         or its username is invalid for file storage
     * @throws IllegalStateException if the storage directory
     *         cannot be created, the storage path is invalid,
     *         saving fails, or atomic replacement is unsupported
     */
    public void saveTrader(Trader trader) {
        if (trader == null)
            throw new IllegalArgumentException("Trader cannot be null.");
        validateUsername(trader.getUsername());

        File directory = new File(directoryName);

        if (!directory.exists() && !directory.mkdirs())
            throw new IllegalStateException("Could not create trader directory.");
        if (!directory.isDirectory())
            throw new IllegalStateException("Trader storage path is not a directory.");

        Path directoryPath = directory.toPath();
        Path filePath = directoryPath.resolve(trader.getUsername() + ".txt");
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(directoryPath, trader.getUsername() + "-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                writer.write(trader.getId() + "\n");
                writer.write(trader.getUsername() + "\n");
                writer.write(trader.getFirstName() + "\n");
                writer.write(trader.getSurname() + "\n");
                writer.write(trader.getBalance() + "\n");
                writer.write(trader.getPasswordHash() + "\n");

                Map<Stock, Integer> holdings = trader.getPortfolio().getHoldings();
                for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
                    Stock stock = entry.getKey();
                    int quantity = entry.getValue();
                    writer.write(stock.getSymbol() + ","
                            + stock.getCompanyName() + ","
                            + stock.getCurrentPrice() + ","
                            + quantity + "\n"
                    );
                }
            }
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            throw new IllegalStateException("Atomic trader saving is not supported.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save trader " + trader.getUsername() + ": " + e.getMessage(), e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {}
            }
        }
    }
    /**
     * Loads a trader account from its saved text file.
     *
     * Returns null if the username is null or blank,
     * or if no matching trader file exists.
     *
     * Reads the trader's ID, username, personal information,
     * account balance, and stored password hash.
     *
     * Portfolio records are restored using Stock objects
     * from the provided MarketService. The saved stock
     * symbols identify the holdings, while share quantities
     * determine the number of shares owned.
     *
     * The method rejects duplicate stock holdings,
     * missing market stocks, and invalid portfolio records.
     *
     * Transaction history is not loaded by this method.
     *
     * @param username the username identifying the saved trader
     * @param marketService the market used to resolve stock holdings
     * @return the restored trader, or null if the username
     *         is blank or the trader file does not exist
     * @throws IllegalArgumentException if the username is
     *         invalid for file storage or marketService is null
     * @throws IllegalStateException if the file is invalid,
     *         account data cannot be parsed, a required stock
     *         is missing, or reading fails
     */
    public Trader loadTrader(String username, MarketService marketService) {
        if (username == null || username.isBlank())
            return null;
        validateUsername(username);
        if (marketService == null)
            throw new IllegalArgumentException("MarketService cannot be null.");

        File file = new File(directoryName, username + ".txt");

        if (!file.exists())
            return null;
        if (!file.isFile())
            throw new IllegalStateException("Trader path is not a file.");

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            int id = Integer.parseInt(scanner.nextLine());
            String savedUsername = scanner.nextLine();
            String firstName = scanner.nextLine();
            String surname = scanner.nextLine();
            double balance = Double.parseDouble(scanner.nextLine());
            String passwordHash = scanner.nextLine();

            if (!savedUsername.equals(username))
                throw new IllegalStateException("Username does not match file name.");

            Trader trader = new Trader(id, savedUsername, firstName, surname, balance, passwordHash);

            int lineNumber = 6;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length != 4)
                        throw new IllegalArgumentException("Expected 4 fields.");
                    String symbol = parts[0].trim();
                    int quantity = Integer.parseInt(parts[3].trim());
                    Stock stock = marketService.findStock(symbol);
                    if (stock == null)
                        throw new IllegalStateException("Stock " + symbol + " is missing from the market.");
                    if (trader.getOwnedShares(stock) > 0)
                        throw new IllegalStateException("Duplicate stock: " + symbol);
                    trader.addShares(stock, quantity);
                } catch (RuntimeException e) {
                    throw new IllegalStateException("Invalid portfolio record on line " + lineNumber, e);
                }
            }
            if (scanner.ioException() != null)
                throw new IllegalStateException("Could not read trader file.", scanner.ioException());
            return trader;
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Could not load trader file " + file.getName() + ": " + e.getMessage(), e);
        }
    }
    /**
     * Loads all saved trader accounts from the storage directory.
     *
     * Searches the configured directory for files ending
     * with ".txt" and attempts to restore each trader.
     *
     * If the directory does not exist, an empty list is returned.
     * The ordering of loaded traders is not guaranteed.
     *
     * Each trader's portfolio is reconstructed using
     * stocks from the provided market.
     *
     * @param marketService the market used to restore stock holdings
     * @return a list containing all successfully loaded traders,
     *         or an empty list if no trader directory exists
     * @throws IllegalArgumentException if marketService is null
     * @throws IllegalStateException if the storage path is invalid,
     *         the directory cannot be read, or a trader file
     *         cannot be loaded
     */
    public List<Trader> loadAllTraders(MarketService marketService) {
        if (marketService == null)
            throw new IllegalArgumentException("MarketService cannot be null.");

        List<Trader> traders = new ArrayList<>();
        File directory = new File(directoryName);

        if (!directory.exists()) return traders;

        if (!directory.isDirectory())
            throw new IllegalStateException("Trader storage path is not a directory.");

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null)
            throw new IllegalStateException("Could not read trader directory.");

        for (File file : files) {
            String fileName = file.getName();
            String username = fileName.substring(0, fileName.length() - 4);
            Trader trader = loadTrader(username, marketService);
            if (trader != null) traders.add(trader);
        }
        return traders;
    }
    /**
     * Validates a username before using it in a file name.
     *
     * Only English letters, digits, and underscores
     * are permitted.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if username is null
     *         or contains characters outside the allowed pattern
     */
    private void validateUsername(String username) {
        if (username == null || !username.matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Invalid username for file storage.");
    }
}
