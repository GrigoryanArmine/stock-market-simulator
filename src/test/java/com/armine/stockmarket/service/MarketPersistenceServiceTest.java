package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MarketPersistenceServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadStocks() {
        String fileName = tempDir.resolve("market.txt").toString();
        MarketPersistenceService persistence = new MarketPersistenceService(fileName);

        MarketService market = new MarketService(persistence);
        market.addStock(new Stock("AAPL", "Apple", 220.50));

        MarketService loadedMarket = new MarketService(persistence);
        persistence.loadMarket(loadedMarket);

        assertNotNull(loadedMarket.findStock("AAPL"));
        assertEquals(220.50, loadedMarket.findStock("AAPL").getCurrentPrice(), 0.001);
    }

    @Test
    void shouldPersistPriceUpdate() {
        String fileName = tempDir.resolve("market.txt").toString();

        MarketPersistenceService persistence = new MarketPersistenceService(fileName);

        MarketService market = new MarketService(persistence);
        market.addStock(new Stock("AAPL", "Apple", 220.50));
        market.updatePrice("AAPL", 250.00);

        MarketService loadedMarket = new MarketService(persistence);
        persistence.loadMarket(loadedMarket);

        assertEquals(250.00, loadedMarket.findStock("AAPL").getCurrentPrice(), 0.001);
    }

    @Test
    void shouldPersistStockRemoval() {
        String fileName = tempDir.resolve("market.txt").toString();

        MarketPersistenceService persistence = new MarketPersistenceService(fileName);

        MarketService market = new MarketService(persistence);
        market.addStock(new Stock("TEST", "Test Company", 100.00));

        market.removeStock("TEST", new TraderService());

        MarketService loadedMarket = new MarketService(persistence);
        persistence.loadMarket(loadedMarket);

        assertNull(loadedMarket.findStock("TEST"));
        assertTrue(loadedMarket.getAllStocks().isEmpty());
    }
}
