package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TransactionService;
import com.armine.stockmarket.model.Admin;

import java.util.Scanner;

public class MainMenuUI {
    private TraderService traderService;
    private TraderPersistenceService traderPersistenceService;
    private MarketService marketService;
    private TradingService tradingService;
    private TransactionService transactionService;
    private Scanner scanner;
    private Admin admin;

    public MainMenuUI(
            TraderService traderService,
            TraderPersistenceService traderPersistenceService,
            MarketService marketService,
            TradingService tradingService,
            TransactionService transactionService,
            Admin admin) {
        this.traderService = traderService;
        this.traderPersistenceService = traderPersistenceService;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
        this.admin = admin;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("   STOCK MARKET SIMULATOR");
        System.out.println("==============================");
        System.out.println();
        System.out.println("1. Trader");
        System.out.println("2. Admin");
        System.out.println("3. Exit");
        System.out.println();
        System.out.print("Choose an option: ");
    }
    private void createAccount() {
        System.out.println();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        if (firstName.isBlank()) {
            System.out.println("First name cannot be empty.");
            return;
        }

        System.out.print("Enter surname: ");
        String surname = scanner.nextLine().trim();
        if (surname.isBlank()) {
            System.out.println("Surname cannot be empty.");
            return;
        }

        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        if (username.isBlank()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        if (traderService.findTraderByUsername(username) != null) {
            System.out.println("Username already exists.");
            return;
        }

        int id = traderService.generateUniqueId();
        Trader trader = new Trader(id, username, firstName, surname, 10000);

        traderService.addTrader(trader);
        traderPersistenceService.saveTrader(trader);

        System.out.println("Account created successfully!");
        System.out.println("Trader ID: " + id);
        System.out.println("Username: " + username);
    }
    public void start() {
        boolean running = true;

        while (running) {
            showMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    showTraderMenu();
                    break;
                case "2":
                    AdminUI adminUI = new AdminUI(admin, marketService, traderService);
                    adminUI.start();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    private void showTraderMenu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("==============================");
            System.out.println("         TRADER MENU");
            System.out.println("==============================");
            System.out.println();
            System.out.println("1. Login");
            System.out.println("2. Create account");
            System.out.println("3. Back");
            System.out.println();
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    Trader selectedTrader = login();
                    if (selectedTrader != null) {
                        ConsoleUI consoleUI = new ConsoleUI(
                                        selectedTrader,
                                        marketService,
                                        tradingService,
                                        traderPersistenceService,
                                        transactionService
                        );
                        consoleUI.start();
                    }
                    break;
                case "2":
                    createAccount();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    private Trader login() {
        System.out.println();
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isBlank()) {
            System.out.println("Username cannot be empty.");
            return null;
        }

        Trader trader = traderService.findTraderByUsername(username);
        if (trader == null) {
            System.out.println("Account not found.");
            return null;
        }

        System.out.println("Welcome, " + trader.getFirstName() + "!");
        return trader;
    }
}