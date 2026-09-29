package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Transaction;
import com.armine.stockmarket.model.TransactionType;
import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Test
    void shouldSaveAndLoadTransactions() {

        String testDirectory = "test-transactions";

        TransactionService transactionService =
                new TransactionService(testDirectory);

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        Trader trader =
                new Trader("Armine", 10000);

        Stock apple =
                marketService.findStock("AAPL");

        Transaction transaction =
                new Transaction(
                        apple,
                        3,
                        apple.getCurrentPrice(),
                        TransactionType.BUY,
                        LocalDateTime.now()
                );

        trader.addTransaction(transaction);

        transactionService.saveTransactions(trader);

        Trader loadedTrader =
                new Trader("Armine", 10000);

        transactionService.loadTransactions(
                loadedTrader,
                marketService
        );

        assertEquals(
                1,
                loadedTrader.getTransactions().size()
        );

        Transaction loadedTransaction =
                loadedTrader.getTransactions().get(0);

        assertEquals(
                TransactionType.BUY,
                loadedTransaction.getType()
        );

        assertEquals(
                "AAPL",
                loadedTransaction.getStock().getSymbol()
        );

        assertEquals(
                3,
                loadedTransaction.getQuantity()
        );

        deleteDirectory(testDirectory);
    }

    @Test
    void shouldKeepTraderTransactionsSeparate() {

        String testDirectory =
                "test-transactions-separate";

        TransactionService transactionService =
                new TransactionService(testDirectory);

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        Trader anna =
                new Trader("Anna", 10000);

        Trader david =
                new Trader("David", 10000);

        Stock apple =
                marketService.findStock("AAPL");

        Stock tesla =
                marketService.findStock("TSLA");

        anna.addTransaction(
                new Transaction(
                        apple,
                        2,
                        apple.getCurrentPrice(),
                        TransactionType.BUY,
                        LocalDateTime.now()
                )
        );

        david.addTransaction(
                new Transaction(
                        tesla,
                        4,
                        tesla.getCurrentPrice(),
                        TransactionType.BUY,
                        LocalDateTime.now()
                )
        );

        transactionService.saveTransactions(anna);
        transactionService.saveTransactions(david);

        Trader loadedAnna =
                new Trader("Anna", 10000);

        Trader loadedDavid =
                new Trader("David", 10000);

        transactionService.loadTransactions(
                loadedAnna,
                marketService
        );

        transactionService.loadTransactions(
                loadedDavid,
                marketService
        );

        assertEquals(
                1,
                loadedAnna.getTransactions().size()
        );

        assertEquals(
                "AAPL",
                loadedAnna.getTransactions()
                        .get(0)
                        .getStock()
                        .getSymbol()
        );

        assertEquals(
                1,
                loadedDavid.getTransactions().size()
        );

        assertEquals(
                "TSLA",
                loadedDavid.getTransactions()
                        .get(0)
                        .getStock()
                        .getSymbol()
        );

        deleteDirectory(testDirectory);
    }

    private void deleteDirectory(
            String directoryName
    ) {

        File directory =
                new File(directoryName);

        File[] files =
                directory.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        directory.delete();
    }
}