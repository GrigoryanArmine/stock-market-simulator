package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.exception.InsufficientFundsException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private Trader trader;
    private MarketService marketService;
    private TradingService tradingService;
    private Scanner scanner;

    public ConsoleUI(Trader trader, MarketService marketService, TradingService tradingService) {
        this.trader = trader;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    showStocks();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    System.out.println("Sell stock - coming soon.");
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
                    running = false;
                    System.out.println("Goodbye!");
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
        System.out.println("Trader: " + trader.getName());
        System.out.println("Balance: $" + trader.getBalance());
        System.out.println();
        System.out.println("1. View available stocks");
        System.out.println("2. Buy stock");
        System.out.println("3. Sell stock");
        System.out.println("4. View portfolio");
        System.out.println("5. View transactions");
        System.out.println("6. Add money");
        System.out.println("7. Exit");
        System.out.println();
        System.out.print("Choose an option: ");
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
        int quantity = scanner.nextInt();
        scanner.nextLine();

        try {
            tradingService.buyStock(
                    trader,
                    stock,
                    quantity
            );

            System.out.println("Purchase successful.");

        } catch (IllegalArgumentException | InsufficientFundsException e) {

            System.out.println(
                    "Purchase failed: " + e.getMessage()
            );
        }
    }
    private void showStocks() {
        System.out.println();
        System.out.println("Available stocks:");

        List<Stock> stocks = marketService.getAllStocks();

        for (Stock stock : stocks) {
            System.out.println(stock);
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
            System.out.println(
                    trader.getTransactions().get(i)
            );
        }
    }

    private void addMoney() {
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        try {
            trader.addMoney(amount);
            System.out.println("Money added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not add money: " + e.getMessage()
            );
        }
    }
}