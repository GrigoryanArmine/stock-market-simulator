package com.armine.stockmarket.model;

import java.time.LocalDateTime;

public class Transaction {
    private Stock stock;
    private int quantity;
    private double pricePerShare;
    private TransactionType type;
    private LocalDateTime dateTime;

    public Transaction(Stock stock, int quantity, double pricePerShare, TransactionType type) {
        this(stock, quantity, pricePerShare, type, LocalDateTime.now());
    }

    public Transaction(Stock stock, int quantity, double pricePerShare, TransactionType type, LocalDateTime dateTime) {
        if (stock == null)
            throw new IllegalArgumentException("Stock cannot be null.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        if (!Double.isFinite(pricePerShare) || pricePerShare <= 0)
            throw new IllegalArgumentException("Price must be a finite, positive number.");
        if (!Double.isFinite(pricePerShare * quantity))
            throw new IllegalArgumentException("Transaction amount is too large.");
        if (type == null)
            throw new IllegalArgumentException("Transaction type cannot be null.");
        if (dateTime == null)
            throw new IllegalArgumentException("Date and time cannot be null.");

        this.stock = stock;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.type = type;
        this.dateTime = dateTime;
    }

    public Transaction(Transaction other) {
        if (other == null) throw new IllegalArgumentException("Transaction cannot be null.");

        this.stock = other.stock;
        this.quantity = other.quantity;
        this.pricePerShare = other.pricePerShare;
        this.type = other.type;
        this.dateTime = other.dateTime;
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