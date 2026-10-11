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
        TraderPersistenceService persistenceService = new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader trader = new Trader(
                123456,
                "armine",
                "Armine",
                "Grigoryan",
                10000
        );

        Stock apple = marketService.findStock("AAPL");
        trader.getPortfolio().addStock(apple, 3);

        persistenceService.saveTrader(trader);

        Trader loadedTrader = persistenceService.loadTrader("armine", marketService);

        assertNotNull(loadedTrader);
        assertEquals(123456, loadedTrader.getId());
        assertEquals("armine", loadedTrader.getUsername());
        assertEquals("Armine", loadedTrader.getFirstName());
        assertEquals("Grigoryan", loadedTrader.getSurname());
        assertEquals(10000, loadedTrader.getBalance(), 0.001);
        assertEquals(3, loadedTrader.getPortfolio().getQuantity(apple));
    }

    @Test
    void shouldKeepDifferentTradersSeparate() {

        TraderPersistenceService persistenceService = new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader armine = new Trader(123456, "armine", "Armine", "Grigoryan", 10000);
        Trader anna = new Trader(654321, "anna", "Anna", "Smith", 5000);

        Stock apple = marketService.findStock("AAPL");
        Stock tesla = marketService.findStock("TSLA");

        armine.getPortfolio().addStock(apple, 2);
        anna.getPortfolio().addStock(tesla, 4);

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);

        Trader loadedArmine = persistenceService.loadTrader("armine", marketService);
        Trader loadedAnna = persistenceService.loadTrader("anna", marketService);

        assertNotNull(loadedArmine);
        assertNotNull(loadedAnna);

        assertEquals(2, loadedArmine.getPortfolio().getQuantity(apple));
        assertEquals(0, loadedArmine.getPortfolio().getQuantity(tesla));

        assertEquals(4, loadedAnna.getPortfolio().getQuantity(tesla));
        assertEquals(0, loadedAnna.getPortfolio().getQuantity(apple));
    }

    @Test
    void shouldLoadAllTraders() {
        TraderPersistenceService persistenceService = new TraderPersistenceService(tempDir.toString());

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        Trader armine = new Trader(123456, "armine", "Armine", "Grigoryan", 10000);

        Trader anna = new Trader(234567, "anna", "Anna", "Smith", 8000);

        Trader david = new Trader(345678, "david", "David", "Brown", 12000);

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);
        persistenceService.saveTrader(david);

        List<Trader> traders = persistenceService.loadAllTraders(marketService);

        assertEquals(3, traders.size());

        assertTrue(traders.stream().anyMatch(t -> t.getUsername().equals("armine")));

        assertTrue(traders.stream().anyMatch(t -> t.getUsername().equals("anna")));

        assertTrue(traders.stream().anyMatch(t -> t.getUsername().equals("david")));
    }

    @Test
    void shouldRejectMissingStockDuringLoading() {
        TraderPersistenceService persistenceService = new TraderPersistenceService(tempDir.toString());

        MarketService originalMarket = new MarketService();

        Stock stock = new Stock("TEST", "Test Company", 100.00);

        originalMarket.addStock(stock);

        Trader trader = new Trader(123456, "testuser", "Test", "User", 1000);

        trader.getPortfolio().addStock(stock, 2);

        persistenceService.saveTrader(trader);

        MarketService emptyMarket = new MarketService();

        assertThrows(IllegalStateException.class, () -> persistenceService.loadTrader("testuser", emptyMarket)
        );

        assertNull(emptyMarket.findStock("TEST"));
    }
}
