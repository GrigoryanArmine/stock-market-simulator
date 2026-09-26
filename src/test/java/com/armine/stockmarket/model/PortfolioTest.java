package com.armine.stockmarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {

    @Test
    public void testAddStock() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        portfolio.addStock(stock, 3);

        assertEquals(
                3,
                portfolio.getQuantity(stock)
        );
    }

    @Test
    public void testAddSameStockTwice() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        portfolio.addStock(stock, 2);
        portfolio.addStock(stock, 3);

        assertEquals(
                5,
                portfolio.getQuantity(stock)
        );
    }

    @Test
    public void testRemoveStock() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        portfolio.addStock(stock, 5);
        portfolio.removeStock(stock, 2);

        assertEquals(
                3,
                portfolio.getQuantity(stock)
        );
    }

    @Test
    public void testRemoveAllShares() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        portfolio.addStock(stock, 3);
        portfolio.removeStock(stock, 3);

        assertEquals(
                0,
                portfolio.getQuantity(stock)
        );

        assertTrue(
                portfolio.isEmpty()
        );
    }

    @Test
    public void testCannotRemoveMoreSharesThanOwned() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        portfolio.addStock(stock, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> portfolio.removeStock(stock, 5)
        );
    }

    @Test
    public void testTotalValue() {

        Portfolio portfolio = new Portfolio();

        Stock apple = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        Stock microsoft = new Stock(
                "MSFT",
                "Microsoft",
                510.00
        );

        portfolio.addStock(apple, 2);
        portfolio.addStock(microsoft, 3);

        double expectedValue =
                (220.50 * 2) + (510.00 * 3);

        assertEquals(
                expectedValue,
                portfolio.getTotalValue()
        );
    }

    @Test
    public void testCannotAddNegativeQuantity() {

        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> portfolio.addStock(stock, -1)
        );
    }
}