package com.armine.stockmarket.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void testCreateTransaction() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        Transaction transaction = new Transaction(
                stock,
                3,
                220.50,
                TransactionType.BUY
        );

        assertEquals(stock, transaction.getStock());
        assertEquals(3, transaction.getQuantity());
        assertEquals(220.50, transaction.getPricePerShare());
        assertEquals(
                TransactionType.BUY,
                transaction.getType()
        );
    }

    @Test
    public void testTransactionTotalAmount() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        Transaction transaction = new Transaction(
                stock,
                3,
                220.50,
                TransactionType.BUY
        );

        assertEquals(
                661.50,
                transaction.getTotalAmount()
        );
    }

    @Test
    public void testTransactionDateTime() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        LocalDateTime dateTime =
                LocalDateTime.of(
                        2026,
                        9,
                        26,
                        22,
                        30
                );

        Transaction transaction =
                new Transaction(
                        stock,
                        2,
                        220.50,
                        TransactionType.BUY,
                        dateTime
                );

        assertEquals(
                dateTime,
                transaction.getDateTime()
        );
    }

    @Test
    public void testCannotCreateTransactionWithZeroQuantity() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        stock,
                        0,
                        220.50,
                        TransactionType.BUY
                )
        );
    }

    @Test
    public void testCannotCreateTransactionWithNullType() {

        Stock stock = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        stock,
                        2,
                        220.50,
                        null
                )
        );
    }
}