package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TraderPersistenceServiceTest {

    @Test
    void shouldSaveAndLoadTrader() {

        String testDirectory = "test-traders";

        TraderPersistenceService persistenceService =
                new TraderPersistenceService(testDirectory);

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        Trader trader =
                new Trader("Armine", 10000);

        Stock apple =
                marketService.findStock("AAPL");

        trader.getPortfolio()
                .addStock(apple, 3);

        persistenceService.saveTrader(trader);

        Trader loadedTrader =
                persistenceService.loadTrader(
                        "Armine",
                        marketService
                );

        assertNotNull(loadedTrader);
        assertEquals(
                "Armine",
                loadedTrader.getName()
        );
        assertEquals(
                10000,
                loadedTrader.getBalance()
        );
        assertEquals(
                3,
                loadedTrader.getPortfolio()
                        .getQuantity(apple)
        );

        deleteDirectory(testDirectory);
    }

    @Test
    void shouldKeepDifferentTradersSeparate() {

        String testDirectory = "test-traders";

        TraderPersistenceService persistenceService =
                new TraderPersistenceService(testDirectory);

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        Trader armine =
                new Trader("Armine", 10000);

        Trader anna =
                new Trader("Anna", 5000);

        Stock apple =
                marketService.findStock("AAPL");

        Stock tesla =
                marketService.findStock("TSLA");

        armine.getPortfolio()
                .addStock(apple, 2);

        anna.getPortfolio()
                .addStock(tesla, 4);

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);

        Trader loadedArmine =
                persistenceService.loadTrader(
                        "Armine",
                        marketService
                );

        Trader loadedAnna =
                persistenceService.loadTrader(
                        "Anna",
                        marketService
                );

        assertNotNull(loadedArmine);
        assertNotNull(loadedAnna);

        assertEquals(
                2,
                loadedArmine.getPortfolio()
                        .getQuantity(apple)
        );

        assertEquals(
                0,
                loadedArmine.getPortfolio()
                        .getQuantity(tesla)
        );

        assertEquals(
                4,
                loadedAnna.getPortfolio()
                        .getQuantity(tesla)
        );

        assertEquals(
                0,
                loadedAnna.getPortfolio()
                        .getQuantity(apple)
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
    @Test
    void shouldLoadAllTraders() {

        String testDirectory = "test-traders-all";

        TraderPersistenceService persistenceService =
                new TraderPersistenceService(testDirectory);

        MarketService marketService =
                new MarketService();

        marketService.initializeMarket();

        Trader armine =
                new Trader("Armine", 10000);

        Trader anna =
                new Trader("Anna", 8000);

        Trader david =
                new Trader("David", 12000);

        persistenceService.saveTrader(armine);
        persistenceService.saveTrader(anna);
        persistenceService.saveTrader(david);

        List<Trader> traders =
                persistenceService.loadAllTraders(
                        marketService
                );

        assertEquals(3, traders.size());

        assertTrue(
                traders.stream()
                        .anyMatch(trader ->
                                trader.getName()
                                        .equals("Armine"))
        );

        assertTrue(
                traders.stream()
                        .anyMatch(trader ->
                                trader.getName()
                                        .equals("Anna"))
        );

        assertTrue(
                traders.stream()
                        .anyMatch(trader ->
                                trader.getName()
                                        .equals("David"))
        );

        deleteDirectory(testDirectory);
    }
}