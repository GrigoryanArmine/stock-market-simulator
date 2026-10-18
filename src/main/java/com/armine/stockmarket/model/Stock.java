package com.armine.stockmarket.model;

import java.util.Locale;
/**
 * Represents a stock available in the trading simulator.
 *
 * Each stock has a unique symbol, a company name, and a current
 * market price. Stocks are identified by their normalized symbols.
 */
public class Stock {
    private String symbol;
    private String companyName;
    private double currentPrice;
    /**
     * Creates a stock with the specified information.
     *
     * The symbol is trimmed and converted to uppercase,
     * and the company name is trimmed.
     *
     * @param symbol the stock's unique trading symbol
     * @param companyName the name of the company
     * @param currentPrice the initial market price
     * @throws IllegalArgumentException if the symbol or company
     *         name is empty, or the price is not finite and positive
     */
    public Stock(String symbol, String companyName, double currentPrice) {
        if (symbol == null || symbol.isBlank())
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        if (companyName == null || companyName.isBlank())
            throw new IllegalArgumentException("Company name cannot be empty.");

        validatePrice(currentPrice);
        this.symbol = symbol.trim().toUpperCase(Locale.ROOT);
        this.companyName = companyName.trim();
        this.currentPrice = currentPrice;
    }
    /**
     * Creates a copy of another stock.
     *
     * @param other the stock to copy
     * @throws IllegalArgumentException if other is null
     */
    public Stock(Stock other) {
        if (other == null) throw new IllegalArgumentException("Stock cannot be null.");

        this.symbol = other.symbol;
        this.companyName = other.companyName;
        this.currentPrice = other.currentPrice;
    }
    /**
     * Returns the normalized stock symbol.
     *
     * @return the uppercase stock symbol
     */
    public String getSymbol() {
        return symbol;
    }
    /**
     * Returns the company name.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }
    /**
     * Returns the current market price.
     *
     * @return the current price per share
     */
    public double getCurrentPrice() {
        return currentPrice;
    }
    /**
     * Updates the stock's current market price.
     *
     * @param newPrice the new price per share
     * @throws IllegalArgumentException if the new price
     *         is not finite or is less than or equal to zero
     */
    public void updatePrice(double newPrice) {
        validatePrice(newPrice);
        this.currentPrice = newPrice;
    }
    /**
     * Validates whether a stock price is finite and positive.
     *
     * @param price the price to validate
     * @throws IllegalArgumentException if the price is NaN,
     *         infinite, zero, or negative
     */
    public static void validatePrice(double price) {
        if (!Double.isFinite(price)) throw new IllegalArgumentException("Please enter a valid numeric price.");
        if (price <= 0) throw new IllegalArgumentException("Stock price must be greater than zero.");
    }
    /**
     * Compares this stock with another object.
     *
     * Two stocks are considered equal when their symbols match,
     * regardless of their company names or current prices.
     *
     * @param obj the object to compare
     * @return true if both objects represent the same stock symbol
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Stock stock))
            return false;
        return symbol.equals(stock.symbol);
    }
    /**
     * Returns a hash code based on the stock symbol.
     *
     * @return the symbol's hash code
     */
    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
    /**
     * Returns a readable representation of the stock.
     *
     * @return the symbol, company name, and current market price
     */
    @Override
    public String toString() {
        return symbol + " - " + companyName + " ($" + currentPrice + ")";
    }
}
