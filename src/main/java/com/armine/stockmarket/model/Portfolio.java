package com.armine.stockmarket.model;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private Map<Stock, Integer> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public Portfolio(Portfolio other) {
        if (other == null) throw new IllegalArgumentException("Portfolio cannot be null.");
        holdings = new HashMap<>(other.holdings);
    }

    public Map<Stock, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }

    public void addStock(Stock stock, int quantity) {
        validateStockAndQuantity(stock, quantity);
        int currentQuantity = getQuantity(stock);
        holdings.put(stock, currentQuantity + quantity);
    }

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

    public int getQuantity(Stock stock) {
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        return holdings.getOrDefault(stock, 0);
    }

    public double getTotalValue() {
        double totalValue = 0;
        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);
            totalValue += stock.getCurrentPrice() * quantity;
        }
        return totalValue;
    }

    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    private void validateStockAndQuantity(Stock stock, int quantity) {
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }

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
