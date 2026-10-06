package com.armine.stockmarket.gui;

import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;
import com.armine.stockmarket.service.TradingService;


import java.util.List;
public class GuiMain {

    public static void main(String[] args) {

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        TraderService traderService = new TraderService();

        TradingService tradingService = new TradingService();

        TraderPersistenceService traderPersistenceService = new TraderPersistenceService("traders");

        TransactionService transactionService = new TransactionService("transactions");

        List<Trader> savedTraders = traderPersistenceService.loadAllTraders(marketService);

        for (Trader trader : savedTraders) {
            transactionService.loadTransactions(trader, marketService);
            traderService.addTrader(trader);
        }

        MainFrame mainFrame = new MainFrame(traderService, marketService, tradingService);
        mainFrame.setVisible(true);
    }
}