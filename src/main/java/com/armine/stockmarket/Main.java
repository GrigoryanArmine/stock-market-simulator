package com.armine.stockmarket;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.ui.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        Trader trader = new Trader(
                "Armine",
                10000
        );

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        TradingService tradingService =
                new TradingService();

        ConsoleUI consoleUI = new ConsoleUI(
                trader,
                marketService,
                tradingService
        );

        consoleUI.start();
    }
}