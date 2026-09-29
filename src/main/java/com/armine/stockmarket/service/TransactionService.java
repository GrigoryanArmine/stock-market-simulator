package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Transaction;
import com.armine.stockmarket.model.TransactionType;
import com.armine.stockmarket.model.Trader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TransactionService {
    private String directoryName;

    public TransactionService(String directoryName) {
        this.directoryName = directoryName;
    }

    public void saveTransactions(Trader trader) {

        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        File directory = new File(directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, trader.getName() + ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            for (Transaction transaction : trader.getTransactions()) {
                writer.write(transaction.getType()
                                + ","
                                + transaction.getStock().getSymbol()
                                + ","
                                + transaction.getStock().getCompanyName()
                                + ","
                                + transaction.getQuantity()
                                + ","
                                + transaction.getPricePerShare()
                                + ","
                                + transaction.getDateTime()
                                + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Could not save transactions: " + e.getMessage());
        }
    }

    public void loadTransactions(Trader trader, MarketService marketService) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        File file = new File(directoryName, trader.getName() + ".txt");
        if (!file.exists()) {
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }

                TransactionType type = TransactionType.valueOf(parts[0]);
                String symbol = parts[1];
                String companyName = parts[2];
                int quantity = Integer.parseInt(parts[3]);
                double price = Double.parseDouble(parts[4]);
                LocalDateTime dateTime = LocalDateTime.parse(parts[5]);

                Stock stock = marketService.findStock(symbol);

                if (stock == null) {
                    stock = new Stock(symbol, companyName, price);
                    marketService.addStock(stock);
                }

                Transaction transaction = new Transaction(stock, quantity, price, type, dateTime);
                trader.addTransaction(transaction);
            }

        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load transactions: " + e.getMessage()
            );
        }
    }
}