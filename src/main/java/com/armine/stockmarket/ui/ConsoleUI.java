package com.armine.stockmarket.ui;

import com.armine.stockmarket.exception.*;
import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
/**
 * Provides the console interface for an authenticated trader.
 *
 * Allows traders to view market stocks, buy and sell shares,
 * inspect their portfolios, review transaction history,
 * and add simulated money to their accounts.
 *
 * Trading operations are delegated to TradingService,
 * while account and transaction data are saved through
 * the corresponding persistence services.
 *
 * The interface displays a menu until the trader
 * successfully saves their data and logs out.
 */
public class ConsoleUI {
    private Trader trader;
    private MarketService marketService;
    private TradingService tradingService;
    private Scanner scanner;
    private TraderPersistenceService traderPersistenceService;
    private TransactionService transactionService;
    /**
     * Creates a console interface for the specified trader.
     *
     * Initializes the required services and a Scanner
     * for reading input from System.in.
     *
     * @param trader the authenticated trader
     * @param marketService the service managing market stocks
     * @param tradingService the service processing trades
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service saving transaction history
     * @throws IllegalArgumentException if any dependency is null
     */
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
    /**
     * Starts the trader's interactive console menu.
     *
     * Continuously displays trading options and processes
     * the user's selection.
     *
     * The menu provides access to market information,
     * stock purchases and sales, portfolio details,
     * transaction history, and deposits.
     *
     * When logout is selected, the interface attempts
     * to save the trader's data. Logout succeeds only
     * if both persistence operations complete successfully.
     */
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
    /**
     * Displays the trader's main menu.
     *
     * Shows the trader's full name, available cash balance,
     * and the available trading operations.
     */
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
    /**
     * Displays all stocks currently available in the market.
     *
     * Stocks are sorted alphabetically by their symbols.
     * If the market is empty, an informational message
     * is displayed instead.
     */
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
    /**
     * Handles the purchase of stock shares through the console.
     *
     * Displays available stocks and prompts the trader
     * for a stock symbol and quantity.
     *
     * The purchase is executed through TradingService.
     * After a successful purchase, the method attempts
     * to save the updated trader and transaction data.
     *
     * Invalid requests and insufficient funds are reported
     * to the user without ending the console session.
     */
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
    /**
     * Handles the sale of stock shares through the console.
     *
     * Displays the trader's holdings sorted by stock symbol.
     * The trader selects a stock and specifies the quantity
     * of shares to sell.
     *
     * The sale is executed through TradingService.
     * After a successful sale, the method attempts
     * to save the updated account and transaction history.
     *
     * Invalid selections and insufficient shares are
     * reported without terminating the session.
     */
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
    /**
     * Displays the trader's current portfolio.
     *
     * Uses the portfolio's string representation
     * to show owned shares and their total market value.
     */
    private void showPortfolio() {
        System.out.println();
        System.out.println(trader.getPortfolio());
    }
    /**
     * Displays the trader's recorded transaction history.
     *
     * Each transaction is printed using its string
     * representation, including the transaction type,
     * quantity, execution price, and timestamp.
     *
     * If no transactions have been recorded,
     * an informational message is displayed.
     */
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
    /**
     * Handles deposits into the trader's simulated account.
     *
     * Prompts the trader for an amount and delegates
     * balance validation and updating to Trader.
     *
     * After a successful deposit, attempts to save
     * the updated account information.
     *
     * Invalid deposit amounts are reported to the user.
     */
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
    /**
     * Attempts to save the trader's account and transaction history.
     *
     * Trader data and transaction history are saved through
     * separate persistence services.
     *
     * If either operation fails with an IllegalStateException,
     * an error message is displayed and false is returned.
     *
     * The two files are not saved as one atomic operation.
     *
     * @return true if both save operations succeed,
     *         false if either operation fails
     */
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
    /**
     * Reads an integer from the console.
     *
     * Repeatedly prompts the user until a valid integer
     * is entered. The remaining newline is consumed
     * after reading the value.
     *
     * @return the integer entered by the user
     */
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
    /**
     * Reads a double value from the console.
     *
     * Repeatedly prompts the user until Scanner accepts
     * a numeric double value.
     *
     * Validation of business constraints, such as requiring
     * a positive finite deposit, is performed separately.
     *
     * @return the double value entered by the user
     */
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