package com.armine.stockmarket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trader account in the stock market simulator.
 *
 * A trader has a unique ID, an available cash balance,
 * a portfolio of owned stocks, and a transaction history.
 *
 * Trader extends User and inherits personal information,
 * authentication details, and common user methods.
 *
 * The class manages account data, while TradingService
 * coordinates the buying and selling of stocks.
 */
public class Trader extends User {
    private int id;
    private double balance;
    private Portfolio portfolio;
    private List<Transaction> transactions;
    /**
     * Creates a trader with the specified account information.
     *
     * The trader starts with an empty portfolio and
     * an empty transaction history.
     *
     * @param id the trader's unique positive identifier
     * @param username the trader's username
     * @param firstName the trader's first name
     * @param surname the trader's surname
     * @param balance the initial non-negative cash balance
     * @param passwordHash the trader's stored password hash
     * @throws IllegalArgumentException if the ID is not positive,
     *         the balance is invalid, or inherited user data is invalid
     */
    public Trader(int id, String username, String firstName, String surname, double balance, String passwordHash) {
        super(firstName, surname, username, passwordHash);
        if (id <= 0)
            throw new IllegalArgumentException("Trader ID must be greater than zero.");
        if (!Double.isFinite(balance) || balance < 0)
            throw new IllegalArgumentException("Balance must be a finite, non-negative number.");

        this.id = id;
        this.balance = balance;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }
    /**
     * Creates a copy of another trader.
     *
     * The new trader has the same account information and balance.
     * The portfolio and transaction list are copied into new
     * collections, while their Stock and Transaction objects
     * remain shared.
     *
     * @param other the trader to copy
     * @throws IllegalArgumentException if other is null
     */
    public Trader(Trader other) {
        super(other);
        this.id = other.id;
        this.balance = other.balance;
        this.portfolio = new Portfolio(other.portfolio);
        this.transactions = new ArrayList<>(other.transactions);
    }
    /**
     * Returns the trader's unique identifier.
     *
     * @return the trader ID
     */
    public int getId() {
        return id;
    }
    /**
     * Returns the trader's available cash balance.
     *
     * The balance represents money available for trading
     * and does not include the market value of owned stocks.
     *
     * @return the available cash balance
     */
    public double getBalance() {
        return balance;
    }
    /**
     * Returns a copy of the trader's portfolio.
     *
     * Changes to the returned portfolio's holdings do not
     * modify the trader's internal portfolio.
     * Stock objects remain shared between the portfolios.
     *
     * @return a copy of the trader's current portfolio
     */
    public Portfolio getPortfolio() {
        return new Portfolio(portfolio);
    }
    /**
     * Returns the number of shares the trader owns
     * of a particular stock.
     *
     * @param stock the stock to look up
     * @return the quantity owned, or zero if not held
     * @throws IllegalArgumentException if stock is null
     */
    public int getOwnedShares(Stock stock) {
        return portfolio.getQuantity(stock);
    }
    /**
     * Adds shares directly to the trader's portfolio.
     *
     * This method updates only the holdings. It does not
     * deduct money or create a transaction record.
     *
     * @param stock the stock whose shares are added
     * @param quantity the positive number of shares to add
     * @throws IllegalArgumentException if stock is null
     *         or quantity is not positive
     */
    public void addShares(Stock stock, int quantity) {
        portfolio.addStock(stock, quantity);
    }
    /**
     * Removes shares directly from the trader's portfolio.
     *
     * This method updates only the holdings. It does not
     * increase the cash balance or create a transaction record.
     *
     * @param stock the stock whose shares are removed
     * @param quantity the positive number of shares to remove
     * @throws IllegalArgumentException if stock is null,
     *         quantity is invalid, the stock is not held,
     *         or the trader owns insufficient shares
     */
    public void removeShares(Stock stock, int quantity) {
        portfolio.removeStock(stock, quantity);
    }
    /**
     * Returns a copy of the trader's transaction list.
     *
     * Modifying the returned list does not change the
     * trader's transaction history. The Transaction objects
     * themselves are not copied.
     *
     * @return a list of the trader's recorded transactions
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    /**
     * Adds a transaction to the trader's history.
     *
     * @param transaction the transaction to record
     * @throws IllegalArgumentException if transaction is null
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        transactions.add(transaction);
    }
    /**
     * Deposits money into the trader's account.
     *
     * The amount must be finite and positive, and the resulting
     * balance must remain finite.
     *
     * @param amount the amount of money to add
     * @throws IllegalArgumentException if the amount is invalid
     *         or the resulting balance is too large
     */
    public void addMoney(double amount) {
        validateAmount(amount);
        double newBalance = balance + amount;
        if (!Double.isFinite(newBalance)) throw new IllegalArgumentException("Resulting balance is too large.");
        balance = newBalance;
    }
    /**
     * Deducts money from the trader's account.
     *
     * The amount must be finite, positive, and no greater
     * than the trader's available balance.
     *
     * @param amount the amount of money to subtract
     * @throws IllegalArgumentException if the amount is invalid
     *         or exceeds the available balance
     */
    public void subtractMoney(double amount) {
        validateAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Not enough money.");
        balance -= amount;
    }
    /**
     * Validates an amount used in balance operations.
     *
     * @param amount the amount to validate
     * @throws IllegalArgumentException if the amount
     *         is not finite or is not positive
     */
    private void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a finite, positive number.");
        }
    }
    /**
     * Returns a readable summary of the trader account.
     *
     * @return the trader ID, username, full name,
     *         and available cash balance
     */
    @Override
    public String toString() {
        return "ID: " + id
                + " | Username: " + getUsername()
                + " | Name: " + getFullName()
                + " | Balance: $" + balance;
    }
}
