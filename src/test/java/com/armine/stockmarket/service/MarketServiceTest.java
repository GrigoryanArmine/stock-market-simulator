package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        // Create a stock and add it to the market
        Stock stock = new Stock("AAPL", "Apple", 220.50);
        marketService.addStock(stock);

        // Create a trader
        Trader trader = new Trader(
                100001,
                "test_trader",
                "Test",
                "Trader",
                10000.00,
                "test-hash"
        );

        // Give the trader 5 shares of AAPL
        trader.getPortfolio().addStock(stock, 5);

        // Register the trader
        traderService.addTrader(trader);

        // Attempt to remove AAPL
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> marketService.removeStock("AAPL", traderService)
        );

        // Verify the reason removal was rejected
        assertEquals(
                "Cannot remove stock. A trader currently owns shares.",
                exception.getMessage()
        );

        // Verify AAPL is still available in the market
        assertSame(stock, marketService.findStock("AAPL"));

        // Verify the trader still owns 5 shares
        assertEquals(5, trader.getPortfolio().getQuantity(stock));
    }
}
