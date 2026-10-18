package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import java.util.Arrays;
import java.util.Scanner;
/**
 * Provides the main console menu for the stock market simulator.
 *
 * Allows users to select the trader or administrator interface,
 * create trader accounts, and authenticate existing users.
 *
 * The class delegates account management and authentication
 * to the corresponding service classes.
 *
 * After successful authentication, it launches either the
 * trader console interface or the administrator console interface.
 */
public class MainMenuUI {
    private TraderService traderService;
    private AdminService adminService;
    private TraderPersistenceService traderPersistenceService;
    private MarketService marketService;
    private TradingService tradingService;
    private TransactionService transactionService;
    private Scanner scanner;
    /**
     * Creates the main console menu with the required services.
     *
     * All services are provided through the constructor and
     * stored for use during menu navigation and authentication.
     *
     * A Scanner is initialized to read input from System.in.
     *
     * @param traderService the service managing trader accounts
     * @param adminService the service authenticating administrators
     * @param traderPersistenceService the service saving trader data
     * @param marketService the service managing available stocks
     * @param tradingService the service processing stock transactions
     * @param transactionService the service managing transaction history
     * @throws IllegalArgumentException if any required service is null
     */
    public MainMenuUI(TraderService traderService, AdminService adminService, TraderPersistenceService traderPersistenceService, MarketService marketService, TradingService tradingService, TransactionService transactionService) {
        if (traderService == null || adminService == null || traderPersistenceService == null || marketService == null || tradingService == null || transactionService == null)
            throw new IllegalArgumentException("Main menu dependencies cannot be null.");

        this.traderService = traderService;
        this.adminService = adminService;
        this.traderPersistenceService = traderPersistenceService;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }
    /**
     * Displays the main application menu.
     *
     * Presents options for accessing the trader interface,
     * accessing the administrator interface, or exiting.
     */
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
    /**
     * Starts the main console menu loop.
     *
     * Continuously displays the main menu and processes
     * the user's selection until the exit option is chosen.
     *
     * Depending on the selected option, opens the trader
     * menu or starts administrator authentication.
     *
     * Invalid options display an error message without
     * terminating the application.
     */
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
    /**
     * Displays and manages the trader menu.
     *
     * Allows users to log in, create a new trader account,
     * or return to the main menu.
     *
     * After successful authentication, starts ConsoleUI
     * for the authenticated trader.
     */
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
    /**
     * Handles trader account registration through the console.
     *
     * Prompts the user for personal information, a username,
     * a password, and password confirmation.
     *
     * Verifies that both passwords match before delegating
     * account creation to TraderService.
     *
     * Attempts to save the newly created account using
     * TraderPersistenceService and displays the result.
     *
     * If saving fails, the account remains registered
     * in memory, but an error message is displayed.
     *
     * Password character arrays are cleared after processing.
     */
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
            if (!Arrays.equals(password, confirmPassword)) {
                System.out.println("Passwords do not match.");
                return;
            }
            Trader trader = traderService.createTrader(firstName, surname, username, password);
            try {
                traderPersistenceService.saveTrader(trader);
            } catch (IllegalStateException ex) {
                System.out.println("Account created in memory, but saving failed: " + ex.getMessage());
                return;
            }
            System.out.println("Account created successfully!");
            System.out.println("Trader ID: " + trader.getId());
            System.out.println("Username: " + trader.getUsername()
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
        }
    }
    /**
     * Authenticates a trader through the console.
     *
     * Prompts for a username and password, then delegates
     * credential verification to TraderService.
     *
     * Displays a welcome message when authentication
     * succeeds or an error message when it fails.
     *
     * The password character array is cleared after processing.
     *
     * @return the authenticated Trader, or null if login fails
     */
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
    /**
     * Authenticates an administrator through the console.
     *
     * Prompts for administrator credentials and verifies
     * them using AdminService.
     *
     * If authentication succeeds, creates and starts AdminUI.
     * Otherwise, displays an invalid credentials message.
     *
     * The password character array is cleared after processing.
     */
    private void loginAdmin() {
        System.out.println();
        System.out.println("ADMIN LOGIN");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        char[] password = scanner.nextLine().toCharArray();
        Admin loggedInAdmin;
        try {
            loggedInAdmin = adminService.login(username, password);
        } finally {
            Arrays.fill(password, '\0');
        }
        if (loggedInAdmin == null) {
            System.out.println("Incorrect username or password.");
            return;
        }
        System.out.println("Welcome, " + loggedInAdmin.getFirstName() + "!");
        AdminUI adminUI = new AdminUI(loggedInAdmin, marketService, traderService);
        adminUI.start();
    }
}
