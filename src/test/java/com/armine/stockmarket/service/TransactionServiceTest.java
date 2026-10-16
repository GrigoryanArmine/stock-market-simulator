package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Transaction;
import com.armine.stockmarket.model.TransactionType;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;


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


    @Test
    void shouldLoadHistoricalTransactionAfterStockRemoval(
            @TempDir Path tempDir) {

        TransactionService service =
                new TransactionService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Stock apple = marketService.requireStock("AAPL");

        Trader trader = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash"
        );

        LocalDateTime tradeDate =
                LocalDateTime.of(2026, 10, 16, 12, 30);

        trader.addTransaction(new Transaction(
                apple, 2, 180.00,
                TransactionType.BUY, tradeDate
        ));

        service.saveTransactions(trader);

        // Remove the stock from the live market.
        marketService.removeStock(
                "AAPL", new TraderService()
        );

        assertNull(marketService.findStock("AAPL"));

        Trader loadedTrader = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash"
        );

        service.loadTransactions(loadedTrader, marketService);

        assertEquals(1, loadedTrader.getTransactions().size());

        Transaction loaded =
                loadedTrader.getTransactions().get(0);

        assertEquals("AAPL", loaded.getStock().getSymbol());
        assertEquals(TransactionType.BUY, loaded.getType());
        assertEquals(2, loaded.getQuantity());
        assertEquals(180.00, loaded.getPricePerShare(), 0.001);
        assertEquals(tradeDate, loaded.getDateTime());

        // Loading historical records must not recreate the stock.
        assertNull(marketService.findStock("AAPL"));
    }

    @Test
    void shouldNotPartiallyLoadInvalidTransactionFile(
            @TempDir Path tempDir) throws IOException {

        TransactionService service =
                new TransactionService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader trader = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash"
        );

        String fileContents =
                "BUY,AAPL,Apple,2,220.5,2026-10-16T12:00:00\n"
                        + "INVALID TRANSACTION\n";

        Files.writeString(
                tempDir.resolve("armine.txt"),
                fileContents,
                StandardCharsets.UTF_8
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.loadTransactions(trader, marketService)
        );

        // Even the valid first record must not be applied.
        assertTrue(trader.getTransactions().isEmpty());
    }

    @Test
    void shouldPreserveExistingHistoryWhenSavingFails(
            @TempDir Path tempDir) throws IOException {

        TransactionService service =
                new TransactionService(tempDir.toString());

        Trader trader = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash"
        );

        Stock apple = new Stock("AAPL", "Apple", 220.50);

        trader.addTransaction(new Transaction(
                apple, 2, 220.50,
                TransactionType.BUY,
                LocalDateTime.of(2026, 10, 16, 12, 0)
        ));

        service.saveTransactions(trader);

        Path savedFile = tempDir.resolve("armine.txt");
        byte[] originalContents = Files.readAllBytes(savedFile);

        // Add another transaction that has not been saved.
        trader.addTransaction(new Transaction(
                apple, 1, 220.50,
                TransactionType.SELL,
                LocalDateTime.of(2026, 10, 16, 13, 0)
        ));

        // This test uses POSIX directory permissions (macOS/Linux).
        Assumptions.assumeTrue(
                Files.getFileStore(tempDir)
                        .supportsFileAttributeView("posix")
        );

        Set<PosixFilePermission> originalPermissions =
                Files.getPosixFilePermissions(tempDir);

        try {
            // Prevent creation of the temporary save file.
            Files.setPosixFilePermissions(
                    tempDir,
                    EnumSet.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_EXECUTE
                    )
            );

            Assumptions.assumeFalse(
                    Files.isWritable(tempDir),
                    "Filesystem does not enforce these permissions."
            );

            assertThrows(
                    IllegalStateException.class,
                    () -> service.saveTransactions(trader)
            );

        } finally {
            // Always restore directory permissions.
            Files.setPosixFilePermissions(
                    tempDir,
                    originalPermissions
            );
        }

        // Failed saving must leave the original file unchanged.
        assertArrayEquals(
                originalContents,
                Files.readAllBytes(savedFile)
        );
    }

}