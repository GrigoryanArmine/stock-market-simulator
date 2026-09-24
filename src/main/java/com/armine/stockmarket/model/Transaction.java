package com.armine.stockmarket.model;

import java.time.LocalDateTime;

public class Transaction {

    private final Stock stock;
    private final int quantity;
    private final double pricePerShare;
    private final TransactionType type;
    private final LocalDateTime dateTime;

    public Transaction(
            Stock stock,
            int quantity,
            double pricePerShare,
            TransactionType type
    ) {

        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        if (pricePerShare <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero."
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "Transaction type cannot be null."
            );
        }

        this.stock = stock;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.type = type;
        this.dateTime = LocalDateTime.now();
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getTotalAmount() {
        return quantity * pricePerShare;
    }

    @Override
    public String toString() {
        return type + " "
                + quantity + " shares of "
                + stock.getSymbol()
                + " at $"
                + pricePerShare
                + " each"
                + " | Total: $"
                + getTotalAmount()
                + " | "
                + dateTime;
    }
}
