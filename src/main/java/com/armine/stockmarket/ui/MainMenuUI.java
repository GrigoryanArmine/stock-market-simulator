package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TransactionService;
import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.service.AdminService;
import com.armine.stockmarket.service.PasswordService;

import java.util.Arrays;

import java.util.Scanner;

public class MainMenuUI {
    private TraderService traderService;
    private AdminService adminService;
    private TraderPersistenceService traderPersistenceService;
    private MarketService marketService;
    private TradingService tradingService;
    private TransactionService transactionService;
    private Scanner scanner;
    private Admin admin;

    public MainMenuUI(
            TraderService traderService,
            AdminService adminService,
            TraderPersistenceService traderPersistenceService,
            MarketService marketService,
            TradingService tradingService,
            TransactionService transactionService,
            Admin admin) {
        this.traderService = traderService;
        this.adminService = adminService;
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

        System.out.print("Enter surname: ");
        String surname = scanner.nextLine().trim();

        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Choose a password: ");
        char[] password = scanner.nextLine().toCharArray();

        System.out.print("Confirm password: ");
        char[] confirmPassword = scanner.nextLine().toCharArray();

        try {
            if (!Arrays.equals(password, confirmPassword))
                throw new IllegalArgumentException("Passwords do not match.");

            Trader trader = traderService.createTrader(firstName, surname, username, password);
            traderPersistenceService.saveTrader(trader);

            System.out.println("Account created successfully!");
            System.out.println("Trader ID: " + trader.getId());
            System.out.println("Username: " + trader.getUsername());

        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());

        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
        }
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
                    loginAdmin();
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
                        ConsoleUI consoleUI = new ConsoleUI(selectedTrader, marketService, tradingService, traderPersistenceService, transactionService);
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

        System.out.print("Enter password: ");
        char[] password = scanner.nextLine().toCharArray();

        try {
            Trader trader = traderService.login(username, password);
            System.out.println("Welcome, " + trader.getFirstName() + "!");
            return trader;

        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return null;
        } finally {
            Arrays.fill(password, '\0');
        }
    }
    private void loginAdmin() {
        System.out.println();
        System.out.println("ADMIN LOGIN");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        char[] password = scanner.nextLine().toCharArray();

        try {
            Admin loggedInAdmin = adminService.login(username, password);
            if (loggedInAdmin == null) {
                System.out.println("Incorrect username or password.");
                return;
            }

            System.out.println("Welcome, " + loggedInAdmin.getFirstName() + "!");

            AdminUI adminUI = new AdminUI(loggedInAdmin, marketService, traderService);
            adminUI.start();

        } finally {
            Arrays.fill(password, '\0');
        }
    }
}