package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;

import java.io.*;
import java.util.List;

public class MarketPersistenceService {
    private String fileName;

    public MarketPersistenceService(String fileName) {
        this.fileName = fileName;
    }

    public void saveMarket(MarketService marketService) {
        List<Stock> stocks = marketService.getAllStocks();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Stock stock : stocks) {
                writer.write(stock.getSymbol() + "," + stock.getCompanyName() + "," + stock.getCurrentPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not save market: " + e.getMessage(), e);
        }
    }

    public void loadMarket(MarketService marketService) {
        File file = new File(fileName);
        marketService.beginLoading();

        try {
            if (!file.exists()) {
                // First launch: initialize default stocks
                marketService.initializeMarket();
                saveMarket(marketService);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] parts = line.split(",", -1);
                    if (parts.length != 3) {
                        throw new IllegalStateException("Invalid market record: " + line);
                    }

                    String symbol = parts[0].trim();
                    String companyName = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());

                    Stock stock = new Stock(symbol, companyName, price);

                    marketService.addStock(stock);
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Could not load market: " + e.getMessage(), e);
        } finally {
            marketService.finishLoading();
        }
    }
}
