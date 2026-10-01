package com.armine.stockmarket.model;

import com.armine.stockmarket.service.MarketService;

public class Admin extends User {
    public Admin(String name) {
        super(name);
    }

    public void addStockToMarket(Stock stock, MarketService marketService) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
        marketService.addStock(stock);
    }

    @Override
    public String toString() {
        return "Admin: " + getName();
    }
}