package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.*;
import com.armine.stockmarket.model.*;
/**
 * Provides the business logic for buying and selling stocks.
 *
 * This service validates trading requests, calculates transaction
 * amounts, updates trader balances and portfolio holdings,
 * and records successful transactions.
 *
 * Buying and selling operations are performed using the stock's
 * current market price. Each completed trade is recorded with
 * its execution price and transaction type.
 *
 * This service does not directly save transactions or trader
 * information to files. Data persistence is handled separately.
 */
public class TradingService {

    /**
     * Creates a service for processing stock trades.
     */
    public TradingService() {
    }
    /**
     * Validates whether a trader can purchase the requested shares.
     *
     * Checks the input values, total transaction amount,
     * available account balance, and maximum share quantity.
     *
     * This method does not modify the trader's account.
     *
     * @param trader the trader attempting to buy shares
     * @param stock the stock to purchase
     * @param quantity the number of shares to purchase
     * @throws InsufficientFundsException if the trader does
     *         not have enough money for the purchase
     * @throws IllegalArgumentException if an input is invalid,
     *         the transaction amount is invalid, or the resulting
     *         share quantity would exceed the integer limit
     */
    public void validateBuy(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateInputs(trader, stock, quantity);
        double totalPrice = calculateTotalPrice(stock.getCurrentPrice(), quantity);

        if (totalPrice > trader.getBalance())
            throw new InsufficientFundsException("Trader does not have enough money.");

        int ownedShares = trader.getOwnedShares(stock);
        if (quantity > Integer.MAX_VALUE - ownedShares)
            throw new IllegalArgumentException("Maximum share quantity exceeded.");
    }
    /**
     * Validates whether a trader can sell the requested shares.
     *
     * Checks the input values, available share quantity,
     * transaction amount, and resulting account balance.
     *
     * This method does not modify the trader's account.
     *
     * @param trader the trader attempting to sell shares
     * @param stock the stock to sell
     * @param quantity the number of shares to sell
     * @throws InsufficientSharesException if the trader
     *         does not own enough shares
     * @throws IllegalArgumentException if an input is invalid,
     *         the transaction amount is invalid, or the
     *         resulting account balance is not finite
     */
    public void validateSell(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateInputs(trader, stock, quantity);
        if (quantity > trader.getOwnedShares(stock))
            throw new InsufficientSharesException("Trader does not have enough shares.");

        double totalPrice = calculateTotalPrice(stock.getCurrentPrice(), quantity);

        if (!Double.isFinite(trader.getBalance() + totalPrice))
            throw new IllegalArgumentException("Resulting balance is too large.");
    }
    /**
     * Executes a stock purchase for the specified trader.
     *
     * First validates the purchase request. If validation
     * succeeds, the method deducts the total purchase amount
     * from the trader's balance, adds shares to the portfolio,
     * and records a BUY transaction.
     *
     * The transaction stores the stock price at the time
     * of execution.
     *
     * @param trader the trader purchasing the stock
     * @param stock the stock to purchase
     * @param quantity the number of shares to purchase
     * @throws InsufficientFundsException if the trader
     *         does not have enough money
     * @throws IllegalArgumentException if the purchase
     *         fails input or transaction validation
     */
    public void buyStock(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateBuy(trader, stock, quantity);

        double pricePerShare = stock.getCurrentPrice();
        double totalPrice = calculateTotalPrice(pricePerShare, quantity);

        Transaction transaction = new Transaction(stock, quantity, pricePerShare, TransactionType.BUY);

        trader.subtractMoney(totalPrice);
        trader.addShares(stock, quantity);
        trader.addTransaction(transaction);
    }
    /**
     * Executes a stock sale for the specified trader.
     *
     * First validates the sale request. If validation
     * succeeds, the method removes the requested shares
     * from the portfolio, adds the sale proceeds to the
     * trader's balance, and records a SELL transaction.
     *
     * The transaction stores the stock price at the time
     * of execution.
     *
     * @param trader the trader selling the stock
     * @param stock the stock to sell
     * @param quantity the number of shares to sell
     * @throws InsufficientSharesException if the trader
     *         does not own enough shares
     * @throws IllegalArgumentException if the sale
     *         fails input or transaction validation
     */
    public void sellStock(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateSell(trader, stock, quantity);

        double pricePerShare = stock.getCurrentPrice();
        double totalPrice = calculateTotalPrice(pricePerShare, quantity);

        Transaction transaction = new Transaction(stock, quantity, pricePerShare, TransactionType.SELL);

        trader.removeShares(stock, quantity);
        trader.addMoney(totalPrice);
        trader.addTransaction(transaction);
    }
    /**
     * Validates the basic inputs required for a trading operation.
     *
     * @param trader the trader involved in the operation
     * @param stock the stock being traded
     * @param quantity the number of shares being traded
     * @throws IllegalArgumentException if trader or stock
     *         is null, or quantity is not positive
     */
    private void validateInputs(Trader trader, Stock stock, int quantity) {
        if (trader == null) throw new IllegalArgumentException("Trader cannot be null.");
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }
    /**
     * Calculates the total monetary amount of a transaction.
     *
     * Multiplies the price per share by the requested
     * quantity and verifies that the result is finite
     * and positive.
     *
     * @param pricePerShare the price of one share
     * @param quantity the number of shares
     * @return the total transaction amount
     * @throws IllegalArgumentException if the calculated
     *         amount is not finite or is not positive
     */
    private double calculateTotalPrice(double pricePerShare, int quantity) {
        double totalPrice = pricePerShare * quantity;
        if (!Double.isFinite(totalPrice) || totalPrice <= 0) throw new IllegalArgumentException("Invalid transaction amount.");
        return totalPrice;
    }
}
