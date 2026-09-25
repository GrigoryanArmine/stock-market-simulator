package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketService {

    private Map<String, Stock> stocks;

    public MarketService() {
        stocks = new HashMap<>();
    }

    public void addStock(Stock stock) {

        if (stock == null) {
            throw new IllegalArgumentException(
                    "Stock cannot be null."
            );
        }

        stocks.put(stock.getSymbol(), stock);
    }

    public Stock findStock(String symbol) {

        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException(
                    "Stock symbol cannot be empty."
            );
        }

        return stocks.get(symbol.toUpperCase());
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }

    public void updatePrice(String symbol, double newPrice) {
        Stock stock = findStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException(
                    "Stock not found."
            );
        }
        stock.updatePrice(newPrice);
    }
}
