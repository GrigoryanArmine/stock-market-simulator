package com.armine.stockmarket.model;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private Map<Stock, Integer> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public void addStock(Stock stock, int quantity) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (holdings.containsKey(stock)) {
            int oldQuantity = holdings.get(stock);
            holdings.put(stock, oldQuantity + quantity);
        } else {
            holdings.put(stock, quantity);
        }
    }

    public void removeStock(Stock stock, int quantity) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (!holdings.containsKey(stock)) {
            throw new IllegalArgumentException("Stock is not in the portfolio.");
        }

        int currentQuantity = holdings.get(stock);

        if (quantity > currentQuantity) {
            throw new IllegalArgumentException(
                    "You do not have enough shares."
            );
        }

        if (quantity == currentQuantity) {
            holdings.remove(stock);
        } else {
            holdings.put(stock, currentQuantity - quantity);
        }
    }

    public int getQuantity(Stock stock) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }

        if (holdings.containsKey(stock)) {
            return holdings.get(stock);
        }

        return 0;
    }

    public double getTotalValue() {
        double totalValue = 0;

        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);
            totalValue += stock.getCurrentPrice() * quantity;
        }

        return totalValue;
    }

    public boolean containsStock(Stock stock) {
        return holdings.containsKey(stock);
    }

    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    public Map<Stock, Integer> getHoldings() {
        return holdings;
    }

    @Override
    public String toString() {
        if (holdings.isEmpty()) {
            return "My portfolio is empty.";
        }

        String result = "My Portfolio:\n";

        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);

            result += stock.getSymbol()
                    + " - "
                    + quantity
                    + " shares\n";
        }

        result += "Total value: $" + getTotalValue();

        return result;
    }
}