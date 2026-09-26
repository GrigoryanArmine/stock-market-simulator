package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TradingServiceTest {

    @Test
    public void testBuyStock() throws InsufficientFundsException {

        Trader trader = new Trader("Armine", 10000);

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        tradingService.buyStock(
                trader,
                stock,
                2
        );

        assertEquals(
                9559.0,
                trader.getBalance()
        );

        assertEquals(
                2,
                trader.getPortfolio().getQuantity(stock)
        );

        assertEquals(
                1,
                trader.getTransactions().size()
        );
    }

    @Test
    public void testSellStock()
            throws InsufficientFundsException,
            InsufficientSharesException {

        Trader trader = new Trader("Armine", 10000);

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        tradingService.buyStock(
                trader,
                stock,
                3
        );

        tradingService.sellStock(
                trader,
                stock,
                1
        );

        assertEquals(
                9559.0,
                trader.getBalance()
        );

        assertEquals(
                2,
                trader.getPortfolio().getQuantity(stock)
        );

        assertEquals(
                2,
                trader.getTransactions().size()
        );
    }

    @Test
    public void testCannotBuyWithoutEnoughMoney() {

        Trader trader = new Trader("Armine", 100);

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        assertThrows(
                InsufficientFundsException.class,
                () -> tradingService.buyStock(
                        trader,
                        stock,
                        1
                )
        );
    }

    @Test
    public void testCannotSellMoreSharesThanOwned()
            throws InsufficientFundsException {

        Trader trader = new Trader("Armine", 10000);

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        tradingService.buyStock(
                trader,
                stock,
                2
        );

        assertThrows(
                InsufficientSharesException.class,
                () -> tradingService.sellStock(
                        trader,
                        stock,
                        5
                )
        );
    }

    @Test
    public void testCannotBuyNegativeQuantity()
            throws InsufficientFundsException {

        Trader trader = new Trader("Armine", 10000);

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        TradingService tradingService =
                new TradingService();

        assertThrows(
                IllegalArgumentException.class,
                () -> tradingService.buyStock(
                        trader,
                        stock,
                        -1
                )
        );
    }
}