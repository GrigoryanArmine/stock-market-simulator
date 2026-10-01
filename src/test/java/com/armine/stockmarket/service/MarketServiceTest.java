package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketServiceTest {

    @Test
    void shouldRemoveStock() {
        MarketService marketService = new MarketService();
        Stock stock = new Stock("AAPL", "Apple", 220.50);

        marketService.addStock(stock);
        marketService.removeStock("AAPL");

        assertNull(marketService.findStock("AAPL"));
    }

    @Test
    void shouldThrowExceptionWhenRemovingStockThatDoesNotExist() {
        MarketService marketService = new MarketService();

        assertThrows(
                IllegalArgumentException.class,
                () -> marketService.removeStock("AAPL")
        );
    }
}