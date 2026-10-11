package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketService {
    private Map<String, Stock> stocks;
    private MarketPersistenceService persistenceService;
    private boolean loading = false;

    public MarketService() {
        this(null);
    }
    public void beginLoading() {
        loading = true;
    }

    public void finishLoading() {
        loading = false;
    }
    public MarketService(MarketPersistenceService persistenceService) {
        this.stocks = new HashMap<>();
        this.persistenceService = persistenceService;
    }

    public void addStock(Stock stock) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
        if (stocks.containsKey(stock.getSymbol())) {
            throw new IllegalArgumentException("Stock symbol already exists.");
        }
        stocks.put(stock.getSymbol(), stock);
        saveChanges();
    }

    public Stock findStock(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        }
        return stocks.get(symbol.toUpperCase());
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }

    public void updatePrice(String symbol, double newPrice) {
        Stock stock = findStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found.");
        }
        stock.updatePrice(newPrice);
        saveChanges();
    }

    public void removeStock(String symbol, TraderService traderService) {
        Stock stock = findStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found.");
        }
        if (traderService == null) {
            throw new IllegalArgumentException("Trader service cannot be null.");
        }

        for (Trader trader : traderService.getAllTraders()) {
            if (trader.getPortfolio().getQuantity(stock) > 0) {
                throw new IllegalArgumentException("Cannot remove stock. A trader currently owns shares.");
            }
        }

        stocks.remove(stock.getSymbol());
        saveChanges();
    }

    public void initializeMarket() {
        addStock(new Stock("AAPL", "Apple", 220.50));
        addStock(new Stock("MSFT", "Microsoft", 510.00));
        addStock(new Stock("GOOG", "Google", 250.00));
        addStock(new Stock("AMZN", "Amazon", 230.00));
        addStock(new Stock("TSLA", "Tesla", 350.00));
    }

    private void saveChanges() {
        if (!loading && persistenceService != null) {
            persistenceService.saveMarket(this);
        }
    }
}
