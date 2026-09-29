package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TraderPersistenceService {
    private final String directoryName;

    public TraderPersistenceService(String directoryName) {
        this.directoryName = directoryName;
    }

    public void saveTrader(Trader trader) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, trader.getName() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(trader.getName() + "\n");
            writer.write(trader.getBalance() + "\n");
            for (Stock stock : trader.getPortfolio().getHoldings().keySet()) {
                int quantity = trader.getPortfolio().getQuantity(stock);
                writer.write(stock.getSymbol()
                                + ","
                                + stock.getCompanyName()
                                + ","
                                + stock.getCurrentPrice()
                                + ","
                                + quantity
                                + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Could not save trader: " + e.getMessage());
        }
    }

    public Trader loadTrader(String name, MarketService marketService) {
        if (name == null || name.isBlank()) {
            return null;
        }
        File file = new File(directoryName, name + ".txt");
        if (!file.exists()) {
            return null;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            String traderName = fileScanner.nextLine();
            double balance = Double.parseDouble(fileScanner.nextLine());
            Trader trader = new Trader(traderName, balance);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    continue;
                }
                String symbol = parts[0];
                String companyName = parts[1];
                double price = Double.parseDouble(parts[2]);
                int quantity = Integer.parseInt(parts[3]);
                Stock stock = marketService.findStock(symbol);
                if (stock == null) {
                    stock = new Stock(symbol, companyName, price);
                    marketService.addStock(stock);
                }
                trader.getPortfolio().addStock(stock, quantity);
            }
            return trader;
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load trader: " + e.getMessage());
            return null;
        }
    }
    public List<Trader> loadAllTraders(MarketService marketService) {
        List<Trader> traders = new ArrayList<>();
        File directory = new File(directoryName);
        if (!directory.exists() || !directory.isDirectory()) {
            return traders;
        }
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            return traders;
        }
        for (File file : files) {
            String fileName = file.getName();
            String traderName = fileName.substring(0, fileName.length() - 4);

            Trader trader = loadTrader(traderName, marketService);
            if (trader != null) {
                traders.add(trader);
            }
        }

        return traders;
    }
}