package com.armine.stockmarket.ui;

import com.armine.stockmarket.exception.*;
import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Trader trader;
    private MarketService marketService;
    private TradingService tradingService;
    private Scanner scanner;
    private TraderPersistenceService traderPersistenceService;
    private TransactionService transactionService;

    public ConsoleUI(Trader trader, MarketService marketService, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService) {
        if (trader == null || marketService == null || tradingService == null || traderPersistenceService == null || transactionService == null)
            throw new IllegalArgumentException("Console UI dependencies cannot be null.");

        this.trader = trader;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInteger();
            switch (choice) {
                case 1:
                    showStocks();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    sellStock();
                    break;
                case 4:
                    showPortfolio();
                    break;
                case 5:
                    showTransactions();
                    break;
                case 6:
                    addMoney();
                    break;
                case 7:
                    if (saveChanges()) {
                        running = false;
                        System.out.println("Your data has been saved.");
                        System.out.println("Logged out successfully.");
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void showMenu() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("   STOCK MARKET SIMULATOR");
        System.out.println("==============================");
        System.out.println("Trader: " + trader.getFullName());
        System.out.println("Balance: $" + trader.getBalance());
        System.out.println();
        System.out.println("1. View available stocks");
        System.out.println("2. Buy stock");
        System.out.println("3. Sell stock");
        System.out.println("4. View portfolio");
        System.out.println("5. View transactions");
        System.out.println("6. Add money");
        System.out.println("7. Logout");
        System.out.println();
        System.out.print("Choose an option: ");
    }

    private void showStocks() {
        System.out.println();
        System.out.println("Available stocks:");
        List<Stock> stocks = marketService.getAllStocks();
        if (stocks.isEmpty()) {
            System.out.println("No stocks available.");
            return;
        }
        stocks.sort(Comparator.comparing(Stock::getSymbol));
        for (Stock stock : stocks) {
            System.out.println(stock);
        }
    }

    private void buyStock() {
        System.out.println();
        if (marketService.getAllStocks().isEmpty()) {
            System.out.println("No stocks available.");
            return;
        }
        showStocks();

        System.out.println();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim();
        try {
            Stock stock = marketService.requireStock(symbol);
            System.out.print("Enter quantity: ");
            int quantity = readInteger();
            tradingService.buyStock(trader, stock, quantity);
            System.out.println("Purchase successful.");
            saveChanges();
        } catch (IllegalArgumentException | InsufficientFundsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void sellStock() {
        System.out.println();
        System.out.println("Your Portfolio:");
        Portfolio portfolio = trader.getPortfolio();
        if (portfolio.isEmpty()) {
            System.out.println("Your portfolio is empty.");
            return;
        }
        List<Stock> stocks = new ArrayList<>(portfolio.getHoldings().keySet());
        stocks.sort(Comparator.comparing(Stock::getSymbol));
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            int quantity = portfolio.getQuantity(stock);
            System.out.println((i + 1) + ". " + stock.getSymbol() + " - " + quantity + " shares");
        }
        System.out.println();
        System.out.print("Choose a stock to sell: ");
        int choice = readInteger();
        if (choice < 1 || choice > stocks.size()) {
            System.out.println("Invalid stock selection.");
            return;
        }
        Stock stock = stocks.get(choice - 1);
        int ownedShares = trader.getOwnedShares(stock);
        System.out.println();
        System.out.println("You selected: " + stock.getSymbol());
        System.out.println("You own " + ownedShares + " shares.");
        System.out.print("Enter quantity to sell: ");
        int quantity = readInteger();
        try {
            tradingService.sellStock(trader, stock, quantity);
            System.out.println("Sale successful.");
            saveChanges();
        } catch (IllegalArgumentException | InsufficientSharesException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showPortfolio() {
        System.out.println();
        System.out.println(trader.getPortfolio());
    }

    private void showTransactions() {
        System.out.println();
        System.out.println("Transactions:");
        List<Transaction> transactions = trader.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private void addMoney() {
        System.out.println();
        System.out.print("Enter amount: ");
        double amount = readDouble();
        try {
            trader.addMoney(amount);
            System.out.println("Money added successfully.");
            saveChanges();
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean saveChanges() {
        try {
            traderPersistenceService.saveTrader(trader);
            transactionService.saveTransactions(trader);
            return true;

        } catch (IllegalStateException ex) {
            System.out.println("Changes are in memory, but saving failed: " + ex.getMessage());
            return false;
        }
    }

    private int readInteger() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter an integer.");
            scanner.nextLine();
            System.out.print("Try again: ");
        }

        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }

    private double readDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            System.out.print("Try again: ");
        }
        double number = scanner.nextDouble();
        scanner.nextLine();
        return number;
    }
}