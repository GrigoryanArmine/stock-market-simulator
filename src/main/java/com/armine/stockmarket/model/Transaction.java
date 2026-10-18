package com.armine.stockmarket.model;

import java.time.LocalDateTime;
/**
 * Represents a stock trading transaction.
 *
 * A transaction records the stock involved, the number of shares,
 * the execution price per share, the transaction type, and
 * the date and time of the operation.
 *
 * The execution price is stored separately from the stock's
 * current market price, preserving the historical trade value
 * when market prices change.
 */
public class Transaction {
    private Stock stock;
    private int quantity;
    private double pricePerShare;
    private TransactionType type;
    private LocalDateTime dateTime;
    /**
     * Creates a transaction using the current date and time.
     *
     * @param stock the stock involved in the transaction
     * @param quantity the positive number of shares traded
     * @param pricePerShare the execution price per share
     * @param type the transaction type (BUY or SELL)
     * @throws IllegalArgumentException if any required value
     *         is invalid, or the total transaction amount is too large
     */
    public Transaction(Stock stock, int quantity, double pricePerShare, TransactionType type) {
        this(stock, quantity, pricePerShare, type, LocalDateTime.now());
    }
    /**
     * Creates a transaction with a specified date and time.
     *
     * @param stock the stock involved in the transaction
     * @param quantity the positive number of shares traded
     * @param pricePerShare the finite, positive execution price per share
     * @param type the transaction type (BUY or SELL)
     * @param dateTime the date and time of the transaction
     * @throws IllegalArgumentException if stock, type, or dateTime
     *         is null; quantity or price is invalid; or the total
     *         transaction amount is not finite
     */
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
    /**
     * Creates a copy of an existing transaction.
     *
     * The transaction data is copied, but the Stock object
     * is shared with the original transaction.
     *
     * @param other the transaction to copy
     * @throws IllegalArgumentException if other is null
     */
    public Transaction(Transaction other) {
        if (other == null) throw new IllegalArgumentException("Transaction cannot be null.");

        this.stock = other.stock;
        this.quantity = other.quantity;
        this.pricePerShare = other.pricePerShare;
        this.type = other.type;
        this.dateTime = other.dateTime;
    }
    /**
     * Returns the stock involved in the transaction.
     *
     * The returned Stock is the referenced object, not a copy.
     *
     * @return the stock associated with the transaction
     */
    public Stock getStock() {
        return stock;
    }
    /**
     * Returns the number of shares traded.
     *
     * @return the transaction quantity
     */
    public int getQuantity() {
        return quantity;
    }
    /**
     * Returns the historical execution price per share.
     *
     * This value does not change when the stock's current
     * market price changes.
     *
     * @return the execution price per share
     */
    public double getPricePerShare() {
        return pricePerShare;
    }
    /**
     * Returns the type of transaction.
     *
     * @return BUY or SELL
     */
    public TransactionType getType() {
        return type;
    }
    /**
     * Returns the transaction date and time.
     *
     * @return the date and time of the transaction
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    /**
     * Calculates the total monetary amount of the transaction.
     *
     * The amount is calculated using the historical execution
     * price rather than the stock's current market price.
     *
     * @return the quantity multiplied by the execution price
     */
    public double getTotalAmount() {
        return quantity * pricePerShare;
    }
    /**
     * Returns a readable summary of the transaction.
     *
     * The summary includes the transaction type, share quantity,
     * stock symbol, execution price, total amount, and timestamp.
     *
     * @return a formatted description of the transaction
     */
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