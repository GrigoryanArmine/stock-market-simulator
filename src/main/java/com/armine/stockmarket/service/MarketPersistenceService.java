package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarketPersistenceService {
    private String fileName;

    public MarketPersistenceService(String fileName) {
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("File name cannot be empty.");
        this.fileName = fileName;
    }

    public MarketPersistenceService(MarketPersistenceService other) {
        if (other == null) throw new IllegalArgumentException("MarketPersistenceService cannot be null.");
        this.fileName = other.fileName;
    }

    public void saveMarket(MarketService marketService) {
        if (marketService == null) throw new IllegalArgumentException("MarketService cannot be null.");

        Path path = Path.of(fileName).toAbsolutePath();
        Path temporaryFile = null;

        try {
            temporaryFile = Files.createTempFile(path.getParent(), "market-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Stock stock : marketService.getAllStocks()) {
                    writer.write(stock.getSymbol() + ","
                                    + stock.getCompanyName() + ","
                                    + stock.getCurrentPrice()
                    );
                    writer.newLine();
                }
            }
            // Replace the market file atomically
            Files.move(temporaryFile, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING
            );

        } catch (AtomicMoveNotSupportedException e) {
            throw new IllegalStateException("Atomic market saving is not supported.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save market: " + e.getMessage(), e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Do not hide the original saving error
                }
            }
        }
    }

    public void loadMarket(MarketService marketService) {
        if (marketService == null)
            throw new IllegalArgumentException("MarketService cannot be null.");
        if (!marketService.getAllStocks().isEmpty())
            throw new IllegalStateException("Market must be empty before loading.");

        Path path = Path.of(fileName);

        List<Stock> loadedStocks = new ArrayList<>();
        Set<String> symbols = new HashSet<>();

        if (!Files.exists(path)) {
            // Prepare default stocks in a separate market
            MarketService defaultMarket = new MarketService();
            defaultMarket.initializeMarket();
            // Save before changing the live market
            saveMarket(defaultMarket);
            loadedStocks.addAll(defaultMarket.getAllStocks());

        } else {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) continue;
                    try {
                        int firstComma = line.indexOf(',');
                        int lastComma = line.lastIndexOf(',');
                        if (firstComma <= 0 || lastComma <= firstComma)
                            throw new IllegalArgumentException("Invalid record format.");

                        String symbol = line.substring(0, firstComma).trim();
                        String companyName = line.substring(firstComma + 1, lastComma).trim();

                        double price = Double.parseDouble(line.substring(lastComma + 1).trim());
                        Stock stock = new Stock(symbol, companyName, price);

                        if (!symbols.add(stock.getSymbol()))
                            throw new IllegalArgumentException("Duplicate stock symbol.");
                        loadedStocks.add(stock);
                    } catch (RuntimeException e) {
                        throw new IllegalStateException("Invalid market record on line " + lineNumber, e);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("Could not load market: " + e.getMessage(), e
                );
            }
        }
        // Apply the validated stocks to the live market
        marketService.beginLoading();
        try {
            for (Stock stock : loadedStocks) {
                marketService.addStock(stock);
            }
        } finally {
            marketService.finishLoading();
        }
    }
}
