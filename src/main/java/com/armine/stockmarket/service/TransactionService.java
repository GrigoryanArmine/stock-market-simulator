package com.armine.stockmarket.service;

import com.armine.stockmarket.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Handles file-based persistence of trader transaction histories.
 *
 * Each trader's transactions are stored in a separate UTF-8
 * text file inside the configured directory.
 *
 * Each transaction record contains the transaction type,
 * stock symbol, company name, quantity, execution price,
 * and transaction date and time.
 *
 * The service preserves historical transaction information,
 * including transactions involving stocks that are no longer
 * available in the current market.
 *
 * Saving uses temporary files and atomic replacement to reduce
 * the risk of losing existing transaction data.
 */
public class TransactionService {
    private String directoryName;
    /**
     * Creates a transaction persistence service using
     * the specified storage directory.
     *
     * @param directoryName the directory used to store
     *        trader transaction files
     * @throws IllegalArgumentException if directoryName
     *         is null or blank
     */
    public TransactionService(String directoryName) {
        if (directoryName == null || directoryName.isBlank())
            throw new IllegalArgumentException("Directory name cannot be empty.");
        this.directoryName = directoryName;
    }
    /**
     * Creates a copy of an existing transaction service.
     *
     * The new service uses the same storage directory
     * as the original service.
     *
     * @param other the transaction service to copy
     * @throws IllegalArgumentException if other is null
     */
    public TransactionService(TransactionService other) {
        if (other == null)
            throw new IllegalArgumentException("TransactionService cannot be null.");
        this.directoryName = other.directoryName;
    }
    /**
     * Saves a trader's transaction history to a text file.
     *
     * Each transaction is written as a comma-separated record
     * containing its type, stock symbol, company name,
     * quantity, execution price, and timestamp.
     *
     * The storage directory is created if necessary.
     * Data is first written to a temporary file in the
     * destination directory.
     *
     * After writing completes, the temporary file atomically
     * replaces the trader's existing transaction file.
     *
     * If saving fails, the method attempts to remove the
     * temporary file without hiding the original exception.
     *
     * @param trader the trader whose transactions will be saved
     * @throws IllegalArgumentException if trader is null
     *         or the username is invalid for file storage
     * @throws IllegalStateException if the storage path is invalid,
     *         saving fails, or atomic replacement is unsupported
     */
    public void saveTransactions(Trader trader) {
        if (trader == null)
            throw new IllegalArgumentException("Trader cannot be null.");

        validateUsername(trader.getUsername());

        Path directory = Path.of(directoryName).toAbsolutePath();
        Path temporaryFile = null;

        if (Files.exists(directory) && !Files.isDirectory(directory)) throw new IllegalStateException("Transaction storage path is not a directory.");

        try {
            Files.createDirectories(directory);
            Path filePath = directory.resolve(trader.getUsername() + ".txt");
            temporaryFile = Files.createTempFile(directory, "transactions-", ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Transaction transaction : trader.getTransactions()) {
                    writer.write(transaction.getType() + ","
                            + transaction.getStock().getSymbol() + ","
                            + transaction.getStock().getCompanyName() + ","
                            + transaction.getQuantity() + ","
                            + transaction.getPricePerShare() + ","
                            + transaction.getDateTime()
                    );
                    writer.newLine();
                }
            }
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            throw new IllegalStateException("Atomic transaction saving is not supported.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save transactions for " + trader.getUsername() + ": " + e.getMessage(), e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {}
            }
        }
    }
    /**
     * Loads a trader's transaction history from its saved file.
     *
     * If no transaction file exists, the method returns without
     * modifying the trader's existing transaction history.
     *
     * Each saved record is parsed and validated before any
     * transactions are added to the trader.
     *
     * Stock symbols are resolved using the provided market.
     * If a stock is no longer available, a separate Stock
     * object is created from the saved historical information.
     * The historical stock is not added to the live market.
     *
     * Company names containing commas are supported.
     *
     * After all records are loaded successfully, the transactions
     * are appended to the trader's existing transaction list.
     * Calling this method repeatedly can therefore add duplicates.
     *
     * @param trader the trader whose transaction history
     *        will be restored
     * @param marketService the market used to resolve
     *        transaction stock symbols
     * @throws IllegalArgumentException if trader or marketService
     *         is null, or the trader's username is invalid
     * @throws IllegalStateException if the transaction path
     *         is invalid, a record cannot be parsed,
     *         or reading the file fails
     */
    public void loadTransactions(Trader trader, MarketService marketService) {
        if (trader == null)
            throw new IllegalArgumentException("Trader cannot be null.");
        if (marketService == null)
            throw new IllegalArgumentException("MarketService cannot be null.");

        validateUsername(trader.getUsername());

        Path filePath = Path.of(directoryName, trader.getUsername() + ".txt");

        if (!Files.exists(filePath)) return;
        if (!Files.isRegularFile(filePath))
            throw new IllegalStateException("Transaction path is not a file.");

        List<Transaction> loadedTransactions = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length < 6)
                        throw new IllegalArgumentException("Expected at least 6 fields.");

                    TransactionType type = TransactionType.valueOf(parts[0].trim());
                    String symbol = parts[1].trim();
                    String companyName = String.join(",", Arrays.copyOfRange(parts, 2, parts.length - 3)).trim();

                    int quantity = Integer.parseInt(parts[parts.length - 3].trim());
                    double price = Double.parseDouble(parts[parts.length - 2].trim());
                    LocalDateTime dateTime = LocalDateTime.parse(parts[parts.length - 1].trim());
                    Stock stock = marketService.findStock(symbol);
                    if (stock == null) {
                        stock = new Stock(symbol, companyName, price);
                    }
                    Transaction transaction = new Transaction(stock, quantity, price, type, dateTime);
                    loadedTransactions.add(transaction);
                } catch (RuntimeException e) {
                    throw new IllegalStateException("Invalid transaction on line " + lineNumber + " for " + trader.getUsername() + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load transactions for " + trader.getUsername() + ": " + e.getMessage(),e);
        }
        for (Transaction transaction : loadedTransactions) {
            trader.addTransaction(transaction);
        }
    }
    /**
     * Validates a trader's username before using it
     * as part of a transaction file name.
     *
     * Only English letters, digits, and underscores
     * are permitted.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if username is null
     *         or contains characters outside the allowed pattern
     */
    private void validateUsername(String username) {
        if (username == null || !username.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid username for file storage.");
        }
    }
}