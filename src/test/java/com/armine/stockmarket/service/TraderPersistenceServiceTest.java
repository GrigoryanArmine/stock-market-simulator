package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TraderPersistenceServiceTest {

    @Test
    public void testSaveAndLoadTrader()
            throws InsufficientFundsException {

        String fileName = "test-trader.txt";

        // Create original trader
        Trader originalTrader = new Trader(
                "Armine",
                10000
        );

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        tradingService.buyStock(
                originalTrader,
                stock,
                3
        );

        // Save trader
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(fileName);

        persistenceService.saveTrader(
                originalTrader
        );

        // Create market
        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        // Load trader
        Trader loadedTrader =
                persistenceService.loadTrader(
                        marketService
                );

        // Check trader information
        assertNotNull(loadedTrader);

        assertEquals(
                "Armine",
                loadedTrader.getName()
        );

        assertEquals(
                9338.50,
                loadedTrader.getBalance()
        );

        // Check portfolio
        assertEquals(
                3,
                loadedTrader.getPortfolio()
                        .getQuantity(stock)
        );

        File file = new File(fileName);
        file.delete();
    }
}