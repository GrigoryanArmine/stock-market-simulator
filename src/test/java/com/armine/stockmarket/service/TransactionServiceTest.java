package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Transaction;
import com.armine.stockmarket.model.TransactionType;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    @Test
    void shouldSaveAndLoadTransactions(@TempDir Path tempDir) {

        TransactionService transactionService =
                new TransactionService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader trader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000,
                "test-hash"
        );

        Stock apple = marketService.findStock("AAPL");

        assertNotNull(apple);

        Transaction transaction = new Transaction(
                apple,
                3,
                apple.getCurrentPrice(),
                TransactionType.BUY,
                LocalDateTime.now()
        );

        trader.addTransaction(transaction);

        transactionService.saveTransactions(trader);

        Trader loadedTrader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000,
                "test-hash"
        );

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
    }

    @Test
    void shouldKeepTraderTransactionsSeparate(@TempDir Path tempDir) {

        TransactionService transactionService =
                new TransactionService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader anna = new Trader(
                123456,
                "anna",
                "Anna",
                "Smith",
                10000,
                "test-hash"
        );

        Trader david = new Trader(
                654321,
                "david",
                "David",
                "Brown",
                10000,
                "test-hash"
        );

        Stock apple = marketService.findStock("AAPL");
        Stock tesla = marketService.findStock("TSLA");

        assertNotNull(apple);
        assertNotNull(tesla);

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

        Trader loadedAnna = new Trader(
                123456,
                "anna",
                "Anna",
                "Smith",
                10000,
                "test-hash"
        );

        Trader loadedDavid = new Trader(
                654321,
                "david",
                "David",
                "Brown",
                10000,
                "test-hash"
        );

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
    }

    @Test
    void shouldThrowExceptionWhenTransactionDirectoryIsInvalid(
            @TempDir Path tempDir) throws java.io.IOException {

        // Create a regular file instead of a directory
        Path invalidDirectory = tempDir.resolve("not-a-directory");
        java.nio.file.Files.createFile(invalidDirectory);

        TransactionService transactionService =
                new TransactionService(invalidDirectory.toString());

        Trader trader = new Trader(
                123456,
                "testuser",
                "Test",
                "User",
                10000,
                "test-hash"
        );

        assertThrows(
                IllegalStateException.class,
                () -> transactionService.saveTransactions(trader)
        );
    }

}