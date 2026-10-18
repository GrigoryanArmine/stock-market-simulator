package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
/**
 * Provides the console interface for an authenticated administrator.
 *
 * Allows administrators to view available stocks, add new stocks,
 * update market prices, remove stocks, and view registered traders.
 *
 * Market management operations are delegated to MarketService,
 * while registered trader information is obtained from TraderService.
 *
 * The interface runs until the administrator selects logout.
 */
public class AdminUI {
    private Admin admin;
    private MarketService marketService;
    private TraderService traderService;
    private Scanner scanner;
    /**
     * Creates an administrator console interface.
     *
     * Initializes the required services and a Scanner
     * for reading input from System.in.
     *
     * @param admin the authenticated administrator
     * @param marketService the service managing market stocks
     * @param traderService the service managing registered traders
     * @throws IllegalArgumentException if any dependency is null
     */
    public AdminUI(Admin admin, MarketService marketService, TraderService traderService) {
        if (admin == null || marketService == null || traderService == null)
            throw new IllegalArgumentException("Admin UI dependencies cannot be null.");
        this.admin = admin;
        this.marketService = marketService;
        this.traderService = traderService;
        this.scanner = new Scanner(System.in);
    }
    /**
     * Displays the administrator's main menu.
     *
     * Shows the administrator's name and the available
     * stock management, trader viewing, and logout options.
     */
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
    /**
     * Starts the administrator's interactive console menu.
     *
     * Continuously displays the menu and processes the
     * administrator's selection.
     *
     * Delegates selected operations to the corresponding
     * private methods.
     *
     * Invalid menu options display an error message.
     * The loop ends when the logout option is selected.
     */
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
    /**
     * Displays all stocks currently available in the market.
     *
     * Retrieves the stock list from MarketService and displays
     * each stock's symbol, company name, and current price.
     *
     * Prices are formatted to two decimal places using
     * the US locale.
     *
     * If the market is empty, an informational message
     * is displayed.
     */
    private void showStocks() {
        System.out.println();
        System.out.println("Available stocks:");
        List<Stock> stocks = marketService.getAllStocks();
        if (stocks.isEmpty()) {
            System.out.println("No stocks available.");
            return;
        }
        for (Stock stock : stocks) {
            System.out.println(stock.getSymbol() + " - " + stock.getCompanyName() + " - $" + String.format(Locale.US, "%.2f", stock.getCurrentPrice()));
        }
    }
    /**
     * Handles adding a new stock through the console.
     *
     * Prompts the administrator for a stock symbol,
     * company name, and initial market price.
     *
     * Creates a Stock object and delegates the addition
     * to MarketService, which checks symbol uniqueness
     * and handles market persistence when configured.
     *
     * Invalid prices, duplicate symbols, and saving
     * failures are reported to the administrator.
     */
    private void addStock() {
        System.out.println();
        System.out.println("Add new stock:");

        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim();

        System.out.print("Enter company name: ");
        String companyName = scanner.nextLine().trim();

        System.out.print("Enter stock price: ");
        String priceInput = scanner.nextLine().trim();

        try {
            double price = Double.parseDouble(priceInput);
            Stock stock = new Stock(symbol, companyName, price);
            marketService.addStock(stock);
            System.out.println("Stock added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric price.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Handles updating the market price of an existing stock.
     *
     * Displays available stocks and prompts the administrator
     * for a stock symbol and a new price.
     *
     * Delegates price validation, updating, and persistence
     * to MarketService.
     *
     * Invalid input, unknown stock symbols, and saving
     * failures are reported to the administrator.
     */
    private void updateStockPrice() {
        System.out.println();
        System.out.println("Update stock price:");
        showStocks();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim();

        System.out.print("Enter new price: ");
        String priceInput = scanner.nextLine().trim();

        try {
            double newPrice = Double.parseDouble(priceInput);
            marketService.updatePrice(symbol, newPrice);
            System.out.println("Stock price updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric price.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Handles removing a stock from the market.
     *
     * Displays available stocks and prompts the administrator
     * for the symbol of the stock to remove.
     *
     * Delegates the removal to MarketService.
     * A stock cannot be removed while a registered
     * trader owns shares of it.
     *
     * Invalid requests and persistence failures
     * are reported to the administrator.
     */
    private void removeStock() {
        System.out.println();
        System.out.println("Remove stock:");
        showStocks();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim();
        try {
            marketService.removeStock(symbol, traderService);
            System.out.println("Stock removed successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Displays information about all registered traders.
     *
     * Retrieves trader accounts from TraderService and
     * displays each trader's ID, username, full name,
     * and available cash balance.
     *
     * Balances are formatted to two decimal places
     * using the US locale.
     *
     * If no traders are registered, an informational
     * message is displayed.
     */
    private void showTraders() {
        System.out.println();
        System.out.println("Registered traders:");
        List<Trader> traders = traderService.getAllTraders();
        if (traders.isEmpty()) {
            System.out.println("No traders registered.");
            return;
        }
        for (Trader trader : traders) {
            System.out.println(
                    "ID: " + trader.getId()
                            + " | Username: "
                            + trader.getUsername()
                            + " | Name: "
                            + trader.getFullName()
                            + " | Balance: $"
                            + String.format(
                            Locale.US,
                            "%.2f",
                            trader.getBalance()
                    )
            );
        }
    }
}