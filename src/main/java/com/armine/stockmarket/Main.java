package com.armine.stockmarket;

import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TransactionService;
import com.armine.stockmarket.ui.MainMenuUI;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Create the market
        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        // Create persistence service
        TraderPersistenceService traderPersistenceService = new TraderPersistenceService("traders");

        // Create services
        TraderService traderService = new TraderService();
        TradingService tradingService = new TradingService();
        TransactionService transactionService = new TransactionService("transactions");

        // Create the admin
        String adminUsername = System.getenv("STOCK_ADMIN_USERNAME");
        String adminPasswordHash = System.getenv("STOCK_ADMIN_PASSWORD_HASH");

        if (adminUsername == null || adminUsername.isBlank() || adminPasswordHash == null || adminPasswordHash.isBlank()) {
            throw new IllegalStateException("Admin credentials are not configured.");
        }

        Admin admin = new Admin("System", "Administrator", adminUsername, adminPasswordHash);

        // Load all previously saved traders
        List<Trader> savedTraders = traderPersistenceService.loadAllTraders(marketService);

        // Add loaded traders to TraderService
        for (Trader trader : savedTraders) {
            transactionService.loadTransactions(trader, marketService);
            traderService.addTrader(trader);
        }

        // Create and start the main menu
        MainMenuUI mainMenuUI = new MainMenuUI(
                traderService,
                traderPersistenceService,
                marketService,
                tradingService,
                transactionService,
                admin
        );
        mainMenuUI.start();
    }
}