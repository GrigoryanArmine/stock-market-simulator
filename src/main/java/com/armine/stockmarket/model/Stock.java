package com.armine.stockmarket.model;
import java.util.Objects;

public class Stock {

    private final String symbol;
    private final String companyName;
    private double currentPrice;

    public Stock(String symbol, String companyName, double currentPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        }

        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty.");
        }

        if (currentPrice <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }

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
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }

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

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName + " ($" + currentPrice + ")";
    }
}

