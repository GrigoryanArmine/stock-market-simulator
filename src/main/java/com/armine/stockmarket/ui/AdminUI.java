package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.util.Scanner;

public class AdminUI {
    private Admin admin;
    private MarketService marketService;
    private TraderService traderService;
    private Scanner scanner;

    public AdminUI(Admin admin, MarketService marketService, TraderService traderService) {
        this.admin = admin;
        this.marketService = marketService;
        this.traderService = traderService;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("          ADMIN MENU");
        System.out.println("==============================");
        System.out.println("Admin: " + admin.getFullName());
        System.out.println();
        System.out.println("1. View all stocks");
        System.out.println("2. Add stock");
        System.out.println("3. Update stock price");
        System.out.println("4. Remove stock");
        System.out.println("5. View registered traders");
        System.out.println("6. Logout");
        System.out.println();
        System.out.print("Choose an option: ");
    }
    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    showStocks();
                    break;
                case "2":
                    addStock();
                    break;
                case "3":
                    updateStockPrice();
                    break;
                case "4":
                    removeStock();
                    break;
                case "5":
                    showTraders();
                    break;
                case "6":
                    running = false;
                    System.out.println("Admin logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    private void showStocks() {
        System.out.println();
        System.out.println("Available stocks:");
        for (Stock stock : marketService.getAllStocks()) {
            System.out.println(stock.getSymbol()
                            + " - "
                            + stock.getCompanyName()
                            + " - $"
                            + stock.getCurrentPrice()
            );
        }
    }
    private void addStock() {
        System.out.println();
        System.out.println("Add new stock:");

        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        if (symbol.isBlank()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        if (marketService.findStock(symbol) != null) {
            System.out.println("Stock already exists.");
            return;
        }
        System.out.print("Enter company name: ");
        String companyName = scanner.nextLine().trim();

        if (companyName.isBlank()) {
            System.out.println("Company name cannot be empty.");
            return;
        }
        System.out.print("Enter stock price: ");

        try {
            double price = Double.parseDouble(scanner.nextLine());
            Stock stock = new Stock(symbol, companyName, price);
            admin.addStockToMarket(stock, marketService);
            System.out.println("Stock added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid price.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    private void updateStockPrice() {
        System.out.println();
        System.out.println("Update stock price:");
        showStocks();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        if (symbol.isBlank()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        if (marketService.findStock(symbol) == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter new price: ");

        try {
            double newPrice = Double.parseDouble(scanner.nextLine());
            marketService.updatePrice(symbol, newPrice);
            System.out.println("Stock price updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid price.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    private void removeStock() {
        System.out.println();
        System.out.println("Remove stock:");
        showStocks();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        if (symbol.isBlank()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        if (marketService.findStock(symbol) == null) {
            System.out.println("Stock not found.");
            return;
        }
        try {
            marketService.removeStock(symbol, traderService);
            System.out.println("Stock removed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    private void showTraders() {
        System.out.println();
        System.out.println("Registered traders:");

        if (traderService.getAllTraders().isEmpty()) {
            System.out.println("No traders registered.");
            return;
        }
        for (Trader trader : traderService.getAllTraders()) {
            System.out.println("ID: " + trader.getId()
                            + " | Username: " + trader.getUsername()
                            + " | Name: " + trader.getFullName()
                            + " | Balance: $" + trader.getBalance()
            );
        }
    }

}