package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TraderPersistenceService {
    private String fileName;

    public TraderPersistenceService(String fileName) {
        this.fileName = fileName;
    }
    public void saveTrader(Trader trader) {
        try {
            FileWriter writer = new FileWriter(fileName);

            writer.write(trader.getName() + "\n");
            writer.write(trader.getBalance() + "\n");

            for (Stock stock : trader.getPortfolio().getHoldings().keySet()) {
                int quantity = trader.getPortfolio().getQuantity(stock);

                writer.write(
                        stock.getSymbol()
                                + ","
                                + stock.getCompanyName()
                                + ","
                                + stock.getCurrentPrice()
                                + ","
                                + quantity
                                + "\n"
                );
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save trader: " + e.getMessage()
            );
        }
    }

    public Trader loadTrader(MarketService marketService) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            String name = fileScanner.nextLine();
            double balance = Double.parseDouble(fileScanner.nextLine());

            Trader trader = new Trader(name, balance);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                String[] parts = line.split(",");

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
            fileScanner.close();
            return trader;

        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load trader: " + e.getMessage());
            return null;
        }
    }
}