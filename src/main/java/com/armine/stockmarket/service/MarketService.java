package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * Manages the stocks available in the trading simulator.
 *
 * The market maintains stocks by their unique symbols and
 * supports adding stocks, searching for stocks, updating prices,
 * and removing stocks.
 *
 * When a persistence service is configured, market changes
 * are saved automatically unless the market is being loaded.
 *
 * If saving fails with an IllegalStateException, operations
 * that add, update, or remove stocks restore the previous
 * in-memory market state.
 */
public class MarketService {
    private Map<String, Stock> stocks;
    private MarketPersistenceService persistenceService;
    private boolean loading = false;
    /**
     * Creates an empty market without automatic persistence.
     *
     * Market changes remain in memory unless saved separately.
     */
    public MarketService() {
        this.stocks = new HashMap<>();
        this.persistenceService = null;
    }
    /**
     * Creates an empty market with automatic persistence.
     *
     * A copy of the provided persistence service is stored
     * and used to save market changes.
     *
     * @param persistenceService the service responsible for
     *        saving market data
     * @throws IllegalArgumentException if persistenceService is null
     */
    public MarketService(MarketPersistenceService persistenceService) {
        if (persistenceService == null)
            throw new IllegalArgumentException("MarketPersistenceService cannot be null.");

        this.stocks = new HashMap<>();
        this.persistenceService = new MarketPersistenceService(persistenceService);
    }
    /**
     * Marks the beginning of a market-loading operation.
     *
     * Automatic persistence is disabled while the market
     * is in loading mode.
     *
     * Call finishLoading() after loading is complete.
     */
    public void beginLoading() {
        loading = true;
    }
    /**
     * Marks the end of a market-loading operation.
     *
     * Re-enables automatic persistence when a persistence
     * service is configured.
     */
    public void finishLoading() {
        loading = false;
    }
    /**
     * Adds a new stock to the market.
     *
     * Each stock must have a unique symbol. When persistence
     * is enabled, the updated market is saved automatically.
     *
     * If saving fails with an IllegalStateException,
     * the newly added stock is removed from the market.
     *
     * @param stock the stock to add
     * @throws IllegalArgumentException if stock is null
     *         or its symbol already exists
     * @throws IllegalStateException if saving the updated
     *         market fails
     */
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
    /**
     * Searches for a stock using its symbol.
     *
     * The search is case-insensitive, and leading
     * and trailing whitespace is ignored.
     *
     * @param symbol the symbol of the stock to find
     * @return the matching stock, or null if no stock
     *         with the specified symbol exists
     * @throws IllegalArgumentException if symbol is null
     *         or blank
     */
    public Stock findStock(String symbol) {
        if (symbol == null || symbol.isBlank())
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        return stocks.get(symbol.trim().toUpperCase(Locale.ROOT));
    }
    /**
     * Returns an existing stock or throws an exception.
     *
     * Unlike findStock(), this method requires the stock
     * to exist in the market.
     *
     * @param symbol the symbol of the required stock
     * @return the matching stock
     * @throws IllegalArgumentException if symbol is invalid
     *         or the stock does not exist
     */
    public Stock requireStock(String symbol) {
        Stock stock = findStock(symbol);
        if (stock == null) throw new IllegalArgumentException("Stock not found.");
        return stock;
    }
    /**
     * Returns a list containing all market stocks.
     *
     * A new list is created, so modifying the returned list
     * does not modify the market's internal collection.
     * However, the Stock objects themselves are shared.
     *
     * The order of stocks is not guaranteed.
     *
     * @return a list of all available market stocks
     */
    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }
    /**
     * Updates the current market price of an existing stock.
     *
     * The stock must exist, and the new price must be finite
     * and greater than zero.
     *
     * When persistence is enabled, the new price is saved.
     * If saving fails with an IllegalStateException,
     * the previous price is restored.
     *
     * @param symbol the symbol of the stock to update
     * @param newPrice the new market price per share
     * @throws IllegalArgumentException if the stock does
     *         not exist or the new price is invalid
     * @throws IllegalStateException if saving the updated
     *         market fails
     */
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
    /**
     * Removes an existing stock from the market.
     *
     * A stock can be removed only when no registered trader
     * currently owns shares of that stock.
     *
     * When persistence is enabled, the updated market
     * is saved automatically. If saving fails with an
     * IllegalStateException, the stock is restored.
     *
     * @param symbol the symbol of the stock to remove
     * @param traderService the service providing registered traders
     * @throws IllegalArgumentException if traderService is null,
     *         the stock does not exist, or a trader still owns shares
     * @throws IllegalStateException if saving the updated
     *         market fails
     */
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
    /**
     * Initializes the market with five example stocks.
     *
     * The default stocks are Apple (AAPL), Microsoft (MSFT),
     * Google (GOOG), Amazon (AMZN), and Tesla (TSLA).
     *
     * Each stock is added through addStock(), which applies
     * symbol validation and automatic persistence when enabled.
     *
     * @throws IllegalArgumentException if any default
     *         stock symbol already exists in the market
     * @throws IllegalStateException if saving a market
     *         change fails
     */
    public void initializeMarket() {
        addStock(new Stock("AAPL", "Apple", 220.50));
        addStock(new Stock("MSFT", "Microsoft", 510.00));
        addStock(new Stock("GOOG", "Google", 250.00));
        addStock(new Stock("AMZN", "Amazon", 230.00));
        addStock(new Stock("TSLA", "Tesla", 350.00));
    }
    /**
     * Saves the current market state when persistence
     * is configured and loading mode is disabled.
     *
     * If either condition is not met, no save is performed.
     *
     * @throws IllegalStateException if the persistence
     *         service fails to save the market
     */
    private void saveChanges() {
        if (!loading && persistenceService != null) {
            persistenceService.saveMarket(this);
        }
    }
}
