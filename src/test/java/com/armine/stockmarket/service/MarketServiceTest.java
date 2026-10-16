
package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MarketServiceTest {

    private MarketService marketService;
    private TraderService traderService;

    @BeforeEach
    void setUp() {
        marketService = new MarketService();
        traderService = new TraderService();
    }

    @Test
    void shouldRemoveStock() {
        Stock stock = new Stock("AAPL", "Apple", 220.50);

        marketService.addStock(stock);
        marketService.removeStock("AAPL", traderService);

        assertNull(marketService.findStock("AAPL"));
    }

    @Test
    void shouldThrowExceptionWhenRemovingStockThatDoesNotExist() {
        assertThrows(
                IllegalArgumentException.class,
                () -> marketService.removeStock("AAPL", traderService)
        );
    }

    @Test
    void shouldNotRemoveStockWhenTraderOwnsShares() {
        Stock stock = new Stock("AAPL", "Apple", 220.50);
        marketService.addStock(stock);

        Trader trader = new Trader(
                100001,
                "test_trader",
                "Test",
                "Trader",
                10000.00,
                "test-hash"
        );

        trader.addShares(stock, 5);
        traderService.addTrader(trader);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> marketService.removeStock("AAPL", traderService)
        );

        assertEquals(
                "Cannot remove stock. A trader currently owns shares.",
                exception.getMessage()
        );

        assertSame(stock, marketService.findStock("AAPL"));
        assertEquals(5, trader.getOwnedShares(stock));
    }

    // Test 4: Failed saving must undo stock addition
    @Test
    void shouldRollbackAddWhenSavingFails(@TempDir Path tempDir)
            throws IOException {

        MarketService failingMarket = createFailingMarket(tempDir);

        Stock stock = new Stock("TEST", "Test Company", 100.00);

        assertThrows(
                IllegalStateException.class,
                () -> failingMarket.addStock(stock)
        );

        assertNull(failingMarket.findStock("TEST"));
        assertTrue(failingMarket.getAllStocks().isEmpty());
    }

    // Test 5: Failed saving must restore the old price
    @Test
    void shouldRollbackPriceWhenSavingFails(@TempDir Path tempDir)
            throws IOException {

        MarketService failingMarket = createFailingMarket(tempDir);

        Stock stock = new Stock("AAPL", "Apple", 220.50);
        seedStock(failingMarket, stock);

        assertThrows(
                IllegalStateException.class,
                () -> failingMarket.updatePrice("AAPL", 300.00)
        );

        assertSame(stock, failingMarket.findStock("AAPL"));
        assertEquals(220.50, stock.getCurrentPrice(), 0.001);
    }

    // Test 6: Failed saving must restore the removed stock
    @Test
    void shouldRollbackRemoveWhenSavingFails(@TempDir Path tempDir)
            throws IOException {

        MarketService failingMarket = createFailingMarket(tempDir);

        Stock stock = new Stock("AAPL", "Apple", 220.50);
        seedStock(failingMarket, stock);

        assertThrows(
                IllegalStateException.class,
                () -> failingMarket.removeStock("AAPL", traderService)
        );

        assertSame(stock, failingMarket.findStock("AAPL"));
        assertEquals(1, failingMarket.getAllStocks().size());
    }

    // Create a storage location where writing must fail
    private MarketService createFailingMarket(Path tempDir)
            throws IOException {

        Path blockingFile = Files.createFile(
                tempDir.resolve("not-a-directory")
        );

        Path invalidMarketPath =
                blockingFile.resolve("market.txt");

        MarketPersistenceService persistenceService =
                new MarketPersistenceService(
                        invalidMarketPath.toString()
                );

        return new MarketService(persistenceService);
    }

    // Set up an existing stock without writing to disk
    private void seedStock(MarketService market, Stock stock) {
        market.beginLoading();

        try {
            market.addStock(stock);
        } finally {
            market.finishLoading();
        }
    }
}
