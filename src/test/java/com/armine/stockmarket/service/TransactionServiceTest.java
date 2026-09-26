package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

    @Test
    public void testSaveTransactions()
            throws InsufficientFundsException {

        String fileName = "test-transactions.txt";

        Trader trader = new Trader(
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
                trader,
                stock,
                3
        );

        TransactionService transactionService =
                new TransactionService(fileName);

        transactionService.saveTransactions(trader);

        File file = new File(fileName);

        assertTrue(file.exists());

        file.delete();
    }
    @Test
    public void testLoadTransactions()
            throws InsufficientFundsException {

        String fileName = "test-transactions.txt";

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

        TransactionService transactionService =
                new TransactionService(fileName);

        // Save the original trader's transactions
        transactionService.saveTransactions(
                originalTrader
        );

        // Create a completely new trader
        Trader newTrader = new Trader(
                "New Trader",
                10000
        );

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        // Load the saved transactions
        transactionService.loadTransactions(
                newTrader,
                marketService
        );

        assertEquals(
                1,
                newTrader.getTransactions().size()
        );

        assertEquals(
                "AAPL",
                newTrader.getTransactions()
                        .get(0)
                        .getStock()
                        .getSymbol()
        );

        assertEquals(
                3,
                newTrader.getTransactions()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                220.50,
                newTrader.getTransactions()
                        .get(0)
                        .getPricePerShare()
        );

        assertEquals(
                "BUY",
                newTrader.getTransactions()
                        .get(0)
                        .getType()
                        .toString()
        );

        File file = new File(fileName);
        file.delete();
    }
}
