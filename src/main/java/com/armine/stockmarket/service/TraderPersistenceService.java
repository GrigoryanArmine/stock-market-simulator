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

public class TraderPersistenceService {
    private String directoryName;

    public TraderPersistenceService(String directoryName) {
        if (directoryName == null || directoryName.isBlank())
            throw new IllegalArgumentException("Directory name cannot be empty.");
        this.directoryName = directoryName;
    }

    public TraderPersistenceService(TraderPersistenceService other) {
        if (other == null)
            throw new IllegalArgumentException("TraderPersistenceService cannot be null.");
        this.directoryName = other.directoryName;
    }

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
            // Atomically replace the trader file
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            throw new IllegalStateException("Atomic trader saving is not supported.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save trader " + trader.getUsername() + ": " + e.getMessage(), e);
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

    private void validateUsername(String username) {
        if (username == null || !username.matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Invalid username for file storage.");
    }
}
