package com.armine.stockmarket.model;

import java.util.Locale;

public class Stock {
    private String symbol;
    private String companyName;
    private double currentPrice;

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

    public Stock(Stock other) {
        if (other == null) throw new IllegalArgumentException("Stock cannot be null.");

        this.symbol = other.symbol;
        this.companyName = other.companyName;
        this.currentPrice = other.currentPrice;
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

    public static void validatePrice(double price) {
        if (!Double.isFinite(price)) throw new IllegalArgumentException("Please enter a valid numeric price.");
        if (price <= 0) throw new IllegalArgumentException("Stock price must be greater than zero.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Stock stock))
            return false;
        return symbol.equals(stock.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName + " ($" + currentPrice + ")";
    }
}
