package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        trader.getPortfolio().addStock(apple, 3);

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

        armine.getPortfolio().addStock(apple, 2);
        anna.getPortfolio().addStock(tesla, 4);

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

        trader.getPortfolio().addStock(stock, 2);
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
}