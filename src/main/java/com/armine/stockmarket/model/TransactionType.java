package com.armine.stockmarket.model;
/**
 * Represents the type of stock trading transaction.
 *
 * This enum distinguishes between buying and selling shares.
 * It is used by the Transaction class to identify the operation
 * performed and record it in the trader's transaction history.
 */
public enum TransactionType {
    /**
     * Represents the purchase of stock shares.
     */
    BUY,
    /**
     * Represents the sale of stock shares.
     */
    SELL
}
