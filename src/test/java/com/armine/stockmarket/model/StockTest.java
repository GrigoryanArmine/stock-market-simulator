package com.armine.stockmarket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StockTest {

    @Test
    public void testCreateStock() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple", stock.getCompanyName());
        assertEquals(220.50, stock.getCurrentPrice());
    }

    @Test
    public void testSymbolBecomesUppercase() {

        Stock stock = new Stock(
                "aapl",
                "Apple",
                220.50
        );

        assertEquals(
                "AAPL",
                stock.getSymbol()
        );
    }

    @Test
    public void testUpdatePrice() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        stock.updatePrice(250.00);

        assertEquals(
                250.00,
                stock.getCurrentPrice()
        );
    }

    @Test
    public void testCannotCreateStockWithZeroPrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Stock(
                        "AAPL",
                        "Apple",
                        0
                )
        );
    }

    @Test
    public void testCannotCreateStockWithNegativePrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Stock(
                        "AAPL",
                        "Apple",
                        -100
                )
        );
    }

    @Test
    public void testStocksWithSameSymbolAreEqual() {

        Stock apple1 = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        Stock apple2 = new Stock(
                "AAPL",
                "Apple Inc.",
                250.00
        );

        assertEquals(
                apple1,
                apple2
        );
    }

    @Test
    public void testStocksWithDifferentSymbolsAreNotEqual() {

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

        assertNotEquals(
                apple,
                microsoft
        );
    }
}