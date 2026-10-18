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
/**
 * Handles file-based persistence of stock market data.
 *
 * Market information is stored in a UTF-8 text file.
 * Each non-empty record contains a stock symbol, company name,
 * and current market price, separated by commas.
 *
 * Saving uses a temporary file followed by atomic replacement
 * of the destination file to reduce the risk of data loss.
 *
 * When no market file exists, the service initializes and saves
 * a default market containing five example stocks.
 */
public class MarketPersistenceService {
    private String fileName;
    /**
     * Creates a market persistence service using the specified file.
     *
     * @param fileName the path of the market data file
     * @throws IllegalArgumentException if fileName is null
     *         or blank
     */
    public MarketPersistenceService(String fileName) {
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("File name cannot be empty.");
        this.fileName = fileName;
    }
    /**
     * Creates a copy of an existing market persistence service.
     *
     * The new service uses the same market file path
     * as the original service.
     *
     * @param other the persistence service to copy
     * @throws IllegalArgumentException if other is null
     */
    public MarketPersistenceService(MarketPersistenceService other) {
        if (other == null) throw new IllegalArgumentException("MarketPersistenceService cannot be null.");
        this.fileName = other.fileName;
    }
    /**
     * Saves all stocks from the specified market to a text file.
     *
     * Each stock is written as a comma-separated record containing
     * its symbol, company name, and current price.
     *
     * The method first writes the market data to a temporary file
     * in the destination directory. After writing completes,
     * the temporary file atomically replaces the destination file.
     *
     * If saving fails, the method attempts to remove the temporary
     * file without hiding the original exception.
     *
     * @param marketService the market containing stocks to save
     * @throws IllegalArgumentException if marketService is null
     * @throws IllegalStateException if saving fails or atomic
     *         file replacement is not supported
     */
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
            Files.move(temporaryFile, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            throw new IllegalStateException("Atomic market saving is not supported.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save market: " + e.getMessage(), e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {}
            }
        }
    }
    /**
     * Loads market data from the configured text file.
     *
     * The destination market must be empty before loading.
     *
     * If the market file does not exist, a separate default
     * market is created, saved, and then loaded into the
     * destination market.
     *
     * If the file exists, the method reads each non-blank line
     * and validates the stock symbol, company name, price,
     * and uniqueness of stock symbols.
     *
     * Commas within company names are supported by using
     * the first comma to separate the symbol and the last
     * comma to separate the price.
     *
     * Records are parsed and validated before the destination
     * market is modified. Loading mode is enabled while
     * applying the stocks to prevent automatic saving.
     *
     * @param marketService the empty market to populate
     * @throws IllegalArgumentException if marketService is null
     * @throws IllegalStateException if the market is not empty,
     *         a record is invalid, duplicate symbols are found,
     *         or a file operation fails
     */
    public void loadMarket(MarketService marketService) {
        if (marketService == null)
            throw new IllegalArgumentException("MarketService cannot be null.");
        if (!marketService.getAllStocks().isEmpty())
            throw new IllegalStateException("Market must be empty before loading.");

        Path path = Path.of(fileName);

        List<Stock> loadedStocks = new ArrayList<>();
        Set<String> symbols = new HashSet<>();

        if (!Files.exists(path)) {
            MarketService defaultMarket = new MarketService();
            defaultMarket.initializeMarket();
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
