package com.armine.stockmarket.exception;
/**
 * Exception thrown when a trader attempts to sell
 * more shares than they currently own.
 *
 * This checked exception is used by TradingService
 * to indicate that a stock sale cannot be completed
 * because the trader has insufficient shares.
 */
public class InsufficientSharesException extends Exception {
    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message the explanation of why the
     *        stock sale could not be completed
     */
    public InsufficientSharesException(String message) {
        super(message);
    }
}
