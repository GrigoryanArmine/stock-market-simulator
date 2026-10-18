package com.armine.stockmarket;

import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.*;
import com.armine.stockmarket.ui.MainMenuUI;

import java.util.Arrays;
import java.util.List;
/**
 * Entry point for the console version of the stock market simulator.
 *
 * Initializes market, trading, authentication, and persistence
 * services. Loads previously saved market information, trader
 * accounts, and transaction histories.
 *
 * After initialization, starts the interactive console menu
 * through MainMenuUI.
 */
public class Main {

    /**
     * Creates the console application entry point.
     */
    public Main() {
    }
    /**
     * Starts the console-based stock market simulator.
     *
     * Initializes the application's services, restores saved
     * data, creates the demonstration administrator account,
     * and launches the console interface.
     *
     * Startup errors are reported to the standard error stream.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        try {
            // Initialize market
            MarketPersistenceService marketPersistenceService = new MarketPersistenceService("market.txt");
            MarketService marketService = new MarketService(marketPersistenceService);
            marketPersistenceService.loadMarket(marketService);

            // Initialize trader services
            TraderService traderService = new TraderService();
            TradingService tradingService = new TradingService();
            TraderPersistenceService traderPersistenceService = new TraderPersistenceService("traders");
            TransactionService transactionService = new TransactionService("transactions");

            // Load saved traders
            List<Trader> savedTraders = traderPersistenceService.loadAllTraders(marketService);
            for (Trader trader : savedTraders) {
                transactionService.loadTransactions(trader, marketService);
                traderService.addTrader(trader);
            }

            // Initialize demo Admin account
            PasswordService passwordService = new PasswordService();
            char[] demoPassword = "DemoAdmin123!".toCharArray();
            String adminPasswordHash;

            try {
                adminPasswordHash = passwordService.hashPassword(demoPassword);
            } finally {
                Arrays.fill(demoPassword, '\0');
            }
            Admin admin = new Admin("System", "Administrator", "demo_admin", adminPasswordHash);
            AdminService adminService = new AdminService(admin, passwordService);

            // Start console application
            MainMenuUI mainMenuUI = new MainMenuUI(
                    traderService,
                    adminService,
                    traderPersistenceService,
                    marketService,
                    tradingService,
                    transactionService
            );
            mainMenuUI.start();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
