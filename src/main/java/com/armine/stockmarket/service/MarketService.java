package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarketService {
    private Map<String, Stock> stocks;
    private MarketPersistenceService persistenceService;
    private boolean loading = false;

    public MarketService() {
        this.stocks = new HashMap<>();
        this.persistenceService = null;
    }

    public MarketService(MarketPersistenceService persistenceService) {
        if (persistenceService == null)
            throw new IllegalArgumentException("MarketPersistenceService cannot be null.");

        this.stocks = new HashMap<>();
        this.persistenceService = new MarketPersistenceService(persistenceService);
    }

    public void beginLoading() {
        loading = true;
    }

    public void finishLoading() {
        loading = false;
    }

    public void addStock(Stock stock) {
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");

        String symbol = stock.getSymbol();

        if (stocks.containsKey(symbol)) throw new IllegalArgumentException("Stock symbol already exists.");

        stocks.put(symbol, stock);

        try {
            saveChanges();
        } catch (IllegalStateException ex) {
            // Restore the market if saving fails
            stocks.remove(symbol);
            throw ex;
        }
    }

    public Stock findStock(String symbol) {
        if (symbol == null || symbol.isBlank())
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        return stocks.get(symbol.trim().toUpperCase(Locale.ROOT));
    }

    public Stock requireStock(String symbol) {
        Stock stock = findStock(symbol);
        if (stock == null) throw new IllegalArgumentException("Stock not found.");
        return stock;
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }

    public void updatePrice(String symbol, double newPrice) {
        Stock stock = requireStock(symbol);

        double oldPrice = stock.getCurrentPrice();

        // Stock validates the new price
        stock.updatePrice(newPrice);
        try {
            saveChanges();
        } catch (IllegalStateException ex) {
            // Restore the previous price
            stock.updatePrice(oldPrice);
            throw ex;
        }
    }

    public void removeStock(String symbol, TraderService traderService) {
        if (traderService == null)
            throw new IllegalArgumentException("Trader service cannot be null.");

        Stock stock = requireStock(symbol);

        for (Trader trader : traderService.getAllTraders()) {
            if (trader.getOwnedShares(stock) > 0)
                throw new IllegalArgumentException("Cannot remove stock. " + "A trader currently owns shares.");
        }

        stocks.remove(stock.getSymbol());

        try {
            saveChanges();
        } catch (IllegalStateException ex) {
            // Restore the removed stock
            stocks.put(stock.getSymbol(), stock);
            throw ex;
        }
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
