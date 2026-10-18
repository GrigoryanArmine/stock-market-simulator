package com.armine.stockmarket.model;

import java.util.HashMap;
import java.util.Map;
/**
 * Represents a trader's stock portfolio.
 *
 * A portfolio maintains the stocks owned by a trader and the number
 * of shares held for each stock.
 *
 * It supports adding and removing shares, checking quantities,
 * and calculating the total market value of the holdings.
 *
 * Portfolio copies contain a separate holdings map, but share
 * references to the same Stock objects.
 */
public class Portfolio {
    private Map<Stock, Integer> holdings;
    /**
     * Creates an empty portfolio.
     */
    public Portfolio() {
        holdings = new HashMap<>();
    }
    /**
     * Creates a copy of an existing portfolio.
     *
     * The holdings map is copied, so changes to the share quantities
     * in one portfolio do not affect the other. The Stock objects
     * themselves are shared between the portfolios.
     *
     * @param other the portfolio to copy
     * @throws IllegalArgumentException if other is null
     */
    public Portfolio(Portfolio other) {
        if (other == null) throw new IllegalArgumentException("Portfolio cannot be null.");
        holdings = new HashMap<>(other.holdings);
    }
    /**
     * Returns a copy of the portfolio's holdings map.
     *
     * Modifying the returned map does not change the portfolio's
     * internal holdings. However, the Stock objects in the map
     * are not copied.
     *
     * @return a map containing stocks and their owned quantities
     */
    public Map<Stock, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }
    /**
     * Adds shares of a stock to the portfolio.
     *
     * If the stock is already owned, the specified quantity is
     * added to its existing quantity. Otherwise, a new holding
     * is created.
     *
     * @param stock the stock to add
     * @param quantity the number of shares to add
     * @throws IllegalArgumentException if stock is null
     *         or quantity is not positive
     */
    public void addStock(Stock stock, int quantity) {
        validateStockAndQuantity(stock, quantity);
        int currentQuantity = getQuantity(stock);
        holdings.put(stock, currentQuantity + quantity);
    }
    /**
     * Removes shares of a stock from the portfolio.
     *
     * If all owned shares are removed, the stock is removed
     * entirely from the holdings map.
     *
     * @param stock the stock whose shares should be removed
     * @param quantity the number of shares to remove
     * @throws IllegalArgumentException if stock is null,
     *         quantity is not positive, the stock is not owned,
     *         or the requested quantity exceeds the shares owned
     */
    public void removeStock(Stock stock, int quantity) {
        validateStockAndQuantity(stock, quantity);
        if (!holdings.containsKey(stock)) throw new IllegalArgumentException("Stock is not in the portfolio.");

        int currentQuantity = holdings.get(stock);
        if (quantity > currentQuantity) throw new IllegalArgumentException("You do not have enough shares.");

        if (quantity == currentQuantity) {
            holdings.remove(stock);
        } else {
            holdings.put(stock, currentQuantity - quantity);
        }
    }
    /**
     * Returns the number of shares owned for a given stock.
     *
     * @param stock the stock to look up
     * @return the number of shares owned, or zero if not held
     * @throws IllegalArgumentException if stock is null
     */
    public int getQuantity(Stock stock) {
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        return holdings.getOrDefault(stock, 0);
    }
    /**
     * Calculates the total current market value of the portfolio.
     *
     * Each holding's value is calculated by multiplying its
     * quantity by the stock's current market price.
     *
     * Because the calculation uses current stock prices,
     * the portfolio value can change when market prices change.
     *
     * @return the total market value of all holdings,
     *         or zero if the portfolio is empty
     */
    public double getTotalValue() {
        double totalValue = 0;
        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);
            totalValue += stock.getCurrentPrice() * quantity;
        }
        return totalValue;
    }
    /**
     * Checks whether the portfolio contains any stock holdings.
     *
     * @return true if the portfolio has no holdings,
     *         false otherwise
     */
    public boolean isEmpty() {
        return holdings.isEmpty();
    }
    /**
     * Validates the stock and share quantity for portfolio operations.
     *
     * @param stock the stock to validate
     * @param quantity the number of shares to validate
     * @throws IllegalArgumentException if stock is null
     *         or quantity is less than or equal to zero
     */
    private void validateStockAndQuantity(Stock stock, int quantity) {
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }
    /**
     * Returns a readable summary of the portfolio.
     *
     * The summary includes each stock's symbol, the number
     * of shares owned, and the portfolio's current total value.
     *
     * @return a formatted portfolio summary, or a message
     *         indicating that the portfolio is empty
     */
    @Override
    public String toString() {
        if (holdings.isEmpty()) return "My portfolio is empty.";

        StringBuilder result = new StringBuilder("My Portfolio:\n");
        for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
            result.append(entry.getKey().getSymbol())
                    .append(" - ")
                    .append(entry.getValue())
                    .append(" shares\n");
        }

        result.append("Total value: $").append(getTotalValue());
        return result.toString();
    }
}
