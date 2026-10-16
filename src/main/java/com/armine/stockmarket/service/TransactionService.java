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

public class TransactionService {
    private String directoryName;

    public TransactionService(String directoryName) {
        if (directoryName == null || directoryName.isBlank())
            throw new IllegalArgumentException("Directory name cannot be empty.");
        this.directoryName = directoryName;
    }

    public TransactionService(TransactionService other) {
        if (other == null)
            throw new IllegalArgumentException("TransactionService cannot be null.");
        this.directoryName = other.directoryName;
    }

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
                } catch (IOException ignored) {
                    // Preserve the original exception
                }
            }
        }
    }

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
                        // Historical stock: do not add to live market
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
        // Apply only after all records are loaded successfully
        for (Transaction transaction : loadedTransactions) {
            trader.addTransaction(transaction);
        }
    }

    private void validateUsername(String username) {
        if (username == null || !username.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid username for file storage.");
        }
    }
}