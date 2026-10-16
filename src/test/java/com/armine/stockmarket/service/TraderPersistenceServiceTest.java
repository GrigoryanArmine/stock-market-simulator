package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;


class TraderPersistenceServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadTrader() {
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader trader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000,
                "test-hash-armine"
        );

        Stock apple = marketService.findStock("AAPL");
        assertNotNull(apple);

        trader.addShares(apple, 3);

        persistenceService.saveTrader(trader);

        Trader loadedTrader =
                persistenceService.loadTrader("armine", marketService);

        assertNotNull(loadedTrader);
        assertEquals(123456, loadedTrader.getId());
        assertEquals("armine", loadedTrader.getUsername());
        assertEquals("Armine", loadedTrader.getFirstName());
        assertEquals("Grigoryan", loadedTrader.getSurname());
        assertEquals(10000, loadedTrader.getBalance(), 0.001);
        assertEquals(3, loadedTrader.getPortfolio().getQuantity(apple));

        // Verify password hash was saved and loaded
        assertEquals(
                "test-hash-armine",
                loadedTrader.getPasswordHash()
        );
    }

    @Test
    void shouldKeepDifferentTradersSeparate() {
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader armine = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash-armine"
        );

        Trader anna = new Trader(
                654321, "anna", "Anna", "Smith",
                5000, "test-hash-anna"
        );

        Stock apple = marketService.findStock("AAPL");
        Stock tesla = marketService.findStock("TSLA");

        assertNotNull(apple);
        assertNotNull(tesla);

        armine.addShares(apple, 2);
        anna.addShares(tesla, 4);

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);

        Trader loadedArmine =
                persistenceService.loadTrader("armine", marketService);

        Trader loadedAnna =
                persistenceService.loadTrader("anna", marketService);

        assertNotNull(loadedArmine);
        assertNotNull(loadedAnna);

        assertEquals(2, loadedArmine.getPortfolio().getQuantity(apple));
        assertEquals(0, loadedArmine.getPortfolio().getQuantity(tesla));

        assertEquals(4, loadedAnna.getPortfolio().getQuantity(tesla));
        assertEquals(0, loadedAnna.getPortfolio().getQuantity(apple));

        // Verify each Trader retains their own password hash
        assertEquals(
                "test-hash-armine",
                loadedArmine.getPasswordHash()
        );

        assertEquals(
                "test-hash-anna",
                loadedAnna.getPasswordHash()
        );
    }

    @Test
    void shouldLoadAllTraders() {
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader armine = new Trader(
                123456, "armine", "Armine", "Grigoryan",
                10000, "test-hash-armine"
        );

        Trader anna = new Trader(
                234567, "anna", "Anna", "Smith",
                8000, "test-hash-anna"
        );

        Trader david = new Trader(
                345678, "david", "David", "Brown",
                12000, "test-hash-david"
        );

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);
        persistenceService.saveTrader(david);

        List<Trader> traders =
                persistenceService.loadAllTraders(marketService);

        assertEquals(3, traders.size());

        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("armine")));

        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("anna")));

        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("david")));

        // Verify all password hashes were preserved
        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("armine")
                        && t.getPasswordHash().equals("test-hash-armine")));

        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("anna")
                        && t.getPasswordHash().equals("test-hash-anna")));

        assertTrue(traders.stream()
                .anyMatch(t -> t.getUsername().equals("david")
                        && t.getPasswordHash().equals("test-hash-david")));
    }

    @Test
    void shouldRejectMissingStockDuringLoading() {
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService originalMarket = new MarketService();

        Stock stock = new Stock("TEST", "Test Company", 100.00);
        originalMarket.addStock(stock);

        Trader trader = new Trader(
                123456, "testuser", "Test", "User",
                1000, "test-hash-user"
        );

        trader.addShares(stock, 2);
        persistenceService.saveTrader(trader);

        MarketService emptyMarket = new MarketService();

        assertThrows(
                IllegalStateException.class,
                () -> persistenceService.loadTrader(
                        "testuser", emptyMarket
                )
        );

        assertNull(emptyMarket.findStock("TEST"));
    }

    @Test
    void shouldSaveUpdatedBalanceAndHoldings() {
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Stock apple = marketService.requireStock("AAPL");

        Trader trader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000,
                "test-hash"
        );

        trader.addShares(apple, 2);
        persistenceService.saveTrader(trader);

        // Update the trader's account.
        trader.addMoney(500);
        trader.addShares(apple, 3);

        // Save the updated information.
        persistenceService.saveTrader(trader);

        Trader loadedTrader =
                persistenceService.loadTrader(
                        "armine",
                        marketService
                );

        assertNotNull(loadedTrader);
        assertEquals(10500, loadedTrader.getBalance(), 0.001);
        assertEquals(5, loadedTrader.getOwnedShares(apple));
        assertEquals("test-hash", loadedTrader.getPasswordHash());
    }

    @Test
    void shouldPreserveExistingTraderFileWhenSavingFails()
            throws IOException {

        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Stock apple = marketService.requireStock("AAPL");

        Trader trader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000,
                "test-hash"
        );

        trader.addShares(apple, 2);
        persistenceService.saveTrader(trader);

        Path savedFile = tempDir.resolve("armine.txt");
        byte[] originalContents = Files.readAllBytes(savedFile);

        // Change the trader's state in memory.
        trader.addMoney(500);
        trader.addShares(apple, 1);

        // This test requires a POSIX filesystem.
        Assumptions.assumeTrue(
                Files.getFileStore(tempDir)
                        .supportsFileAttributeView("posix")
        );

        Set<PosixFilePermission> originalPermissions =
                Files.getPosixFilePermissions(tempDir);

        try {
            // Prevent creating a temporary save file.
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
                    () -> persistenceService.saveTrader(trader)
            );

        } finally {
            // Restore permissions even when the test fails.
            Files.setPosixFilePermissions(
                    tempDir,
                    originalPermissions
            );
        }

        // The previously saved account must remain unchanged.
        assertArrayEquals(
                originalContents,
                Files.readAllBytes(savedFile)
        );

        Trader loadedTrader =
                persistenceService.loadTrader(
                        "armine",
                        marketService
                );

        assertNotNull(loadedTrader);
        assertEquals(10000, loadedTrader.getBalance(), 0.001);
        assertEquals(2, loadedTrader.getOwnedShares(apple));
    }

    @Test
    void shouldRejectCorruptedTraderFile()
            throws IOException {

        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Stock apple = marketService.requireStock("AAPL");

        String fileContents =
                "123456\n"
                        + "armine\n"
                        + "Armine\n"
                        + "Grigoryan\n"
                        + "10000\n"
                        + "test-hash\n"
                        + "AAPL,Apple,220.5,3\n"
                        + "INVALID PORTFOLIO RECORD\n";

        Files.writeString(
                tempDir.resolve("armine.txt"),
                fileContents,
                StandardCharsets.UTF_8
        );

        assertThrows(
                IllegalStateException.class,
                () -> persistenceService.loadTrader(
                        "armine",
                        marketService
                )
        );

        // The market itself must remain unchanged.
        assertSame(apple, marketService.requireStock("AAPL"));
        assertEquals(5, marketService.getAllStocks().size());
    }

}