package com.armine.stockmarket;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.ui.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        TraderPersistenceService traderPersistenceService =
                new TraderPersistenceService(
                        "trader.txt"
                );

        TransactionService transactionService =
                new TransactionService(
                        "transactions.txt"
                );

        Trader trader =
                traderPersistenceService.loadTrader(
                        marketService
                );

        if (trader == null) {

            trader = new Trader(
                    "Armine",
                    10000
            );
        }

        transactionService.loadTransactions(
                trader,
                marketService
        );

        TradingService tradingService =
                new TradingService();

        ConsoleUI consoleUI =
                new ConsoleUI(
                        trader,
                        marketService,
                        tradingService,
                        traderPersistenceService,
                        transactionService
                );

        consoleUI.start();
    }
}