package com.armine.stockmarket.ui;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;

import java.util.ArrayList;
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
        this.trader = trader;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.scanner = new Scanner(System.in);
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;
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
                    traderPersistenceService.saveTrader(trader);
                    transactionService.saveTransactions(trader);
                    running = false;
                    System.out.println("Your data has been saved.");
                    System.out.println("Logged out successfully.");
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
        for (Stock stock : stocks) {
            System.out.println(stock);
        }
    }

    private void buyStock() {
        System.out.println();
        System.out.println("Available stocks:");

        List<Stock> stocks = marketService.getAllStocks();

        for (Stock stock : stocks) {
            System.out.println(stock);
        }

        System.out.println();

        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine();

        Stock stock = marketService.findStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter quantity: ");
        int quantity = readInteger();
        try {
            tradingService.buyStock(trader, stock, quantity);
            System.out.println("Purchase successful.");

        } catch (IllegalArgumentException | InsufficientFundsException e) {
            System.out.println("Purchase failed: " + e.getMessage());
        }
    }

    private void sellStock() {
        System.out.println();
        System.out.println("Your Portfolio:");

        if (trader.getPortfolio().isEmpty()) {
            System.out.println("Your portfolio is empty.");
            return;
        }

        List<Stock> stocks = new ArrayList<>();
        for (Stock stock : trader.getPortfolio().getHoldings().keySet()) {
            stocks.add(stock);
        }
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            int quantity = trader.getPortfolio().getQuantity(stock);

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

        int ownedShares = trader.getPortfolio().getQuantity(stock);

        System.out.println();
        System.out.println("You selected: " + stock.getSymbol());

        System.out.println("You own " + ownedShares + " shares.");

        System.out.print("Enter quantity to sell: ");
        int quantity = readInteger();

        try {

            tradingService.sellStock(trader, stock, quantity);
            System.out.println("Sale successful.");

        } catch (IllegalArgumentException | InsufficientSharesException e) {
            System.out.println("Sale failed: " + e.getMessage());
        }
    }

    private void showPortfolio() {
        System.out.println();
        System.out.println(trader.getPortfolio());
    }

    private void showTransactions() {
        System.out.println();
        System.out.println("Transactions:");

        if (trader.getTransactions().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (int i = 0; i < trader.getTransactions().size(); i++) {
            System.out.println(trader.getTransactions().get(i));
        }
    }

    private void addMoney() {
        System.out.print("Enter amount: ");
        double amount = readDouble();
        try {
            trader.addMoney(amount);
            System.out.println("Money added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not add money: " + e.getMessage());
        }
    }

    private int readInteger() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
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