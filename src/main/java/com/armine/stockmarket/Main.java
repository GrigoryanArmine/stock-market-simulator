package com.armine.stockmarket;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.ui.MainMenuUI;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TransactionService;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Create the market
        MarketService marketService = new MarketService();
        marketService.initializeMarket();


        // Create persistence service
        TraderPersistenceService traderPersistenceService = new TraderPersistenceService("traders");

        // Create trader service
        TraderService traderService = new TraderService();
        TradingService tradingService = new TradingService();
        TransactionService transactionService = new TransactionService("transactions");
        // Load all previously saved traders
        List<Trader> savedTraders = traderPersistenceService.loadAllTraders(marketService);

        // Add loaded traders to TraderService
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
                        transactionService);

        mainMenuUI.start();
    }
}