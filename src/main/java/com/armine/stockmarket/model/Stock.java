package com.armine.stockmarket.model;
import java.util.Objects;

public class Stock {

    private String symbol;
    private String companyName;
    private double currentPrice;

    public Stock(String symbol, String companyName, double currentPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        }

        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty.");
        }
        validatePrice(currentPrice);

        this.symbol = symbol.toUpperCase();
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void updatePrice(double newPrice) {
        validatePrice(newPrice);
        this.currentPrice = newPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Stock stock)) {
            return false;
        }

        return symbol.equals(stock.symbol);
    }
    public static void validatePrice(double price) {
        if (!Double.isFinite(price)) {
            throw new IllegalArgumentException("Please enter a valid numeric price.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName + " ($" + currentPrice + ")";
    }
}

