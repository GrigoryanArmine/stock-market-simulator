package com.armine.stockmarket.exception;
/**
 * Exception thrown when a trader does not have enough
 * available funds to complete a stock purchase.
 *
 * This checked exception is used by TradingService
 * to indicate that a purchase cannot be completed
 * because its total cost exceeds the trader's balance.
 */
public class InsufficientFundsException extends Exception {
    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message the explanation of why the
     *        stock purchase could not be completed
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}
