package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TradingServiceTest {

    private TradingService tradingService;
    private Trader trader;
    private Stock stock;

    @BeforeEach
    void setUp() {
        tradingService = new TradingService();

        trader = new Trader(
                1,
                "test_trader",
                "Test",
                "Trader",
                1000.00,
                "test-hash"
        );

        stock = new Stock("AAPL", "Apple", 100.00);
    }

    @Test
    void shouldBuyStockSuccessfully() throws InsufficientFundsException {
        tradingService.buyStock(trader, stock, 3);

        assertEquals(700.00, trader.getBalance(), 0.001);
        assertEquals(3, trader.getPortfolio().getQuantity(stock));
        assertEquals(1, trader.getTransactions().size());
    }

    @Test
    void shouldRejectBuyWhenInsufficientFunds() {
        assertThrows(
                InsufficientFundsException.class,
                () -> tradingService.buyStock(trader, stock, 11)
        );

        assertEquals(1000.00, trader.getBalance(), 0.001);
        assertEquals(0, trader.getPortfolio().getQuantity(stock));
        assertTrue(trader.getTransactions().isEmpty());
    }

    @Test
    void shouldSellStockSuccessfully() throws InsufficientSharesException {
        trader.getPortfolio().addStock(stock, 5);

        tradingService.sellStock(trader, stock, 2);

        assertEquals(1200.00, trader.getBalance(), 0.001);
        assertEquals(3, trader.getPortfolio().getQuantity(stock));
        assertEquals(1, trader.getTransactions().size());
    }

    @Test
    void shouldRejectSellWhenInsufficientShares() {
        trader.getPortfolio().addStock(stock, 2);

        assertThrows(
                InsufficientSharesException.class,
                () -> tradingService.sellStock(trader, stock, 5)
        );

        assertEquals(1000.00, trader.getBalance(), 0.001);
        assertEquals(2, trader.getPortfolio().getQuantity(stock));
        assertTrue(trader.getTransactions().isEmpty());
    }

    @Test
    void shouldRejectZeroOrNegativeQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> tradingService.buyStock(trader, stock, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> tradingService.sellStock(trader, stock, -1)
        );
    }

    @Test
    void shouldAllowBuyingWithExactBalance() throws InsufficientFundsException {
        tradingService.buyStock(trader, stock, 10);

        assertEquals(0.00, trader.getBalance(), 0.001);
        assertEquals(10, trader.getPortfolio().getQuantity(stock));
    }
}